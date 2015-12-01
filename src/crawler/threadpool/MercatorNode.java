package crawler.threadpool;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import crawler.errors.QueueFullException;
import db.dbo.DomainInfo;

public class MercatorNode {
	private final Logger logger = Logger.getLogger(getClass());
	private MercatorNode next = null;
	
	DomainInfo domainInfo;
	
	public void setDomainInfo(DomainInfo domainInfo) {
		this.domainInfo = domainInfo;
	}
	
	public DomainInfo getDomainInfo() {
		return domainInfo;
	}

	public Date getLastCrawledTime() {
		return domainInfo.getLastCrawledTime();
	}

	public void setLastCrawledTime(Date lastCrawledTime) {
		domainInfo.setLastCrawledTime(lastCrawledTime);
	}

	public boolean isQueriable() {
		Date now = Calendar.getInstance().getTime();
		Date lastCrawledTime = domainInfo.getLastCrawledTime();
		float crawlDelay = domainInfo.getCrawlDelay();
		logger.debug("Comparing now:" + now.getTime() + " and last crawled:" + lastCrawledTime.getTime()
				+ " with diff:" + (now.getTime() - lastCrawledTime.getTime())/1000);
		if((now.getTime() - lastCrawledTime.getTime())/1000 > crawlDelay) {
			return true;
		} else {
			return false;
		}
	}

	public void setQueriable(boolean isQueriable) {
		domainInfo.setQueriable(isQueriable);
	}

	public String getUserAgent() {
		return domainInfo.getUserAgent();
	}

	public void setUserAgent(String userAgent) {
		domainInfo.setUserAgent(userAgent);
	}

	public MercatorNode getNext() {
		return next;
	}

	public void setNext(MercatorNode next) {
		this.next = next;
	}

	public String getDomain() {
		return domainInfo.getDomain();
	}

	public void setDomain(String domain) {
		domainInfo.setDomain(domain);
	}

	public float getCrawlDelay() {
		return domainInfo.getCrawlDelay();
	}

	public void setCrawlDelay(float crawlDelay) {
		domainInfo.setCrawlDelay(crawlDelay);
	}

	public void addDisallowPath(String path) {
		domainInfo.getPathAccessMap().put(path, false);
	}

	public void addAllowPath(String path) {
		if (domainInfo.getPathAccessMap().containsKey(path)) {
			if (domainInfo.getPathAccessMap().get(path)) // if path access is disallow, then
											// cannot change
				domainInfo.getPathAccessMap().put(path, true);
		} else {
			domainInfo.getPathAccessMap().put(path, true);
		}
	}

	/**
	 * Checks if the given relative path is allowed for the given domain
	 * 
	 * @param path
	 * @return
	 */
	public boolean isAllowed(String path) {
		if (domainInfo.getPathAccessMap().containsKey(path)) { // if exact match then do
												// whatever policy says
			logger.debug("MercatorNode policy found exact match for:" + path
					+ ":" + domainInfo.getPathAccessMap().get(path));
			return domainInfo.getPathAccessMap().get(path);
		} else {
			logger.debug("Going through pathAccessMap" + domainInfo.getPathAccessMap()
					+ " to check for " + path);
			for (Entry<String, Boolean> entry : domainInfo.getPathAccessMap().entrySet()) {
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
		domainInfo = new DomainInfo();
		domainInfo.setDomain(domain);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MercatorNode: " + domainInfo.toString());
		return sb.toString();
	}
}
