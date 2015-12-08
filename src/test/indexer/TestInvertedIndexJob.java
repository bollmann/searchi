package test.indexer;

import indexer.InvertedIndex;
import indexer.WordCounts;
import indexer.dao.DocumentFeatures;
import indexer.dao.InvertedIndexRow;
import indexer.offline.InvertedIndexJob;
import indexer.offline.InvertedIndexJob.Feature;
import indexer.offline.Tokenizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import crawler.dao.URLContent;
import edu.stanford.nlp.util.StringUtils;

public class TestInvertedIndexJob extends TestCase {
	
	@Test
	public void testComputeCounts() {
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
		
		Map<Feature, WordCounts> allCounts = InvertedIndexJob.computeCounts(page);
		
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
	public void testSaveInvertedIndexRow() {
		for (int i = 0; i < 10; i++) {
			DynamoDBMapper db = InvertedIndex.connectDB();
			List<DocumentFeatures> features = new ArrayList<DocumentFeatures>();
			DocumentFeatures feat1 = new DocumentFeatures();
			feat1.setEuclideanTermFrequency(0.0);
			feat1.setHeaderCount(1);
			feat1.setLinkCount(2);
			feat1.setMaximumTermFrequency(3.0);
			feat1.setMetaTagCount(4);
			feat1.setTotalCount(5);
			feat1.setUrl("this/url");
			features.add(feat1);
			features.add(feat1);

			InvertedIndexRow row = new InvertedIndexRow("abc" + i, i, features);
			db.save(row);
		}
	}
}
