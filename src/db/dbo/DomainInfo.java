package db.dbo;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import crawler.threadpool.Queue;

@DynamoDBTable(tableName = "DomainInfo")
public class DomainInfo {
	
	private String domain;
	private Date lastCrawledTime;
	private float crawlDelay = 0.5f; // crawl delay default 2 secs
	private String userAgent;
	private Map<String, Boolean> pathAccessMap;
	private Queue<String> urls;
	private boolean isQueriable = false;
	
	public DomainInfo() {
		pathAccessMap = new HashMap<String, Boolean>();
		lastCrawledTime = Calendar.getInstance().getTime();
	}
	
	@DynamoDBHashKey(attributeName = "domain")
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	@DynamoDBAttribute(attributeName = "lastCrawledTime")
	public Date getLastCrawledTime() {
		return lastCrawledTime;
	}
	public void setLastCrawledTime(Date lastCrawledTime) {
		this.lastCrawledTime = lastCrawledTime;
	}
	
	@DynamoDBAttribute(attributeName = "crawlDelay")
	public float getCrawlDelay() {
		return crawlDelay;
	}
	public void setCrawlDelay(float crawlDelay) {
		this.crawlDelay = crawlDelay;
	}
	
	@DynamoDBAttribute(attributeName = "userAgent")
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	@DynamoDBAttribute(attributeName = "pathAccessMap")
	public Map<String, Boolean> getPathAccessMap() {
		return pathAccessMap;
	}
	public void setPathAccessMap(Map<String, Boolean> pathAccessMap) {
		this.pathAccessMap = pathAccessMap;
	}
	
	@DynamoDBAttribute(attributeName = "isQueriable")
	public boolean isQueriable() {
		return isQueriable;
	}
	public void setQueriable(boolean isQueriable) {
		this.isQueriable = isQueriable;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Domain Info:: ")
		.append("domain=").append(domain)
				.append(" crawlDelay=").append(crawlDelay)
				.append(" pathAccessInfo=").append(pathAccessMap);
		return sb.toString();
	}

}
