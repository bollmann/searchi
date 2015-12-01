package test.indexer;

import indexer.WordCounts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

public class TestWordCounts extends TestCase {

	@Test
	public void testCounts() {
		List<String> input = Arrays.asList("a", "b", "c", "a", "b", "a", "c",
				"d", "d", "d", "a");
		Map<String, Integer> expected = new HashMap<String, Integer>();
		expected.put("a", 4);
		expected.put("b", 2);
		expected.put("c", 2);
		expected.put("d", 3);

		WordCounts actual = new WordCounts(input);

		assertEquals(expected, actual.getCounts());
		assertEquals("a", actual.getMaxWord());

		double alpha = 0.5;
		Map<String, Double> maxFreqs = new HashMap<String, Double>();
		maxFreqs.put("a", alpha + (1 - alpha) * 1);
		maxFreqs.put("b", alpha + (1 - alpha) * 2 / 4);
		maxFreqs.put("c", alpha + (1 - alpha) * 2 / 4);
		maxFreqs.put("d", alpha + (1 - alpha) * 3 / 4);

		for (String word : actual)
			assertEquals(maxFreqs.get(word),
					actual.getMaximumTermFrequency(word));

		double docSize = Math.sqrt(sqr(expected.get("a"))
				+ sqr(expected.get("b")) + sqr(expected.get("c"))
				+ sqr(expected.get("d")));
		
		Map<String, Double> euclidFreqs = new HashMap<String, Double>();
		euclidFreqs.put("a", expected.get("a") / docSize);
		euclidFreqs.put("b", expected.get("b") / docSize);
		euclidFreqs.put("c", expected.get("c") / docSize);
		euclidFreqs.put("d", expected.get("d") / docSize);

		for (String word : actual)
			assertEquals(euclidFreqs.get(word), actual.getEuclideanTermFrequency(word));
	}

	private static int sqr(int n) {
		return n * n;
	}
}
