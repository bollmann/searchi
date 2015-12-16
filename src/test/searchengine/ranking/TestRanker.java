package test.searchengine.ranking;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import searchengine.query.QueryWord;
import searchengine.ranking.Ranker;
import searchengine.ranking.RankerHeader;
import searchengine.ranking.RankerLinks;
import searchengine.ranking.RankerMeta;
import searchengine.ranking.RankerPosition;
import searchengine.ranking.RankerQueryMatch;
import searchengine.ranking.RankerTfIdf;
import searchengine.ranking.RankerTotalCount;
import searchengine.ranking.RankerUrlCount;

public class TestRanker extends TestCase {

	@Test
	public void testRankDocumentsOnTfIdf() {
		QueryWord qWordA = new QueryWord("a");
		QueryWord qWordAn = new QueryWord("an");
		QueryWord qWordthe = new QueryWord("the");
		
		List<QueryWord> query = new ArrayList<>();		
		query.add(qWordA);
		query.add(qWordAn);
		query.add(qWordthe);
		
		List<DocumentScore> scores = new ArrayList<DocumentScore>();

		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setTfidf(2f);
		score1.addFeatures(qWordA, feat1);

		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setTfidf(1f);
		score2.addFeatures(qWordAn, feat2);

		scores.add(score1);
		scores.add(score2);
		
		Map<QueryWord, Integer>wordDfs = new HashMap<>();
		wordDfs.put(qWordA, 20);
		wordDfs.put(qWordAn, 10);

		int corpusSize = 2000;
		Ranker rankerTfIdf = new RankerTfIdf(scores, query, corpusSize, wordDfs);
		rankerTfIdf.computeRank();
		List<Double> ranks = rankerTfIdf.getRanks();
		
		assertEquals(2, ranks.size());
		assertEquals(1.0, ranks.get(0));
		assertEquals(0.0, ranks.get(1));
	}

	@Test
	public void testRankDocumentsOnTotalCount() {
		QueryWord qWordA = new QueryWord("a");
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setTotalCount(1);
		score1.addFeatures(qWordA, feat1);
		
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setTotalCount(2);
		score2.addFeatures(qWordA, feat2);

		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		scores.add(score1);
		scores.add(score2);
		
		Ranker rankerTotal = new RankerTotalCount(scores);
		rankerTotal.computeRank();
		List<Double> ranks = rankerTotal.getRanks();
		assertEquals(2, ranks.size());
		assertEquals(1.0, ranks.get(1)); // Doc2 at ind 1 has more normalized count
		assertEquals(0.0, ranks.get(0)); // Doc1 at ind 0 has less normalized count
	}

	@Test
	public void testRankDocumentsOnTotalCountWithMoreThanOneFeature() {		
		QueryWord qWordA = new QueryWord("a");
		QueryWord qWordAn = new QueryWord("an");
		QueryWord qWordthe = new QueryWord("the");
		List<DocumentScore> scores = new ArrayList<DocumentScore>();

		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat11 = new DocumentFeatures();
		feat11.setTotalCount(1);
		DocumentFeatures feat12 = new DocumentFeatures();
		feat12.setTotalCount(5);
		score1.addFeatures(qWordA, feat11);
		score1.addFeatures(qWordAn, feat12);

		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat21 = new DocumentFeatures();
		feat21.setTotalCount(2);
		DocumentFeatures feat22 = new DocumentFeatures();
		feat22.setTotalCount(1);
		score2.addFeatures(qWordA, feat21);
		score2.addFeatures(qWordthe, feat22);
		
		DocumentScore score3 = new DocumentScore(3);
		DocumentFeatures feat31 = new DocumentFeatures();
		feat31.setTotalCount(2);
		DocumentFeatures feat32 = new DocumentFeatures();
		feat32.setTotalCount(1);
		DocumentFeatures feat33 = new DocumentFeatures();
		feat33.setTotalCount(4);
		score3.addFeatures(qWordA, feat31);
		score3.addFeatures(qWordAn, feat32);
		score3.addFeatures(qWordthe, feat33);

		scores.add(score1);
		scores.add(score2);
		scores.add(score3);
		
		Ranker rankerTotal = new RankerTotalCount(scores);
		rankerTotal.computeRank();
		List<Double> ranks = rankerTotal.getRanks();
		
		assertEquals(3, ranks.size());
		assertEquals(1.0, ranks.get(2)); // Doc3 at ind 2 has most normalized count
		assertEquals(0.0, ranks.get(1)); // Doc2 at ind 1 has least normalized count
		assertEquals(0.75, ranks.get(0)); 
	}

