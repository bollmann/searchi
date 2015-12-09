package test.indexer.ranking;

import indexer.DocumentScore;
import indexer.dao.DocumentFeatures;
import indexer.rank.comparators.DocumentScoreComparators;
import indexer.ranking.Ranker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
		feat1.setDocId(1);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setDocId(2);
		DocumentFeatures feat3 = new DocumentFeatures();
		feat3.setDocId(3);
		List<DocumentFeatures> feats1 = new ArrayList<>();
		feats1.add(feat1);
		feats1.add(feat2);
		feats1.add(feat3);

		DocumentFeatures feat4 = new DocumentFeatures();
		feat4.setDocId(2);
		DocumentFeatures feat5 = new DocumentFeatures();
		feat5.setDocId(4);
		DocumentFeatures feat6 = new DocumentFeatures();
		feat6.setDocId(5);
		List<DocumentFeatures> feats2 = new ArrayList<>();
		feats2.add(feat4);
		feats2.add(feat5);
		feats2.add(feat6);

		Map<String, List<DocumentFeatures>> invertedIndex = new HashMap<String, List<DocumentFeatures>>();
		invertedIndex.put("a", feats1);
		invertedIndex.put("an", feats2);
		invertedIndex.put("the", new ArrayList<DocumentFeatures>()); // a result
																		// for a
																		// query
																		// word
																		// can't
																		// be
																		// null

		List<DocumentScore> results = Ranker
				.getDocumentScoresForQueryAndInvertedIndex(query, invertedIndex);
		assertEquals(5, results.size());
		for (DocumentScore result : results) {
			if (result.getDocId() == 2) {
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

		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setEuclideanTermFrequency(2f);
		score1.addFeatures("a", feat1);

		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setEuclideanTermFrequency(1f);
		score2.addFeatures("an", feat2);

		scores.add(score1);
		scores.add(score2);

		Map<String, Integer> wordDfs = new HashMap<String, Integer>() {
			{
				put("a", 1);
				put("an", 1);
			}
		};

		int corpusSize = 2000;
		Map<Integer, DocumentScore> resultMap = Ranker.rankDocumentsOnTfIdf(
				scores, query, corpusSize, wordDfs);
		List<DocumentScore> result = new ArrayList<>(resultMap.values());
		Collections.sort(result, DocumentScoreComparators.getTfIdfComparator(
				query, corpusSize, wordDfs));
		System.out.println(result);
		// TODO not sure about scores
		assertEquals(1, result.get(0).getDocId());
		assertEquals(2, result.get(1).getDocId());
	}

	@Test
	public void testRankDocumentsOnTotalCount() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setTotalCount(1);
		score1.addFeatures("a", feat1);
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setTotalCount(2);
		score2.addFeatures("a", feat2);

		scores.add(score1);
		scores.add(score2);
		Map<Integer, DocumentScore> resultMap = Ranker
				.rankDocumentsOnTotalCount(scores);
		List<DocumentScore> resultList = new ArrayList<DocumentScore>(
				resultMap.values());
		Collections.sort(resultList,
				DocumentScoreComparators.getTotalCountComparator());
		assertEquals(2, resultList.get(0).getDocId());
		assertEquals(2.0f, resultList.get(0).getScore());
		assertEquals(1, resultList.get(1).getDocId());
		assertEquals(1.0f, resultList.get(1).getScore());
	}

	@Test
	public void testRankDocumentsOnTotalCountWithMoreThanOneFeature() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();

		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat11 = new DocumentFeatures();
		feat11.setTotalCount(1);
		DocumentFeatures feat12 = new DocumentFeatures();
		feat12.setTotalCount(5);
		score1.addFeatures("a", feat11);
		score1.addFeatures("b", feat12);

		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat21 = new DocumentFeatures();
		feat21.setTotalCount(2);
		DocumentFeatures feat22 = new DocumentFeatures();
		feat22.setTotalCount(1);
		score2.addFeatures("a", feat21);
		score2.addFeatures("c", feat22);

		scores.add(score1);
		scores.add(score2);
		Map<Integer, DocumentScore> resultMap = Ranker
				.rankDocumentsOnTotalCount(scores);
		List<DocumentScore> resultList = new ArrayList<DocumentScore>(
				resultMap.values());
		Collections.sort(resultList,
				DocumentScoreComparators.getTotalCountComparator());
		assertEquals(1, resultList.get(0).getDocId());
		assertEquals(6.0f, resultList.get(0).getScore());
		assertEquals(2, resultList.get(1).getDocId());
		assertEquals(3.0f, resultList.get(1).getScore());
	}

	@Test
	public void testRankDocumentsOnLinkCount() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setLinkCount(1);
		score1.addFeatures("a", feat1);
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setLinkCount(2);
		score2.addFeatures("a", feat2);

		scores.add(score1);
		scores.add(score2);
		Map<Integer, DocumentScore> resultMap = Ranker
				.rankDocumentsOnLinkCount(scores);
		List<DocumentScore> resultList = new ArrayList<DocumentScore>(
				resultMap.values());
		Collections.sort(resultList,
				DocumentScoreComparators.getLinkCountsComparator());
		assertEquals(2, resultList.get(0).getDocId());
		assertEquals(2.0f, resultList.get(0).getScore());
		assertEquals(1, resultList.get(1).getDocId());
		assertEquals(1.0f, resultList.get(1).getScore());
	}

	@Test
	public void testRankDocumentsOnLinkCountForMultipleFeatures() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat11 = new DocumentFeatures();
		feat11.setLinkCount(1);
		DocumentFeatures feat12 = new DocumentFeatures();
		feat12.setLinkCount(3);
		score1.addFeatures("a", feat11);
		score1.addFeatures("b", feat12);

		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat21 = new DocumentFeatures();
		feat21.setLinkCount(2);
		DocumentFeatures feat22 = new DocumentFeatures();
		feat22.setLinkCount(3);
		score2.addFeatures("a", feat21);
		score2.addFeatures("b", feat22);

		scores.add(score1);
		scores.add(score2);
		Map<Integer, DocumentScore> resultMap = Ranker
				.rankDocumentsOnLinkCount(scores);
		List<DocumentScore> resultList = new ArrayList<DocumentScore>(
				resultMap.values());
		Collections.sort(resultList,
				DocumentScoreComparators.getLinkCountsComparator());
		assertEquals(2, resultList.get(0).getDocId());
		assertEquals(5.0f, resultList.get(0).getScore());
		assertEquals(1, resultList.get(1).getDocId());
		assertEquals(4f, resultList.get(1).getScore());
	}

	@Test
	public void testRankDocumentsOnMeta() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setMetaTagCount(1);
		score1.addFeatures("a", feat1);
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setMetaTagCount(2);
		score2.addFeatures("a", feat2);

		scores.add(score1);
		scores.add(score2);
		Map<Integer, DocumentScore> resultMap = Ranker
				.rankDocumentsOnMetaCount(scores);
		List<DocumentScore> resultList = new ArrayList<DocumentScore>(
				resultMap.values());
		Collections.sort(resultList,
				DocumentScoreComparators.getMetaTagCountsComparator());
		assertEquals(2, resultList.get(0).getDocId());
		assertEquals(2.0f, resultList.get(0).getScore());
		assertEquals(1, resultList.get(1).getDocId());
		assertEquals(1.0f, resultList.get(1).getScore());
	}

	@Test
	public void testRankDocumentsOnHeaderCount() {
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setHeaderCount(1);
		score1.addFeatures("a", feat1);
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setHeaderCount(2);
		score2.addFeatures("a", feat2);

		scores.add(score1);
		scores.add(score2);
		Map<Integer, DocumentScore> resultMap = Ranker
				.rankDocumentsOnHeaderCount(scores);
		List<DocumentScore> resultList = new ArrayList<DocumentScore>(
				resultMap.values());
		Collections.sort(resultList,
				DocumentScoreComparators.getHeaderCountComparator());
		assertEquals(2, resultList.get(0).getDocId());
		assertEquals(2.0f, resultList.get(0).getScore());
		assertEquals(1, resultList.get(1).getDocId());
		assertEquals(1.0f, resultList.get(1).getScore());
	}

	@Test
	public void testCombineRankedListsWithWeights() {
		Map<Integer, DocumentScore> map1 = new HashMap<Integer, DocumentScore>();
		DocumentScore score11 = new DocumentScore(1);
		score11.setScore(11f);
		DocumentScore score12 = new DocumentScore(2);
		score12.setScore(12f);
		DocumentScore score13 = new DocumentScore(3);
		score13.setScore(13f);
		map1.put(score11.getDocId(), score11);
		map1.put(score12.getDocId(), score12);
		map1.put(score13.getDocId(), score13);

		Map<Integer, DocumentScore> map2 = new HashMap<Integer, DocumentScore>();
		DocumentScore score21 = new DocumentScore(1);
		score21.setScore(21f);
		DocumentScore score22 = new DocumentScore(2);
		score22.setScore(22f);
		DocumentScore score23 = new DocumentScore(3);
		score23.setScore(23f);
		map2.put(score21.getDocId(), score21);
		map2.put(score22.getDocId(), score22);
		map2.put(score23.getDocId(), score23);

		Map<Integer, DocumentScore> map3 = new HashMap<Integer, DocumentScore>();
		DocumentScore score31 = new DocumentScore(1);
		score31.setScore(31f);
		DocumentScore score32 = new DocumentScore(2);
		score32.setScore(32f);
		DocumentScore score33 = new DocumentScore(3);
		score33.setScore(33f);
		map3.put(score31.getDocId(), score31);
		map3.put(score32.getDocId(), score32);
		map3.put(score33.getDocId(), score33);

		List<Map<Integer, DocumentScore>> combinedMapList = new ArrayList<Map<Integer, DocumentScore>>();
		combinedMapList.add(map1);
		combinedMapList.add(map2);
		combinedMapList.add(map3);

		List<Double> weights = new ArrayList<Double>() {
			{
				add(2.0);
				add(3.0);
				add(4.0);
			}
		};

		List<DocumentScore> result = Ranker.combineRankedListsWithWeights(
				combinedMapList, weights);
		assertEquals(3, result.get(0).getDocId());
		assertEquals(227, Math.round(result.get(0).getScore())); // 13*2 + 23*3
																	// + 33*4
		assertEquals(2, result.get(1).getDocId());
		assertEquals(218, Math.round(result.get(1).getScore())); // 12*2 + 22*3
																	// + 32*4
		assertEquals(1, result.get(2).getDocId());
		assertEquals(209, Math.round(result.get(2).getScore())); // 11*2 + 21*3
																	// + 31*4
	}

	@Test
	public void testRankDocumentsOnQueryWordPresenceCount() {
		List<String> query = Arrays.asList("Some random query".split(" "));
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setMetaTagCount(1);
		score1.addFeatures("some", feat1);
		score1.addFeatures("random", feat1);
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setMetaTagCount(2);
		score2.addFeatures("random", feat2);

		scores.add(score1);
		scores.add(score2);
		Map<Integer, DocumentScore> resultMap = Ranker
				.rankDocumentsOnQueryWordPresenceCount(scores, query);
		List<DocumentScore> resultList = new ArrayList<DocumentScore>(
				resultMap.values());
		Collections.sort(resultList,
				DocumentScoreComparators.getQueryWordPresenceComparator(query));
		assertEquals(1, resultList.get(0).getDocId());
		assertEquals(2.0f, resultList.get(0).getScore());
		assertEquals(2, resultList.get(1).getDocId());
		assertEquals(1.0f, resultList.get(1).getScore());
	}

	@Test
	public void testCombinationOfFeaturesAndRanking() {
		List<String> query = Arrays.asList("some random query".split(" "));

		String doc1 = "<html><body>" + "<h1>random</h1>"
				+ "<meta name='' value='some random'>"
				+ "<title>Some random</title>" + "<a href=''>query</a>"
				+ "</body></html>";
		DocumentScore score1 = new DocumentScore(1);
		// feature for "some"
		DocumentFeatures feat11 = new DocumentFeatures();
		feat11.setTfidf(1.0f);
		feat11.setHeaderCount(0);
		feat11.setTotalCount(2);
		feat11.setMetaTagCount(1);
		feat11.setLinkCount(0);
		score1.addFeatures("some", feat11);
		// feature for "random"
		DocumentFeatures feat12 = new DocumentFeatures();
		feat12.setTfidf(2.0f);
		feat12.setHeaderCount(1);
		feat12.setTotalCount(3);
		feat12.setMetaTagCount(1);
		feat12.setLinkCount(0);
		score1.addFeatures("random", feat12);
		// feature for "query"
		DocumentFeatures feat13 = new DocumentFeatures();
		feat13.setTfidf(3.0f);
		feat13.setHeaderCount(0);
		feat13.setTotalCount(1);
		feat13.setMetaTagCount(0);
		feat13.setLinkCount(1);
		score1.addFeatures("query", feat13);
		
		
		String doc2 = "<html><body>" + "<h1>random random</h1>"
				+ "<meta name='' value='the query'>"
				+ "<title>random query</title>" + "<a href=''>random query</a>"
				+ "</body></html>";
		DocumentScore score2 = new DocumentScore(2);
		// no features for "some"

		// feature for "random"
		DocumentFeatures feat22 = new DocumentFeatures();
		feat22.setTfidf(3.0f);
		feat22.setHeaderCount(2);
		feat22.setTotalCount(4);
		feat22.setMetaTagCount(0);
		feat22.setLinkCount(1);
		score2.addFeatures("random", feat22);
		// feature for "query"
		DocumentFeatures feat23 = new DocumentFeatures();
		feat23.setTfidf(4.0f);
		feat23.setHeaderCount(0);
		feat23.setTotalCount(3);
		feat23.setMetaTagCount(1);
		feat23.setLinkCount(1);
		score2.addFeatures("query", feat23);
		List<DocumentScore> scoreList = new ArrayList<>();
		scoreList.add(score1);
		scoreList.add(score2);
		
		Map<String, Integer> wordDfs = new HashMap<String, Integer>();
		wordDfs.put("some", 1);
		wordDfs.put("random", 2);
		wordDfs.put("query", 2);
		int corpusSize = 13;
		
		Map<Integer, DocumentScore> tfIdf = Ranker.rankDocumentsOnTfIdf(scoreList, query, corpusSize, wordDfs);
		Map<Integer, DocumentScore> header = Ranker.rankDocumentsOnHeaderCount(scoreList);
		Map<Integer, DocumentScore> total = Ranker.rankDocumentsOnTotalCount(scoreList);
		Map<Integer, DocumentScore> links = Ranker.rankDocumentsOnLinkCount(scoreList);
		Map<Integer, DocumentScore> meta = Ranker.rankDocumentsOnMetaCount(scoreList);
		Map<Integer, DocumentScore> queryPresence = Ranker.rankDocumentsOnQueryWordPresenceCount(scoreList, query);
		
		List<DocumentScore> rankedScores = new ArrayList<>(tfIdf.values());
		Collections.sort(rankedScores,
				DocumentScoreComparators.getTfIdfComparator(query, corpusSize, wordDfs));
		assertEquals(2, rankedScores.get(0).getDocId());
		assertEquals(13, Math.round(rankedScores.get(0).getScore()));
		assertEquals(1, rankedScores.get(1).getDocId());
		assertEquals(12, Math.round(rankedScores.get(1).getScore()));
		
		rankedScores = new ArrayList<>(header.values());
		Collections.sort(rankedScores,
				DocumentScoreComparators.getHeaderCountComparator());
		assertEquals(2, rankedScores.get(0).getDocId());
		assertEquals(2, Math.round(rankedScores.get(0).getScore()));
		assertEquals(1, rankedScores.get(1).getDocId());
		assertEquals(1, Math.round(rankedScores.get(1).getScore()));
		
		rankedScores = new ArrayList<>(total.values());
		Collections.sort(rankedScores,
				DocumentScoreComparators.getTotalCountComparator());
		assertEquals(2, rankedScores.get(0).getDocId());
		assertEquals(7, Math.round(rankedScores.get(0).getScore()));
		assertEquals(1, rankedScores.get(1).getDocId());
		assertEquals(6, Math.round(rankedScores.get(1).getScore()));
		
		rankedScores = new ArrayList<>(links.values());
		Collections.sort(rankedScores,
				DocumentScoreComparators.getLinkCountsComparator());
		assertEquals(2, rankedScores.get(0).getDocId());
		assertEquals(2, Math.round(rankedScores.get(0).getScore()));
		assertEquals(1, rankedScores.get(1).getDocId());
		assertEquals(1, Math.round(rankedScores.get(1).getScore()));
		
		rankedScores = new ArrayList<>(meta.values());
		Collections.sort(rankedScores,
				DocumentScoreComparators.getMetaTagCountsComparator());
		assertEquals(1, rankedScores.get(0).getDocId());
		assertEquals(2, Math.round(rankedScores.get(0).getScore()));
		assertEquals(2, rankedScores.get(1).getDocId());
		assertEquals(1, Math.round(rankedScores.get(1).getScore()));
		
		rankedScores = new ArrayList<>(queryPresence.values());
		Collections.sort(rankedScores,
				DocumentScoreComparators.getQueryWordPresenceComparator(query));
		assertEquals(1, rankedScores.get(0).getDocId());
		assertEquals(3, Math.round(rankedScores.get(0).getScore()));
		assertEquals(2, rankedScores.get(1).getDocId());
		assertEquals(2, Math.round(rankedScores.get(1).getScore()));
	}
}
