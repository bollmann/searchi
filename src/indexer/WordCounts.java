package indexer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WordCounts implements Iterable<String> {
	private Map<String, Integer> wordCounts;
	private String maxWord;
	
	public WordCounts(Iterable<String> words) {
		wordCounts = new HashMap<String, Integer>();
		maxWord = null;
		
		for(String word: words) {
			Integer counts = wordCounts.get(word);
			if (counts == null)
				wordCounts.put(word, 1);
			else
				wordCounts.put(word, counts + 1);

			// update maxWord, if necessary:
			if (maxWord == null || wordCounts.get(maxWord) < wordCounts.get(word))
				maxWord = word;			
		}
	}
	
	public WordCounts(WordCounts other) {
		this.wordCounts = new HashMap<String, Integer>(other.wordCounts);
		this.maxWord = (other.maxWord == null ? null : new String(other.maxWord));
	}
	
	/** Adding word counts together. */ 
	public WordCounts addCounts(WordCounts other) {
		for(String word: other.wordCounts.keySet()) {
			if(this.wordCounts.containsKey(word))
				this.wordCounts.put(word, this.wordCounts.get(word) + other.wordCounts.get(word));
			else
				this.wordCounts.put(word, other.wordCounts.get(word));
			
			if(maxWord == null || wordCounts.get(maxWord) < wordCounts.get(word))
				maxWord = word;			
		}
		return this;
	}
	
	/** Calculate TF for a word */
	public double getMaximumTermFrequency(String word) {
		double alpha = 0.5;
		return alpha + (1 - alpha) * ((double) wordCounts.get(word) / wordCounts.get(maxWord));
	}
	/** Get Euclidean Term Freq of a word */
	public double getEuclideanTermFrequency(String word) {
		int docSize = 0;
		for(String w: wordCounts.keySet())
			docSize += Math.pow(wordCounts.get(w), 2.0);
		return getCounts(word) / Math.sqrt((double) docSize);
	}
	
	/** Get counts of a word */
	public int getCounts(String word) {
		if(wordCounts.containsKey(word))
			return wordCounts.get(word);
		else
			return 0;
	}
	
	/** Get TF-IDF scores dfor a word - document */
	public double getTFIDF(String word, int corpusSize, int df) {
		return this.getMaximumTermFrequency(word) * Math.log((double) corpusSize / df);
	}

	@Override
	public Iterator<String> iterator() {
		return wordCounts.keySet().iterator();
	}
	
	public Map<String, Integer> getCounts() {
		return wordCounts;
	}
	
	public String getMaxWord() {
		return maxWord;
	}
}
