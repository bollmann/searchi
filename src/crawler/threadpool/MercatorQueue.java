package crawler.threadpool;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import crawler.errors.NoDomainConfigException;
import crawler.parsers.Parser;

public class MercatorQueue {
	private Integer size;
	private Integer urlsProcessed = 0;
	private final Logger logger = Logger.getLogger(getClass());
	MercatorNode head = null, last = null;
	Set<String> frontier;
	Map<String, MercatorNode> domainNodeMap;
	private int MAX_INACTIVE_DOMAIN_INTERVAL = 60 * 60;

	public Integer getUrlsProcessed() {
		return urlsProcessed;
	}

	public void setUrlsProcessed(Integer urlsProcessed) {
		this.urlsProcessed = urlsProcessed;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Set<String> getFrontier() {
		return frontier;
	}

	public Map<String, MercatorNode> getDomainNodeMap() {
		return domainNodeMap;
	}

	public MercatorNode getHead() {
		return head;
	}

	public void setHead(MercatorNode head) {
		this.head = head;
	}

	public MercatorNode getLast() {
		return last;
	}

	public void setLast(MercatorNode last) {
		this.last = last;
	}
	
	public static String getRobotsTxtUrl(String url) throws URISyntaxException {
		URI uri = new URI(url);
		return uri.getScheme() + "://"
				+ uri.getHost() + "/robots.txt";
	}

	public boolean isDomainPresentForUrl(String url) throws URISyntaxException {
		String domain = Parser.getDomainForUrl(url);
		logger.debug("Checking if url:" + url + " has mapped domain:" + domain
				+ "?" + domainNodeMap.containsKey(domain));
		if (domainNodeMap.containsKey(domain)) {
			return true;
		}
		return false;
	}

//	/**
//	 * Finds the appropriate domain queue for the given complete url and adds it
//	 * there if not already visited. Otherwise you have to create a new
//	 * MercatorNode with the domain for this. Should use findQueueForDomain
//	 * {@link #findQueueForDomain(String)}
//	 * 
//	 * @param url
//	 *            the absolute url of the website page to be crawled
//	 * @throws MalformedURLException
//	 * @throws URISyntaxException
//	 */
//	public void enqueueUrl(String url) throws NoDomainConfigException,
//			URISyntaxException {
//		String domain = Parser.getDomainForUrl(url);
//		URI parsedUrl = new URI(url);
//		if (isDomainPresentForUrl(url)) {
//			MercatorNode node = domainNodeMap.get(domain);
//			// check allow and disallow for enqueueing
//			logger.debug("Checking if url:" + url + " is allowed for node:"
//					+ node.getDomain() + " - "
//					+ node.isAllowed(parsedUrl.getPath()));
//			if (node.isAllowed(parsedUrl.getPath())) {
//				logger.debug("Adding " + url + " to mq:" + node);
//				size++;
//				node.enqueueUrl(url);
//			} else {
//				System.out.println("URL " + url + " disallowed for domain "
//						+ domain);
//			}
//			frontier.add(url);
//		} else {
//			throw new NoDomainConfigException(
//					"No domain config exists for this url! Create a config first.");
//		}
//	}

	public void addUrlToFrontier(String url) {
		synchronized (frontier) {
			frontier.add(url);
		}
	}

	public boolean isVisited(String url) {
		if (frontier.contains(url)) {
			return true;
		}
		return false;
	}

//	public void checkAndNotifyQueues() {
//		Calendar cal = Calendar.getInstance();
//		Date now = cal.getTime();
//
//		MercatorNode node = head;
//		logger.debug("Checking and notifying. Head is " + head);
//		while (node != null) {
//			// synchronized (node) {
//			logger.debug("Inside check sync block. last crawled "
//					+ node.getLastCrawledTime() + " q size:"
//					+ node.getUrls().getSize());
//			if (node.getLastCrawledTime() != null
//					&& node.getUrls().getSize() != 0) {
//				Date lastCrawled = node.getLastCrawledTime();
//				long diff = (now.getTime() - lastCrawled.getTime()) / 1000;
//				logger.debug("Check and notify checking node:" + node
//						+ ". Time diff b/w now and last crawl time:" + diff);
//				if (diff > node.getCrawlDelay()) {
//					// dequeue from node and add to outgoing job queue
//
//					String url = node.dequeueUrl();
//					size--;
//					logger.debug("Dequeued url:" + url + " from node:" + node);
//
//					// synchronized (outgoingJobQueue) {
//					// outgoingJobQueue.enqueue(url);
//					// outgoingJobQueue.notify();
//					// urlsProcessed++;
//					// }
//
//				}
//			}
//
//			// } // end of synchronization
//
//			node = node.getNext();
//		}
//	}

	public void addNode(MercatorNode node) {
		if (!domainNodeMap.containsKey(node.getDomain())) {
			domainNodeMap.put(node.getDomain(), node);
			if (head == null) {
				head = node;
				last = node;
				return;
			}
			// otherwise add at the end
			last.setNext(node);
			last = node;

			logger.debug("After adding node, queue now:" + toString());
			logger.info("After adding node, mercator queue size is " + size);
		} else {
			logger.info("Ignoring the addition of new node:" + node
					+ " as node with that domain already exists");
		}
	}

	public void removeNode(String domain) {
		if (head == null) {
			return;
		}
		MercatorNode node = head;
		if (head.getDomain().equals(domain)) {
			head = head.getNext();
			return;
		}
		while (node.getNext() != null) {
			if (node.getNext().getDomain().equals(domain)) {
				logger.info("Changing next node of " + node + " from:"
						+ node.getNext() + " to:" + node.getNext().getNext());
				node.setNext(node.getNext().getNext());
				return;
			}
			node = node.getNext();
		}
	}

	public void cleanUp() {
		Date now = Calendar.getInstance().getTime();
		if (head == null) {
			return;
		}
		MercatorNode node = head;
		if ((now.getTime() - node.getLastCrawledTime().getTime()) / 1000 > MAX_INACTIVE_DOMAIN_INTERVAL) {
			head = head.getNext();
		}
		while (node.getNext() != null) {
			if ((now.getTime() - node.getNext().getLastCrawledTime().getTime()) / 1000 > MAX_INACTIVE_DOMAIN_INTERVAL) {
				logger.info("Changing next node of " + node + " from:"
						+ node.getNext() + " to:" + node.getNext().getNext());
				node.setNext(node.getNext().getNext());
			}
			node = node.getNext();
		}
	}

	// --------------------- SHOULD BE AT END -----------------------

	public String toString() {
		StringBuilder sb = new StringBuilder();
		MercatorNode node = head;
		int depth = 0;
		while (node != null) {
			sb.append(depth++ + " " + node.toString() + "\n");
			node = node.getNext();
		}

		return sb.toString();
	}

	public MercatorQueue() {
		frontier = Collections.synchronizedSet(new HashSet<String>());
		domainNodeMap = new ConcurrentHashMap<String, MercatorNode>();
		size = 0;
		urlsProcessed = 0;
	}
}
