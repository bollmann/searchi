package crawler.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crawler.clients.HttpClient;
import crawler.errors.NoDomainConfigException;
import crawler.info.URLInfo;
import crawler.parsers.Parser;
import crawler.policies.FilePolicy;
import crawler.requests.Http10Request;
import crawler.requests.HttpRequest;
import crawler.responses.HttpResponse;
import crawler.servlet.multinodal.producer.UrlPoster;
import crawler.servlet.multinodal.producer.UrlProducer;
import crawler.servlet.multinodal.status.WorkerStatus;
import crawler.threadpool.DiskBackedQueue;
import crawler.threadpool.MercatorNode;
import crawler.threadpool.MercatorQueue;
import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;

public class CrawlerMaster extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	private DynamoDBWrapper ddb = null;
	private S3Wrapper s3 = null;
	private DiskBackedQueue<String> q = null;
	private MercatorQueue mq = null;
	private Map<String, WorkerStatus> workerStatusMap = null;

	@Override
	public void init() {
		ddb = DynamoDBWrapper.getInstance(DynamoDBWrapper.US_EAST);
		s3 = S3Wrapper.getInstance();
		workerStatusMap = new HashMap<String, WorkerStatus>();

		String flushData = (String) getServletContext().getInitParameter(
				"flushData");

		String flushQueue = (String) getServletContext().getInitParameter(
				"flushQueue");

		if (flushData.equals("yes")) {
			ddb.deleteTable("URLMetaInfo");
			s3.deleteBucket(s3.URL_BUCKET);

		}

		if (flushQueue.equals("yes")) {
			s3.deleteBucket(s3.URL_QUEUE_BUCKET);
			ddb.deleteTable("QueueInfo");
		}

		s3.createBucket(s3.URL_BUCKET);
		s3.createBucket(s3.URL_QUEUE_BUCKET);
		ddb.createTable("URLMetaInfo", 15, 15, "url", "S");

		// look for queue in s3. If not there, then initialize to new

		DiskBackedQueue<String> q = null;

		try {
			String queueContent = s3.getItem(s3.URL_QUEUE_BUCKET, "queueState");
			logger.info("queue content " + queueContent);
			Type listType = new TypeToken<DiskBackedQueue<String>>() {
			}.getType();
			System.out.println("Reading queue for s3. Resuming saved state.");
			q = new Gson().fromJson(queueContent, listType);
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error("Couldn't find saved queue");
			q = new DiskBackedQueue<String>(1000);
		}

		mq = new MercatorQueue();
		mq.setOutgoingJobQueue(q);

		List<String> seedUrls = new ArrayList<String>() {
			{
				add("https://en.wikipedia.org/wiki/Main_Page");
				add("https://www.reddit.com/");
				// add("https://dbappserv.cis.upenn.edu/crawltest.html");
			}
		};

		for (String url : seedUrls) {
			q.enqueue(url);
		}

		Integer maxUrls = Integer.parseInt((String) getServletContext()
				.getInitParameter("maxUrls"));
		UrlProducer producer = new UrlProducer(mq, q, maxUrls, workerStatusMap);
		Thread t = new Thread(producer);
		t.start();
		UrlPoster poster = new UrlPoster(q, workerStatusMap);
		poster.start();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (request.getPathInfo() == null) {
			String content = "<html><body>Master is running!</body></html>";
			PrintWriter out = response.getWriter();
			out.print(content);
			return;
		}

		if (request.getPathInfo().equals("/workerStatus")) {
			boolean created;
			String ipAddress = request.getRemoteAddr();
			int port = -1;
			if (request.getParameter("port") != null) {
				port = Integer.parseInt(request.getParameter("port"));
			} else {
				response.sendError(400, "No port given!");
				return;
			}
			String status = null;
			if (request.getParameter("status") != null) {
				status = request.getParameter("status");
			} else {
				response.sendError(400, "No status given!");
				return;
			}
			Integer urlsProcessed = 0;
			if (request.getParameter("urlsProcessed") != null) {
				urlsProcessed = Integer.parseInt(request
						.getParameter("urlsProcessed"));
			} else {
				response.sendError(400, "No urlsProcessed given!");
				return;
			}
			String workerKey = ipAddress + ":" + port;
			WorkerStatus workerStatus = null;

			synchronized (workerStatusMap) {
				if (workerStatusMap.containsKey(workerKey)) {
					logger.info("Found existing entry for " + workerKey
							+ " in worker status map");
					workerStatus = workerStatusMap.get(workerKey);
					created = false;
				} else {
					logger.info("Creating new entry for " + workerKey
							+ " in worker status map");
					workerStatus = new WorkerStatus();
					workerStatus.setIpAddress(ipAddress);
					workerStatus.setPort(port);
					created = true;
				}
			}
			workerStatus.setIpAddress(ipAddress);
			if (urlsProcessed > workerStatus.getUrlProcessed()) {
				workerStatus.setUrlProcessed(urlsProcessed);
			} else {
				workerStatus.setUrlProcessed(workerStatus.getUrlProcessed() + urlsProcessed);
			}
			workerStatus.setStatus(status);

			synchronized (workerStatusMap) {
				workerStatusMap.put(workerStatus.getIpAddress() + ":"
						+ workerStatus.getPort(), workerStatus);
			}
		} else if (request.getPathInfo().equals("/status")) {
			String content = null;
			try {
				content = FilePolicy
						.readFile("resources/master_status_page.html");
			} catch (IOException e) {
//				e.printStackTrace();
				logger.error("Error in reading file");
				return;
			}
			StringBuilder sb = new StringBuilder();
			synchronized (workerStatusMap) {
				for (Entry<String, WorkerStatus> entry : workerStatusMap
						.entrySet()) {
					WorkerStatus status = entry.getValue();
					if (status.isActive()) {
						sb.append("<tr>");
						sb.append("<td>" + status.getIpAddress() + ":"
								+ status.getPort() + "</td>" + "<td>"
								+ status.getStatus() + "</td>" + "<td>"
								+ status.getUrlProcessed() + "</td>");
						sb.append("</tr>");
					}
				}
			}

			content = content.replace("<$workers$>", sb.toString());
			content = content.replace("<$queueItems$>", "Items in queue: "
					+ String.valueOf(q.getSize()));
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(content);
			return;
		} else if (request.getPathInfo().equals("/queueStatus")) {
			String content = null;
			try {
				content = FilePolicy
						.readFile("resources/master_queue_status_page.html");
			} catch (IOException e) {
//				e.printStackTrace();
				logger.error("Error in reading file");
				return;
			}
			StringBuilder sb = new StringBuilder();
			sb.append("<ol>");
			for (String url : q.getOutputList()) {
				sb.append("<li>" + url + "</li>");
			}
			sb.append("</ol>");
			content = content.replace("<$queueStatus$>", sb.toString());
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(content);
		}
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (request.getPathInfo().equals("/enqueueURLs")) {

			BufferedReader br = request.getReader();
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
			response.setStatus(200);
			response.flushBuffer();
			Type listType = new TypeToken<List<String>>() {
			}.getType();
			List<String> urls = new Gson().fromJson(sb.toString(), listType);
			logger.info("Master got urls: " + urls);
			for (String url : urls) {
				enqueueURL(url);
			}
		}
	}

	public void enqueueURL(String url) throws MalformedURLException {
		try {
			synchronized (mq) {
				mq.enqueueUrl(url);
			}
		} catch (NoDomainConfigException e) {
			URLInfo urlInfo = new URLInfo(url);
			if (urlInfo.getProtocol() == null) {
				return;
			}
			String httpRobotsUrl = urlInfo.getProtocol() + "://"
					+ urlInfo.getHostName() + "/robots.txt";
			logger.info("Trying " + urlInfo.getProtocol() + " connection to:"
					+ urlInfo.getProtocol() + "://" + urlInfo.getHostName()
					+ "/robots.txt");
			try {
				HttpRequest request = new Http10Request();
				request.setPath(new URL(url).getPath());
				request.setMethod("HEAD");
				request.setHeader("User-Agent", "cis455crawler");
				HttpResponse response = HttpClient.genericHead(httpRobotsUrl,
						request);
				logger.debug("Got head");
				if (response.getResponse().getResponseCode() == 200) {
					request = new Http10Request();
					request.setPath(new URL(url).getPath());
					request.setMethod("GET");
					request.setHeader("User-Agent", "cis455crawler");
					response = HttpClient.genericGet(httpRobotsUrl, request);
					MercatorNode node = Parser.parseRobotsContent(
							urlInfo.getHostName(),
							new String(response.getBody()));
					addNodeToMq(node, url);
				} else {
					// make default mn
					MercatorNode node = new MercatorNode(urlInfo.getHostName());
					addNodeToMq(node, url);

				}
			} catch (MalformedURLException | NoDomainConfigException e1) {
				logger.error("There's something wrong even after adding to MQ the new url hasn't been added to the apt queue!");
				e1.printStackTrace();
			} catch (IOException e1) {
				logger.error("Got an io exception with url:" + url);
				e1.printStackTrace();
			} catch (ParseException e1) {
				logger.error("Got a parse exception with url:" + url);
				e1.printStackTrace();
			}
		}

	}

	public void addNodeToMq(MercatorNode node, String url)
			throws MalformedURLException, NoDomainConfigException {
		synchronized (mq) {
			node.setLastCrawledTime(Calendar.getInstance().getTime());
			mq.addNode(node);
			mq.enqueueUrl(url);
			logger.info("Setting last crawled time to now.");
		}
	}

	@Override
	public void destroy() {
		System.out.println("Exiting master gracefully.");
		DynamoDBWrapper ddb = DynamoDBWrapper
				.getInstance(DynamoDBWrapper.US_EAST);
		S3Wrapper s3 = S3Wrapper.getInstance();
		String queueContent = new Gson().toJson(q);
		s3.putItem(s3.URL_QUEUE_BUCKET, "queueState", queueContent);
		ddb.displaySaveStatistics();
	}
}