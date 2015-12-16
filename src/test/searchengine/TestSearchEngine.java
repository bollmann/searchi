package test.searchengine;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import searchengine.SearchEngine;
import searchengine.query.QueryWord;

public class TestSearchEngine extends TestCase {

	@Test
	public void testGetDocumentScoresForQueryAndInvertedIndex() {
		QueryWord qWordA = new QueryWord("a");
		QueryWord qWordAn = new QueryWord("an");
		QueryWord qWordthe = new QueryWord("the");
		
		List<QueryWord> query = new ArrayList<>();
		query.add(qWordA);
		query.add(qWordAn);		
		query.add(qWordthe);
		
		DocumentFeatures feat1 = new DocumentFeatures();
		feat1.setDocId(1);
		DocumentFeatures feat2 = new DocumentFeatures();
		feat2.setDocId(2);
		DocumentFeatures feat3 = new DocumentFeatures();
		feat3.setDocId(3);
		List<DocumentFeatures> feats1 = new ArrayList<>();
		feats1.add(feat1);
		feats1.add(feat2);
		feats1.add(feat3);

		DocumentFeatures feat4 = new DocumentFeatures();
		feat4.setDocId(2);
		DocumentFeatures feat5 = new DocumentFeatures();
		feat5.setDocId(4);
		DocumentFeatures feat6 = new DocumentFeatures();
		feat6.setDocId(5);
		List<DocumentFeatures> feats2 = new ArrayList<>();
		feats2.add(feat4);
		feats2.add(feat5);
		feats2.add(feat6);

		Map<QueryWord, List<DocumentFeatures>> invertedIndex = new HashMap<>();
		invertedIndex.put(qWordA, feats1);
		invertedIndex.put(qWordAn, feats2);
		invertedIndex.put(qWordthe, new ArrayList<DocumentFeatures>()); // a result for a query
																	 // word can't be null
		SearchEngine searchAPI = new SearchEngine(query, invertedIndex, 1000);
		List<DocumentScore> results = 
			searchAPI.formDocumentScoresForQueryFromInvertedIndex();
		
		assertEquals(5, results.size());
		for (DocumentScore result : results) {
			if (result.getDocId() == 2) {
				assertEquals(2, result.getWordFeatures().size());
			} else {
				assertEquals(1, result.getWordFeatures().size());
			}
		}

	}
}
