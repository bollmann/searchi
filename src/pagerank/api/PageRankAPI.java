package pagerank.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pagerank.db.dao.PRDao;
import pagerank.db.ddl.PRCreateTable;

import db.wrappers.DynamoDBWrapper;

public final class PageRankAPI {
	
	private DynamoDBWrapper dynamoWrapper;
	
	public PageRankAPI() {
		this.dynamoWrapper = DynamoDBWrapper.getInstance(
			DynamoDBWrapper.US_EAST,
			DynamoDBWrapper.CLIENT_PROFILE);
	}
	
	/**
	 * Get PageRank score for specified page
	 * @param String page
	 * @return double pageRank
	 */
	public double getPageRank(String page) throws Exception {
		if (page == null || page.isEmpty()) {
			throw new Exception("Invalid page. Can't find pagerank score");
		}
		
		PRDao pageRank = (PRDao) dynamoWrapper.getItem(page, PRDao.class); 
		if (pageRank == null) {
			throw new Exception("Pagerank not found for specified page");
		}
		
		return pageRank.getPageScore(); 		
	}
	
	/**
	 * Batch Get for PageRank scores of multiple pages
	 * @param List<String> pages
	 * @return Map <String, Double> pageRanks
	 */
	public Map <String, Double> getPageRankBatch(List<String> pages) throws IllegalArgumentException {
		if (pages == null || pages.isEmpty()) {
			throw new IllegalArgumentException("Invalid page. Can't find pagerank score");
		}
		
		List<Object> items = new ArrayList<>();
		for (String page : pages) {
			PRDao dao = new PRDao();
			dao.setPage(page);
			dao.setPageScore(-1.0);
			items.add(dao);
		}
		
		List<Object> pageRankItems = dynamoWrapper.getBatchItem(items).get(PRCreateTable.PR_TABLE_NAME);
		Map<String, Double> pageRanks = new HashMap<>();
		for (Object pageObj: pageRankItems) {
			PRDao pageDAO = (PRDao) pageObj;
			if (pageDAO == null || pageDAO.getPageScore() < 0) continue;
			
			pageRanks.put(pageDAO.getPage(), pageDAO.getPageScore());
		}
		return pageRanks;
	}
	
	
}
