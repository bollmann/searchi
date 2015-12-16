package indexer.clients;

import indexer.db.dao.ImageIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import searchengine.query.QueryWord;

public class ImageIndexFetcher extends Thread {
	private final Logger logger = Logger.getLogger(getClass());
	private Map<QueryWord, List<String>> imageIndex = null;
	private String word;
	
	public ImageIndexFetcher(Map<QueryWord, List<String>> imageIndex, String word) {
		this.imageIndex = imageIndex;
		this.word = word;
	}
	
	@Override
	public void run() {
		InvertedIndexClient ii = InvertedIndexClient.getInstance();
		List<ImageIndex> rows = ii.getImageLocations(word);
		
		List<String> imageURLs = new ArrayList<>();
		for(ImageIndex row: rows) {
			imageURLs.addAll(row.getImageUrls());
		}
		
		logger.info("Found " + rows.size() + " images for " + word);
		imageIndex.put(new QueryWord(word), imageURLs);
	}
}