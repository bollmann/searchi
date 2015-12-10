package test.indexer;

import indexer.WordCounts;
import indexer.db.dao.DocumentFeatures;
import indexer.offline.InvertedIndexJob;
import indexer.offline.InvertedIndexJob.FeatureType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import utils.file.FileUtils;
import utils.nlp.Dictionary;

import com.google.gson.Gson;

import crawler.dao.URLContent;

public class TestWordCountsPerformance {

	private static final String TEST_DIR = "/home/ishan/Git/Repos/searchi/hadoop-2.6.0/asdfg";

	@Test
	public void testWithUnigram() throws IOException {

		long startTime = System.currentTimeMillis();

		File inputDir = new File(TEST_DIR);
		File[] files = inputDir.listFiles();

		for (int i = 0; i < files.length; ++i) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(files[i])));

			String line = "";
			while ((line = br.readLine()) != null) {
				String jsonBlob = line.trim().split("\t")[1];
				URLContent page = new Gson().fromJson(jsonBlob.toString(),
						URLContent.class);

				Dictionary dict = Dictionary.createInstance(new FileInputStream("resources/dict/all-english"));
				Map<FeatureType, WordCounts> allCounts = InvertedIndexJob
						.computeCounts(page, 1, dict);

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
	
	@Test
	public void testPerformanceForLargeHtmlFiles() throws IOException {
		String content = null;
		try {
			content = FileUtils.readFile("testcontent/Wiki-html-file.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		URLContent urlContent = new URLContent();
		urlContent.setContent(content);
		urlContent.setUrl("http://www.google.com");
		Date start = Calendar.getInstance().getTime();
		Map<FeatureType, WordCounts> wordCounts = InvertedIndexJob.computeCounts(urlContent, 3,null);
		Date end = Calendar.getInstance().getTime();
		long timeTakenForBigram = (end.getTime() - start.getTime());
		start = Calendar.getInstance().getTime();
		wordCounts = InvertedIndexJob.computeCounts(urlContent, 1,null);
		end = Calendar.getInstance().getTime();
		long timeTakenForUnigram = (end.getTime() - start.getTime());
		System.out.println("Time taken for unigram:" + timeTakenForUnigram + " and bigram:" + timeTakenForBigram);
	}

}
