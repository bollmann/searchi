package mapreduce.lib;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.log4j.Logger;

import com.google.gson.Gson;

import utils.string.StringUtils;

public class URLsFromJsonReader extends RecordReader<Text, Text> {
	private class PageBlob {
		String url;
		List<String> outgoingLinks;
	}
	
	private Text url;
	private Text outLinks;
	private int done;
	
	private static Logger logger = Logger.getLogger(URLsFromJsonReader.class);
	
	@Override
	public void close() throws IOException { }

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return url;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		done = 1;
		return outLinks;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return done;
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		
		if (!(split instanceof FileSplit))
			throw new IOException("InputSplit doc is not a FileSplit!");
		
		FileSplit docSplit = (FileSplit) split;
		final Path path = docSplit.getPath();
				
		FileSystem fs = path.getFileSystem(context.getConfiguration());
		FSDataInputStream in = fs.open(path);
		Gson gson = new Gson();
		
		PageBlob blob = gson.fromJson(new InputStreamReader(in, Charset.forName("UTF-8")), PageBlob.class);
		in.close();
		
		url = new Text(blob.url.trim());
		outLinks = new Text(StringUtils.listToString(blob.outgoingLinks, " ",
			Arrays.asList(blob.url.trim())));
		done = 0;
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		return (done == 0);
	}
	
}
