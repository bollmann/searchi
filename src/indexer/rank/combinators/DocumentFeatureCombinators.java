package indexer.rank.combinators;

import indexer.WordCounts;
import indexer.dao.DocumentFeatures;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

public class DocumentFeatureCombinators {
	private static final Logger logger = Logger
			.getLogger(DocumentFeatureCombinators.class);

	public static float combineTfIdfs(
			Map<String, DocumentFeatures> wordFeatures, List<String> query,
			int corpusSize, Map<String, Integer> wordDfs) {
		float result = 0.0f;
		WordCounts queryCounts = new WordCounts(query);
		for (String queryWord : query) {
			DocumentFeatures feature = wordFeatures.get(queryWord);
			logger.info("Combiner looking for word:" + queryWord + " and got feature:" + feature);
			if (feature != null) {
				
				double queryWeight = queryCounts.getTFIDF(queryWord,
						corpusSize, wordDfs.get(queryWord));
//				double docWeight = feature.getEuclideanTermFrequency();
				
				double docWeight = feature.getTfidf();
				
				result += queryWeight * docWeight;
				logger.info("queryWeight is " + queryWeight + " and docweight is tfidf=" + docWeight + " and result is " + result);
			}
		}
		return result;
	}

	public static int combineTotalCounts(
			Map<String, DocumentFeatures> wordFeatures) {
		int result = 0;
		for (Entry<String, DocumentFeatures> entry : wordFeatures.entrySet()) {
			// logger.info("Adding total count:" +
			// entry.getValue().getTotalCount() + " for word " +
			// entry.getKey());
			result += entry.getValue().getTotalCount();
		}
		return result;
	}

	public static int combineHeaderCounts(
			Map<String, DocumentFeatures> wordFeatures) {
		int result = 0;
		for (Entry<String, DocumentFeatures> entry : wordFeatures.entrySet()) {
			result += entry.getValue().getHeaderCount();
		}
		return result;
	}

	public static int combineLinkCounts(
			Map<String, DocumentFeatures> wordFeatures) {
		int result = 0;
		for (Entry<String, DocumentFeatures> entry : wordFeatures.entrySet()) {
			result += entry.getValue().getLinkCount();
		}
		return result;
	}

	public static int combineMetaTagCounts(
			Map<String, DocumentFeatures> wordFeatures) {
		int result = 0;
		for (Entry<String, DocumentFeatures> entry : wordFeatures.entrySet()) {
			result += entry.getValue().getMetaTagCount();
		}
		return result;
	}

	public static Map<String, Set<Integer>> combinePositions(
			Map<String, DocumentFeatures> wordFeatures) {
		Map<String, Set<Integer>> result = new HashMap<String, Set<Integer>>();
		for (Entry<String, DocumentFeatures> entry : wordFeatures.entrySet()) {
			result.put(entry.getKey(), entry.getValue().getPositions());
		}
		return result;
	}
	
	public static int combineQueryWordPresenceCounts(
			Map<String, DocumentFeatures> wordFeatures, List<String> query) {
		return wordFeatures.size();
	}

}
