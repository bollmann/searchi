package crawler.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import utils.file.FileUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crawler.servlet.background.UrlProcessorThread;
import crawler.servlet.multinodal.status.WorkerStatus;
import crawler.threadpool.DiskBackedQueue;
import crawler.threadpool.MercatorQueue;
import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;

public class SingleNodeCrawler extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	private WorkerStatus workerStatus = null;
	private DiskBackedQueue<String> urlQueue;
	private Set<String> allowedDomains = null;
	private MercatorQueue mq = null;
	private DynamoDBWrapper ddb = null;
	private S3Wrapper s3 = null;
	private Set<String> blacklistedDomains;

	public void initDB() {
		ddb = DynamoDBWrapper.getInstance(DynamoDBWrapper.US_EAST);
		s3 = S3Wrapper.getInstance();
		String flushData = (String) getServletContext().getInitParameter(
				"flushData");

		String flushQueue = (String) getServletContext().getInitParameter(
				"flushQueue");

		if (flushData.equals("yes")) {
			ddb.deleteTable("URLMetaInfo2");
			s3.deleteBucket(s3.URL_BUCKET);

		}

		if (flushQueue.equals("yes")) {
			s3.deleteBucket(s3.URL_QUEUE_BUCKET);
			ddb.deleteTable("QueueInfo");
		}

		s3.createBucket(s3.URL_BUCKET);
		s3.createBucket(s3.URL_QUEUE_BUCKET);
		ddb.createTable("URLMetaInfo2", 30, 30, "url", "S");
		ddb.createTable("DomainInfo", 15, 15, "domain", "S");

		// look for queue in s3. If not there, then initialize to new

		try {
			String queueContent = s3.getItem(s3.URL_QUEUE_BUCKET, "queueState");
			logger.debug("queue content " + queueContent);
			Type listType = new TypeToken<DiskBackedQueue<String>>() {
			}.getType();
			logger.info("Reading queue for s3. Resuming saved state.");
			urlQueue = new Gson().fromJson(queueContent, listType);
		} catch (Exception e) {
			// e.printStackTrace();
			// logger.error("Couldn't find saved queue");
			// urlQueue = new DiskBackedQueue<String>(1000);
		}

	}

	@Override
	public void init() {
		/* ------------------------------ DB ------------------------ */
		initDB();

		/* ------------------------------ QUEUE ------------------------ */
		String domainRange = (String) getServletContext().getInitParameter(
				"domain-range");
		urlQueue = new DiskBackedQueue<String>(domainRange);
		mq = new MercatorQueue();

		/* ------------------------------ Worker ------------------------ */
		String workerIP = (String) getServletContext().getInitParameter(
				"worker-ip");
		Integer workerPort = Integer.parseInt(getServletContext()
				.getInitParameter("worker-port"));
		workerStatus = new WorkerStatus();
		workerStatus.setIpAddress(workerIP);
		workerStatus.setPort(workerPort);
		workerStatus.setStatus("active");

		/* ------------------------------ Domain Config ------------------------ */
		// fill allowedDomains from config file
		allowedDomains = new HashSet<String>();
		blacklistedDomains = new HashSet<String>();
		String domainConfigFile = getServletContext().getInitParameter(
				"domain-config");
		
		String blackListConfigFile = getServletContext().getInitParameter(
				"blacklist-config");

		// allowedDomains.add("wikipedia.org");
		// allowedDomains.add("reddit.com");

		try {
			// domain list
			for (String domainConfig : domainConfigFile.split(",")) {
				logger.info("Reading domain config file:" + domainConfig);
				allowedDomains = readConfigFile(domainConfig, 2);
			}
			
			// blacklisted domains
//			for (String blackListConfig : blackListConfigFile.split(",")) {
//				logger.info("Reading domain config file:" + blackListConfig);
//				allowedDomains = readConfigFile(blackListConfig, 1);
//			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* ------------------------------ Seeding ------------------------ */

		List<String> seedUrls = new ArrayList<String>() {
			{
				add("https://news.ycombinator.com/");
				// add("https://en.wikipedia.org/wiki/Main_Page");
				// add("https://www.reddit.com/");
				// add("http://www.dmoz.org/");
				// add("http://www.dtelepathy.com/blog/inspiration/14-beautiful-content-heavy-websites-for-inspiration");
				// add("http://www.nytimes.com/");
				// add("http://www.onlinenewspapers.com/Top50/Top50-CurrentUS.htm");
				// add("https://dbappserv.cis.upenn.edu/crawltest.html");
			}
		};

		for (String url : seedUrls) {
			synchronized (urlQueue) {
				urlQueue.enqueue(url);
			}
		}

		Iterator<String> iter = allowedDomains.iterator();
		while (iter.hasNext()) {
			urlQueue.enqueue("http://www." + iter.next());
		}

		/* ------------------------------ Thread Start ------------------------ */
		Integer maxProcessors = 200;
		for (int i = 0; i < maxProcessors; i++) {
			UrlProcessorThread processor = new UrlProcessorThread(mq,
					allowedDomains, blacklistedDomains, urlQueue, workerStatus);
			processor.start();
		}

	}

	public Set<String> readConfigFile(String fileName, int field) throws IOException {
		Set<String> result = new HashSet<String>();
		String content = FileUtils.readFile(fileName);
		for (String line : content.split("\n")) {
			if (line.split(",").length < (field-1)) {
				continue;
			}
			String domain = line.split(",")[field-1];
			logger.debug("Read domain:" + domain);
			result.add(domain);
		}
		return result;
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if (request.getPathInfo().equals("/status")) {
			String content = null;
			try {
				content = FileUtils
						.readFile("resources/master_status_page.html");
			} catch (IOException e) {
				// e.printStackTrace();
				logger.error("error in reading status page");
				response.sendError(404);
				return;
			}
			StringBuilder sb = new StringBuilder();

			sb.append("<tr>");
			sb.append("<td>" + workerStatus.getIpAddress() + ":"
					+ workerStatus.getPort() + "</td>" + "<td>"
					+ workerStatus.getStatus() + "</td>" + "<td>"
					+ workerStatus.getUrlProcessed() + "</td>");
			sb.append("</tr>");

			content = content.replace("<$workers$>", sb.toString());
			response.setContentType("text/html");
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			out.println(content);
			return;
		}
	}

}
