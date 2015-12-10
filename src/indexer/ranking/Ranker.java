package indexer.ranking;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;
import indexer.rank.combinators.DocumentFeatureCombinators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import utils.Tuple;

public class Ranker {
	private static final Logger logger = Logger.getLogger(Ranker.class);

	public static List<DocumentScore> getDocumentScoresForQueryAndInvertedIndex(
			List<String> query,
			Map<String, List<DocumentFeatures>> invertedIndex) {
		Map<Integer, DocumentScore> documentRanks = new HashMap<Integer, DocumentScore>();

		for (String word : query) {
			List<DocumentFeatures> docs = invertedIndex.get(word);

			for (DocumentFeatures features : docs) {
				DocumentScore rankedDoc = documentRanks
						.get(features.getDocId());
				if (rankedDoc == null) {
					rankedDoc = new DocumentScore(word, features);
					documentRanks.put(features.getDocId(), rankedDoc);
				} else {
					rankedDoc.addFeatures(word, features);
				}
			}
		}
		List<DocumentScore> results = new ArrayList<DocumentScore>(
				documentRanks.values());
		return results;
	}

	public static Map<Integer, DocumentScore> rankDocumentsOnTfIdf(
			List<DocumentScore> documentList, List<String> query,
			int corpusSize, Map<String, Integer> wordDfs) {
		Map<Integer, DocumentScore> resultMap = new HashMap<Integer, DocumentScore>();
		int i = 0;
		for (DocumentScore score : documentList) {
			i++;
			if (i % 500 == 0) {
				logger.info("Ranked " + i + " documents");
			}
			// logger.info("Looking at " + score.getDocId());
			float scoreValue = DocumentFeatureCombinators.combineTfIdfs(
					score.getWordFeatures(), query, corpusSize, wordDfs);
			DocumentScore newScore = new DocumentScore(score.getDocId());

			newScore.setScore(scoreValue);
			newScore.setWordFeatures(score.getWordFeatures());
			resultMap.put(newScore.getDocId(), newScore);
			// logger.info("Setting scoreValue of " + newScore.getDocId() +
			// " to "
			// + scoreValue + ". Resultmap now " + resultMap);
		}
		return resultMap;
	}

	public static Map<Integer, DocumentScore> rankDocumentsOnTotalCount(
			List<DocumentScore> documentList) {
		Map<Integer, DocumentScore> resultMap = new HashMap<Integer, DocumentScore>();
		for (DocumentScore score : documentList) {
			// logger.info("Looking at " + score.getDocId());
			float scoreValue = DocumentFeatureCombinators
					.combineTotalCounts(score.getWordFeatures());
			DocumentScore newScore = new DocumentScore(score.getDocId());

			newScore.setScore(scoreValue);
			newScore.setWordFeatures(score.getWordFeatures());
			resultMap.put(newScore.getDocId(), newScore);
			// logger.info("Setting scoreValue of " + newScore.getDocId() +
			// " to "
			// + scoreValue + ". Resultmap now " + resultMap);
		}
		return resultMap;
	}

	public static Map<Integer, DocumentScore> rankDocumentsOnLinkCount(
			List<DocumentScore> documentList) {
		Map<Integer, DocumentScore> resultMap = new HashMap<Integer, DocumentScore>();
		for (DocumentScore score : documentList) {
			// logger.info("Looking at " + score.getDocId());
			float scoreValue = DocumentFeatureCombinators
					.combineLinkCounts(score.getWordFeatures());
			DocumentScore newScore = new DocumentScore(score.getDocId());

			newScore.setScore(scoreValue);
			newScore.setWordFeatures(score.getWordFeatures());
			resultMap.put(newScore.getDocId(), newScore);
			// logger.info("Setting scoreValue of " + newScore.getDocId() +
			// " to "
			// + scoreValue + ". Resultmap now " + resultMap);
		}
		return resultMap;
	}

	public static Map<Integer, DocumentScore> rankDocumentsOnMetaCount(
			List<DocumentScore> documentList) {
		Map<Integer, DocumentScore> resultMap = new HashMap<Integer, DocumentScore>();
		for (DocumentScore score : documentList) {
			// logger.info("Looking at " + score.getDocId());
			float scoreValue = DocumentFeatureCombinators
					.combineMetaTagCounts(score.getWordFeatures());
			DocumentScore newScore = new DocumentScore(score.getDocId());

			newScore.setScore(scoreValue);
			newScore.setWordFeatures(score.getWordFeatures());
			resultMap.put(newScore.getDocId(), newScore);
			// logger.info("Setting scoreValue of " + newScore.getDocId() +
			// " to "
			// + scoreValue + ". Resultmap now " + resultMap);
		}
		return resultMap;
	}

