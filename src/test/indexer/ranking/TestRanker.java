package test.indexer.ranking;

import indexer.DocumentScore;
import indexer.dao.DocumentFeatures;
import indexer.ranking.Ranker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

public class TestRanker extends TestCase {

	@Test
	public void testGetDocumentScoresForQueryAndInvertedIndex() {
		List<String> query = Arrays.asList("a an the".split(" "));
		
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setUrl("1");
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setUrl("2");
		DocumentFeatures feat3 = new DocumentFeatures();
		feat3.setUrl("3");
		List<DocumentFeatures> feats1 = new ArrayList<>();
		feats1.add(feat1);
		feats1.add(feat2);
		feats1.add(feat3);
		
		DocumentFeatures feat4 = new DocumentFeatures();
		feat4.setUrl("1");
		DocumentFeatures feat5 = new DocumentFeatures();
		feat5.setUrl("4");
		DocumentFeatures feat6 = new DocumentFeatures();
		feat6.setUrl("5");
		List<DocumentFeatures> feats2 = new ArrayList<>();
		feats2.add(feat4);
		feats2.add(feat5);
		feats2.add(feat6);
		
		Map<String, List<DocumentFeatures>> invertedIndex = new HashMap<String, List<DocumentFeatures>>();
		invertedIndex.put("a", feats1);
		invertedIndex.put("an", feats2);
		invertedIndex.put("the", new ArrayList<DocumentFeatures>()); // a result for a query word can't be null
		
		List<DocumentScore> results = Ranker.getDocumentScoresForQueryAndInvertedIndex(query, invertedIndex);
		assertEquals(5, results.size());
		for(DocumentScore result : results) {
			if(result.getUrl().equals("1")) {
				assertEquals(2, result.getWordFeatures().size());
			} else {
				assertEquals(1, result.getWordFeatures().size());
			}
		}
		
		
	}
	
	@Test
	public void testRankDocumentsOnTfIdf() {
		List<String> query = Arrays.asList("a an a the".split(" "));
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		
		DocumentScore score1 = new DocumentScore("1");
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setEuclideanTermFrequency(2f);
		score1.addFeatures("a", feat1);
		
		DocumentScore score2 = new DocumentScore("2");
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setEuclideanTermFrequency(1f);
		score2.addFeatures("an", feat2);
		
		scores.add(score1);
		scores.add(score2);
		
		Map<String, Integer> wordDfs = new HashMap<String, Integer>() {{
			put("a", 1);
			put("an", 1);
		}};
		
		int corpusSize = 2000;
		List<DocumentScore> result = Ranker.rankDocumentsOnTfIdf(scores, query, corpusSize, wordDfs);
		System.out.println(result);
		// TODO not sure about scores
		assertEquals("1", result.get(0).getUrl());
		assertEquals("2", result.get(1).getUrl());
	}
	
	@Test
	public void testRankDocumentsOnTotalCount() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore("1");
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setTotalCount(1);
		score1.addFeatures("a", feat1);
		DocumentScore score2 = new DocumentScore("2");
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setTotalCount(2);
		score2.addFeatures("a", feat2);
		
