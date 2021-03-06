package test.utils.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import searchengine.query.QueryWord;
import utils.nlp.QueryProcessor;

public class TestQueryProcessor extends TestCase {

	@Test
	public void testRemoveStopWords() {
		List<String> query = Arrays.asList("is running".split(" "));
		QueryProcessor p = QueryProcessor.getInstance();
		List<String> result = p.removeStopWords(query, 50);
		assertEquals(1, result.size());
		assertEquals("running", result.get(0));

		result = p.removeStopWords(query, 49);
		assertEquals(2, result.size());
		assertEquals("running", result.get(1));
	}

	@Test
	public void testRankWords() {
		String query = "just some random crap";
		Date start = Calendar.getInstance().getTime();
		QueryProcessor q = QueryProcessor.getInstance();
		Date end = Calendar.getInstance().getTime();
		System.out.println("Loaded in " + (end.getTime() - start.getTime()));
		start = Calendar.getInstance().getTime();
		Map<String, Integer> rankMap = q.rankWords(query);
		end = Calendar.getInstance().getTime();
		System.out
				.println(rankMap + " in " + (end.getTime() - start.getTime()));
	}
	
	@Test
	public void testGenerateNGrams() {
		List<String> query = Arrays.asList("this is a query".split(" "));
		int nGrams = 3;
		QueryProcessor q = QueryProcessor.getInstance();
		Map<Integer, List<String>> result = q.generateNGrams(query, nGrams);
		List<String> unigrams = result.get(1);
		assertTrue(unigrams.contains("this"));
		assertEquals(4, unigrams.size());
		
		List<String> bigrams = result.get(2);
		assertTrue(bigrams.contains("this is"));
		assertEquals(3, bigrams.size());
		
		List<String> trigrams = result.get(3);
		assertTrue(trigrams.contains("this is a"));
		assertEquals(2, trigrams.size());
	}
	
	@Test
	public void testGetProcessedQuery() {
		List<String> query = Arrays.asList("this is a query".split(" "));
		int nGrams = 3;
		QueryProcessor q = QueryProcessor.getInstance();
		List<QueryWord> result = q.getProcessedQuery(query, 2);
		assertEquals(7, result.size());
		List<String> wordResults = new ArrayList<String>();
		for(QueryWord qWord : result) {
			wordResults.add(qWord.getWord());
		}
		assertTrue(wordResults.contains("this"));
		assertTrue(wordResults.contains("is"));
		assertTrue(wordResults.contains("a"));
		assertTrue(wordResults.contains("query"));
		assertTrue(wordResults.contains("this is"));
		assertTrue(wordResults.contains("is a"));
		assertTrue(wordResults.contains("a query"));
//		System.out.println(result);
	}
	
	@Test
	public void testGetProcessedQuery2() {
		List<String> query = Arrays.asList("top university rankings".split(" "));
		int nGrams = 3;
		QueryProcessor q = QueryProcessor.getInstance();
		List<QueryWord> result = q.getProcessedQuery(query, 2);
//		assertEquals(11, result.size());
		List<String> wordResults = new ArrayList<String>();
		for(QueryWord qWord : result) {
			wordResults.add(qWord.getWord());
		}
		System.out.println(result);
	}
}
