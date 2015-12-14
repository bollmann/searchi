package searchengine;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import searchengine.ranking.Ranker;

import searchengine.ranking.RankerInfo.RankerType;
import searchengine.ranking.RankingEngine;

public final class SearchEngine {
	
	private static final Logger logger = Logger.getLogger(SearchEngine.class);

	private final List<String> query;
	private final Map<String, List<DocumentFeatures>> invertedIndexMap;
	private final int corpusSize;

	private final Map<String, Integer> wordDfs;
	private List<DocumentScore> documentList;

	private RankingEngine rankingEngine;	

	public SearchEngine(List<String> query,
		Map<String, List<DocumentFeatures>> invertedIndexMap, int corpusSize) {
		
		this.query = query;
		this.invertedIndexMap = invertedIndexMap;
		this.corpusSize = corpusSize;

		this.wordDfs = new HashMap<String, Integer>();
		for (Entry<String, List<DocumentFeatures>> entry : invertedIndexMap
				.entrySet()) {
			wordDfs.put(entry.getKey(), entry.getValue().size());
		}		
	}
	
	/** Top level API to get Ranked results.*/
	public List<DocumentScore> getRankedIndexerResults() {
					
		formDocumentScoresForQueryFromInvertedIndex();
		List<DocumentScore> rankedDocs = null;
		
		if (rankingEngine == null) {
			initDefaultRankingEngine();
		}
		
		try {			
			List<Ranker> rankers = rankingEngine.applyRankers();
			
			rankedDocs = combineRankings(rankers);
			Collections.sort(rankedDocs);
			
		} catch (Exception e) {
			logger.error("Couldn't get Ranked Results due to exception: ", e);
			e.printStackTrace();
		}
		
		return rankedDocs;

	}

	/** Wraps documents in document scores with inverted index data */
	public List<DocumentScore> formDocumentScoresForQueryFromInvertedIndex() {
		Map<Integer, DocumentScore> documentRanks = new HashMap<Integer, DocumentScore>();

		for (String word : query) {
			List<DocumentFeatures> docs = invertedIndexMap.get(word);

			for (DocumentFeatures features : docs) {
				DocumentScore rankedDoc = documentRanks
						.get(features.getDocId());
				if (rankedDoc == null) {
					rankedDoc = new DocumentScore(word, features);
					documentRanks.put(features.getDocId(), rankedDoc);
				} else {
					rankedDoc.addFeatures(word, features);
				}
			}
		}
		documentList = new ArrayList<DocumentScore>(documentRanks.values());
		return documentList;
	}

	/**
	 * Returns final ranked documents using weighted combination
	 * of different rankers.
	 * 
	 * @throws Exception
	 */
	public List<DocumentScore> combineRankings(List<Ranker> rankers)
			throws Exception {

		if (rankers == null || rankers.isEmpty()) {
			throw new Exception("Rankers Can't be null for comnbination");
		}

		int numDocs = rankers.get(0).getRanks().size();

		for (int ind = 0; ind < numDocs; ++ind) {
			double score = 0;
			for (Ranker ranker : rankers) {
				score += ranker.getRanks().get(ind) * ranker.getWeight();
			}
			documentList.get(ind).setScore(score);
		}
		return documentList;
	}
	
	public void setRankingEngine(RankingEngine rEngine) {
		this.rankingEngine = rEngine;
	}
	
	public RankingEngine getRankingEngine() {
		return this.rankingEngine;
	}

	/** Initializaes the default ranking engine to be used
	 *  if none is specified
	 */
	private void initDefaultRankingEngine() {
		rankingEngine = new RankingEngine(documentList,
				query, corpusSize, wordDfs);
			
		rankingEngine.addRanker(RankerType.RANKER_TFIDF, 5.0);
		rankingEngine.addRanker(RankerType.RANKER_HEADER, 1.0);
		rankingEngine.addRanker(RankerType.RANKER_LINKS, 3.0);
		rankingEngine.addRanker(RankerType.RANKER_META, 4.0);
		rankingEngine.addRanker(RankerType.RANKER_POSITION, -10.0);
		rankingEngine.addRanker(RankerType.RANKER_QUERYMATCH, 2.0);
		rankingEngine.addRanker(RankerType.RANKER_TOTALCOUNT, 1.0);
		rankingEngine.addRanker(RankerType.RANKER_URLCOUNT, 10.0);
				
	}

}
