package indexer.offline;

import indexer.WordCounts;
import indexer.db.dao.DocumentFeatures;
import indexer.db.dao.InvertedIndex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import utils.file.FileUtils;
import utils.nlp.Dictionary;

import com.google.gson.Gson;

import crawler.dao.URLContent;
import edu.stanford.nlp.util.StringUtils;

public class InvertedIndexJob {
	public static final float ENGLISH_THRESHOLD = 0.5f;

	public static enum FeatureType {
		TOTAL_COUNTS, HEADER_COUNTS, LINK_COUNTS, META_TAG_COUNTS
	};

	private static Logger logger = Logger.getLogger(InvertedIndexJob.class);

	/** Mapper Class for Document Indexer */
	public static class DocumentIndexer extends Mapper<Text, Text, Text, Text> {

		@Override
		public void map(Text docId, Text jsonBlob, Context context)
				throws IOException, InterruptedException {

			URLContent page = new Gson().fromJson(jsonBlob.toString(),
					URLContent.class);

			Dictionary dict = Dictionary.createInstance(Dictionary.JAR_RESOURCE);
			Map<FeatureType, WordCounts> allCounts = computeCounts(page, 1, dict);
			WordCounts totWordCounts = allCounts.get(FeatureType.TOTAL_COUNTS);

			// discard document, if number of english words in doc is below
			// threshold
			if (totWordCounts.getPercentage() < ENGLISH_THRESHOLD)
				return;

			for (String word : totWordCounts) {
				DocumentFeatures doc = new DocumentFeatures();

				doc.setDocId(Integer.parseInt(docId.toString()));
				doc.setEuclideanTermFrequency(allCounts.get(
						FeatureType.TOTAL_COUNTS).getEuclideanTermFrequency(
						word));
				doc.setMaximumTermFrequency(allCounts.get(
						FeatureType.TOTAL_COUNTS).getMaximumTermFrequency(word));
				doc.setTotalCount(allCounts.get(FeatureType.TOTAL_COUNTS)
						.getCounts(word));
				doc.setHeaderCount(allCounts.get(FeatureType.HEADER_COUNTS)
						.getCounts(word));
				doc.setLinkCount(allCounts.get(FeatureType.LINK_COUNTS)
						.getCounts(word));
				doc.setMetaTagCount(allCounts.get(FeatureType.META_TAG_COUNTS)
						.getCounts(word));
				doc.setPositions(totWordCounts.getPosition(word));

				context.write(new Text(word), new Text(new Gson().toJson(doc)));
			}
		}
	}

	/** Reducer Class for Corpus Indexer */
	public static class CorpusIndexer extends
			Reducer<Text, Text, NullWritable, Text> {
		public static final int MAX_ENTRIES_PER_ROW = 4000;
		private static final int topK = 8000;

		private void writeRow(String word, int page,
				List<DocumentFeatures> docs, Context context)
				throws IOException, InterruptedException {
			InvertedIndex row = new InvertedIndex(word, page, docs);

			NullWritable key = NullWritable.get();
			Text value = new Text(new Gson().toJson(row));
			context.write(key, value);
		}

		@Override
		public void reduce(Text word, Iterable<Text> jsonFeatures,
				Context context) throws IOException, InterruptedException {
			List<DocumentFeatures> docs = new ArrayList<DocumentFeatures>();
			Set<Integer> seenDocs = new HashSet<Integer>();
			int corpusSize = context.getConfiguration().getInt("corpusSize",
					100000);

			for (Text jsonFeature : jsonFeatures) {
				DocumentFeatures features = new Gson().fromJson(
						jsonFeature.toString(), DocumentFeatures.class);

				if (seenDocs.contains(features.getDocId()))
					continue;

				seenDocs.add(features.getDocId());
				docs.add(features);
			}

			int docSize = seenDocs.size();
			final float idf = (float) Math.log(corpusSize / docSize);

			// add tf idf values as well.
			for (DocumentFeatures doc : docs)
				doc.setTfidf(doc.getMaximumTermFrequency() * idf);

			Collections.sort(docs, new Comparator<DocumentFeatures>() {
				@Override
				public int compare(DocumentFeatures o1, DocumentFeatures o2) {
					if (o1.getTfidf() > o2.getTfidf())
						return -1;
					else if (o1.getTfidf() < o2.getTfidf())
						return 1;
					else
						return 0;
				}
			});

			List<DocumentFeatures> docsToWrite = new ArrayList<DocumentFeatures>();
			int page = 0;
			int entryPos = 0;
			for (int i = 0; i < topK && i < docs.size(); ++i) {
				if (entryPos < MAX_ENTRIES_PER_ROW) {
					docsToWrite.add(docs.get(i));
					++entryPos;
				} else {
					writeRow(word.toString(), page, docsToWrite, context);
					++page;
					entryPos = 0;
					docsToWrite = new ArrayList<DocumentFeatures>();
				}
			}
			if (docsToWrite.size() > 0) {
				writeRow(word.toString(), page, docsToWrite, context);
			}
		}
	}

