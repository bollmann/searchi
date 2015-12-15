package utils.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import searchengine.query.QueryWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.stanford.nlp.util.StringUtils;

public class QueryProcessor {
	private final static Logger logger = Logger.getLogger(QueryProcessor.class);

	private final static Map<String, Integer> posRankMap = new HashMap<>();
	private static QueryProcessor instance;
	private MaxentTagger tagger;

	public static QueryProcessor getInstance() {
		if (instance == null) {
			instance = new QueryProcessor();
		}
		return instance;
	}

	public void initPosRankMap() {
		posRankMap.put("NNP", 100); // barrack, obama
		posRankMap.put("NN", 90); // president
		posRankMap.put("JJ", 80); // random
		posRankMap.put("WP", 3);
		posRankMap.put("RB", 10); // crap
		posRankMap.put("DT", 5); // the
		posRankMap.put("VBZ", 4); // is
	}

	private QueryProcessor() {
		tagger = new MaxentTagger(
		// 5 words in 100 ms
				"resources/gate-EN-twitter-fast.model");
		// 5 words in 161 ms
		// "resources/gate-EN-twitter.model");

		initPosRankMap();
	}

	/**
	 * Removes stop words. Should check for
	 * 
	 * @param stopWordPercentageThreshhold
	 *            is the threshhold of percentage of stop words in the sentence
	 *            over which stop words will NOT be removed
	 * @param query
	 * @return list of query words that aren't stop words
	 */
	public List<String> removeStopWords(List<String> query,
			double stopWordPercentageThreshhold) {
		List<String> result = new ArrayList<String>();
		int count = 0;
		for (String queryWord : query) {
			// if (!stopWords.contains(queryWord)) {
			// // logger.info("Found non stopword "+ queryWord);
			// result.add(queryWord);
			// } else {
			// // logger.info("Found stopword "+ queryWord);
			// count++;
			// }
			count++;
		}
		logger.info("Stopword percentage of query is "
				+ (count * 100 / (double) query.size()) + " and threshhold is "
				+ stopWordPercentageThreshhold);
		if ((count * 100 / (double) query.size()) > stopWordPercentageThreshhold) {
			return query;
		} else {
			return result;
		}
	}

	public Map<String, String> getPOSMap(String query) {
		Map<String, String> posMap = new HashMap<>();
		String tagged = tagger.tagString(query);
		for (String word : tagged.split(" ")) {
			posMap.put(word.split("_")[0], word.split("_")[1]);
		}
		return posMap;
	}

	public Map<String, Integer> rankWords(String query) {
		Map<String, Integer> rankMap = new HashMap<>();
		Map<String, String> posMap = getPOSMap(query);
		logger.info(posMap);
		for (Entry<String, String> entry : posMap.entrySet()) {
			rankMap.put(entry.getKey(), posRankMap.get(entry.getValue()));
		}

		return rankMap;
	}

	// public List<List<String>> generateSynonymEquivalents(List<String> query)
	// {
	// List<List<String>> result = new ArrayList<List<String>>();
	// Map<String, Set<String>> synReplacements = new HashMap<>();
	//
	// for (String queryWord : query) {
	// Synset[] synsets = database.getSynsets(queryWord);
	// // Display the word forms and definitions for synsets retrieved
	// if (synsets.length > 0) {
	//
	// for (int i = 0; i < synsets.length; i++) {
	// String[] wordForms = synsets[i].getWordForms();
	//
	// for (int j = 0; j < wordForms.length; j++) {
	// // System.out.print((j > 0 ? ", " : "") + wordForms[j]);
	// if (synReplacements.containsKey(queryWord)) {
	// synReplacements.get(queryWord).add(wordForms[j]);
	// } else {
	// Set<String> synList = new HashSet<String>();
	// synList.add(queryWord);
	// synList.add(wordForms[j]);
	// synReplacements.put(queryWord, synList);
	// }
	// }
	// }
	// } else {
	// // System.err.println("No synsets exist that contain "
	// // + "the word form '" + queryWord + "'");
	// }
	// }
	// logger.info("Replacement map is " + synReplacements);
	// for (Entry<String, Set<String>> entry : synReplacements.entrySet()) {
	//
	// }
	//
	// return result;
	// }

	public Map<Integer, List<String>> generateNGrams(List<String> unigrams,
			int nGramLimit) {

		Map<Integer, List<String>> nGramMap = new HashMap<>();
		for (int i = 1; i <= nGramLimit; i++) {
			List<String> result = new ArrayList<String>();
			if (unigrams.size() < i) {
				nGramMap.put(i, result);
				continue;
			}
			for (String gram : StringUtils.getNgrams(unigrams, i, i)) {
				result.add(gram.toLowerCase());
			}
			nGramMap.put(i, result);
		}
		return nGramMap;
	}

	public List<QueryWord> getProcessedQuery(List<String> query, int nGramLimit) {
		// TODO Auto-generated method stub
		String queryString = StringUtils.join(query, " ");
		Map<String, Integer> wordRanks = rankWords(queryString);

		Map<Integer, List<String>> nGramMap = generateNGrams(query, nGramLimit);
		List<QueryWord> result = new ArrayList<QueryWord>();
		Set<String> seenWords = new HashSet<String>();
		for (Entry<Integer, List<String>> entry : nGramMap.entrySet()) {
			// for entry i, make a query word that is nGram i, and since it will
			// have
			// i entries in each word in the values, split them and add values
			// and
			// put that weight in the query weight
			for (String wordString : entry.getValue()) {
				if (!seenWords.contains(wordString)) {
					QueryWord qWord = new QueryWord(wordString);
					qWord.setnGramSize(entry.getKey());
					double weight = 0.0;
					for (String word : wordString.split(" ")) {
						weight += wordRanks.get(word);
					}
//					logger.debug("Giving " + wordString + " weight of " + weight);
					qWord.setWeight(weight * (1 /(double) 100));
					seenWords.add(wordString);
					result.add(qWord);
				}
			}
		}
		return result;
	}
}
