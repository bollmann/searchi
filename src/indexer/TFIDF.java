package indexer;

import java.util.HashMap;
import java.util.Map;

public class TFIDF {
	private enum ComputationType { TF, TFIDF };
	
	private ComputationType type;
	private Map<String, Double> wordCounts;
	private int corpusSize;
	private Map<String, Integer> dfs;
	
	private TFIDF() {
		this.type = ComputationType.TF;
		this.wordCounts = new HashMap<String, Double>();
	}
	
	private TFIDF(int corpusSize, Map<String, Integer> dfs) {
		this.type = ComputationType.TFIDF;
		this.wordCounts = new HashMap<String, Double>();

		this.corpusSize = corpusSize;
		this.dfs = dfs;
	}
	
	private Map<String, Double> compute(Iterable<String> words) {
		String maxWord = null;
		
		for(String word: words) {
			Double counts = wordCounts.get(word);

			if (counts == null)
				wordCounts.put(word, 1.0);
			else
				wordCounts.put(word, counts + 1.0);

			// update maxWord, if necessary:
			if (maxWord == null || wordCounts.get(maxWord) < wordCounts.get(word))
				maxWord = word;
		}
		
		for(String word: wordCounts.keySet()) {
			if (type == ComputationType.TFIDF)
				wordCounts.put(word, getTFIDF(word, maxWord));
			else
				wordCounts.put(word, getTF(word, maxWord));
		}
		return wordCounts;
	}
	
	private double getTFIDF(String word, String maxWord) {
		return getTF(word, maxWord) * Math.log((double) corpusSize / dfs.get(word));
	}
	
	private double getTF(String word, String maxWord) {
		double alpha = 0.5; // TODO: tune this parameter!
		return alpha + (1 - alpha) * (wordCounts.get(word) / wordCounts.get(maxWord));
	}
	
	public static Map<String, Double> computeTFIDFs(Iterable<String> words, int corpusSize, Map<String, Integer> dfs) {
		TFIDF tfidf = new TFIDF(corpusSize, dfs);
		return tfidf.compute(words);
	}
	
	public static Map<String, Double> computeTFs(Iterable<String> words) {
		TFIDF tfidf = new TFIDF();
		return tfidf.compute(words);
	}
}