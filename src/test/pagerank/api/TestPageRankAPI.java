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
		Assert.assertEquals(0.42, prAPI.getPageRank("http://yahoo.com"));
		Assert.assertEquals(0.234, prAPI.getPageRank("http://abc.xyz"));
	}
	
	@Test(expected=Exception.class)
	public void testGetPageRankWithNonExistingPage() throws Exception {
		PageRankAPI prAPI = new PageRankAPI();
		Assert.assertEquals(0.42, prAPI.getPageRank("http://cnbc.com"));
	}

	@Test
	public void testGetPageRankBatcWithExistingPages() throws Exception {
		
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

}
