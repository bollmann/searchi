package servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mapreduce.worker.HeartBeat;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import parsers.Parser;
import requests.Http10Request;
import requests.HttpRequest;
import responses.HttpResponse;
import servlet.multinodal.status.WorkerStatus;
import clients.HttpClient;

import com.amazonaws.AmazonServiceException;
import com.google.gson.Gson;

import crawler.info.URLInfo;
import dao.URLContent;
import db.dbo.URLMetaInfo;
import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;

public class CrawlerSlave extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	private String masterIPPort = null;
	private WorkerStatus workerStatus = null;

	@Override
	public void init() {
		masterIPPort = (String) getServletContext().getInitParameter("master");
		String workerIP = (String) getServletContext().getInitParameter("worker-ip");
		Integer workerPort = Integer.parseInt(getServletContext().getInitParameter("worker-port"));
		workerStatus = new WorkerStatus();
		workerStatus.setIpAddress(workerIP);
		workerStatus.setPort(workerPort);
		workerStatus.setStatus("active");
		HeartBeat hb = new HeartBeat("http://" + masterIPPort + "/master/workerStatus");
		hb.setWorkerStatus(workerStatus);
		hb.run();
	}

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<html><body>Worker working!</body></html>");
		return;

	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		String url = request.getParameter("url");
		List<String> outgoingUrls = null;
		try {
			outgoingUrls = handleURL(url);
			workerStatus.setUrlProcessed(workerStatus.getUrlProcessed()+1);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (outgoingUrls.size() > 0) {
			postLinksToMaster(outgoingUrls);
		}
	}
	
	public void postLinksToMaster(List<String> urls) {
		String content = new Gson().toJson(urls);
		Http10Request request = new Http10Request();
		request.setBody(content);
		try {
			HttpClient.post(masterIPPort + "/master/enqueueURLs", request);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			logger.error("Posting to server failed!");
			e.printStackTrace();
		}
	}

	public List<String> handleURL(String url) throws IOException,
			ParseException {
		System.out.println("Attempting to process " + url);

		DynamoDBWrapper ddb = DynamoDBWrapper
				.getInstance(DynamoDBWrapper.URL_CONTENT_ENDPOINT);
		S3Wrapper s3 = S3Wrapper.getInstance();

		URLMetaInfo info = (URLMetaInfo) ddb.getItem(url, URLMetaInfo.class);

		URLContent urlContent = null;

		if (info != null) {
			logger.info("Found a db entry for:" + url
					+ " so looking in s3 for " + info.getId());
			URLContent oldUrlContent = null;
			String content = s3.getItem(info.getId());
			Gson gson = new Gson();
			logger.debug("Parsing content to urlcontent:" + content);
			oldUrlContent = gson.fromJson(content, URLContent.class);

			urlContent = getPersistentContent(oldUrlContent);
		} else {
			logger.info("Getting fresh data for:" + url);
			urlContent = getNewContent(url);
		}

		List<String> links = new ArrayList<String>();
		
		if (urlContent != null) {
			if (urlContent.getContentType() == null) {
				return new ArrayList<String>();
			}
			// only extract links from text/html
			logger.debug("Does this contain html?"
					+ urlContent.getContentType().contains("text/html"));
			if (Parser.isAllowedCrawlContentType(urlContent.getContentType())) {
				logger.debug("Content is html. Parsing for links");
				
				try {
					links = extractLinksFromContent(new URLInfo(url),
							urlContent.getContent());
				} catch (Exception e) {
					logger.error("Skipping " + url
							+ " as error in parsing content for links");
					e.printStackTrace();
					return new ArrayList<String>();
				}
				try {
					logger.info("Saving data for " + url);
					Date start = Calendar.getInstance().getTime();

					URLMetaInfo toSave = new URLMetaInfo();
					toSave.setUrl(url);
					toSave.setLastCrawledOn(Calendar.getInstance().getTime());

					toSave.setType(urlContent.getContentType());
					toSave.setSize(urlContent.getContent().length());
					ddb.putItem(toSave);

					String id = toSave.getId();
					Gson gson = new Gson();
					// toSave.setOutgoingURLs(links);
					// List<String> outgoingLinkIds = URLMetaInfo
					// .convertLinksToIds(links, ddb);
					urlContent.setOutgoingLinks(links);
					String serializeJson = gson.toJson(urlContent);
					s3.putItem(id, serializeJson);
					Date end = Calendar.getInstance().getTime();
					logger.info("Saved data for " + url + " in time "
							+ (end.getTime() - start.getTime()));
				} catch (AmazonServiceException e) {
					e.printStackTrace();
				}
			}
		} else {
			logger.error("UrlContent was null");
		}
		// } // end of synchonization
		return links;
	}

	private URLContent getNewContent(String url) throws IOException,
			ParseException {
		System.out.println("Downloading new content for " + url);
		// HEAD. check content
		String content = null;
		URLContent urlContent = null;
		HttpRequest request = new Http10Request();
		request.setPath(new URL(url).getPath());
		request.setMethod("HEAD");
		request.setHeader("User-Agent", "cis455crawler");
		HttpResponse response = HttpClient.genericHead(url, request);
		logger.debug("Head got response code:"
				+ response.getResponse().getResponseCode());
		if (response.getResponse().getResponseCode() == 200
		// && response.containsHeader("Content-Type")
		// && response.containsHeader("Content-Length")
		) {
			int length = 0;
			if (response.getHeader("Content-Length") != null) {
				length = Parser.convertByesToMBytes(Integer.parseInt(response
						.getHeader("Content-Length")));
			}
			boolean contentPolicy = Parser.isAllowedCrawlContentType(response
					.getHeader("Content-Type"))
					&& (length <= Parser.maxFileSize);
			logger.info("Checking if allowed content type "
					+ response.getHeader("Content-Type")
					+ ":"
					+ Parser.isAllowedCrawlContentType(response
							.getHeader("Content-Type")) + " length ok?:"
					+ length + "<=" + Parser.maxFileSize);
			if (contentPolicy) {
				request = new Http10Request();
				request.setPath(new URL(url).getPath());
				request.setMethod("GET");
				request.setHeader("User-Agent", "cis455crawler");
				request.setHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				response = HttpClient.genericGet(url, request);
				content = new String(response.getBody());
				urlContent = new URLContent();
				urlContent.setUrl(url);
				urlContent.setContent(content);
				urlContent.setContentType(response.getHeader("Content-Type"));
				Date crawledOn = Calendar.getInstance().getTime();
				urlContent.setCrawledOn(crawledOn);
			} else {
				System.out.println("Skipped "
						+ url
						+ " because "
						+ response.getHeader("Content-Type")
						+ " allowed? "
						+ Parser.isAllowedCrawlContentType(response
								.getHeader("Content-Type")) + " length: "
						+ length + " <= " + Parser.maxFileSize);
			}
		} else if (response.getResponse().getResponseCode() == 301) {
			String location = response.getHeader("Location");
			System.out.println("Got redirect from:" + url + " to " + location);
			logger.debug("Got redirect to " + location);
			List<String> urls = new ArrayList<String>();
			urls.add(url);
			postLinksToMaster(urls);
			return null;
		}
		return urlContent;
	}

	private URLContent getPersistentContent(URLContent urlContent)
			throws IOException, ParseException {
		URLContent content = null;
		// if present, check last crawled time
		Date lastCrawled = urlContent.getCrawledOn();
		HttpRequest request = new Http10Request();
		request.setPath(new URL(urlContent.getUrl()).getPath());
		request.setMethod("HEAD");
		request.setHeader("User-Agent", "cis455crawler");
		request.setDateHeader("If-Modified-Since", lastCrawled.getTime());
		HttpResponse response = HttpClient.genericHead(urlContent.getUrl(),
				request);
		if (response.getResponse().getResponseCode() == 304) {
			logger.info("Got 304 for head for if modified url:"
					+ urlContent.getUrl() + " ifmodified:"
					+ Parser.formatDate(lastCrawled));
			System.out.println("Not downloading " + urlContent.getUrl()
					+ ". It already exists in db.");
			// just use this content
			content = urlContent;
		} else if (response.getResponse().getResponseCode() == 200) {
			logger.info("Got a " + response.getResponse().getResponseCode()
					+ " for a url so getting fresh data");
			System.out.println("Refreshing content for " + urlContent.getUrl()
					+ " as it has been updated since the last crawl.");
			// make a new get
			content = getNewContent(urlContent.getUrl());
		} else if (response.getResponse().getResponseCode() == 301) {
			String location = response.getHeader("Location");
			System.out.println("Got redirect from:" + urlContent.getUrl()
					+ " to " + location);
			logger.debug("Got redirect to " + location);
			List<String> urls = new ArrayList<String>();
			urls.add(location);
			postLinksToMaster(urls);
			return null;
		}
		return content;
	}

	public List<String> extractLinksFromContent(URLInfo urlRoot, String content) {
		Tidy tidy = new Tidy();
		tidy.setInputEncoding("UTF-8");
		tidy.setOutputEncoding("UTF-8");
		tidy.setWraplen(Integer.MAX_VALUE);
		tidy.setPrintBodyOnly(true);
		tidy.setXmlOut(true);
		tidy.setSmartIndent(true);
		tidy.setQuiet(true);
		tidy.setOnlyErrors(true);
		ByteArrayInputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(content.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("Error in extracting links from html for:" + urlRoot);
			e.printStackTrace();
			return new ArrayList<String>();
		}
		logger.debug("Parsing dom");
		Document doc = tidy.parseDOM(inputStream, System.err);
		logger.debug("Finished parsing dom");
		String[] linkTags = { "a", "link" };
		List<String> links = new ArrayList<String>();
		for (String linkTag : linkTags) {
			NodeList nl = doc.getElementsByTagName(linkTag);
			for (int i = 0; i < nl.getLength(); i++) {
				Node linkItem = nl.item(i);
				String linkHref = null;
				if (linkItem.getAttributes().getNamedItem("href") != null) {
					linkHref = linkItem.getAttributes().getNamedItem("href")
							.getNodeValue();
				} else {
					continue;
				}

				logger.debug("Looking at link:" + linkHref);

				try {
					new URL(linkHref);
				} catch (MalformedURLException e) {
					// Not an absolute url. So add protocol and domain
					// check for different derelativization
					if (linkHref.startsWith("/")) {
						// is relative to root
						linkHref = urlRoot.getRoot() + linkHref;
					} else {
						if (urlRoot.toString().endsWith("/")) {
							linkHref = urlRoot.toString() + linkHref;
						} else {
							linkHref = urlRoot.toString() + "/" + linkHref;
						}
					}

					// e.printStackTrace();
				}
				logger.debug("Link derelativized to:" + linkHref);
				links.add(linkHref);
			}
		}

		return links;
	}
}