	@Test
	public void testRankDocumentsOnLinkCount() {
		QueryWord qWordA = new QueryWord("a");
		
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setLinkCount(3);
		score1.addFeatures(qWordA, feat1);
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setLinkCount(2);
		score2.addFeatures(qWordA, feat2);

		scores.add(score1);
		scores.add(score2);
		
		Ranker rankerLinks = new RankerLinks(scores);
		rankerLinks.computeRank();
		List<Double> ranks = rankerLinks.getRanks();
		
		assertEquals(2, ranks.size());
		assertEquals(1.0, ranks.get(0));
		assertEquals(0.0, ranks.get(1));
	}

	@Test
	public void testRankDocumentsOnLinkCountForMultipleFeatures() {
		QueryWord qWordA = new QueryWord("a");
		QueryWord qWordAn = new QueryWord("an");
		QueryWord qWordthe = new QueryWord("the");
		
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat11 = new DocumentFeatures();
		feat11.setLinkCount(5);
		DocumentFeatures feat12 = new DocumentFeatures();
		feat12.setLinkCount(3);
		score1.addFeatures(qWordA, feat11);
		score1.addFeatures(qWordAn, feat12);

		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat21 = new DocumentFeatures();
		feat21.setLinkCount(2);
		DocumentFeatures feat22 = new DocumentFeatures();
		feat22.setLinkCount(3);
		score2.addFeatures(qWordA, feat21);
		score2.addFeatures(qWordAn, feat22);

		DocumentScore score3 = new DocumentScore(3);
		DocumentFeatures feat31 = new DocumentFeatures();
		feat31.setLinkCount(2);
		DocumentFeatures feat32 = new DocumentFeatures();
		feat32.setLinkCount(1);
		DocumentFeatures feat33 = new DocumentFeatures();
		feat33.setLinkCount(3);
		score3.addFeatures(qWordA, feat31);
		score3.addFeatures(qWordAn, feat32);
		score3.addFeatures(qWordthe, feat33);

		scores.add(score1);
		scores.add(score2);
		scores.add(score3);
		
		Ranker rankerLinks = new RankerLinks(scores);
		rankerLinks.computeRank();
		List<Double> ranks = rankerLinks.getRanks();
		
		assertEquals(3, ranks.size());
		assertEquals(1.0, ranks.get(0));
		assertEquals(0.0, ranks.get(1));
		assertEquals(0, Double.compare(ranks.get(2), 1.0/3));
	}

	@Test
	public void testRankDocumentsOnMeta() {
		QueryWord qWordA = new QueryWord("a");
		
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setMetaTagCount(1);
		score1.addFeatures(qWordA, feat1);
		
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setMetaTagCount(2);
		score2.addFeatures(qWordA, feat2);

		scores.add(score1);		
		scores.add(score2);
		
		Ranker rankerMeta = new RankerMeta(scores);
		rankerMeta.computeRank();
		List<Double> ranks = rankerMeta.getRanks();
		
		assertEquals(2, ranks.size());
		assertEquals(0.0, ranks.get(0));
		assertEquals(1.0, ranks.get(1));
	}

