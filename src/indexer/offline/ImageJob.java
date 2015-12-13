package indexer.offline;

import indexer.WordCounts;
import indexer.db.dao.ImageIndex;
import indexer.offline.Tokenizer.TokenType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import utils.file.FileUtils;
import utils.nlp.Dictionary;

import com.google.gson.Gson;

import crawler.dao.URLContent;

public class ImageJob {
	public static double ENGLISH_IMAGE_TRESHOLD = 0.3;
	
	public static class ImageExtractor extends Mapper<Text, Text, Text, Text> {
		@Override
		public void map(Text docId, Text json, Context context)
				throws IOException, InterruptedException {
			URLContent page = new Gson().fromJson(json.toString(),
					URLContent.class);
			Document doc = Jsoup.parse(page.getContent(), page.getUrl());
			Elements images = doc.select("img[src][alt],img[src][title]");

			Dictionary dict = Dictionary.createInstance(Dictionary.JAR_RESOURCE);
			for (Element image : images) {
				String imageUrl = image.attr("src");
				
				// if url is malformed, don't include image at all.
				try {
					URL isGoodURL = new URL(imageUrl);
				} catch (MalformedURLException e) {
					continue;
				}
				
				WordCounts alt = new WordCounts(
					new Tokenizer(image.attr("alt"), TokenType.UNIGRAM).getTokens(), 1, dict);
				WordCounts title = new WordCounts(
					new Tokenizer(image.attr("title"), TokenType.UNIGRAM).getTokens(), 1, dict);
				
				if(alt.getPercentage() < ENGLISH_IMAGE_TRESHOLD || 
						title.getPercentage() < ENGLISH_IMAGE_TRESHOLD)
					continue;
				
				for (String word: alt)
					context.write(new Text(word), new Text(imageUrl));
				for (String word: title)
					context.write(new Text(word), new Text(imageUrl));
			}
		}
	}

	public static class ImageAggregator extends
			Reducer<Text, Text, NullWritable, Text> {
		public static int MAX_URLS_PER_IMAGE_WORD = 800;

		@Override
		public void reduce(Text imageWord, Iterable<Text> urls, Context context)
				throws IOException, InterruptedException {
			ImageIndex image = new ImageIndex();
			image.setImageWord(imageWord.toString());
			Set<String> imageUrls = new HashSet<>();

			Iterator<Text> iter = urls.iterator();
			for (int i = 0; i < MAX_URLS_PER_IMAGE_WORD && iter.hasNext(); ++i)
				imageUrls.add(iter.next().toString());
			image.setImageUrls(imageUrls);

			context.write(NullWritable.get(),
				new Text(new Gson().toJson(image)));
		}
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		System.out.println(String.format("starting job with args: inputdir=%s, outputdir=%s",
				args[0], args[1]));

		FileUtils.deleteIfExists(java.nio.file.Paths.get(args[1]));

		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "build image index");
		job.setJarByClass(ImageJob.class);

		job.setMapperClass(ImageExtractor.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setReducerClass(ImageAggregator.class);
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}