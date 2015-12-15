package indexer.clients;

import indexer.db.dao.DocumentFeatures;
import indexer.db.dao.InvertedIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import searchengine.query.QueryWord;

public class InvertedIndexFetcher extends Thread {
	private final Logger logger = Logger.getLogger(getClass());
	private Map<QueryWord, List<DocumentFeatures>> invertedIndex = null;
	private String word;
	public InvertedIndexFetcher(Map<QueryWord, List<DocumentFeatures>> invertedIndex, String word) {
		this.invertedIndex = invertedIndex;
		this.word = word;
	}
	
	@Override
	public void run() {
		InvertedIndexClient ii = InvertedIndexClient.getInstance();
//		logger.info("Fetcher found " + ii.getCache().size() + " in cache of iiclient id " + ii.hashCode());
		List<InvertedIndex> rows = ii.getDocumentLocations(word);
		List<DocumentFeatures> featureList = new ArrayList<DocumentFeatures>();
		for (InvertedIndex row : rows) {
			featureList.addAll(row.getFeatures());
		}
		logger.info("Found " + rows.size() + " rows for " + word);
		invertedIndex.put(new QueryWord(word), featureList);
	}
}
