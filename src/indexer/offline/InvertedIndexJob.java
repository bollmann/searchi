package indexer.offline;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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

import utils.file.FileUtils;

public class InvertedIndexJob {
	private static Logger logger = Logger.getLogger(InvertedIndexJob.class);
	
	public static List<String> tokenize(String doc) {
		String[] words = doc.split("\\s+");
		return Arrays.asList(words);
	}
	
	public static class DocumentIndexer extends Mapper<Text, Text, Text, Text>  {

		private String computeWordCounts(Map<String, Integer> wordCounts, Iterable<String> words) {
			Iterator<String> iter = words.iterator();
			String maxWord = null;
			while (iter.hasNext()) {
				String word = iter.next();
				Integer counts = wordCounts.get(word);

				if (counts == null)
					wordCounts.put(word, 1);
				else
					wordCounts.put(word, counts + 1);

				// update maxWord, if necessary:
				if (maxWord == null || wordCounts.get(maxWord) < wordCounts.get(word))
					maxWord = word;
			}
			return maxWord;
		}
		
		private double getTermFrequency(String word, String maxWord, Map<String, Integer> counts) {
			double alpha = 0.5; // TODO: tune this parameter!
			return alpha + (1 - alpha) * counts.get(word) / (float) counts.get(maxWord);
		}
		
		@Override
		public void map(Text url, Text doc, Context context) throws IOException, InterruptedException {
			//logger.info(String.format("map input: key=%s, value=%s", url.toString(), doc.toString()));
			List<String> words = tokenize(doc.toString());
			Map<String, Integer> wordCounts = new HashMap<String, Integer>();
			String maxWord = computeWordCounts(wordCounts, words);
			
			//logger.info(String.format("map: key=%s, values=%s", url.toString(), words.toString()));
			
			for(String word: wordCounts.keySet()) {
				//logger.info(String.format("map context write: key=%s, value=%s", url.toString(), word));
				
				double tfScore = getTermFrequency(word, maxWord, wordCounts);
				String value = String.format("%s\t%d\t%f", url, wordCounts.get(word), tfScore);
				context.write(new Text(word), new Text(value));
			}
		}
	}
	
	public static class CorpusIndexer extends Reducer<Text, Text, Text, Text> {
		@Override
		public void reduce(Text word, Iterable<Text> urls, Context context)
				throws IOException, InterruptedException {
			// needed just for sorting; doesn't do anything itself.
			Iterator<Text> iter = urls.iterator();
			while (iter.hasNext()) {
				Text url = iter.next();
				context.write(word, new Text(url.toString()));
			}
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		logger.info(String.format("starting job with args: inputdir=%s, outputdir=%s", args[0], args[1]));
		
		FileUtils.deleteIfExists(java.nio.file.Paths.get(args[1]));
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "build document vectors");
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