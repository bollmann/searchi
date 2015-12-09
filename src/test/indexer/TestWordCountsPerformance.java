package test.indexer;

import indexer.WordCounts;
import indexer.dao.DocumentFeatures;
import indexer.offline.InvertedIndexJob;
import indexer.offline.InvertedIndexJob.Feature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.Map;

import org.apache.hadoop.io.Text;
import org.joda.time.DateTime;
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
				Map<Feature, WordCounts> allCounts = job.computeCounts(page, 1);

				
				WordCounts wordCounts = allCounts.get(Feature.TOTAL_COUNTS);
				
				for (String word : wordCounts) {
					DocumentFeatures doc = new DocumentFeatures();

					doc.setUrl(page.getUrl());
					doc.setEuclideanTermFrequency(allCounts.get(
						Feature.TOTAL_COUNTS).getEuclideanTermFrequency(word));
					doc.setMaximumTermFrequency(allCounts.get(Feature.TOTAL_COUNTS)
						.getMaximumTermFrequency(word));
					doc.setTotalCount(allCounts.get(Feature.TOTAL_COUNTS)
						.getCounts(word));
					doc.setHeaderCount(allCounts.get(Feature.HEADER_COUNTS)
						.getCounts(word));
					doc.setLinkCount(allCounts.get(Feature.LINK_COUNTS)
	                    .getCounts(word));
	                doc.setMetaTagCount(allCounts.get(Feature.META_TAG_COUNTS)
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
