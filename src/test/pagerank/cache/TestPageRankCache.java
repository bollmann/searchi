package test.pagerank.cache;

import java.util.Map;

import org.junit.Test;

import pagerank.cache.PageRankCache;
import junit.framework.TestCase;

public class TestPageRankCache extends TestCase {

	@Test
	public void testPageRankCacheInstance() {
		PageRankCache cache = PageRankCache.getInstance();
		assertNotNull(cache);		
	}
	
	@Test
	public void testLoadFromDB() {
		PageRankCache cache = PageRankCache.getInstance();
		assertNotNull(cache);
		
		long startTime = System.currentTimeMillis();
		cache.loadFromDB("PageRank");
		long endTime = System.currentTimeMillis();
		
		System.out.println("Total time taken - " + Long.toString(endTime - startTime));
		Map<String, Double> ranks = cache.getPageRanks();		
		assertTrue(ranks.containsKey("https://twitter.com/webmonkey"));
		System.out.println(ranks.keySet().size());
	}
	
	
}
