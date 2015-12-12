package test.indexer.dao;

import indexer.db.dao.DocumentFeatures;
import indexer.db.dao.DocumentFeaturesMarshaller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

public class TestDocumentFeaturesMarshaller extends TestCase {
	
	@Test
	public void testRoundtrip() {
		DocumentFeatures feature1 = new DocumentFeatures();
		feature1.setDocId(12345);
		feature1.setEuclideanTermFrequency(0.1f);
		feature1.setMaximumTermFrequency(0.2f);
		feature1.setTfidf(0.3f);
		feature1.setTotalCount(100);
		feature1.setHeaderCount(9);
		feature1.setLinkCount(18);
		feature1.setMetaTagCount(27);
		feature1.setPositions(new HashSet<Integer>() { { add(1); add(2); add(3); add(4); add(5); } });
		
		DocumentFeatures feature2 = new DocumentFeatures();
		feature2.setDocId(6789);
		feature2.setEuclideanTermFrequency(0.4f);
		feature2.setMaximumTermFrequency(0.5f);
		feature2.setTfidf(0.6f);
		feature2.setTotalCount(10000);
		feature2.setHeaderCount(900);
		feature2.setLinkCount(1800);
		feature2.setMetaTagCount(2700);
		feature2.setPositions(new HashSet<Integer>() { { add(9); add(8); add(7); add(6); } });

		
		DocumentFeatures feature3 = new DocumentFeatures();
		feature3.setDocId(6789);
		feature3.setEuclideanTermFrequency(0.7f);
		feature3.setMaximumTermFrequency(0.8f);
		feature3.setTfidf(0.9f);
		feature3.setTotalCount(-100);
		feature3.setHeaderCount(-9);
		feature3.setLinkCount(-18);
		feature3.setMetaTagCount(-27);
		feature3.setPositions(new HashSet<Integer>() { { add(99); add(88); add(77); add(66); } });

		List<DocumentFeatures> features = new ArrayList<>();
		features.add(feature1);
		features.add(feature2);
		features.add(feature3);
		
		DocumentFeaturesMarshaller m = new DocumentFeaturesMarshaller();
		String rawFeatures = m.marshall(features);
				
		Class<List<DocumentFeatures>> clazz = (Class<List<DocumentFeatures>>) features.getClass();
		List<DocumentFeatures> serializedFeatures = m.unmarshall(clazz, rawFeatures);
		
		assertEquals(features, serializedFeatures);
	}
}
