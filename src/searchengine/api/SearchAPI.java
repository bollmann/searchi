package searchengine.api;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import searchengine.ranking.Ranker;
import searchengine.ranking.RankerHeader;
import searchengine.ranking.RankerInfo.RankerType;
import searchengine.ranking.RankerLinks;
import searchengine.ranking.RankerMeta;
import searchengine.ranking.RankerPosition;
import searchengine.ranking.RankerQueryMatch;
import searchengine.ranking.RankerTfIdf;
import searchengine.ranking.RankerTotalCount;

public final class SearchAPI {
	private static final Logger logger = Logger.getLogger(SearchAPI.class);
	
	private final List<String> query;
	private final Map<String, List<DocumentFeatures>> invertedIndexMap;
	private final int corpusSize;

	private final Map<String, Integer> wordDfs;
	private List<DocumentScore> documentList;
	private Map<RankerType, Double> rankerSet;
	
	public SearchAPI(List<String> query, Map<String, List<DocumentFeatures>> invertedIndexMap,
			int corpusSize) {
		this.query = query;
		this.invertedIndexMap = invertedIndexMap;		
		this.corpusSize = corpusSize;
		
		this.wordDfs = new HashMap<String, Integer>();
		for(Entry<String, List<DocumentFeatures>> entry : invertedIndexMap.entrySet()) {
			wordDfs.put(entry.getKey(), entry.getValue().size());
		}
		
		this.rankerSet = new HashMap<>();
	}
	
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

	public void setRanker(RankerType ranker, double weight) {
		rankerSet.put(ranker, weight);
	}
	
	public void setRankers(Map<RankerType, Double> rs) {
		rankerSet.putAll(rs);
	}
	
	/** Apply the set rankers to the documents 
	 * @throws Exception */
	public List<Ranker> applyRankers() throws Exception {
		
		List<Ranker> rankers = new ArrayList<>(rankerSet.size());
		 
		if (rankerSet.containsKey(RankerType.RANKER_TFIDF)) {
			Ranker rankerTfIdf = new RankerTfIdf(documentList, query, corpusSize, wordDfs);
			rankerTfIdf.setWeight(rankerSet.get(RankerType.RANKER_TFIDF));
			rankers.add(rankerTfIdf);
		}
		
		if (rankerSet.containsKey(RankerType.RANKER_HEADER)) {
			Ranker rankerHeader = new RankerHeader(documentList);
			rankerHeader.setWeight(rankerSet.get(RankerType.RANKER_HEADER));
			rankers.add(rankerHeader);
		}
		
		if (rankerSet.containsKey(RankerType.RANKER_LINKS)) {
			Ranker rankerLinks = new RankerLinks(documentList);
			rankerLinks.setWeight(rankerSet.get(RankerType.RANKER_LINKS));
			rankers.add(rankerLinks);
		}
		
		if (rankerSet.containsKey(RankerType.RANKER_META)) {
			Ranker rankerMeta = new RankerMeta(documentList);
			rankerMeta.setWeight(rankerSet.get(RankerType.RANKER_META));
			rankers.add(rankerMeta);
		}
		
		if (rankerSet.containsKey(RankerType.RANKER_TOTALCOUNT)) {
			Ranker rankerTotalCnt = new RankerTotalCount(documentList);
			rankerTotalCnt.setWeight(rankerSet.get(RankerType.RANKER_TOTALCOUNT));
			rankers.add(rankerTotalCnt);
		}
		
		if (rankerSet.containsKey(RankerType.RANKER_QUERYMATCH)) {
			Ranker rankerQueryMatch = new RankerQueryMatch(documentList);
			rankerQueryMatch.setWeight(rankerSet.get(RankerType.RANKER_QUERYMATCH));
			rankers.add(rankerQueryMatch);
		}

		if (rankerSet.containsKey(RankerType.RANKER_POSITION)) {
			Ranker rankerPosition = new RankerPosition(documentList, query);
			rankerPosition.setWeight(rankerSet.get(RankerType.RANKER_POSITION));
			rankers.add(rankerPosition);
		}
		
		// Mulithreaded ranker execution
		ExecutorService es = Executors.newFixedThreadPool(rankers.size());
		for (Ranker ranker : rankers) {
			es.execute(ranker);
		}
		
		es.shutdown();			
		try {
			es.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			throw new Exception(e);
		}
		
		for (Ranker ranker : rankers) {
			logger.info("Name - " + ranker.getRankingName() +" -- " +ranker.getRanks().get(0));
		}
		
		return rankers;	
	}
	
	/** Weighted combination using different rankers. 
	 * @throws Exception */
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
}
