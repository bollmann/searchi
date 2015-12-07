package indexer.offline;

import indexer.WordCounts;
import indexer.dao.DocumentFeatures;
import indexer.dao.InvertedIndexRow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import utils.file.FileUtils;

import com.google.gson.Gson;

import crawler.dao.URLContent;

public class InvertedIndexJob {
	public static enum Feature {
		TOTAL_COUNTS, HEADER_COUNTS, LINK_COUNTS, META_TAG_COUNTS
	};

	private static Logger logger = Logger.getLogger(InvertedIndexJob.class);

	/** Mapper Class for Document Indexer */
	public static class DocumentIndexer extends
			Mapper<LongWritable, Text, Text, Text> {

		@Override
		public void map(LongWritable lineNr, Text jsonBlob, Context context)
				throws IOException, InterruptedException {
			URLContent page = new Gson().fromJson(jsonBlob.toString(),
					URLContent.class);
			Map<Feature, WordCounts> allCounts = computeCounts(page);

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
				doc.setLinkCount(allCounts.get(Feature.LINK_COUNTS).getCounts(
						word));

				context.write(new Text(word), new Text(new Gson().toJson(doc)));
			}
		}
	}

	/** Reducer Class for Corpus Indexer */
	public static class CorpusIndexer extends
			Reducer<Text, Text, NullWritable, Text> {
		public static final int MAX_ENTRIES_PER_ROW = 400;

		private void writeRow(String word, int page,
				List<DocumentFeatures> docs, Context context)
				throws IOException, InterruptedException {
			InvertedIndexRow row = new InvertedIndexRow(word, page, docs);

			NullWritable key = NullWritable.get();
			Text value = new Text(new Gson().toJson(row));
			context.write(key, value);
		}

		@Override
		public void reduce(Text word, Iterable<Text> jsonFeatures,
				Context context) throws IOException, InterruptedException {
			ArrayList<DocumentFeatures> docs = new ArrayList<DocumentFeatures>();

			int page = 0;
			int entryPos = 0;
			for (Text jsonFeature : jsonFeatures) {
				DocumentFeatures features = new Gson().fromJson(
						jsonFeature.toString(), DocumentFeatures.class);
				if (entryPos < MAX_ENTRIES_PER_ROW) {
					docs.add(features);
					++entryPos;
				} else {

					writeRow(word.toString(), page, docs, context);

					++page;
					entryPos = 0;
					docs = new ArrayList<DocumentFeatures>();
				}
			}

			if (docs.size() > 0)
				writeRow(word.toString(), page, docs, context);
		}
	}

	/** Main Indexer MapReduce Job */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		logger.info(String.format(
				"starting job with args: inputdir=%s, outputdir=%s", args[0],
				args[1]));

		FileUtils.deleteIfExists(java.nio.file.Paths.get(args[1]));

		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "build inverted index");
		job.setJarByClass(InvertedIndexJob.class);

		job.setMapperClass(DocumentIndexer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(CorpusIndexer.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

	/** Calculating all counts for Page contents */
	public static Map<Feature, WordCounts> computeCounts(URLContent page) {
		Document doc = Jsoup.parse(page.getContent(), page.getUrl());
		doc.select("script,style").remove();

		WordCounts linkCounts = new WordCounts(new Tokenizer(doc.select(
				"a[href]").text()).getTokens());
		WordCounts metaTagCounts = new WordCounts(new Tokenizer(
				extractMetaTags(doc)).getTokens());
		WordCounts headerCounts = new WordCounts(new Tokenizer(doc.select(
				"title,h1,h2,h3,h4,h5,h6").text()).getTokens());
		WordCounts totalCounts = new WordCounts(new Tokenizer(doc.select(
				"title,body").text()).getTokens()).addCounts(metaTagCounts);

		Map<Feature, WordCounts> allCounts = new HashMap<Feature, WordCounts>();
		allCounts.put(Feature.LINK_COUNTS, linkCounts);
		allCounts.put(Feature.META_TAG_COUNTS, metaTagCounts);
		allCounts.put(Feature.HEADER_COUNTS, headerCounts);
		allCounts.put(Feature.TOTAL_COUNTS, totalCounts);

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