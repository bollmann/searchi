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
	private QueryWord qword;
	
	public InvertedIndexFetcher(Map<QueryWord, List<DocumentFeatures>> invertedIndex, QueryWord qword) {
		this.invertedIndex = invertedIndex;
		this.qword = qword;
	}
	
	@Override
	public void run() {
		InvertedIndexClient ii = InvertedIndexClient.getInstance();
//		logger.info("Fetcher found " + ii.getCache().size() + " in cache of iiclient id " + ii.hashCode());
		List<InvertedIndex> rows = ii.getDocumentLocations(qword.getWord());
		List<DocumentFeatures> featureList = new ArrayList<DocumentFeatures>();
		for (InvertedIndex row : rows) {
			featureList.addAll(row.getFeatures());
		}
		logger.info("Found " + rows.size() + " rows for " + qword.getWord());
		invertedIndex.put(qword, featureList);
	}
}
