package pagerank.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import pagerank.db.ddl.PRCreateTable;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import db.wrappers.DynamoDBWrapper;
import db.wrappers.ScanSegmentTask;

public final class DomainRankCache {
	
	private static final Logger logger = Logger.getLogger(DomainRankCache.class);
	
	private static DomainRankCache drCache;
	private final DynamoDBWrapper dynamoWrapper;
	
	private final String KEY_NAME = "Domain";
	private final String VALUE_NAME = "DomainScore";
	private final int numSegments;
	private Map<String, Double> domainRankMap;
	
	private DomainRankCache() {
		this.dynamoWrapper = DynamoDBWrapper.getInstance(
			DynamoDBWrapper.US_EAST, DynamoDBWrapper.CLIENT_PROFILE);
		this.numSegments = 4;
		this.domainRankMap = new HashMap<>();
	}
	
	public static DomainRankCache getInstance() {
		if (drCache == null) {
			drCache = new DomainRankCache();
			drCache.loadFromDB(PRCreateTable.DR_TABLE_NAME);
		}
		return drCache;
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
				
				String domain = item.get(KEY_NAME).getS();
				String domainRank = item.get(VALUE_NAME).getN();
				
				domainRankMap.put(domain, Double.parseDouble(domainRank));
			}
		}
		logger.info("Cache Loaded. Normalizing the values");
		double max= 1000.0;
		for (String dom : domainRankMap.keySet()) {			
			domainRankMap.put(dom, domainRankMap.get(dom)/max);
		}
	}
	
	public Map<String, Double> getDomainRanks() {
		return domainRankMap;
	}
	
	public double getDomainRank(String domain) {
		if (domainRankMap.containsKey(domain)) 
			return domainRankMap.get(domain);
		
		return 0.0;
	}

}
