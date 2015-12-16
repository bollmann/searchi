package test.utils.searchengine;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import searchengine.servlets.SearchResult;
import utils.searchengine.SearchEngineUtils;

public class TestSearchEngineUtils extends TestCase {

	@Test
	public void testDiversifyResults() {
		List<SearchResult> srList = new ArrayList<>();
		// same domain
		SearchResult sr1 = new SearchResult();
		sr1.setUrl("http://www.google.com/a?q=a");
		SearchResult sr2 = new SearchResult();
		sr2.setUrl("http://www.google.com/a?q=b");
		SearchResult sr3 = new SearchResult();
		sr3.setUrl("http://www.google.com/a?q=c");
		SearchResult sr4 = new SearchResult();
		sr4.setUrl("http://www.google.com/b?q=a");
		SearchResult sr5 = new SearchResult();
		sr5.setUrl("http://www.google.com/b?q=a");
		SearchResult sr6 = new SearchResult();
		sr6.setUrl("http://www.google.com/c?q=a");
		srList.add(sr1);
		srList.add(sr2);
		srList.add(sr3);
		srList.add(sr4);
		srList.add(sr5);
		srList.add(sr6);
		
		List<SearchResult> resultList = SearchEngineUtils.diversifyResults(srList, 3);
		assertEquals(3, resultList.size());
		assertEquals(sr1.getUrl(), resultList.get(0).getUrl());
		assertEquals(sr4.getUrl(), resultList.get(1).getUrl());
		assertEquals(sr6.getUrl(), resultList.get(2).getUrl());
		// different domain
		SearchResult sr7 = new SearchResult();
		sr7.setUrl("http://www.yahoo.com/a?q=a");
		srList.add(sr7);
		
		resultList = SearchEngineUtils.diversifyResults(srList, 3);
		assertEquals(4, resultList.size());
		assertEquals(sr1.getUrl(), resultList.get(0).getUrl());
		assertEquals(sr4.getUrl(), resultList.get(1).getUrl());
		assertEquals(sr6.getUrl(), resultList.get(2).getUrl());
		assertEquals(sr7.getUrl(), resultList.get(3).getUrl());
	}
}