		scores.add(score1);
		scores.add(score2);
		List<DocumentScore> resultList = Ranker.rankDocumentsOnTotalCount(scores);
		assertEquals("2", resultList.get(0).getUrl());
		assertEquals(2.0f, resultList.get(0).getScore());
		assertEquals("1", resultList.get(1).getUrl());
		assertEquals(1.0f, resultList.get(1).getScore());
	}
	
	@Test
	public void testRankDocumentsOnTotalCountWithMoreThanOneFeature() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		
		DocumentScore score1 = new DocumentScore("1");
		DocumentFeatures feat11 = new DocumentFeatures();
		feat11.setTotalCount(1);
		DocumentFeatures feat12 = new DocumentFeatures();
		feat12.setTotalCount(5);
		score1.addFeatures("a", feat11);
		score1.addFeatures("b", feat12);
		
		DocumentScore score2 = new DocumentScore("2");
		DocumentFeatures feat21 = new DocumentFeatures();
		feat21.setTotalCount(2);
		DocumentFeatures feat22 = new DocumentFeatures();
		feat22.setTotalCount(1);
		score2.addFeatures("a", feat21);
		score2.addFeatures("c", feat22);
		
		scores.add(score1);
		scores.add(score2);
		List<DocumentScore> resultList = Ranker.rankDocumentsOnTotalCount(scores);
		assertEquals("1", resultList.get(0).getUrl());
		assertEquals(6.0f, resultList.get(0).getScore());
		assertEquals("2", resultList.get(1).getUrl());
		assertEquals(3.0f, resultList.get(1).getScore());
	}
	
	@Test
	public void testRankDocumentsOnLinkCount() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore("1");
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setLinkCount(1);
		score1.addFeatures("a", feat1);
		DocumentScore score2 = new DocumentScore("2");
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setLinkCount(2);
		score2.addFeatures("a", feat2);
		
		scores.add(score1);
		scores.add(score2);
		List<DocumentScore> resultList = Ranker.rankDocumentsOnLinkCount(scores);
		assertEquals("2", resultList.get(0).getUrl());
		assertEquals(2.0f, resultList.get(0).getScore());
		assertEquals("1", resultList.get(1).getUrl());
		assertEquals(1.0f, resultList.get(1).getScore());
	}
	
	@Test
	public void testRankDocumentsOnLinkCountForMultipleFeatures() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore("1");
		DocumentFeatures feat11 = new DocumentFeatures();
		feat11.setLinkCount(1);
		DocumentFeatures feat12 = new DocumentFeatures();
		feat12.setLinkCount(3);
		score1.addFeatures("a", feat11);
		score1.addFeatures("b", feat12);
		
		DocumentScore score2 = new DocumentScore("2");
		DocumentFeatures feat21 = new DocumentFeatures();
		feat21.setLinkCount(2);
		DocumentFeatures feat22 = new DocumentFeatures();
		feat22.setLinkCount(3);
		score2.addFeatures("a", feat21);
		score2.addFeatures("b", feat22);
		
		scores.add(score1);
		scores.add(score2);
		List<DocumentScore> resultList = Ranker.rankDocumentsOnLinkCount(scores);
		assertEquals("2", resultList.get(0).getUrl());
		assertEquals(5.0f, resultList.get(0).getScore());
		assertEquals("1", resultList.get(1).getUrl());
		assertEquals(4f, resultList.get(1).getScore());
	}
	
	@Test
	public void testRankDocumentsOnMeta() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore("1");
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setMetaTagCount(1);
		score1.addFeatures("a", feat1);
		DocumentScore score2 = new DocumentScore("2");
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setMetaTagCount(2);
		score2.addFeatures("a", feat2);
		
		scores.add(score1);
		scores.add(score2);
		List<DocumentScore> resultList = Ranker.rankDocumentsOnMetaCount(scores);
		assertEquals("2", resultList.get(0).getUrl());
		assertEquals(2.0f, resultList.get(0).getScore());
		assertEquals("1", resultList.get(1).getUrl());
		assertEquals(1.0f, resultList.get(1).getScore());
	}
	
	
	@Test
	public void testRankDocumentsOnHeaderCount() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore("1");
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setHeaderCount(1);
		score1.addFeatures("a", feat1);
		DocumentScore score2 = new DocumentScore("2");
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setHeaderCount(2);
		score2.addFeatures("a", feat2);
		
		scores.add(score1);
		scores.add(score2);
		List<DocumentScore> resultList = Ranker.rankDocumentsOnHeaderCount(scores);
		assertEquals("2", resultList.get(0).getUrl());
		assertEquals(2.0f, resultList.get(0).getScore());
		assertEquals("1", resultList.get(1).getUrl());
		assertEquals(1.0f, resultList.get(1).getScore());
	}
}
