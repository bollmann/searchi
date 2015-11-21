package crawler;

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

import parsers.Parser;
import requests.Http10Request;
import requests.HttpRequest;
import responses.HttpResponse;
import threadpool.MercatorNode;
import threadpool.MercatorQueue;
import threadpool.Queue;
import clients.HttpClient;
import crawler.info.URLInfo;
import dao.URLContent;
import db.dbo.URLMetaInfo;
import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;
import errors.NoDomainConfigException;

public class URLHandler {
	private final Logger logger = Logger.getLogger(getClass());
	private MercatorQueue mq;
	private Queue<String> q;

	public URLHandler(MercatorQueue mq, Queue<String> q) {
		this.mq = mq;
		this.q = q;
	}

	/**
	 * Checks if the url is present in the Berkeley disk store. If it is, it
	 * will check with a head request to the url if the content has been
	 * modified since the creation date of the entry in the db. If not, it'll
	 * just pick up the db content. If it has, a fresh request is made and put
	 * to the db. If it is not present in the Berkeley disk store, it will query
	 * the site with a GET anyway
	 * 
	 * @param url
	 * @param mq
	 * @param q
	 * @throws ParseException
	 * @throws IOException
	 */
	public void handleURL(String url) throws IOException, ParseException {
		if (!mq.isDomainPresentForUrl(url)) {
			enqueueURL(url);
			logger.info("Returning after enqueueing as domain not present");
			return;
		} else {

			String domain = new URL(url).getHost();
			MercatorNode node = mq.getDomainNodeMap().get(domain);
			// have to synchronize on the node so that a node processes and
			// updates the domain config for a domain before letting any other
			// thread to get it
			
			synchronized (node) {
				logger.info("Handling url:" + url);
				System.out.println("Attempting to process " + url);
				
				DynamoDBWrapper ddb = DynamoDBWrapper.getInstance("http://localhost:8000");
				S3Wrapper s3 = S3Wrapper.getInstance();
				
				URLMetaInfo info = (URLMetaInfo) ddb.getItem(url, URLMetaInfo.class);

				URLContent urlContent = null;
				
				
				
				if (info != null) {
					logger.info("Found a db entry for:" + url);
					URLContent oldUrlContent = new URLContent();
					oldUrlContent.setAbsolutePath(url);
					String content = s3.getItem(url);
					oldUrlContent.setContent(content);
					urlContent = getPersistentContent(oldUrlContent);
				} else {
					logger.info("Getting fresh data for:" + url);
					urlContent = getNewContent(url);
				}
				if (urlContent != null) {
					logger.debug("Saving content of:"
							+ urlContent.getAbsolutePath());
					// save content. Doesn't matter if old. Just replaces
					// content anyway
					s3.putItem(url, urlContent.getContent());
					
					// only extract links from text/html
					logger.debug("Does this contain html?"
							+ urlContent.getContentType().contains("text/html"));
					if (Parser.isAllowedCrawlContentType(urlContent.getContentType())) {
						logger.debug("Content is html. Parsing for links");
						List<String> links = extractLinksFromContent(
								new URLInfo(url), urlContent.getContent());
						// System.out.println("Got links:" + links);
						logger.info("Got links:" + links);
						for (String link : links) {
							enqueueURL(link);
						}
						URLMetaInfo toSave = new URLMetaInfo();
						toSave.setUrl(url);
						toSave.setLastCrawledOn(Calendar.getInstance().getTime());
						toSave.setOutgoingURLs(links);
						toSave.setType(urlContent.getContentType());
						toSave.setSize(urlContent.getContent().length());
						ddb.putItem(toSave);
					}
				} else {
					logger.error("UrlContent was null");
				}
			} // end of synchonization
		}
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
				urlContent.setAbsolutePath(url);
				urlContent.setContent(content);
				urlContent.setContentType(response.getHeader("Content-Type"));
				Date crawledOn = Calendar.getInstance().getTime();
				urlContent.setCrawledOn(crawledOn);

				// update last crawled on mq
				synchronized (mq) {
					String domain = new URL(url).getHost();
					MercatorNode node = mq.getDomainNodeMap().get(domain);
					node.setLastCrawledTime(Calendar.getInstance().getTime());
				}
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
			enqueueURL(location);
		}
		return urlContent;
	}

	private URLContent getPersistentContent(URLContent urlContent)
			throws IOException, ParseException {
		URLContent content = null;
		// if present, check last crawled time
		Date lastCrawled = urlContent.getCrawledOn();
		HttpRequest request = new Http10Request();
		request.setPath(new URL(urlContent.getAbsolutePath()).getPath());
		request.setMethod("HEAD");
		request.setHeader("User-Agent", "cis455crawler");
		request.setDateHeader("If-Modified-Since", lastCrawled.getTime());
		HttpResponse response = HttpClient.genericHead(
				urlContent.getAbsolutePath(), request);
		if (response.getResponse().getResponseCode() == 304) {
			logger.info("Got 304 for head for if modified url:"
					+ urlContent.getAbsolutePath() + " ifmodified:"
					+ Parser.formatDate(lastCrawled));
			System.out.println("Not downloading "
					+ urlContent.getAbsolutePath()
					+ ". It already exists in db.");
			// just use this content
			content = urlContent;
		} else if (response.getResponse().getResponseCode() == 200) {
			logger.info("Got a " + response.getResponse().getResponseCode()
					+ " for a url so getting fresh data");
			System.out.println("Refreshing content for "
					+ urlContent.getAbsolutePath()
					+ " as it has been updated since the last crawl.");
			// make a new get
			content = getNewContent(urlContent.getAbsolutePath());
		} else if (response.getResponse().getResponseCode() == 301) {
			String location = response.getHeader("Location");
			System.out.println("Got redirect from:"
					+ urlContent.getAbsolutePath() + " to " + location);
			logger.debug("Got redirect to " + location);
			enqueueURL(location);
		}
		return content;
	}

	public void enqueueURL(String url) throws MalformedURLException {
		try {
			synchronized (mq) {
				mq.enqueueUrl(url);
			}
		} catch (NoDomainConfigException e) {
			URLInfo urlInfo = new URLInfo(url);
			String httpRobotsUrl = urlInfo.getProtocol() + "://"
					+ urlInfo.getHostName() + "/robots.txt";
			logger.debug("Trying " + urlInfo.getProtocol() + " connection to:"
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

	/**
	 * Parses the html content in content and extracts links. Derelativizes them
	 * as necssary by adding urlRoot as prefix to the links. Therefore should
	 * have protocol and hostname.
	 * 
	 * @param urlRoot
	 *            should be absolute url with protocol
	 * @param content
	 *            should be html content
	 * @return
	 */
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