	@Test
	public void testRankDocumentsOnHeaderCount() {
		QueryWord qWordA = new QueryWord("a");
		
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setHeaderCount(1);
		score1.addFeatures(qWordA, feat1);
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setHeaderCount(2);
		score2.addFeatures(qWordA, feat2);

		scores.add(score1);
		scores.add(score2);
		
		Ranker rankerHeader = new RankerHeader(scores);
		rankerHeader.computeRank();
		List<Double> ranks = rankerHeader.getRanks();
		
		assertEquals(2, ranks.size());
		assertEquals(0.0, ranks.get(0));
		assertEquals(1.0, ranks.get(1));
	}
	
	@Test
	public void testRankDocumentsOnQueryWordMatchCount() {
		//QUERY - "Some random query"
		QueryWord qWordA = new QueryWord("some");
		QueryWord qWordAn = new QueryWord("random");
		QueryWord qWordthe = new QueryWord("query");
		
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setMetaTagCount(1);
		score1.addFeatures(qWordA, feat1);
		score1.addFeatures(qWordAn, feat1);
		score1.addFeatures(qWordthe, feat1);
		
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setTotalCount(3);
		score2.addFeatures(qWordAn, feat2);

		scores.add(score1);
		scores.add(score2);
		
		Ranker rankerQueryMatch = new RankerQueryMatch(scores);
		rankerQueryMatch.computeRank();
		List<Double> ranks = rankerQueryMatch.getRanks();
		
		assertEquals(2, ranks.size());
		assertEquals(1.0, ranks.get(0));
		assertEquals(0.0, ranks.get(1));
	}

	
	@Test
	public void testRankDocumentsOnPosition() {
		QueryWord qWordA = new QueryWord("some");
		QueryWord qWordAn = new QueryWord("random");
		QueryWord qWordthe = new QueryWord("query");
		
		List<QueryWord> query = new ArrayList<>();
		query.add(qWordA);
		query.add(qWordAn);
		query.add(qWordthe);
		
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat11 = new DocumentFeatures();
		Set<Integer> pos1 = new HashSet<Integer>();
		pos1.add(23);
		feat11.setPositions(pos1);
		score1.addFeatures(qWordA, feat11);
		
		DocumentFeatures feat12 = new DocumentFeatures();
		Set<Integer> pos2 = new HashSet<Integer>();
		pos2.add(25);
		feat12.setPositions(pos2);
		score1.addFeatures(qWordAn, feat12);
		
		DocumentFeatures feat13 = new DocumentFeatures();
		Set<Integer> pos3 = new HashSet<Integer>();
		pos3.add(28);
		feat13.setPositions(pos3);
		score1.addFeatures(qWordthe, feat13);
		
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat21 = new DocumentFeatures();
		Set<Integer> pos21 = new HashSet<Integer>();
		pos21.add(1);
		feat21.setPositions(pos21);
		score2.addFeatures(qWordA, feat21);
		
		DocumentFeatures feat22 = new DocumentFeatures();
		Set<Integer> pos22 = new HashSet<Integer>();
		pos22.add(24);
		feat22.setPositions(pos22);
		score2.addFeatures(qWordAn, feat22);
		
		DocumentFeatures feat23 = new DocumentFeatures();
		Set<Integer> pos23 = new HashSet<Integer>();
		pos23.add(55);
		feat23.setPositions(pos23);
		score2.addFeatures(qWordthe, feat23);

		scores.add(score1);
		scores.add(score2);
		
		Ranker rankerPos = new RankerPosition(scores, query);		
		rankerPos.computeRank();
		List<Double> ranks = rankerPos.getRanks();
		
		assertEquals(2, ranks.size());
		assertEquals(0.0, ranks.get(0)); // Lower normalized position score is better
		assertEquals(1.0, ranks.get(1));
	}

