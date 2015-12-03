package indexer.offline;

import indexer.InvertedIndex;
import indexer.WordCounts;
import indexer.dao.DocumentFeatures;
import indexer.dao.DocumentFeaturesMarshaller;
import indexer.dao.InvertedIndexRow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
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

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.gson.Gson;

import crawler.dao.URLContent;

public class InvertedIndexJob {
	private static Logger logger = Logger.getLogger(InvertedIndexJob.class);

	/** Mapper Class for Document Indexer */
	public static class DocumentIndexer extends
			Mapper<LongWritable, Text, Text, Text> {
		
		@Override
		public void map(LongWritable lineNr, Text jsonBlob, Context context)
				throws IOException, InterruptedException {
			URLContent page = new Gson().fromJson(jsonBlob.toString(), URLContent.class);
			Map<String, WordCounts> allCounts = computeCounts(page);
			
			WordCounts wordCounts = allCounts.get("totalCounts");
			for (String word : wordCounts) {
				// value format: url max-tf euclid-tf total-counts# link-counts#
				//               metatag-counts# header-counts#
				String value = String.format("%s\t%f\t%f\t%d\t%d\t%d\t%d",
						page.getUrl(),
						allCounts.get("totalCounts").getEuclideanTermFrequency(word),
						allCounts.get("totalCounts").getMaximumTermFrequency(word),
						allCounts.get("totalCounts").getCounts(word), 
						allCounts.get("linkCounts").getCounts(word),
						allCounts.get("metaTagCounts").getCounts(word),
						allCounts.get("headerCounts").getCounts(word));

				context.write(new Text(word), new Text(value));
			}
		}
	}

	/** Reducer Class for Corpus Indexer */
	public static class CorpusIndexer extends Reducer<Text, Text, Text, Text> {
		public static final int MAX_ENTRIES_PER_ROW = 2;
		public static final int ROWS_PER_BATCH_WRITE = 1;

		private DynamoDBMapper db;
		
		public CorpusIndexer() {
			this.db = InvertedIndex.connectDB();
		}
		
		@Override
		public void reduce(Text word, Iterable<Text> urls, Context context)
				throws IOException, InterruptedException {
			Set<String> seenURLs = new HashSet<String>();
			List<InvertedIndexRow> rows = new ArrayList<InvertedIndexRow>();
			
			int page = 0;
			for (Text stats: urls) {
				List<DocumentFeatures> docs = new ArrayList<DocumentFeatures>();

				for (int i = 0; i < MAX_ENTRIES_PER_ROW; ++i) {
					DocumentFeaturesMarshaller m = new DocumentFeaturesMarshaller();
					DocumentFeatures features = m.unmarshall(DocumentFeatures.class, stats.toString());
					logger.info("unmarshalled object: " + features.toString());
					
					if(!seenURLs.contains(features.getUrl()))
						docs.add(features);
				}
				InvertedIndexRow row = new InvertedIndexRow(word.toString(), page, docs);
				logger.info("adding new row: " + row.toString());
				rows.add(row);
				++page;
				
				if(rows.size() >= ROWS_PER_BATCH_WRITE) {
					this.db.batchSave(rows);
					rows = new LinkedList<InvertedIndexRow>();
				}
			}
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
	public static Map<String, WordCounts> computeCounts(URLContent page) {
		Document doc = Jsoup.parse(page.getContent(), page.getUrl());
		doc.select("script,style").remove();
		
		WordCounts linkCounts = new WordCounts(
				new Tokenizer(doc.select("a[href]").text()).getTokens());
		WordCounts metaTagCounts = new WordCounts(
				new Tokenizer(extractMetaTags(doc)).getTokens());
		WordCounts headerCounts = new WordCounts(
				new Tokenizer(doc.select("title,h1,h2,h3,h4,h5,h6").text()).getTokens());
		WordCounts totalCounts = new WordCounts(new Tokenizer(doc.select(
				"title,body").text()).getTokens()).addCounts(metaTagCounts);

		Map<String, WordCounts> allCounts = new HashMap<String, WordCounts>();
		allCounts.put("linkCounts", linkCounts);
		allCounts.put("metaTagCounts", metaTagCounts);
		allCounts.put("headerCounts", headerCounts);
		allCounts.put("totalCounts", totalCounts);
		
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