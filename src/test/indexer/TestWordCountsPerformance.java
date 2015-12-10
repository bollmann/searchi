package test.indexer;

import indexer.WordCounts;
import indexer.dao.DocumentFeatures;
import indexer.offline.InvertedIndexJob;
import indexer.offline.InvertedIndexJob.FeatureType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;

import crawler.dao.URLContent;

public class TestWordCountsPerformance {
	
	private static final String TEST_DIR = "/home/ishan/Git/Repos/searchi/hadoop-2.6.0/asdfg";
	 
	@Test
	public void testWithUnigram() throws IOException {
		
		long startTime = System.currentTimeMillis();
		
		File inputDir = new File(TEST_DIR);
		File[] files = inputDir.listFiles();
		
		int cnt = 0;
		for (int i = 0; i < files.length; ++i) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(files[i])));
			//System.out.println(files[i].getName());
			
			String line = "";
			while ((line = br.readLine()) != null) {
				String jsonBlob = line.trim().split("\t")[1];
				URLContent page = new Gson().fromJson(jsonBlob.toString(),
						URLContent.class);
				
				InvertedIndexJob job = new InvertedIndexJob();
				Map<FeatureType, WordCounts> allCounts = job.computeCounts(page, 1);

				
				WordCounts wordCounts = allCounts.get(FeatureType.TOTAL_COUNTS);
				Integer id = 0;
				for (String word : wordCounts) {
					DocumentFeatures doc = new DocumentFeatures();
					doc.setDocId(id);
					id++;
					doc.setEuclideanTermFrequency(allCounts.get(
						FeatureType.TOTAL_COUNTS).getEuclideanTermFrequency(word));
					doc.setMaximumTermFrequency(allCounts.get(FeatureType.TOTAL_COUNTS)
						.getMaximumTermFrequency(word));
					doc.setTotalCount(allCounts.get(FeatureType.TOTAL_COUNTS)
						.getCounts(word));
					doc.setHeaderCount(allCounts.get(FeatureType.HEADER_COUNTS)
						.getCounts(word));
					doc.setLinkCount(allCounts.get(FeatureType.LINK_COUNTS)
	                    .getCounts(word));
	                doc.setMetaTagCount(allCounts.get(FeatureType.META_TAG_COUNTS)
	                    .getCounts(word));
	                doc.setPositions(wordCounts.getPosition(word));
				}
			}
			br.close();
		}
			
		long endTime = System.currentTimeMillis();
		System.out.println(endTime - startTime);
	}

}
