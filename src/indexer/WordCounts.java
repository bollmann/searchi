package indexer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import utils.nlp.Dictionary;

// TODO -- Break into modular classes
public class WordCounts implements Iterable<String> {
	private final Logger logger = Logger.getLogger(getClass());
	
	private Map<String, Integer> wordCounts;
	private Map<String, Set<Integer>> wordPos;
	private Map<Integer, Set<String>> nGrams;
	private Map<Integer, Integer> nGramCounts;
	private Map<Integer, String> nGramMaxWords;
	private Map<Integer, Integer> nGramNormalizedDocSize;
	
	private Dictionary dict;
	
	private int totalWords; 
	private int totalWordsEnglish;
	private int nGramSize;

	
	public WordCounts(Iterable<String> words) {
		this.dict = null;
		initCounts(words, 1);
	}

	public WordCounts(Iterable<String> words, int nValue) {
		this.dict = null;
		initCounts(words, nValue);
	}
	
	public WordCounts(Iterable<String> words, int nValue, 
		Dictionary dict) {
		this.dict = dict;
		initCounts(words, nValue);
	}
	

	private void initCounts(Iterable<String> words, int nValue) {
		wordCounts = new HashMap<>();
		wordPos = new HashMap<>();
		nGrams = new HashMap<>();
		nGramCounts = new HashMap<>();
		nGramMaxWords = new HashMap<>();
		nGramNormalizedDocSize = new HashMap<>();
		
		totalWords = 0;
		totalWordsEnglish = 0;
		nGramSize = nValue;
		
		int index = 0;
		for (String word : words) {
			totalWords++;
			if (nGramSize == 1 && dict != null && dict.contains(word))
				totalWordsEnglish++;
			
			Integer counts = wordCounts.get(word);
			if (counts == null)
				wordCounts.put(word, 1);
			else
				wordCounts.put(word, counts + 1);

			if (!wordPos.containsKey(word))
				wordPos.put(word, new HashSet<Integer>());

			if (wordPos.get(word).size() <= 5)
				wordPos.get(word).add(index  + 1);

			//int nValue = getNGramSize(word);
			if (nGrams.get(nGramSize) == null)
				nGrams.put(nGramSize, new HashSet<String>());
			nGrams.get(nGramSize).add(word);

			Integer nCnts = nGramCounts.get(nGramSize);
			if (nCnts == null) {
				nGramCounts.put(nGramSize, 1);
			} else {
				nGramCounts.put(nGramSize, nCnts + 1);
			}

			// update maxWord, if necessary:
			String maxNWord = nGramMaxWords.get(nGramSize);
			if (maxNWord == null
					|| wordCounts.get(maxNWord) < wordCounts.get(word))
				nGramMaxWords.put(nGramSize, word);

			index++;
		}
	}

	public WordCounts(WordCounts other) {
		this.wordCounts = new HashMap<String, Integer>(other.wordCounts);
		for (String key : other.wordPos.keySet()) {
			this.wordPos.put(key, new HashSet<>(other.wordPos.get(key)));
		}
		this.nGrams = new HashMap<>(other.nGrams);
		this.nGramCounts = new HashMap<>(other.nGramCounts);
		this.nGramMaxWords = new HashMap<>(other.nGramMaxWords);
		this.nGramNormalizedDocSize = new HashMap<>(other.nGramNormalizedDocSize);
		this.totalWords = other.totalWords;
		this.totalWordsEnglish = other.totalWordsEnglish;
	}

	/** Adding word counts together. */
	public WordCounts addCounts(WordCounts other) {
		this.totalWords += other.totalWords;
		this.totalWordsEnglish += other.totalWordsEnglish;
		
		for (String word : other.wordCounts.keySet()) {
			if (this.wordCounts.containsKey(word))
				this.wordCounts.put(word, this.wordCounts.get(word)
						+ other.wordCounts.get(word));
			else
				this.wordCounts.put(word, other.wordCounts.get(word));

			// Doesn't include other's word pos

			int nValue = getNGramSize(word);
			if (nGrams.get(nValue) == null) {
				nGrams.put(nValue, new HashSet<String>());
			}
			nGrams.get(nValue).add(word);

			Integer nCnts = nGramCounts.get(nValue);
			if (nCnts == null) {
				nGramCounts.put(nValue, other.wordCounts.get(word));
			} else {
				nGramCounts.put(nValue, nCnts + other.wordCounts.get(word));
			}

			String maxNWord = nGramMaxWords.get(nValue);
			if (maxNWord == null
					|| wordCounts.get(maxNWord) < wordCounts.get(word))
				nGramMaxWords.put(nValue, word);
		}
		return this;
	}
	
