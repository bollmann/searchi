package test.indexer;

import indexer.WordCounts;
import indexer.offline.InvertedIndexJob;
import indexer.offline.InvertedIndexJob.FeatureType;
import indexer.offline.Tokenizer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.BeforeClass;
import org.junit.Test;

import utils.file.FilePolicy;
import utils.nlp.Dictionary;

import crawler.dao.URLContent;
import edu.stanford.nlp.util.StringUtils;

public class TestInvertedIndexJob extends TestCase {
	private Dictionary dict;
	
	@BeforeClass
	public void setup() throws FileNotFoundException, IOException {
		dict = Dictionary.createInstance(new FileInputStream("resources/dict/all-english"));
	}
	
	@Test
	public void testComputeCounts() throws IOException {
		StringBuilder content = new StringBuilder();
		content.append("<html><head><title>Awesome Schnitzel's Schnitzel Page</title>");
		content.append("<meta name=\"description\" content=\"Schnitzel Page\"/>");
		content.append("<meta name=\"keywords\" content=\"Schnitzel, Hitzl, Bratwurst!\"/>");
		content.append("</head><body>");
		content.append("<h1>Schnitzel Test. What's your favorite schnitzel?</h1>");
		content.append("<p>The GMAN schnitzel, obviously</p>\n");
		content.append("<a href='/some/other/schnitzel-page'>some schnitzel in some link!</a>");
		content.append("<script>Anything in here should not be counted</script>");
		content.append("<style>same goes for css stylesheets</style>");
		content.append("</body></html>");
		
		URLContent page = new URLContent("http://www.schnitzel.com/");
		page.setContent(content.toString());
		
		Map<FeatureType, WordCounts> allCounts = InvertedIndexJob.computeCounts(page, 1, dict);
		
		// link counts
		Map<String, Integer> expLinkCounts = new HashMap<String, Integer>();
		expLinkCounts.put("some", 2);
		expLinkCounts.put("schnitzel", 1);
		expLinkCounts.put("in", 1);
		expLinkCounts.put("link", 1);

		assertEquals(expLinkCounts, allCounts.get("linkCounts").getCounts());
		
		// header counts
		Map<String, Integer> expHeaderCounts = new HashMap<String, Integer>();
		expHeaderCounts.put("awesome", 1);
		expHeaderCounts.put("schnitzel", 4);
		expHeaderCounts.put("page", 1);
		expHeaderCounts.put("test", 1);
		expHeaderCounts.put("what", 1);
		expHeaderCounts.put("your", 1);
		expHeaderCounts.put("favorite", 1);
		
		assertEquals(expHeaderCounts, allCounts.get("headerCounts").getCounts());
		
		// meta tag counts
		Map<String, Integer> expMetaCounts = new HashMap<String, Integer>();
		expMetaCounts.put("schnitzel", 2);
		expMetaCounts.put("hitzl", 1);
		expMetaCounts.put("bratwurst", 1);
		expMetaCounts.put("page", 1);
		
		assertEquals(expMetaCounts, allCounts.get("metaTagCounts").getCounts());
		
		// normal counts
		Map<String, Integer> expNormalCounts = new HashMap<String, Integer>();
		expNormalCounts.put("awesome", 1);
		expNormalCounts.put("schnitzel", 8);
		expNormalCounts.put("page", 2);
		expNormalCounts.put("test", 1);
		expNormalCounts.put("what", 1);
		expNormalCounts.put("your", 1);
		expNormalCounts.put("favorite", 1);
		expNormalCounts.put("hitzl", 1);
		expNormalCounts.put("bratwurst", 1);
		expNormalCounts.put("the", 1);
		expNormalCounts.put("gman", 1);
		expNormalCounts.put("obviously", 1);
		expNormalCounts.put("some", 2);
		expNormalCounts.put("link", 1);
		expNormalCounts.put("in", 1);

		assertEquals(expNormalCounts, allCounts.get("normalCounts").getCounts());
	}
	
	@Test
	public void testGetNGrams() {
		final String input = "Hello world. This is a test";
		List<String> tokens = new Tokenizer(input).getTokens();
		Collection<String> ngrams = StringUtils.getNgrams(tokens, 1, 1);
		
		// unigrams
		assertEquals(tokens, ngrams);
		
		// bigrams
		List<String> bigrams = new ArrayList<>();
		for(int i = 1; i < tokens.size(); ++i) {
			bigrams.add(tokens.get(i - 1));
			bigrams.add(tokens.get(i - 1) + " " + tokens.get(i));
			if(i == tokens.size() - 1)
				bigrams.add(tokens.get(i));
		}
		assertEquals(bigrams, StringUtils.getNgrams(tokens, 1, 2));
	}
	
	@Test
	public void testComputeCountsUnigram() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("Two words. <b><p>Other words</p></b>");
		sb.append("<a href='http://www.google.com'>Link link text</a>");
		sb.append("</body></html>");
		URLContent content = new URLContent();
		content.setContent(sb.toString());
		content.setUrl("http://www.google.com");
		Map<FeatureType, WordCounts> wordCounts = InvertedIndexJob.computeCounts(content, 1, dict);
		System.out.println(wordCounts);
		
		//total counts
		assertEquals(1, (int) wordCounts.get(FeatureType.TOTAL_COUNTS).getWordCounts().get("two"));
		assertTrue(wordCounts.get(FeatureType.TOTAL_COUNTS).getWordPos().get("two").contains(1));
		
