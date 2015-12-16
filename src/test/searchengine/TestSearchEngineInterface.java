package test.searchengine;

import indexer.clients.InvertedIndexClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import searchengine.SearchEngine;
import searchengine.query.QueryWord;
import utils.nlp.QueryProcessor;

public class TestSearchEngineInterface extends TestCase {

	@Test
	public void testImageSearch() {
		List<String> query = Arrays.asList("cat".split(" "));
		QueryProcessor queryProcessor = QueryProcessor.getInstance();
		List<QueryWord> processedQuery = queryProcessor
				.getProcessedQuery(query, 1);
		InvertedIndexClient iic = InvertedIndexClient.getInstance();
		Map<QueryWord, List<String>> imageIndex = iic
				.getImageIndexForQueryMultiThreaded(processedQuery);
//		System.out.println(imageIndex);
		List<String> rankedDocs = SearchEngine
				.formDocumentScoresForQueryFromImageIndex(processedQuery, imageIndex);
//		System.out.println(rankedDocs);
	}
}
