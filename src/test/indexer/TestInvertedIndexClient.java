package test.indexer;

import indexer.clients.InvertedIndexClient;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import searchengine.query.QueryWord;

public class TestInvertedIndexClient extends TestCase {
	
	@Test
	public void testInvertedIndexForQuery() {
		InvertedIndexClient iic = InvertedIndexClient.getInstance();
		List<String> words = Arrays.asList("some available query on barrack obama".split(" "));
		List<QueryWord> query = new ArrayList<>();
		
		for (String word : words) {
			query.add(new QueryWord(word));
		}
		
		Date start, end;
		start = Calendar.getInstance().getTime();
		Map<QueryWord, List<DocumentFeatures>> results = iic.getInvertedIndexForQueryMultiThreaded(query);
		end = Calendar.getInstance().getTime();
		System.out.println("Time taken for single threaded: " + (end.getTime() - start.getTime()));
//		System.out.println(results.size());
		assertEquals(6, results.size());
		assertFalse(results.get("some").size() == 0);
		assertFalse(results.get("available").size() == 0);
		assertFalse(results.get("query").size() == 0);
		
		start = Calendar.getInstance().getTime();
		results = iic.getInvertedIndexForQueryMultiThreaded(query);
		end = Calendar.getInstance().getTime();
		System.out.println("Time taken for multi threaded: " + (end.getTime() - start.getTime()));
//		System.out.println(results.size());
		assertEquals(3, results.size());
		assertFalse(results.get("some").size() == 0);
		assertFalse(results.get("available").size() == 0);
		assertFalse(results.get("query").size() == 0);
	}
	
	@Test
	public void testImageIndexForQuery() {
		InvertedIndexClient iic = InvertedIndexClient.getInstance();
		
		List<QueryWord> query = new ArrayList<>();
		query.add(new QueryWord("cats"));
		query.add(new QueryWord("and"));
		query.add(new QueryWord("dogs"));
		
		Map<QueryWord, List<String>> imageResults = iic.getImageIndexForQueryMultiThreaded(query);
		
		for(QueryWord word: imageResults.keySet()) {
			List<String> images = imageResults.get(word);
			System.out.println("got " + images.size() + " images for query word '" + word.getWord() + "': ");
			System.out.println(images);
		}
		assertTrue(true);
	}
}
