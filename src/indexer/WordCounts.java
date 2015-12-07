package indexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class WordCounts implements Iterable<String> {
	private Map<String, Integer> wordCounts;
	private Map<String, Set<Integer>> wordPos;
	private Map<Integer, Set<String> > nGrams;
	private Map<Integer, Integer> nGramCounts;
	private Map<Integer, String> nGramMaxWords;
	
	public WordCounts (Iterable<String> words) {
		initCounts(words, 1);
	}
	
	public WordCounts (Iterable<String> words, int maxN) {
		initCounts(words, maxN);
	}
	
	public void initCounts(Iterable<String> words, int maxN) {
		wordCounts = new HashMap<>();
		wordPos = new HashMap<>();
		nGrams = new HashMap<>();
		nGramCounts = new HashMap<>();
		nGramMaxWords = new HashMap<>();
		
		int index = 0;
		for(String word: words) {
			Integer counts = wordCounts.get(word);
			if (counts == null)
				wordCounts.put(word, 1);
			else
				wordCounts.put(word, counts + 1);
			
			if (!wordPos.containsKey(word)) {
				wordPos.put(word, new HashSet<Integer>());
			}
			if (wordPos.get(word).size() <= 5) {
				wordPos.get(word).add((int) (index / maxN) + 1); 
			}
			
			int nValue = getNValForNGram(word);
			if (nGrams.get(nValue) == null) {
				nGrams.put(nValue, new HashSet<String>());
			}
			nGrams.get(nValue).add(word);
			
			Integer nCnts = nGramCounts.get(nValue);
			if (nCnts == null) {
				nGramCounts.put(nValue, 1);
			}
			else {
				nGramCounts.put(nValue, nCnts + 1);
			}

			// update maxWord, if necessary:
			String maxNWord = nGramMaxWords.get(nValue);
			if (maxNWord == null || wordCounts.get(maxNWord) < wordCounts.get(word))
				nGramMaxWords.put(nValue, word);
			
			index++;
		}
	}
	
	public WordCounts(WordCounts other) {
		this.wordCounts = new HashMap<String, Integer>(other.wordCounts);
		for (String key : other.wordPos.keySet()) {
			this.wordPos.put(key, new HashSet<>(other.wordPos.get(key)));
		}		
		this.nGramCounts = new HashMap<>(other.nGramCounts);
		this.nGramMaxWords = new HashMap<>(other.nGramMaxWords);
		
	}
	
	/** Adding word counts together. */ 
	public WordCounts addCounts(WordCounts other) {
		for(String word: other.wordCounts.keySet()) {
			if(this.wordCounts.containsKey(word))
				this.wordCounts.put(word, this.wordCounts.get(word) + other.wordCounts.get(word));
			else
				this.wordCounts.put(word, other.wordCounts.get(word));
			
			// Doesn't include other's word pos
			
			int nValue = getNValForNGram(word);
			Integer nCnts = nGramCounts.get(nValue);
			if (nCnts == null) {
				nGramCounts.put(nValue, other.wordCounts.get(word));
			}
			else {
				nGramCounts.put(nValue, nCnts + other.wordCounts.get(word));
			}
			
			String maxNWord = nGramMaxWords.get(nValue);
			if (maxNWord == null || wordCounts.get(maxNWord) < wordCounts.get(word))
				nGramMaxWords.put(nValue, word);
		}
		return this;
	}
	
	public Set<Integer> getPosition(String word) {
		if (!wordPos.containsKey(word)) {
			return new HashSet<>();
		}
		return wordPos.get(word);
	}
	
	/** Calculate TF for a word */
	public double getMaximumTermFrequency(String word) {
		double alpha = 0.5;
		int nValue = getNValForNGram(word);
		return alpha + (1 - alpha) * 
			((double) wordCounts.get(word) / wordCounts.get(nGramMaxWords.get(nValue)));
	}
	
	/** Get Euclidean Term Freq of a word */
	public double getEuclideanTermFrequency(String word) {
		int nValue = getNValForNGram(word);
		
		int docSize = 0;		
		for (String nGram : nGrams.get(nValue)) {
			docSize += Math.pow(wordCounts.get(nGram), 2.0);
		}		
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
		return getMaxNWord(1);
	}
	
	public String getMaxNWord(int nValue) {
		return nGramMaxWords.get(nValue);
	}
	
	private int getNValForNGram(String word) {
		return word.split(" ").length;
	}
}
