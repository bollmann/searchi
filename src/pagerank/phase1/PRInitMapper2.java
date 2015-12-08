package pagerank.phase1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import com.google.gson.Gson;

import utils.string.StringUtils;

public class PRInitMapper2 extends Mapper<Text, Text, Text, Text> {
	
	private static final String DUMMY_ACC_PAGE = "DUMMY_ACC_PAGE";
	
	private class PageBlob {
		String url;
		List<String> outgoingLinks;
	}

	@Override
	public void map(Text key, Text jsonBlob, Context context)
			throws IOException, InterruptedException {

		Gson gson = new Gson();
		PageBlob blob = gson.fromJson(jsonBlob.toString(), PageBlob.class);
		if (blob == null) return;

		String normalizedUrl = StringUtils.normalizeUrlToString(blob.url.trim());
		Text url = new Text(normalizedUrl);
		
//		String outLinksStr = StringUtils.listToString(blob.outgoingLinks,
//				" ", Arrays.asList(normalizedUrl));
		
		// TODO - CHECK FOR MEMORY FOOTPRINT FOR DUMMY_ACC_PAGE
		
		StringBuffer strBuf = new StringBuffer();
		boolean isFirst = true;
		Set<String> isSeen = new HashSet<>();
		
		List<String> outLinks = (blob.outgoingLinks == null) 
			? new ArrayList<String>() : blob.outgoingLinks;
			
		for (String outLink : outLinks) {
			String normalUrl = StringUtils.normalizeUrlToString(outLink.trim());			
			if (normalUrl == null || normalUrl.isEmpty()) {
				continue;
			}
			if (isSeen.contains(normalUrl)) {
				continue;
			}
			isSeen.add(normalUrl);
			
			if (normalUrl.equals(normalizedUrl)) {
				continue;
			}
			
			if (!isFirst) {
				strBuf.append(" ");
			}
			strBuf.append(normalUrl);
			isFirst = false;
			
			Text outUrl = new Text(normalUrl);
			context.write(outUrl, new Text(DUMMY_ACC_PAGE));
			context.write(new Text(DUMMY_ACC_PAGE), outUrl);
		}
		
		// If there are no outgoing links
		String outLinksStr = strBuf.toString();
		if (outLinksStr.isEmpty()) {
			outLinksStr = DUMMY_ACC_PAGE;
		}

		context.write(url, new Text(outLinksStr));
		context.write(new Text(DUMMY_ACC_PAGE), url);
	}

}