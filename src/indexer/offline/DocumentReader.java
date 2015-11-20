package indexer.offline;

import java.io.IOException;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DocumentReader extends RecordReader<Text, Text> {
	private Text url;
	private Text words;
	private int done;
	
	private static Logger logger = Logger.getLogger(DocumentReader.class);
	
	@Override
	public void close() throws IOException { }

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return url;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		done = 1;
		return words;
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
		
		logger.debug(String.format("reading file split for file '%s'...\n", path.toString()));
		
		FileSystem fs = path.getFileSystem(context.getConfiguration());
		url = new Text(path.getName());
		
		FSDataInputStream in = fs.open(path);
		Document doc = Jsoup.parse(in, null, url.toString());
		words = new Text(doc.select("body").text());
		in.close();
		
		done = 0;
	}

	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		return (done == 0);
	}
}