	@Test
	public void testRankDocumentsOnUrlCount() {
		//QUERY - "Some random query"
		QueryWord qWordA = new QueryWord("some");
		QueryWord qWordAn = new QueryWord("random");
		QueryWord qWordthe = new QueryWord("query");
		
		List<QueryWord> query = new ArrayList<>();
		query.add(qWordA);
		query.add(qWordAn);
		query.add(qWordthe);
		
		Map<Integer, String> docMap = new HashMap<>();
		docMap.put(1, "some_random");
		docMap.put(2,  "query");
		List<DocumentScore> scores = new ArrayList<DocumentScore>();
		DocumentScore score1 = new DocumentScore(1);
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setMetaTagCount(1);
		score1.addFeatures(qWordA, feat1);
		score1.addFeatures(qWordAn, feat1);
		score1.addFeatures(qWordthe, feat1);
		
		DocumentScore score2 = new DocumentScore(2);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setTotalCount(3);
		score2.addFeatures(qWordAn, feat2);

		scores.add(score1);
		scores.add(score2);
		
		Ranker rankerUrlCount = new RankerUrlCount(scores, query);
		rankerUrlCount.computeRank();
		List<Double> ranks = rankerUrlCount.getRanks();
		
		assertEquals(2, ranks.size());
		assertEquals(1.0, ranks.get(0));
		assertEquals(0.0, ranks.get(1));
	}
	
//
//	@Test
//	public void testCombineRankedListsWithWeights() {
//		Map<Integer, DocumentScore> map1 = new HashMap<Integer, DocumentScore>();
//		DocumentScore score11 = new DocumentScore(1);
//		score11.setScore(11f);
//		DocumentScore score12 = new DocumentScore(2);
//		score12.setScore(12f);
//		DocumentScore score13 = new DocumentScore(3);
//		score13.setScore(13f);
//		map1.put(score11.getDocId(), score11);
//		map1.put(score12.getDocId(), score12);
//		map1.put(score13.getDocId(), score13);
//
//		Map<Integer, DocumentScore> map2 = new HashMap<Integer, DocumentScore>();
//		DocumentScore score21 = new DocumentScore(1);
//		score21.setScore(21f);
//		DocumentScore score22 = new DocumentScore(2);
//		score22.setScore(22f);
//		DocumentScore score23 = new DocumentScore(3);
//		score23.setScore(23f);
//		map2.put(score21.getDocId(), score21);
//		map2.put(score22.getDocId(), score22);
//		map2.put(score23.getDocId(), score23);
//
//		Map<Integer, DocumentScore> map3 = new HashMap<Integer, DocumentScore>();
//		DocumentScore score31 = new DocumentScore(1);
//		score31.setScore(31f);
//		DocumentScore score32 = new DocumentScore(2);
//		score32.setScore(32f);
//		DocumentScore score33 = new DocumentScore(3);
//		score33.setScore(33f);
//		map3.put(score31.getDocId(), score31);
//		map3.put(score32.getDocId(), score32);
//		map3.put(score33.getDocId(), score33);
//
//		List<Map<Integer, DocumentScore>> combinedMapList = new ArrayList<Map<Integer, DocumentScore>>();
//		combinedMapList.add(map1);
//		combinedMapList.add(map2);
//		combinedMapList.add(map3);
//
//		List<Double> weights = new ArrayList<Double>() {
//			{
//				add(2.0);
//				add(3.0);
//				add(4.0);
//			}
//		};
//
//		List<DocumentScore> result = RankerImpl.combineRankedListsWithWeights(
//				combinedMapList, weights);
//		assertEquals(3, result.get(0).getDocId());
//		assertEquals(227, Math.round(result.get(0).getScore())); // 13*2 + 23*3
//																	// + 33*4
//		assertEquals(2, result.get(1).getDocId());
//		assertEquals(218, Math.round(result.get(1).getScore())); // 12*2 + 22*3
//																	// + 32*4
//		assertEquals(1, result.get(2).getDocId());
//		assertEquals(209, Math.round(result.get(2).getScore())); // 11*2 + 21*3
//																	// + 31*4
//	}
//		
//	@Test
//	public void testCombinationOfFeaturesAndRanking() {
//		List<String> query = Arrays.asList("some random query".split(" "));
//
//		String doc1 = "<html><body>" + "<h1>random</h1>"
//				+ "<meta name='' value='some random'>"
//				+ "<title>Some random</title>" + "<a href=''>query</a>"
//				+ "</body></html>";
//		DocumentScore score1 = new DocumentScore(1);
//		// feature for "some"
//		DocumentFeatures feat11 = new DocumentFeatures();
//		feat11.setTfidf(1.0f);
//		feat11.setHeaderCount(0);
//		feat11.setTotalCount(2);
//		feat11.setMetaTagCount(1);
//		feat11.setLinkCount(0);
//		Set<Integer> pos11 = new HashSet<>();
//		pos11.add(2);
//		pos11.add(4);
//		feat11.setPositions(pos11);
//		score1.addFeatures("some", feat11);
//		// feature for "random"
//		DocumentFeatures feat12 = new DocumentFeatures();
//		feat12.setTfidf(2.0f);
//		feat12.setHeaderCount(1);
//		feat12.setTotalCount(3);
//		feat12.setMetaTagCount(1);
//		feat12.setLinkCount(0);
//		Set<Integer> pos12 = new HashSet<>();
//		pos12.add(1);
//		pos12.add(3);
//		pos12.add(5);
//		feat12.setPositions(pos12);
//		score1.addFeatures("random", feat12);
//		// feature for "query"
//		DocumentFeatures feat13 = new DocumentFeatures();
//		feat13.setTfidf(3.0f);
//		feat13.setHeaderCount(0);
//		feat13.setTotalCount(1);
//		feat13.setMetaTagCount(0);
//		feat13.setLinkCount(1);
//		Set<Integer> pos13 = new HashSet<>();
//		pos13.add(6);
//		feat13.setPositions(pos13);
//		score1.addFeatures("query", feat13);
//		
//		
//		String doc2 = "<html><body>" + "<h1>random random</h1>"
//				+ "<meta name='' value='the query'>"
//				+ "<title>random query</title>" + "<a href=''>random query</a>"
//				+ "</body></html>";
//		DocumentScore score2 = new DocumentScore(2);
//		// no features for "some"
//
//		// feature for "random"
//		DocumentFeatures feat22 = new DocumentFeatures();
//		feat22.setTfidf(3.0f);
//		feat22.setHeaderCount(2);
//		feat22.setTotalCount(4);
//		feat22.setMetaTagCount(0);
//		feat22.setLinkCount(1);
//		Set<Integer> pos21 = new HashSet<>();
//		pos21.add(1);
//		pos21.add(2);
//		pos21.add(5);
//		pos21.add(7);
//		feat22.setPositions(pos21);
//		score2.addFeatures("random", feat22);
//		// feature for "query"
//		DocumentFeatures feat23 = new DocumentFeatures();
//		feat23.setTfidf(4.0f);
//		feat23.setHeaderCount(0);
//		feat23.setTotalCount(3);
//		feat23.setMetaTagCount(1);
//		feat23.setLinkCount(1);
//		Set<Integer> pos23 = new HashSet<>();
//		pos23.add(4);
//		pos23.add(6);
//		pos23.add(8);
//		feat23.setPositions(pos23);
//		score2.addFeatures("query", feat23);
//		List<DocumentScore> scoreList = new ArrayList<>();
//		scoreList.add(score1);
//		scoreList.add(score2);
//		
//		Map<String, Integer> wordDfs = new HashMap<String, Integer>();
//		wordDfs.put("some", 1);
//		wordDfs.put("random", 2);
//		wordDfs.put("query", 2);
//		int corpusSize = 13;
//		
//		Map<Integer, DocumentScore> tfIdf = RankerImpl.rankDocumentsOnTfIdf(scoreList, query, corpusSize, wordDfs);
//		Map<Integer, DocumentScore> header = RankerImpl.rankDocumentsOnHeaderCount(scoreList);
//		Map<Integer, DocumentScore> total = RankerImpl.rankDocumentsOnTotalCount(scoreList);
//		Map<Integer, DocumentScore> links = RankerImpl.rankDocumentsOnLinkCount(scoreList);
//		Map<Integer, DocumentScore> meta = RankerImpl.rankDocumentsOnMetaCount(scoreList);
//		Map<Integer, DocumentScore> queryPresence = RankerImpl.rankDocumentsOnQueryWordPresenceCount(scoreList, query);
//		Map<Integer, DocumentScore> positions = RankerImpl.rankDocumentsOnPosition(scoreList, query);
//		
//		List<DocumentScore> rankedScores = new ArrayList<>(tfIdf.values());
//		Collections.sort(rankedScores,
//				DocumentScoreComparators.getTfIdfComparator(query, corpusSize, wordDfs));
//		assertEquals(2, rankedScores.get(0).getDocId());
//		assertEquals(13, Math.round(rankedScores.get(0).getScore()));
//		assertEquals(1, rankedScores.get(1).getDocId());
//		assertEquals(12, Math.round(rankedScores.get(1).getScore()));
//		
//		rankedScores = new ArrayList<>(header.values());
//		Collections.sort(rankedScores,
//				DocumentScoreComparators.getHeaderCountComparator());
//		assertEquals(2, rankedScores.get(0).getDocId());
//		assertEquals(2, Math.round(rankedScores.get(0).getScore()));
//		assertEquals(1, rankedScores.get(1).getDocId());
//		assertEquals(1, Math.round(rankedScores.get(1).getScore()));
//		
//		rankedScores = new ArrayList<>(total.values());
//		Collections.sort(rankedScores,
//				DocumentScoreComparators.getTotalCountComparator());
//		assertEquals(2, rankedScores.get(0).getDocId());
//		assertEquals(7, Math.round(rankedScores.get(0).getScore()));
//		assertEquals(1, rankedScores.get(1).getDocId());
//		assertEquals(6, Math.round(rankedScores.get(1).getScore()));
//		
//		rankedScores = new ArrayList<>(links.values());
//		Collections.sort(rankedScores,
//				DocumentScoreComparators.getLinkCountsComparator());
//		assertEquals(2, rankedScores.get(0).getDocId());
//		assertEquals(2, Math.round(rankedScores.get(0).getScore()));
//		assertEquals(1, rankedScores.get(1).getDocId());
//		assertEquals(1, Math.round(rankedScores.get(1).getScore()));
//		
//		rankedScores = new ArrayList<>(meta.values());
//		Collections.sort(rankedScores,
//				DocumentScoreComparators.getMetaTagCountsComparator());
//		assertEquals(1, rankedScores.get(0).getDocId());
//		assertEquals(2, Math.round(rankedScores.get(0).getScore()));
//		assertEquals(2, rankedScores.get(1).getDocId());
//		assertEquals(1, Math.round(rankedScores.get(1).getScore()));
//		
//		rankedScores = new ArrayList<>(queryPresence.values());
//		Collections.sort(rankedScores,
//				DocumentScoreComparators.getQueryWordPresenceComparator(query));
//		assertEquals(1, rankedScores.get(0).getDocId());
//		assertEquals(3, Math.round(rankedScores.get(0).getScore()));
//		assertEquals(2, rankedScores.get(1).getDocId());
//		assertEquals(2, Math.round(rankedScores.get(1).getScore()));
//		
//		rankedScores = new ArrayList<>(positions.values());
//		Collections.sort(rankedScores,
//				DocumentScoreComparators.getPositionComparator(query));
//		assertEquals(1, rankedScores.get(0).getDocId());
//		assertEquals(2, Math.round(rankedScores.get(0).getScore()));
//		assertEquals(2, rankedScores.get(1).getDocId());
//		assertEquals(Integer.MAX_VALUE, Math.round(rankedScores.get(1).getScore()));
//	}
//
	
}