		assertEquals(1, (int) wordCounts.get(FeatureType.TOTAL_COUNTS).getWordCounts().get("words"));
		assertTrue(wordCounts.get(FeatureType.TOTAL_COUNTS).getWordPos().get("words").contains(2));
		
		//link counts
		assertEquals(2, (int) wordCounts.get(FeatureType.LINK_COUNTS).getWordCounts().get("link"));
		assertTrue(wordCounts.get(FeatureType.LINK_COUNTS).getWordPos().get("text").contains(3));
		assertEquals(1, (int) wordCounts.get(FeatureType.LINK_COUNTS).getWordCounts().get("text"));
		assertTrue(wordCounts.get(FeatureType.LINK_COUNTS).getWordPos().get("link").contains(1));
		assertTrue(wordCounts.get(FeatureType.LINK_COUNTS).getWordPos().get("link").contains(2));
	}
	
	@Test
	public void testStringUtilsGetUnigrams() {
		List<String> lineTokens = Arrays.asList("This is a test sentence".split(" "));
		Collection<String> nGrams = StringUtils.getNgrams(lineTokens, 1, 1);
		List<String> nGramList = new ArrayList<String>(nGrams);
		assertEquals("This", nGramList.get(0));
		assertEquals("is", nGramList.get(1));
		assertEquals("a", nGramList.get(2));
		assertEquals("test", nGramList.get(3));
		assertEquals("sentence", nGramList.get(4));
	}
	
	@Test
	public void testStringUtilsGetBigrams() {
		List<String> lineTokens = Arrays.asList("This is a test sentence".split(" "));
		Collection<String> nGrams = StringUtils.getNgrams(lineTokens, 2, 2);
		List<String> nGramList = new ArrayList<String>(nGrams);
		assertEquals("This is", nGramList.get(0));
		assertEquals("is a", nGramList.get(1));
		assertEquals("a test", nGramList.get(2));
		assertEquals("test sentence", nGramList.get(3));
	}
	
	@Test
	public void testComputeCountsBigram() throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("Two words. New York City.");
		sb.append("<a href='http://www.google.com'>Link link text</a>");
		sb.append("</body></html>");
		URLContent content = new URLContent();
		content.setContent(sb.toString());
		content.setUrl("http://www.google.com");
		Map<FeatureType, WordCounts> wordCounts = InvertedIndexJob.computeCounts(content, 2, dict);
//		System.out.println(wordCounts);
		
		//total counts
		assertEquals(1, (int) wordCounts.get(FeatureType.TOTAL_COUNTS).getWordCounts().get("two"));
		assertEquals(1, (int) wordCounts.get(FeatureType.TOTAL_COUNTS).getWordCounts().get("two words"));
		assertTrue(wordCounts.get(FeatureType.TOTAL_COUNTS).getWordPos().get("two").contains(1));
		
		assertEquals(1, (int) wordCounts.get(FeatureType.TOTAL_COUNTS).getWordCounts().get("words"));
		assertTrue(wordCounts.get(FeatureType.TOTAL_COUNTS).getWordPos().get("words").contains(2));
		
		//link counts
		assertEquals(2, (int) wordCounts.get(FeatureType.LINK_COUNTS).getWordCounts().get("link"));
		assertTrue(wordCounts.get(FeatureType.LINK_COUNTS).getWordPos().get("text").contains(3));
		assertEquals(1, (int) wordCounts.get(FeatureType.LINK_COUNTS).getWordCounts().get("text"));
		assertTrue(wordCounts.get(FeatureType.LINK_COUNTS).getWordPos().get("link").contains(1));
		assertTrue(wordCounts.get(FeatureType.LINK_COUNTS).getWordPos().get("link").contains(2));
	}
	
	@Test
	public void testPerformanceForLargeHtmlFiles() throws IOException {
		String content = null;
		try {
			content = FilePolicy.readFile("testcontent/Wiki-html-file.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		URLContent urlContent = new URLContent();
		urlContent.setContent(content);
		urlContent.setUrl("http://www.google.com");
		Date start = Calendar.getInstance().getTime();
		Map<FeatureType, WordCounts> wordCounts = InvertedIndexJob.computeCounts(urlContent, 3, dict);
		Date end = Calendar.getInstance().getTime();
		long timeTakenForBigram = (end.getTime() - start.getTime());
		start = Calendar.getInstance().getTime();
		wordCounts = InvertedIndexJob.computeCounts(urlContent, 1, dict);
		end = Calendar.getInstance().getTime();
		long timeTakenForUnigram = (end.getTime() - start.getTime());
		System.out.println("Time taken for unigram:" + timeTakenForUnigram + " and bigram:" + timeTakenForBigram);
	}

//	@Test
//	public void testSaveInvertedIndexRow() {
//		for (int i = 0; i < 10; i++) {
//			DynamoDBMapper db = InvertedIndex.connectDB();
//			List<DocumentFeatureTypes> FeatureTypes = new ArrayList<DocumentFeatureTypes>();
//			DocumentFeatureTypes feat1 = new DocumentFeatureTypes();
//			feat1.setEuclideanTermFrequency(0.0F);
//			feat1.setHeaderCount(1);
//			feat1.setLinkCount(2);
//			feat1.setMaximumTermFrequency(3.0F);
//			feat1.setMetaTagCount(4);
//			feat1.setTotalCount(5);
//			feat1.setUrl("this/url");
//			FeatureTypes.add(feat1);
//			FeatureTypes.add(feat1);
//
//			InvertedIndexRow row = new InvertedIndexRow("abc" + i, i, FeatureTypes);
//			db.save(row);
//		}
//	}
}
