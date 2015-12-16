package pagerank.api;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pagerank.cache.DomainRankCache;
import pagerank.cache.PageRankCache;
import pagerank.db.dao.DRDao;
import pagerank.db.dao.PRDao;
import pagerank.db.ddl.PRCreateTable;
import utils.string.StringUtils;
import db.wrappers.DynamoDBWrapper;

public final class PageRankAPI {

	private final DynamoDBWrapper dynamoWrapper;

	private final DomainRankCache drCache;
	private final PageRankCache prCache;

	public PageRankAPI() {
		this.dynamoWrapper = DynamoDBWrapper.getInstance(
				DynamoDBWrapper.US_EAST, DynamoDBWrapper.CLIENT_PROFILE);
		this.drCache = DomainRankCache.getInstance();
		
		// TODO Rethink this
		this.prCache = null;//PageRankCache.getInstance();
		
	}

	/**
	 * Get PageRank score for specified page
	 * 
	 * @param String
	 *            page
	 * @return double pageRank
	 */
	public double getPageRank(String page) throws Exception {
		if (page == null || page.isEmpty()) {
			throw new Exception("Invalid page. Can't find pagerank score");
		}
		String pageNormalized = StringUtils.normalizeUrlToString(page.trim());
		PRDao pageRank = (PRDao) dynamoWrapper.getItem(pageNormalized, PRDao.class);
		if (pageRank == null) {
			return 0.0;
		}

		return pageRank.getPageScore();
	}

	/**
	 * Batch Get for PageRank scores of multiple pages
	 * 
	 * @param List
	 *            <String> pages
	 * @return Map <String, Double> pageRanks
	 */
	public Map<String, Double> getPageRankBatch(List<String> pages)
			throws IllegalArgumentException {
		if (pages == null || pages.isEmpty()) {
			throw new IllegalArgumentException(
					"Invalid page. Can't find pagerank score");
		}

		List<Object> items = new ArrayList<>();
		for (String page : pages) {
			PRDao dao = new PRDao();
			dao.setPage(StringUtils.normalizeUrlToString(page.trim()));
			dao.setPageScore(0.0);
			items.add(dao);
		}
		List<Object> pageRankItems = new ArrayList<>();
		try {
			pageRankItems = dynamoWrapper.getBatchItem(items).get(
					PRCreateTable.PR_TABLE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
			return new HashMap<String, Double>();
		}
		Map<String, Double> pageRanks = new HashMap<>();
		for (Object pageObj : pageRankItems) {
			PRDao pageDAO = (PRDao) pageObj;
			if (pageDAO == null || pageDAO.getPageScore() < 0)
				continue;

			pageRanks.put(pageDAO.getPage(), pageDAO.getPageScore());
		}
		return pageRanks;
	}
	
	/**
	 * Get cahched PageRank score for specified page
	 * using a in-memory cache
	 * @param String  page
	 * @return double pageRank
	 */
	public double getPageRankCached(String page) throws Exception {
		if (page == null || page.isEmpty()) {
			throw new Exception("Invalid page. Can't find pagerank score");
		}
		
		String pageNormalized = StringUtils.normalizeUrlToString(page.trim());
		return prCache.getPageRank(pageNormalized);
	}

	/**
	 * Get DomainRank score for specified page
	 * 
	 * @param String page
	 * @return double domainRank
	 * @throws MalformedURLException 
	 */
	public double getDomainRank(String page) throws IllegalArgumentException {
		if (page == null || page.isEmpty()) {
			throw new IllegalArgumentException("Invalid page. Can't find domainrank score");
		}

		String domain = page;
		try {
			domain = StringUtils.getDomainFromUrl(page.trim());
		} catch (Exception e) {
			// Do Nothing - Try to find for page as is.
		}

		DRDao domainRank = (DRDao) dynamoWrapper.getItem(domain, DRDao.class);
		if (domainRank == null) {
			return 0.0;
		}

		return domainRank.getDomainScore();
	}
	
	/**
	 * Get cached DomainRank score for specified page
	 * using a in-memory cache
	 * 
	 * @param String page
	 * @return double domainRank
	 * @throws MalformedURLException 
	 */
	public double getDomainRankCached(String page) throws IllegalArgumentException {
		if (page == null || page.isEmpty()) {
			throw new IllegalArgumentException("Invalid page. Can't find domainrank score");
		}

		String domain = page.trim();
		try {
			domain = StringUtils.getDomainFromUrl(page);
		} catch (Exception e) {
			// Do Nothing - Try to find for page as is.
		}
		
		return drCache.getDomainRank(domain);
	}

	/**
	 * Batch Get for DomainRank scores of multiple domains
	 * 
	 * @param List <String> pages
	 * @return Map <String, Double> domainRanks
	 * @throws MalformedURLException
	 */
	public Map<String, Double> getDomainRankBatch(List<String> pages)
			throws IllegalArgumentException, MalformedURLException {
		if (pages == null || pages.isEmpty()) {
			throw new IllegalArgumentException(
					"Invalid domain. Can't find domainrank score");
		}

		List<Object> items = new ArrayList<>();
		Map<String, String> pagesToDomain = new HashMap<>();
		Set<String> isSeen = new HashSet<>();
		for (String page : pages) {
			String domain = StringUtils.getDomainFromUrl(page.trim()); 
			pagesToDomain.put(page, domain);
			if (isSeen.contains(domain)) {
				continue;
			}
			isSeen.add(domain);
			
			DRDao dao = new DRDao();			
			dao.setDomain(domain);
			items.add(dao);
		}

		System.out.println(dynamoWrapper.getBatchItem(items));
		List<Object> domainRankItems = dynamoWrapper.getBatchItem(items).get(
				PRCreateTable.DR_TABLE_NAME);
		Map<String, Double> ranks = new HashMap<>();
		for (Object domainObj : domainRankItems) {
			DRDao domainDAO = (DRDao) domainObj;
			if (domainDAO == null || domainDAO.getDomainScore() < 0)
				continue;

			ranks.put(domainDAO.getDomain(), domainDAO.getDomainScore());
		}
		
		Map<String, Double> domainRanks = new HashMap<>();
		for (String page : pagesToDomain.keySet()) {
			String domain = pagesToDomain.get(page);
			if (ranks.containsKey(domain)) {
				domainRanks.put(page, ranks.get(domain));
			}
			else {
				domainRanks.put(page, 0.0);
			}
		}	
		
		return domainRanks;
	}

}
