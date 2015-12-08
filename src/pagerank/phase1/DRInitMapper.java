package pagerank.phase1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import utils.string.StringUtils;

import com.google.gson.Gson;

public class DRInitMapper extends Mapper<LongWritable, Text, Text, Text> {
	
	private class PageBlob {
		String url;
		List<String> outgoingLinks = new ArrayList<String>();
	}

	@Override
	public void map(LongWritable lineNr, Text jsonBlob, Context context)
			throws IOException, InterruptedException {

		Gson gson = new Gson();
		PageBlob blob = gson.fromJson(jsonBlob.toString(), PageBlob.class);

		String urlDomain = StringUtils.getDomainFromUrl(blob.url.trim());
		Text url = new Text(urlDomain);
		Text outLinks = new Text(StringUtils.listToDomainString(
				blob.outgoingLinks,	" ", Arrays.asList(urlDomain)));

		context.write(url, outLinks);
	}

}
