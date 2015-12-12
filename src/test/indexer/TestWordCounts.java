package test.indexer;

import indexer.WordCounts;
import indexer.offline.InvertedIndexJob;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.jsoup.Jsoup;
import org.junit.Test;

import utils.nlp.Dictionary;

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
		actual.computeNormalDocSizes();
		
		assertEquals(expected, actual.getCounts());
		assertEquals("a", actual.getMaxWord());
		
		assertEquals(new HashSet<>(Arrays.asList(1,4,6,11)), actual.getPosition("a"));
		assertEquals(new HashSet<>(Arrays.asList(2,5)), actual.getPosition("b"));
		assertEquals(new HashSet<>(Arrays.asList(3,7)), actual.getPosition("c"));
		assertEquals(new HashSet<>(Arrays.asList(8,9,10)), actual.getPosition("d"));

		float alpha = 0.5f;
		Map<String, Float> maxFreqs = new HashMap<String, Float>();
		maxFreqs.put("a", alpha + (1 - alpha) * 1);
		maxFreqs.put("b", alpha + (1 - alpha) * 2 / 4);
		maxFreqs.put("c", alpha + (1 - alpha) * 2 / 4);
		maxFreqs.put("d", alpha + (1 - alpha) * 3 / 4);

		for (String word : actual)
			assertEquals(maxFreqs.get(word),
					actual.getMaximumTermFrequency(word));

		float docSize = (float) Math.sqrt(sqr(expected.get("a"))
				+ sqr(expected.get("b")) + sqr(expected.get("c"))
				+ sqr(expected.get("d")));
		
		Map<String, Float> euclidFreqs = new HashMap<>();
		euclidFreqs.put("a", expected.get("a") / docSize);
		euclidFreqs.put("b", expected.get("b") / docSize);
		euclidFreqs.put("c", expected.get("c") / docSize);
		euclidFreqs.put("d", expected.get("d") / docSize);

		for (String word : actual)
			assertEquals(euclidFreqs.get(word), actual.getEuclideanTermFrequency(word));
	}
	
	@Test
	public void testBiCounts() {
		List<String> input = 
			Arrays.asList("a b", "b c", "a b", "a b");
		Map<String, Integer> expected = new HashMap<String, Integer>();
		expected.put("a b", 3);
		expected.put("b c", 1);

		WordCounts actual = new WordCounts(input, 2);
		actual.computeNormalDocSizes();
		
		assertEquals(expected, actual.getCounts());
		assertEquals("a b", actual.getMaxNWord(2));

		float alpha = 0.5f;
		Map<String, Float> maxFreqs = new HashMap<String, Float>();
		maxFreqs.put("a b", alpha + (1 - alpha) * 3 / 3);
		maxFreqs.put("b c", alpha + (1 - alpha) * 1 / 3);

		for (String word : actual)
			assertEquals(maxFreqs.get(word),
					actual.getMaximumTermFrequency(word));

		double docSize2 = Math.sqrt(sqr(expected.get("a b"))
				+ sqr(expected.get("b c")));
		
		Map<String, Float> euclidFreqs = new HashMap<>();
		euclidFreqs.put("a b", (float) (expected.get("a b") / docSize2));
		euclidFreqs.put("b c", (float) (expected.get("b c") / docSize2));

		for (String word : actual)
			assertEquals(euclidFreqs.get(word), actual.getEuclideanTermFrequency(word));
		
		//Assert positions
		assertEquals(new HashSet<>(Arrays.asList(1,3,4)), actual.getPosition("a b"));
		assertEquals(new HashSet<>(Arrays.asList(2)), actual.getPosition("b c"));
	}
	
	@Test
	public void testTriCounts() {
		List<String> input = 
			Arrays.asList("a b c", "a c b", "a c b", "a b c", "a c b");
		Map<String, Integer> expected = new HashMap<String, Integer>();		
		expected.put("a b c",2);
		expected.put("a c b", 3);

		WordCounts actual = new WordCounts(input, 3);
		actual.computeNormalDocSizes();
		
		assertEquals(expected, actual.getCounts());		
		assertEquals("a c b", actual.getMaxNWord(3));

		float alpha = 0.5f;
		Map<String, Float> maxFreqs = new HashMap<String, Float>();
		maxFreqs.put("a b c", alpha + (1 - alpha) * 2 / 3);
		maxFreqs.put("a c b", alpha + (1 - alpha) * 3 / 3);

		for (String word : actual)
			assertEquals(maxFreqs.get(word),
					actual.getMaximumTermFrequency(word));
		
		double docSize3 = Math.sqrt(sqr(expected.get("a b c"))
				+ sqr(expected.get("a c b")));
		
		
		Map<String, Float> euclidFreqs = new HashMap<>();
		euclidFreqs.put("a b c", (float) (expected.get("a b c") / docSize3));
		euclidFreqs.put("a c b", (float) (expected.get("a c b") / docSize3));

		for (String word : actual)
			assertEquals(euclidFreqs.get(word), actual.getEuclideanTermFrequency(word));
		
		//Assert positions
		assertEquals(new HashSet<>(Arrays.asList(1,4)), actual.getPosition("a b c"));
		assertEquals(new HashSet<>(Arrays.asList(2,3,5)), actual.getPosition("a c b"));
	}



	private static int sqr(int n) {
		return n * n;
	}
	
	@Test
	public void testEnglishPercentage() throws IOException {
		List<String> nonEnglish = Arrays.asList(
				"testcontent/cn.nytimes.html",
				"testcontent/sample-german-page",
				"testcontent/some-french-page.html",
				"testcontent/sample-spanish-page",
				"testcontent/sample-dutch-page",
				"testcontent/sample-dutch-page2",
				"testcontent/sample-fake-english"
			);
		List<String> english = Arrays.asList(
				"testcontent/sample-english-page",
				"testcontent/sample-english-page2",
				"testcontent/en.wikipedia.org",
				"testcontent/www.nytimes.com");
		
		Dictionary dict = Dictionary.createInstance(new FileInputStream("resources/dict/all-english"));
		
		for(String f: nonEnglish) {
			String[] words = Jsoup.parse(new File(f), "utf-8").select("body").text().toLowerCase().split(" ");
			WordCounts docCounts = new WordCounts(Arrays.asList(words), 1, dict);
			System.out.println("english percentage in doc " + f + ": " + docCounts.getPercentage());
			assertTrue(docCounts.getPercentage() < InvertedIndexJob.ENGLISH_THRESHOLD);
		}
		
		for(String f: english) {
			String[] words = Jsoup.parse(new File(f), "utf-8").select("body").text().toLowerCase().split(" ");
			WordCounts docCounts = new WordCounts(Arrays.asList(words), 1, dict);
			System.out.println("english percentage in doc " + f + ": " + docCounts.getPercentage());
			assertTrue(docCounts.getPercentage() > InvertedIndexJob.ENGLISH_THRESHOLD);
		}
	}
}