	public static Map<Integer, DocumentScore> rankDocumentsOnHeaderCount(
			List<DocumentScore> documentList) {
		Map<Integer, DocumentScore> resultMap = new HashMap<Integer, DocumentScore>();
		for (DocumentScore score : documentList) {
			// logger.info("Looking at " + score.getDocId());
			float scoreValue = DocumentFeatureCombinators
					.combineHeaderCounts(score.getWordFeatures());
			DocumentScore newScore = new DocumentScore(score.getDocId());

			newScore.setScore(scoreValue);
			newScore.setWordFeatures(score.getWordFeatures());
			resultMap.put(newScore.getDocId(), newScore);
			// logger.info("Setting scoreValue of " + newScore.getDocId() +
			// " to "
			// + scoreValue + ". Resultmap now " + resultMap);
		}
		return resultMap;
	}

	public static Map<Integer, DocumentScore> rankDocumentsOnQueryWordPresenceCount(
			List<DocumentScore> documentList, List<String> query) {
		Map<Integer, DocumentScore> resultMap = new HashMap<Integer, DocumentScore>();
		for (DocumentScore score : documentList) {
			// logger.info("Looking at " + score.getDocId());
			float scoreValue = DocumentFeatureCombinators
					.combineQueryWordPresenceCounts(score.getWordFeatures(),
							query);
			DocumentScore newScore = new DocumentScore(score.getDocId());

			newScore.setScore(scoreValue);
			newScore.setWordFeatures(score.getWordFeatures());
			resultMap.put(newScore.getDocId(), newScore);
			// logger.info("Setting scoreValue of " + newScore.getDocId() +
			// " to "
			// + scoreValue + ". Resultmap now " + resultMap);
		}
		return resultMap;
	}

	public static Map<Integer, DocumentScore> rankDocumentsOnPosition(
			List<DocumentScore> documentList, List<String> query) {
		List<Tuple<String>> consecutiveWordTuples = new ArrayList<Tuple<String>>();
		for (int i = 0; i < query.size()-1; i++) {
			Tuple<String> tuple = new Tuple<>(query.get(i), query.get(i+1));
			consecutiveWordTuples.add(tuple);
		}
		
		Map<Integer, DocumentScore> resultMap = new HashMap<Integer, DocumentScore>();
		for (DocumentScore score : documentList) {
			// logger.info("Looking at " + score.getDocId());
			float scoreValue = DocumentFeatureCombinators
					.combinePositions(score.getWordFeatures(),
							consecutiveWordTuples);
			DocumentScore newScore = new DocumentScore(score.getDocId());

			newScore.setScore(scoreValue);
			newScore.setWordFeatures(score.getWordFeatures());
			resultMap.put(newScore.getDocId(), newScore);
			// logger.info("Setting scoreValue of " + newScore.getDocId() +
			// " to "
			// + scoreValue + ". Resultmap now " + resultMap);
		}
		return resultMap;
		
	}

	public static List<DocumentScore> combineRankedListsWithWeights(
			List<Map<Integer, DocumentScore>> rankedLists, List<Double> weights) {
		Map<Integer, List<DocumentScore>> combinedRankedMap = new HashMap<Integer, List<DocumentScore>>();
		if (rankedLists.size() != weights.size()) {
			// logger.error("Ranked list size and weights size is not the same!");
			return null;
		}
		// combine into document : list of rankings in order
		// logger.info("Forming combined ranked map");
		Map<Integer, DocumentScore> rankedMap = rankedLists.get(0);
		for (Integer key : rankedMap.keySet()) {
			// logger.info("Forming combined ranked map for url " + key);
			combinedRankedMap.put(key, new ArrayList<DocumentScore>());
			for (int i = 0; i < rankedLists.size(); i++) {
				Map<Integer, DocumentScore> map = rankedLists.get(i);
				// logger.info("Looking for document score in " + i +
				// "th map and found "+ map);
				combinedRankedMap.get(key).add(map.get(key));
			}
		}
		// logger.info("CombinedRankedMap is " + combinedRankedMap);
		List<DocumentScore> resultList = new ArrayList<DocumentScore>();
		for (Entry<Integer, List<DocumentScore>> entry : combinedRankedMap
				.entrySet()) {
			float score = 0.0f;

			for (int i = 0; i < entry.getValue().size(); i++) {
				// logger.info("Currently for url " + entry.getKey() +
				// " multiplying score "
				// + entry.getValue().get(i).getScore() + " * " +
				// weights.get(i));
				score += (entry.getValue().get(i).getScore() * weights.get(i));
			}
			DocumentScore docScore = new DocumentScore(entry.getKey());
			docScore.setScore(score);
			// logger.info("Setting score for " + entry.getKey() + " to score:"
			// + score);
			resultList.add(docScore);
		}
		Collections.sort(resultList);
		return resultList;
	}
}
