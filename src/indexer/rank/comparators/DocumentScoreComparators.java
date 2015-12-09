package indexer.rank.comparators;

import indexer.DocumentScore;
import indexer.rank.combinators.DocumentFeatureCombinators;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class DocumentScoreComparators {
	private static final Logger logger = Logger
			.getLogger(DocumentScoreComparators.class);

	public static Comparator<DocumentScore> getTfIdfComparator(
			final List<String> query, final int corpusSize,
			final Map<String, Integer> wordDfs) {
		Comparator<DocumentScore> comparator = new Comparator<DocumentScore>() {
			@Override
			public int compare(DocumentScore o1, DocumentScore o2) {
				float tfIdf1 = DocumentFeatureCombinators.combineTfIdfs(
						o1.getWordFeatures(), query, corpusSize, wordDfs);
				float tfIdf2 = DocumentFeatureCombinators.combineTfIdfs(
						o2.getWordFeatures(), query, corpusSize, wordDfs);
				return (-1) * Float.compare(tfIdf1, tfIdf2);
			}

		};
		return comparator;
	}

	public static Comparator<DocumentScore> getTotalCountComparator() {
		Comparator<DocumentScore> comparator = new Comparator<DocumentScore>() {
			@Override
			public int compare(DocumentScore o1, DocumentScore o2) {
				// logger.info("Finding total count for " + o1.getUrl());
				float totalCount1 = DocumentFeatureCombinators
						.combineTotalCounts(o1.getWordFeatures());
				// logger.info("Finding total count for " + o2.getUrl());
				float totalCount2 = DocumentFeatureCombinators
						.combineTotalCounts(o2.getWordFeatures());
				return (-1) * Float.compare(totalCount1, totalCount2);
			}

		};
		return comparator;
	}

	public static Comparator<DocumentScore> getHeaderCountComparator() {
		Comparator<DocumentScore> comparator = new Comparator<DocumentScore>() {
			@Override
			public int compare(DocumentScore o1, DocumentScore o2) {
				float headerCount1 = DocumentFeatureCombinators
						.combineHeaderCounts(o1.getWordFeatures());
				float headerCount2 = DocumentFeatureCombinators
						.combineHeaderCounts(o2.getWordFeatures());
				return (-1) * Float.compare(headerCount1, headerCount2);
			}

		};
		return comparator;
	}

	public static Comparator<DocumentScore> getLinkCountsComparator() {
		Comparator<DocumentScore> comparator = new Comparator<DocumentScore>() {
			@Override
			public int compare(DocumentScore o1, DocumentScore o2) {
				float linkCount1 = DocumentFeatureCombinators
						.combineLinkCounts(o1.getWordFeatures());
				float linkCount2 = DocumentFeatureCombinators
						.combineLinkCounts(o2.getWordFeatures());
				return (-1) * Float.compare(linkCount1, linkCount2);
			}
		};
		return comparator;
	}

	public static Comparator<DocumentScore> getMetaTagCountsComparator() {
		Comparator<DocumentScore> comparator = new Comparator<DocumentScore>() {
			@Override
			public int compare(DocumentScore o1, DocumentScore o2) {
				float metaTagCount1 = DocumentFeatureCombinators
						.combineMetaTagCounts(o1.getWordFeatures());
				float metaTagCount2 = DocumentFeatureCombinators
						.combineMetaTagCounts(o2.getWordFeatures());
				return (-1) * Float.compare(metaTagCount1, metaTagCount2);
			}

		};
		return comparator;
	}
	
	public static Comparator<DocumentScore> getQueryWordPresenceComparator(final List<String> query) {
		Comparator<DocumentScore> comparator = new Comparator<DocumentScore>() {
			@Override
			public int compare(DocumentScore o1, DocumentScore o2) {
				float metaTagCount1 = DocumentFeatureCombinators
						.combineQueryWordPresenceCounts(o1.getWordFeatures(), query);
				float metaTagCount2 = DocumentFeatureCombinators
						.combineQueryWordPresenceCounts(o2.getWordFeatures(), query);
				return (-1) * Float.compare(metaTagCount1, metaTagCount2);
			}

		};
		return comparator;
	}

	// public static int getPairwisePositions(List<Integer> queryPositions) {
	// StringUtils
	// }

	// TODO incomplete!
	public static Comparator<DocumentScore> getPositionComparator(
			List<Integer> queryPositions) {
		int positionDifferences;
		Comparator<DocumentScore> comparator = new Comparator<DocumentScore>() {
			@Override
			public int compare(DocumentScore o1, DocumentScore o2) {
				Map<String, Set<Integer>> positionMap1 = DocumentFeatureCombinators
						.combinePositions(o1.getWordFeatures());
				Map<String, Set<Integer>> positionMap2 = DocumentFeatureCombinators
						.combinePositions(o2.getWordFeatures());
				// TODO Auto-generated method stub
				return (-1) * Float.compare(1, 2);
			}

		};
		return comparator;
	}

}
