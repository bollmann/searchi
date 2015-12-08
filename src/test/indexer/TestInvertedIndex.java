package test.indexer;

import indexer.InvertedIndex;
import indexer.dao.DocumentFeatures;

import java.util.List;
import junit.framework.TestCase;

import org.junit.Test;

public class TestInvertedIndex extends TestCase {

	@Test
	public void testGetDocumentsFor() {
		InvertedIndex index = new InvertedIndex();
		List<DocumentFeatures> aCandidates = index.getDocumentsForWord("a");
		List<DocumentFeatures> availableCandidates = index.getDocumentsForWord("available");
		List<DocumentFeatures> advanceCandidates = index.getDocumentsForWord("advance");
		
		try {
			DocumentFeatures aFeature = aCandidates.get(0);
			DocumentFeatures availableFeature = availableCandidates.get(0);
			DocumentFeatures advanceFeature = advanceCandidates.get(0);
		} catch(ArrayIndexOutOfBoundsException e) {
			assertTrue(false);
		}
		assertTrue(true);
	}
	
	@Test
	public void testRankDocuments() {
		
	}
}
