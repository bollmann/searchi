package crawler.threadpool;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import crawler.errors.QueueFullException;

public class MercatorNode {
	private final Logger logger = Logger.getLogger(getClass());
	private MercatorNode next = null;
	private String domain;
	private Date lastCrawledTime;
	private float crawlDelay = 0.5f; // crawl delay default 2 secs
	private String userAgent;
	private Map<String, Boolean> pathAccessMap;
	private Queue<String> urls;
	private boolean isQueriable = false;

	public Date getLastCrawledTime() {
		return lastCrawledTime;
	}

	public void setLastCrawledTime(Date lastCrawledTime) {
		this.lastCrawledTime = lastCrawledTime;
	}

	public boolean isQueriable() {
		Date now = Calendar.getInstance().getTime();
		logger.debug("Comparing now:" + now.getTime() + " and last crawled:" + lastCrawledTime.getTime()
				+ " with diff:" + (now.getTime() - lastCrawledTime.getTime())/1000);
		if((now.getTime() - lastCrawledTime.getTime())/1000 > crawlDelay) {
			return true;
		} else {
			return false;
		}
	}

	public void setQueriable(boolean isQueriable) {
		this.isQueriable = isQueriable;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public MercatorNode getNext() {
		return next;
	}

	public void setNext(MercatorNode next) {
		this.next = next;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public float getCrawlDelay() {
		return crawlDelay;
	}

	public void setCrawlDelay(float crawlDelay) {
		this.crawlDelay = crawlDelay;
	}

	public Queue<String> getUrls() {
		return urls;
	}

	public synchronized void enqueueUrl(String url) {
		try {
			urls.enqueue(url);
		} catch (QueueFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String dequeueUrl() throws IndexOutOfBoundsException {
		return urls.dequeue();
	}

	public void addDisallowPath(String path) {
		pathAccessMap.put(path, false);
	}

	public void addAllowPath(String path) {
		if (pathAccessMap.containsKey(path)) {
			if (pathAccessMap.get(path)) // if path access is disallow, then
											// cannot change
				pathAccessMap.put(path, true);
		} else {
			pathAccessMap.put(path, true);
		}
	}

	/**
	 * Checks if the given relative path is allowed for the given domain
	 * 
	 * @param path
	 * @return
	 */
	public boolean isAllowed(String path) {
		if (pathAccessMap.containsKey(path)) { // if exact match then do
												// whatever policy says
			logger.debug("MercatorNode policy found exact match for:" + path
					+ ":" + pathAccessMap.get(path));
			return pathAccessMap.get(path);
		} else {
			logger.debug("Going through pathAccessMap" + pathAccessMap
					+ " to check for " + path);
			for (Entry<String, Boolean> entry : pathAccessMap.entrySet()) {
				String key = entry.getKey();

				logger.debug("Checking with policy:" + key + "="
						+ entry.getValue() + " for " + path);
				if (!path.endsWith("/")) {
					path = path + "/";
				}
				// has to be prefix match
				if (!entry.getValue() && path.startsWith(key)) {
					return false; // if file path has a disallow prefix, then
									// can't access.
					// would have gotten an exact match policy with allow by now
					// otherwise
				}

			}
			logger.debug("Returning default of true");
			return true; // allow by default
		}
	}

	public MercatorNode(String domain) {
		this.domain = domain;
		urls = new Queue<String>();
		pathAccessMap = new HashMap<String, Boolean>();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MercatorNode: domain=").append(domain)
				.append(" crawlDelay=").append(crawlDelay)
				.append(" pathAccessInfo=").append(pathAccessMap)
//				.append(" in queue:" + urls.toString());
				.append(" in queue of size:" + urls.getSize());

		return sb.toString();
	}
}
