package indexer.ranking;

import indexer.DocumentScore;
import indexer.dao.DocumentFeatures;
import indexer.rank.combinators.DocumentFeatureCombinators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class Ranker {
	private static final Logger logger = Logger.getLogger(Ranker.class);
	
	public static List<DocumentScore> getDocumentScoresForQueryAndInvertedIndex(
			List<String> query, Map<String, List<DocumentFeatures>> invertedIndex) {
		Map<String, DocumentScore> documentRanks = new HashMap<String, DocumentScore>();

		for (String word : query) {
			List<DocumentFeatures> docs = invertedIndex.get(word);

			for (DocumentFeatures features : docs) {
				DocumentScore rankedDoc = documentRanks.get(features.getUrl());
				if (rankedDoc == null) {
					rankedDoc = new DocumentScore(word, features);
					documentRanks.put(features.getUrl(), rankedDoc);
				} else {
					rankedDoc.addFeatures(word, features);
				}
			}
		}
		List<DocumentScore> results = new ArrayList<DocumentScore>(
				documentRanks.values());
		return results;
	}

	public static Map<String, DocumentScore> rankDocumentsOnTfIdf(
			List<DocumentScore> documentList, List<String> query,
			int corpusSize, Map<String, Integer> wordDfs) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Map<String, DocumentScore> resultList = new HashMap<String, DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
			score.setScore(DocumentFeatureCombinators.combineTfIdfs(score
					.getWordFeatures(), query, corpusSize, wordDfs));
			resultList.put(score.getUrl(), score);
		}
		return resultList;
	}

	public static Map<String, DocumentScore> rankDocumentsOnTotalCount(
			List<DocumentScore> documentList) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Map<String, DocumentScore> resultList = new HashMap<String, DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
//			logger.info("Setting scores for " + score.getUrl());
			score.setScore(DocumentFeatureCombinators.combineTotalCounts(score
					.getWordFeatures()));
			resultList.put(score.getUrl(), score);
		}
		return resultList;
	}

	public static Map<String, DocumentScore> rankDocumentsOnLinkCount(
			List<DocumentScore> documentList) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Map<String, DocumentScore> resultList = new HashMap<String, DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
			score.setScore(DocumentFeatureCombinators.combineLinkCounts(score
					.getWordFeatures()));
			resultList.put(score.getUrl(), score);
		}
		return resultList;
	}

	public static Map<String, DocumentScore> rankDocumentsOnMetaCount(
			List<DocumentScore> documentList) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Map<String, DocumentScore> resultList = new HashMap<String, DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
			score.setScore(DocumentFeatureCombinators
					.combineMetaTagCounts(score.getWordFeatures()));
			resultList.put(score.getUrl(), score);
		}
		return resultList;
	}

	public static Map<String, DocumentScore> rankDocumentsOnHeaderCount(
			List<DocumentScore> documentList) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Map<String, DocumentScore> resultList = new HashMap<String, DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
			score.setScore(DocumentFeatureCombinators.combineHeaderCounts(score
					.getWordFeatures()));
			resultList.put(score.getUrl(), score);
		}
		return resultList;
	}
	
	public static Map<String, DocumentScore> rankDocumentsOnQueryWordPresenceCount(
			List<DocumentScore> documentList, List<String> query) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Map<String, DocumentScore> resultList = new HashMap<String, DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
			score.setScore(DocumentFeatureCombinators.combineQueryWordPresenceCounts(score
					.getWordFeatures(), query));
			resultList.put(score.getUrl(), score);
		}
		return resultList;
	}
	
	public static List<DocumentScore> combineRankedListsWithWeights(List<Map<String, DocumentScore>> rankedLists,
			List<Double> weights) {
		Map<String, List<DocumentScore>> combinedRankedMap = new HashMap<String, List<DocumentScore>>();
		if(rankedLists.size() != weights.size()) {
//			logger.error("Ranked list size and weights size is not the same!");
			return null;
		}
		// combine into document : list of rankings in order
//		logger.info("Forming combined ranked map");
		Map<String, DocumentScore> rankedMap = rankedLists.get(0);
		for(String key : rankedMap.keySet()) {
//			logger.info("Forming combined ranked map for url " + key);
			combinedRankedMap.put(key, new ArrayList<DocumentScore>());
			for(int i=0;i<rankedLists.size();i++) {
				Map<String, DocumentScore> map = rankedLists.get(i);
//				logger.info("Looking for document score in " + i + "th map and found "+ map);
				combinedRankedMap.get(key).add(map.get(key));
			}
		}
//		logger.info("CombinedRankedMap is " + combinedRankedMap);
		List<DocumentScore> resultList = new ArrayList<DocumentScore>();
		for(Entry<String, List<DocumentScore>> entry : combinedRankedMap.entrySet()) {
			float score = 0.0f;
			
			for(int i=0;i<entry.getValue().size();i++) {
//				logger.info("Currently for url " + entry.getKey() + " multiplying score "
//			+ entry.getValue().get(i).getScore() + " * " + weights.get(i));
				score += (entry.getValue().get(i).getScore() * weights.get(i));
			}
			DocumentScore docScore = new DocumentScore(entry.getKey());
			docScore.setScore(score);
//			logger.info("Setting score for " + entry.getKey() + " to score:" + score);
			resultList.add(docScore);
		}
		Collections.sort(resultList);
		return resultList;
	}
}
