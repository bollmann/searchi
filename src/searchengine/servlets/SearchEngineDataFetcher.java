package searchengine.servlets;

import indexer.DocumentScore;
import indexer.clients.InvertedIndexClient;
import indexer.db.dao.DocumentFeatures;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import searchengine.api.SearchAPI;
import searchengine.ranking.Ranker;
import searchengine.ranking.RankerInfo.RankerType;

public class SearchEngineDataFetcher {
	private static final Logger logger = Logger.getLogger(SearchEngineDataFetcher.class);

	public static List<DocumentScore> getRankedIndexerResults(List<String> query, Map<String, List<DocumentFeatures>> invertedIndex,
			InvertedIndexClient iic) {
		SearchAPI searchAPI = new SearchAPI(query, invertedIndex, iic.getCorpusSize());			
		searchAPI.formDocumentScoresForQueryFromInvertedIndex();
		List<DocumentScore> rankedDocs = null;
		try {
			searchAPI.addRanker(RankerType.RANKER_TFIDF, 1.0);
			searchAPI.addRanker(RankerType.RANKER_HEADER, 1.0);
			searchAPI.addRanker(RankerType.RANKER_LINKS, 1.0);
			searchAPI.addRanker(RankerType.RANKER_META, 1.0);
			searchAPI.addRanker(RankerType.RANKER_POSITION, -1.0);
			searchAPI.addRanker(RankerType.RANKER_QUERYMATCH, 1.0);
			searchAPI.addRanker(RankerType.RANKER_TOTALCOUNT, 1.0);
			searchAPI.addRanker(RankerType.RANKER_URLCOUNT, 10.0);

			List<Ranker> rankers = searchAPI.applyRankers();
			
			rankedDocs = searchAPI.combineRankings(rankers);
			Collections.sort(rankedDocs);
		} catch (Exception e) {
			//TODO Handle this exception;
//			return;
			e.printStackTrace();
		}
		
		return rankedDocs;

	}
}
