package indexer;

import indexer.dao.DocumentFeatures;
import indexer.dao.InvertedIndexRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class InvertedIndexFetcher extends Thread {
	private final Logger logger = Logger.getLogger(getClass());
	private Map<String, List<DocumentFeatures>> invertedIndex = null;
	private String word;
	public InvertedIndexFetcher(Map<String, List<DocumentFeatures>> invertedIndex, String word) {
		this.invertedIndex = invertedIndex;
		this.word = word;
	}
	
	@Override
	public void run() {
		InvertedIndex ii = new InvertedIndex();
		List<InvertedIndexRow> rows = ii.getDocumentLocations(word);
		List<DocumentFeatures> featureList = new ArrayList<DocumentFeatures>();
		for (InvertedIndexRow row : rows) {
			featureList.addAll(row.getFeatures());
		}
		logger.info("Found " + rows.size() + " rows for " + word);
		invertedIndex.put(word, featureList);
	}
}