	/** Main Indexer MapReduce Job */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		logger.info(String
				.format("starting job with args: inputdir=%s, outputdir=%s, corpusSize=%s",
						args[0], args[1], args[2]));

		FileUtils.deleteIfExists(java.nio.file.Paths.get(args[1]));
		int corpusSize = Integer.parseInt(args[2]);

		Configuration conf = new Configuration();
		conf.setInt("corpusSize", corpusSize);
		Job job = Job.getInstance(conf, "build inverted index");
		job.setJarByClass(InvertedIndexJob.class);

		job.setMapperClass(DocumentIndexer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(CorpusIndexer.class);
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	/**
	 * Calculating all counts for tokens and ngrams for Page contents
	 * 
	 * @throws IOException
	 */
	public static Map<FeatureType, WordCounts> computeCounts(URLContent page,
			int nGramSize, Dictionary dict) throws IOException {

		Document doc = Jsoup.parse(page.getContent(), page.getUrl());
		doc.select("script,style").remove();

		List<String> linkTokens = new Tokenizer(doc.select("a[href]").text()
				.replace(".", " ")).getTokens();
		WordCounts linkCounts = new WordCounts(StringUtils.getNgrams(
				linkTokens, 1, nGramSize), nGramSize, dict);
		linkCounts.computeNormalDocSizes();

		List<String> metaTagTokens = new Tokenizer(extractMetaTags(doc))
				.getTokens();
		WordCounts metaTagCounts = new WordCounts(StringUtils.getNgrams(
				metaTagTokens, 1, nGramSize), nGramSize, dict);
		metaTagCounts.computeNormalDocSizes();

		List<String> headerTokens = new Tokenizer(doc
				.select("title,h1,h2,h3,h4,h5,h6").text().replace(".", " "))
				.getTokens();
		WordCounts headerCounts = new WordCounts(StringUtils.getNgrams(
				headerTokens, 1, nGramSize), nGramSize, dict);
		headerCounts.computeNormalDocSizes();

		List<String> normalTokens = new Tokenizer(doc.select("title,body")
				.text().replace(".", " ")).getTokens();
		// logger.info("Normal tokens:" + normalTokens);
		WordCounts totalCounts = new WordCounts(StringUtils.getNgrams(
				normalTokens, 1, nGramSize), nGramSize, dict)
				.addCounts(metaTagCounts);
		totalCounts.computeNormalDocSizes();

		Map<FeatureType, WordCounts> allCounts = new HashMap<FeatureType, WordCounts>();
		allCounts.put(FeatureType.LINK_COUNTS, linkCounts);
		allCounts.put(FeatureType.META_TAG_COUNTS, metaTagCounts);
		allCounts.put(FeatureType.HEADER_COUNTS, headerCounts);
		allCounts.put(FeatureType.TOTAL_COUNTS, totalCounts);

		return allCounts;
	}

	@SuppressWarnings("unused")
	private static String extractLinks(Document doc) {
		StringBuffer result = new StringBuffer();
		for (Element link : doc.select("a[href]")) {
			result.append(link.attr("href") + " ");
			result.append(link.text() + " ");
		}
		return result.toString();
	}

	private static String extractMetaTags(Document doc) {
		StringBuffer result = new StringBuffer();
		for (Element metaTag : doc
				.select("meta[name~=(keywords|description)][content]"))
			result.append(metaTag.attr("content") + " ");
		return result.toString();
	}
}
