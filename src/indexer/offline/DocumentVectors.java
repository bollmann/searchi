package indexer.offline;

import utils.file.FileUtils;
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

public class DocumentVectors {
	private static Logger logger = Logger.getLogger(DocumentVectors.class);
	
	public static List<String> tokenize(String doc) {
		String[] words = doc.split("\\s+");
		return Arrays.asList(words);
	}
	
	public static class WordTokenizer extends Mapper<Text, Text, Text, Text>  {
		
		@Override
		public void map(Text url, Text doc, Context context) throws IOException, InterruptedException {
			logger.info(String.format("map input: key=%s, value=%s", url.toString(), doc.toString()));
			List<String> words = tokenize(doc.toString());

			logger.info(String.format("map: key=%s, values=%s", url.toString(), words.toString()));
			
			for(String word: words) {
				logger.info(String.format("map context write: key=%s, value=%s", url.toString(), word));
				context.write(url, new Text(word));
			}
		}
	}
	
	public static class TFVector extends Reducer<Text, Text, Text, Text> {

		@Override
		public void reduce(Text url, Iterable<Text> words, Context context) throws IOException, InterruptedException {
			Map<Text, Integer> wordCounts = new HashMap<Text, Integer>();
			Text maxWord = null;
			
			// determine word counts.
			Iterator<Text> iter = words.iterator();
			while(iter.hasNext()) {
				Text word = iter.next();
				Integer counts = wordCounts.get(word);
							
				if(counts == null)
					wordCounts.put(new Text(word.toString()), 1); // FIXME: why do we have to copy the word here?
				else
					wordCounts.put(word, counts+1);
				
				logger.info(String.format("reduce: key=%s, value=(%s, %d)", 
						url.toString(), 
						word.toString(), 
						wordCounts.get(word)));
				
				// update maxWord, if necessary:
				if(maxWord == null || wordCounts.get(maxWord) < wordCounts.get(word))
					maxWord = new Text(word.toString());
			}
			
			// emit the counts to output file:
			for(Text word: wordCounts.keySet()) {
				double tfScore = getTermFrequency(word, maxWord, wordCounts);
				Text wordCount = new Text(String.format("%s\t%d\t%f", word, wordCounts.get(word), tfScore));
				context.write(url, wordCount);
			}
		}
		
		private double getTermFrequency(Text word, Text maxWord, Map<Text, Integer> counts) {
			double alpha = 0.5; // TODO: tune this parameter!
			return alpha + (1 - alpha) * counts.get(word) / (float) counts.get(maxWord);
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		logger.info(String.format("starting job with args: inputdir=%s, outputdir=%s", args[0], args[1]));
		
		FileUtils.deleteIfExists(java.nio.file.Paths.get(args[1]));
		
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "build document vectors");
		job.setJarByClass(DocumentVectors.class);
		
		job.setMapperClass(WordTokenizer.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setReducerClass(TFVector.class);
		
		job.setInputFormatClass(DocumentInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
