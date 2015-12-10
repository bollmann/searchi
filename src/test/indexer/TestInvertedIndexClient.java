package test.indexer;

import indexer.clients.InvertedIndexClient;
import indexer.db.dao.DocumentFeatures;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

public class TestInvertedIndexClient extends TestCase {
	
	@Test
	public void testInvertedIndexForQuery() {
		InvertedIndexClient iic = InvertedIndexClient.getInstance();
		List<String> query = Arrays.asList("some available query on barrack obama".split(" "));
		Date start, end;
		start = Calendar.getInstance().getTime();
		Map<String, List<DocumentFeatures>> results = iic.getInvertedIndexForQueryMultiThreaded(query);
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
	
	
}
