package indexer.offline;

import indexer.WordCounts;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.file.FileUtils;

public class InvertedIndexJob {
	private static Logger logger = Logger.getLogger(InvertedIndexJob.class);
	
	public static final String TEXT_SPLIT = "[^a-zA-Z0-9'-]+";
	public static final String AHREF_SPLIT = "\\s+|[.,;_/-]+";
	
	private static Iterable<String> extractLinks(Document dom) {
		Elements links = dom.select("a[href]");
		List<String> tokens = new LinkedList<String>();
		for(Element link: links) {
			tokens.addAll(Arrays.asList(link.attr("href").split(AHREF_SPLIT)));
			tokens.addAll(Arrays.asList(link.text().split(TEXT_SPLIT)));
		}
		return tokens;
	}
	
	private static Iterable<String> extractMetaTags(Document dom) {
		Elements links = dom.select("meta[name~=(description|keywords)][content]");
		List<String> tokens = new LinkedList<String>();
		for(Element link: links)
			tokens.addAll(Arrays.asList(link.attr("content").split(TEXT_SPLIT)));
		return tokens;
	}
	
	private static Iterable<String> extractText(Document dom, String selector) {
		Elements headers = dom.select(selector);
		List<String> tokens = new LinkedList<String>();
		for(Element header: headers)
			tokens.addAll(Arrays.asList(header.text().split(TEXT_SPLIT)));
		return tokens;
	}
	
	public static class DocumentIndexer extends Mapper<Text, Text, Text, Text>  {
		@Override
		public void map(Text url, Text doc, Context context) throws IOException, InterruptedException {
			Document dom = Jsoup.parse(doc.toString(), url.toString());
			WordCounts linkCounts = new WordCounts(extractLinks(dom));
			WordCounts metaTagCounts = new WordCounts(extractMetaTags(dom));
			WordCounts headerCounts = new WordCounts(extractText(dom, "title,h1,h2,h3,h4,h5,h6"));
			WordCounts textCounts = new WordCounts(extractText(dom, "title,body"));
	
			WordCounts allCounts = new WordCounts(textCounts)
				.addCounts(headerCounts)
				.addCounts(metaTagCounts)
				.addCounts(linkCounts);
			
			for(String word: allCounts) {
				// value format: url max-tf euclid-tf total-counts# link-counts# metatag-counts# header-counts#
				String value = String.format("%s\t%f\t%f\t%d\t%d\t%d\t%d", 
						url.toString(),
						allCounts.getEuclideanTermFrequency(word),
						allCounts.getMaximumTermFrequency(word),
						allCounts.getCounts(word),
						linkCounts.getCounts(word),
						metaTagCounts.getCounts(word),
						headerCounts.getCounts(word));
				
				context.write(new Text(word), new Text(value));
			}
		}
	}
	
	public static class CorpusIndexer extends Reducer<Text, Text, Text, Text> {
		@Override
		public void reduce(Text word, Iterable<Text> urls, Context context)
				throws IOException, InterruptedException {
			Set<String> seenURLs = new HashSet<String>();
			
			for(Text stats: urls) {
				String value = stats.toString();
				String parts[] = value.split("\t");
				if(!seenURLs.contains(parts[0]))
					context.write(word, new Text(value));
				seenURLs.add(parts[0]);
			}
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		logger.info(String.format("starting job with args: inputdir=%s, outputdir=%s", args[0], args[1]));
		
		FileUtils.deleteIfExists(java.nio.file.Paths.get(args[1]));
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "build inverted index");
		job.setJarByClass(InvertedIndexJob.class);
		
		job.setMapperClass(DocumentIndexer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setReducerClass(CorpusIndexer.class);
		
		job.setInputFormatClass(DocumentInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}