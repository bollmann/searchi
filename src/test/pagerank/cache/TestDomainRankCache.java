package test.pagerank.cache;

import java.net.MalformedURLException;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import pagerank.api.PageRankAPI;
import pagerank.cache.DomainRankCache;

public class TestDomainRankCache extends TestCase {

	@Test
	public void testGetInstance() {
		DomainRankCache cache = DomainRankCache.getInstance();
		assertNotNull(cache);	
	}

	@Test
	public void testLoadFromDB() throws IllegalArgumentException, MalformedURLException {
		DomainRankCache cache = DomainRankCache.getInstance();
		assertNotNull(cache);
		
		long startTime = System.currentTimeMillis();
		cache.loadFromDB("DomainRank");
		long endTime = System.currentTimeMillis();
		
		System.out.println("Total time for load - " + Long.toString(endTime - startTime));
		
		Map<String, Double> ranks = cache.getDomainRanks();		
		assertTrue(ranks.containsKey("intocareers.org"));
		System.out.println(ranks.keySet().size());
		
		/** ------ Comparing Cache fetch vs DB fetch -------*/
		
		startTime = System.currentTimeMillis();
		double rank1 = cache.getDomainRank("intocareers.org");
		endTime = System.currentTimeMillis();		
		System.out.println("Fetching val - " + rank1 + " took " + Double.toString(endTime - startTime));
		
		PageRankAPI prAPI = new PageRankAPI();
		
		startTime = System.currentTimeMillis();
		double rank2 = prAPI.getDomainRank("http://intocareers.org");
		endTime = System.currentTimeMillis();
		System.out.println("Fetching val - " + rank2 + " took " + Double.toString(endTime - startTime));
		
	}

}
