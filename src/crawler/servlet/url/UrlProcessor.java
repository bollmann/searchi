package crawler.servlet.url;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import utils.nlp.LanguageDetector;
import utils.nlp.PornDetector;

import com.amazonaws.AmazonServiceException;
import com.google.gson.Gson;

import crawler.clients.HttpClient;
import crawler.dao.URLContent;
import crawler.errors.QueueFullException;
import crawler.info.URLInfo;
import crawler.parsers.Parser;
import crawler.requests.Http10Request;
import crawler.requests.HttpRequest;
import crawler.responses.HttpResponse;
import crawler.threadpool.DiskBackedQueue;
import db.dbo.URLMetaInfo;
import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;

public class UrlProcessor {
	private final Logger logger = Logger.getLogger(getClass());
	private DiskBackedQueue<String> urlQueue = null;
	private DynamoDBWrapper ddb = null;
	private S3Wrapper s3 = null;

	public UrlProcessor(DiskBackedQueue<String> urlQueue) {
		this.urlQueue = urlQueue;

		ddb = DynamoDBWrapper.getInstance(DynamoDBWrapper.US_EAST);
		s3 = S3Wrapper.getInstance();
	}

	public List<String> handleURL(String url) throws IOException,
			ParseException, QueueFullException {
		System.out.println("Attempting to process " + url);

		URLMetaInfo info = null;
		try {
			info = (URLMetaInfo) ddb.getItem(url, URLMetaInfo.class);
		} catch (Exception e) {

		}

		URLContent urlContent = null;

		if (info != null) {
			 logger.info("Not getting new data for " + url
			 + " as it's already present in ddb");
			// logger.info("Looking for item")
			// String content = s3.getItem(info.getId());
			// Gson gson = new Gson();
			// logger.debug("Parsing content to urlcontent:" + content);
			// urlContent = gson.fromJson(content, URLContent.class);
			// return new ArrayList<String>();
		} else {
			logger.debug("Getting fresh data for:" + url);
			urlContent = getNewContent(url);
		}

		List<String> links = new ArrayList<String>();

		if (urlContent != null) {
			if (urlContent.getContentType() == null) {
				urlContent.setContentType("text/html");
			}
			// only extract links from text/html
			logger.debug("Does this contain html?"
					+ urlContent.getContentType().contains("text/html"));
			if (Parser.isAllowedCrawlContentType(urlContent.getContentType())) {

				if (urlContent.getContentType().contains("text/html")) {
					logger.debug("Content is html. Parsing for links");

					try {
						links = extractLinksFromContent(new URLInfo(url),
								urlContent.getContent());
					} catch (Exception e) {
						logger.error("Skipping " + url
								+ " as error in parsing content for links");
						// e.printStackTrace();
						return new ArrayList<String>();
					}
				}
				try {
					logger.info("Saving data for " + url);
					Date start = Calendar.getInstance().getTime();

					URLMetaInfo toSave = new URLMetaInfo(url);
					logger.info("Saving data for " + url + "with id "
							+ toSave.getId());

					// toSave.setUrl(url);
					toSave.setLastCrawledOn(Calendar.getInstance().getTime());

					toSave.setType(urlContent.getContentType());
					toSave.setSize(urlContent.getContent().length());
					ddb.putItem(toSave);

					Gson gson = new Gson();
					urlContent.setOutgoingLinks(links);
					String serializeJson = gson.toJson(urlContent);

					s3.putItem(toSave.getId(), serializeJson);
					Date end = Calendar.getInstance().getTime();
					logger.info("Saved data for " + url + " in time "
							+ (end.getTime() - start.getTime()));
				} catch (AmazonServiceException e) {
					// e.printStackTrace();
					logger.error("Got an error while saving the url.");
				}
			}
		} else {
			logger.debug("UrlContent was null");
		}
		return links;
	}

	public URLContent getNewContent(String url) throws IOException,
			ParseException, QueueFullException {
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
			if (request.getHeader("Content-Language") != null) {
				if (!request.getHeader("Content-Language").startsWith("en")) {
					return null;
				}
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
				request.setHeader(
						"Accept",
						"text/html,application/pdf,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				response = HttpClient.genericGet(url, request);

				content = new String(response.getBody());
				logger.info("Content type:"
						+ response.getHeader("Content-Type") + " and body "
						+ content);
				if (response.getHeader("Content-Type")
						.equals("application/pdf")) {
					content = "<html><body>" + content + "</body></html>";
				}

				// file handling logic
				if (!LanguageDetector.isEnglish(content)
						&& !PornDetector.isPorn(content)) {
					return null;
				}

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
			synchronized (urlQueue) {
				urlQueue.enqueue(location);
				urlQueue.notify();
			}
			return null;
		}
		return urlContent;
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
			// e.printStackTrace();
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
					if (linkHref.startsWith("#")) { // remove anchor links to
													// same page
						continue;
					}
				} else {
					continue;
				}

				logger.debug("Looking at link:" + linkHref);

				try {
					new URL(linkHref);
				} catch (MalformedURLException e) {
					// Not an absolute url. So add protocol and domain
					// check for different derelativization

					// is top level domain
					if (linkHref.startsWith("//")) {
						linkHref = urlRoot.getProtocol() + ":" + linkHref;
					} else if (linkHref.startsWith("/")) {
						// is relative to root
						linkHref = urlRoot.getRoot() + linkHref;
					} else {
						if (urlRoot.toString().endsWith("/")) {
							linkHref = urlRoot.toString() + linkHref;
						} else {
							linkHref = urlRoot.toString() + "/" + linkHref;
						}
					}

				}
				logger.debug("Link derelativized to:" + linkHref);
				links.add(linkHref);
			}
		}

		return links;
	}
}
