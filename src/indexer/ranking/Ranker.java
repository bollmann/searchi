package indexer.ranking;

import indexer.DocumentScore;
import indexer.dao.DocumentFeatures;
import indexer.rank.combinators.DocumentFeatureCombinators;
import indexer.rank.comparators.DocumentScoreComparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class Ranker {
	private static final Logger logger = Logger.getLogger(Ranker.class);
	
	public static List<DocumentScore> getDocumentScoresForQueryAndInvertedIndex(
			List<String> query, Map<String, List<DocumentFeatures>> invertedIndex) {
		Map<String, DocumentScore> documentRanks = new HashMap<String, DocumentScore>();

		for (String word : query) {
			// TODO: optimize based on different table
			// layout, multi-thread requests, etc.
//			InvertedIndex ii = new InvertedIndex();
//			List<InvertedIndexRow> rows = ii.getDocumentLocations(word);
//			List<DocumentFeatures> docs = new ArrayList<DocumentFeatures>();
//			for (InvertedIndexRow row : rows) {
//				docs.addAll(row.getFeatures());
//			}
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

	public static List<DocumentScore> rankDocumentsOnTfIdf(
			List<DocumentScore> documentList, List<String> query,
			int corpusSize, Map<String, Integer> wordDfs) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Collections.sort(documentListCopy,
				DocumentScoreComparators.getTfIdfComparator(query, corpusSize, wordDfs));
		List<DocumentScore> resultList = new ArrayList<DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
			score.setScore(DocumentFeatureCombinators.combineTfIdfs(score
					.getWordFeatures(), query, corpusSize, wordDfs));
			resultList.add(score);
		}
		return resultList;
	}

	public static List<DocumentScore> rankDocumentsOnTotalCount(
			List<DocumentScore> documentList) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Collections.sort(documentListCopy,
				DocumentScoreComparators.getTotalCountComparator());
		List<DocumentScore> resultList = new ArrayList<DocumentScore>();
		
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
//			logger.info("Setting scores for " + score.getUrl());
			score.setScore(DocumentFeatureCombinators.combineTotalCounts(score
					.getWordFeatures()));
			resultList.add(score);
		}
		return resultList;
	}

	public static List<DocumentScore> rankDocumentsOnLinkCount(
			List<DocumentScore> documentList) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Collections.sort(documentListCopy,
				DocumentScoreComparators.getLinkCountsComparator());
		List<DocumentScore> resultList = new ArrayList<DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
			score.setScore(DocumentFeatureCombinators.combineLinkCounts(score
					.getWordFeatures()));
			resultList.add(score);
		}
		return resultList;
	}

	public static List<DocumentScore> rankDocumentsOnMetaCount(
			List<DocumentScore> documentList) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Collections.sort(documentListCopy,
				DocumentScoreComparators.getMetaTagCountsComparator());
		List<DocumentScore> resultList = new ArrayList<DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
			score.setScore(DocumentFeatureCombinators
					.combineMetaTagCounts(score.getWordFeatures()));
			resultList.add(score);
		}
		return resultList;
	}

	public static List<DocumentScore> rankDocumentsOnHeaderCount(
			List<DocumentScore> documentList) {
		List<DocumentScore> documentListCopy = new ArrayList<DocumentScore>(
				documentList);
		Collections.sort(documentListCopy,
				DocumentScoreComparators.getHeaderCountComparator());
		List<DocumentScore> resultList = new ArrayList<DocumentScore>();
		for (int i = 0; i < documentListCopy.size(); i++) {
			DocumentScore score = documentListCopy.get(i);
			score.setScore(DocumentFeatureCombinators.combineHeaderCounts(score
					.getWordFeatures()));
			resultList.add(score);
		}
		return resultList;
	}
}
