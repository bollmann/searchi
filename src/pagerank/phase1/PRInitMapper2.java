package pagerank.phase1;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.google.gson.Gson;
import utils.string.StringUtils;

public class PRInitMapper2 extends Mapper<LongWritable, Text, Text, Text> {

	private class PageBlob {
		String url;
		List<String> outgoingLinks;
	}

	@Override
	public void map(LongWritable lineNr, Text jsonBlob, Context context)
			throws IOException, InterruptedException {

		Gson gson = new Gson();
		PageBlob blob = gson.fromJson(jsonBlob.toString(), PageBlob.class);

		Text url = new Text(blob.url.trim());
		Text outLinks = new Text(StringUtils.listToString(
				blob.outgoingLinks,	" ", Arrays.asList(blob.url.trim())));

		context.write(url, outLinks);
	}

}