	/** Computes normalized doc sizes for all ngrams
	 *  for computing getEuclideanTermFrequency	 */
	public void computeNormalDocSizes() {
		for (int nValue : nGramCounts.keySet()) {
			Set<String> ngrams = nGrams.get(nValue);
			int docSize = 0;
			for (String nGram : ngrams) {
				docSize += Math.pow(wordCounts.get(nGram), 2.0);
			}
			nGramNormalizedDocSize.put(nValue,docSize);
		}		
	}

	public Set<Integer> getPosition(String word) {
		if (!wordPos.containsKey(word)) {
			return new HashSet<>();
		}
		return wordPos.get(word);
	}

	/** Calculate TF for a word */
	public float getMaximumTermFrequency(String word) {
		float alpha = 0.5F;
		//int nValue = getNGramSize(word);
		
		float tf = ((float) wordCounts.get(word) / wordCounts.get(nGramMaxWords.get(nGramSize)));
		return alpha + (1 - alpha) * tf; 
	}

	/** Get Euclidean Term Freq of a word */
	public float getEuclideanTermFrequency(String word) {
		//int nValue = getNGramSize(word);
		
		int docSize = nGramNormalizedDocSize.get(nGramSize);
		return (float) (getCounts(word) / Math.sqrt((double) docSize));
	}

	/** Get counts of a word */
	public int getCounts(String word) {
		if (wordCounts.containsKey(word))
			return wordCounts.get(word);
		else
			return 0;
	}

	/** Get TF-IDF scores for a word - document */
	public double getTFIDF(String word, int corpusSize, int df) {
		return this.getMaximumTermFrequency(word)
				* Math.log((double) corpusSize / df);
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

	/** Percentage of English words in counts */
	public float getPercentage() {
		if(dict == null)
			throw new IllegalStateException("WordCounts: no dictionary present!");
		
		if (nGramSize == 1)
			return ((float) totalWordsEnglish) / totalWords;
		return 1f;
	}
	
	public Map<String, Integer> getWordCounts() {
		return wordCounts;
	}

	public void setWordCounts(Map<String, Integer> wordCounts) {
		this.wordCounts = wordCounts;
	}

	public Map<String, Set<Integer>> getWordPos() {
		return wordPos;
	}

	public void setWordPos(Map<String, Set<Integer>> wordPos) {
		this.wordPos = wordPos;
	}

	public Map<Integer, Set<String>> getnGrams() {
		return nGrams;
	}

	public void setnGrams(Map<Integer, Set<String>> nGrams) {
		this.nGrams = nGrams;
	}

	public Map<Integer, Integer> getnGramCounts() {
		return nGramCounts;
	}

	public void setnGramCounts(Map<Integer, Integer> nGramCounts) {
		this.nGramCounts = nGramCounts;
	}

	public Map<Integer, String> getnGramMaxWords() {
		return nGramMaxWords;
	}

	public void setnGramMaxWords(Map<Integer, String> nGramMaxWords) {
		this.nGramMaxWords = nGramMaxWords;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("wordCounts:" + wordCounts + " ");
		sb.append("wordPos:" + wordPos + " ");
		sb.append("nGrams:" + nGrams + " ");
		sb.append("nGramCounts:" + nGramCounts + " ");
		sb.append("nGramMaxWords:" + nGramMaxWords + " ");
		return sb.toString();
	}

	@Override
	public Iterator<String> iterator() {
		return wordCounts.keySet().iterator();
	}

	private int getNGramSize(String word) {
		return word.split(" ").length;
	}
}
