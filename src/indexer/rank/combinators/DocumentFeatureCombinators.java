package indexer.rank.combinators;

import indexer.WordCounts;
import indexer.dao.DocumentFeatures;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import utils.Tuple;

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
//			logger.info("Combiner looking for word:" + queryWord + " and got feature:" + feature);
			if (feature != null) {
				
				double queryWeight = queryCounts.getTFIDF(queryWord,
						corpusSize, wordDfs.get(queryWord));
//				double docWeight = feature.getEuclideanTermFrequency();
				
				double docWeight = feature.getTfidf();
				
				result += queryWeight * docWeight;
//				logger.info("queryWeight is " + queryWeight + " and docweight is tfidf=" + docWeight + " and result is " + result);
			}
		}
		return result;
	}

	public static int combineTotalCounts(
			Map<String, DocumentFeatures> wordFeatures) {
		int result = 0;
		for (Entry<String, DocumentFeatures> entry : wordFeatures.entrySet()) {
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

	public static int combinePositions(
			Map<String, DocumentFeatures> wordFeatures, List<Tuple<String>> consecutiveWordTuples) {
		int result = 0;
		
		for(Tuple<String> queryTuple : consecutiveWordTuples) {
			// go through the tuples (a, b) which are at a distance of 1 from each other
			// look for a and b in the features. if any one of them is absent, return max int
			// if both are present, pairwise compare and find the minimum difference tuple of positions of
			// a and b
			DocumentFeatures feat1 = wordFeatures.get(queryTuple.getFirst());
			DocumentFeatures feat2 = wordFeatures.get(queryTuple.getSecond());
			int minDifference = Integer.MAX_VALUE;
			if(feat1 == null || feat2 == null) {
				return minDifference;
			}
			
			for(int posA : feat1.getPositions()) {
				for(int posB : feat2.getPositions()) {
					int diff = Math.abs(posA - posB);
					if(diff < minDifference) {
						minDifference  = diff;
					}
				}
			}
			result += minDifference;
		}
		return result;
	}
	
	public static int combineQueryWordPresenceCounts(
			Map<String, DocumentFeatures> wordFeatures, List<String> query) {
		return wordFeatures.size();
	}

}
