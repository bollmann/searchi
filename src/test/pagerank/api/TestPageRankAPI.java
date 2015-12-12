package test.pagerank.api;

import java.util.Arrays;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import pagerank.api.PageRankAPI;

@RunWith(JUnit4.class)
public final class TestPageRankAPI {
	
	@Test
	public void testGetPageRankWithExistingPage() throws Exception {
		PageRankAPI prAPI = new PageRankAPI();
		Assert.assertEquals(0.42, prAPI.getPageRank("yahoo.com"));
		Assert.assertEquals(0.234, prAPI.getPageRank("google.com"));
	}
	
	@Test
	public void testGetPageRankWithNonExistingPage() throws Exception {
		PageRankAPI prAPI = new PageRankAPI();
		Assert.assertEquals(0.0, prAPI.getPageRank("http://cnbc.com"));
	}

	@Test
	public void testGetPageRankBatchWithExistingPages() throws Exception {
		
		PageRankAPI prAPI = new PageRankAPI();
		Map<String, Double> pageRanks = prAPI.getPageRankBatch(Arrays.asList(
			"http://yahoo.com",
			"https://www.google.com",
			"http://abc.xyz"));
		
		Assert.assertTrue(pageRanks.containsKey("http://yahoo.com"));
		Assert.assertTrue(pageRanks.containsKey("https://www.google.com"));
		Assert.assertTrue(pageRanks.containsKey("http://abc.xyz"));
		Assert.assertEquals(0.42, pageRanks.get("http://yahoo.com"));
		Assert.assertEquals(0.687, pageRanks.get("https://www.google.com"));
		Assert.assertEquals(0.234, pageRanks.get("http://abc.xyz"));
	}
	
	@Test
	public void testGetPageRankBatcWithNonExistingPages() throws Exception {
		
		PageRankAPI prAPI = new PageRankAPI();
		Map<String, Double> pageRanks = prAPI.getPageRankBatch(Arrays.asList(
			"http://yahoo.com",
			"https://www.google.org",
			"http://xyz.abc"));
		
		
		Assert.assertTrue(pageRanks.containsKey("http://yahoo.com"));
		Assert.assertFalse(pageRanks.containsKey("https://www.google.org"));
		Assert.assertFalse(pageRanks.containsKey("http://xyz.abc"));
		Assert.assertEquals(0.42, pageRanks.get("http://yahoo.com"));	
	}
	
	
	@Test
	public void testGetDomainRankWithExistingDomain() throws Exception {
		PageRankAPI prAPI = new PageRankAPI();
		
		Assert.assertEquals(0, Double.compare(15.590904937838324, prAPI.getDomainRank("http://www.yahoo.com")));
		Assert.assertEquals(0, Double.compare(2937.146823963398, prAPI.getDomainRank("http://www.google.com")));
	}
	
	@Test
	public void testGetDomainRankWithNonExistingDomain() throws Exception {
		PageRankAPI prAPI = new PageRankAPI();
		Assert.assertEquals(0.0, prAPI.getDomainRank("http://asdh.com"));
	}
	
	@Test
	public void testGetDomainRankBatchWithExistingPages() throws Exception {
		
		PageRankAPI prAPI = new PageRankAPI();
		Map<String, Double> pageRanks = prAPI.getDomainRankBatch(Arrays.asList(
			"http://yahoo.com",
			"https://www.google.com"));
		
		Assert.assertTrue(pageRanks.containsKey("http://yahoo.com"));
		Assert.assertTrue(pageRanks.containsKey("https://www.google.com"));
		Assert.assertEquals(15.590904937838324, pageRanks.get("http://yahoo.com"));
		Assert.assertEquals(2937.146823963398, pageRanks.get("https://www.google.com"));
	}
	
	@Test
	public void testGetDomainRankBatcWithNonExistingPages() throws Exception {
		
		PageRankAPI prAPI = new PageRankAPI();
		Map<String, Double> pageRanks = prAPI.getDomainRankBatch(Arrays.asList(
			"http://aasfd.com/aasd/asd",
			"https://www.google.com/das",
			"http://xyz.abc"));
		
		
		Assert.assertTrue(pageRanks.containsKey("http://aasfd.com/aasd/asd"));
		Assert.assertTrue(pageRanks.containsKey("https://www.google.com/das"));
		Assert.assertTrue(pageRanks.containsKey("http://xyz.abc"));
		Assert.assertEquals(0.0, pageRanks.get("http://aasfd.com/aasd/asd"));
		Assert.assertEquals(2937.146823963398, pageRanks.get("https://www.google.com/das"));
		Assert.assertEquals(0.0, pageRanks.get("http://xyz.abc"));
	}	
	

}
