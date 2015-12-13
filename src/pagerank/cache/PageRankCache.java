package pagerank.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import db.wrappers.DynamoDBWrapper;
import db.wrappers.ScanSegmentTask;

public final class PageRankCache {
	private static final Logger logger = Logger.getLogger(PageRankCache.class);
	
	private static PageRankCache prCache;
	private final DynamoDBWrapper dynamoWrapper;
	
	private final String KEY_NAME = "Page";
	private final String VALUE_NAME = "PageScore";
	private final int numSegments;
	private Map<String, Double> pageRankMap;
	
	
	private PageRankCache() {
		this.dynamoWrapper = DynamoDBWrapper.getInstance(
			DynamoDBWrapper.US_EAST, DynamoDBWrapper.CLIENT_PROFILE);
		this.numSegments = 4;
		this.pageRankMap = new HashMap<>();
	}
	
	public static PageRankCache getInstance() {
		if (prCache == null) {
			prCache = new PageRankCache();
		}
		return prCache;
	}
	
	public void loadFromDB(String tableName) {
		List<ScanSegmentTask> tasks = 
			dynamoWrapper.scanParallel(tableName, numSegments);
		
		for (ScanSegmentTask task : tasks) {
			if (task.getItems() == null || task.getItems().isEmpty())
				continue;
			
			for (Map<String, AttributeValue> item : task.getItems()) {
				if (!item.containsKey(KEY_NAME) || !item.containsKey(VALUE_NAME))
					continue;
				
				String page = item.get(KEY_NAME).getS();
				String pageRank = item.get(VALUE_NAME).getN();
				
				pageRankMap.put(page, Double.parseDouble(pageRank));				
			}
			logger.info("Num of keys from the segment - " + pageRankMap.keySet().size());
		}
	}
	
	public Map<String, Double> getPageRanks() {
		return pageRankMap;
	}
	
	public double getPageRank(String page) {
		if (pageRankMap.containsKey(page)) 
			return pageRankMap.get(page);
		
		return 0.0;
	}
	
}
