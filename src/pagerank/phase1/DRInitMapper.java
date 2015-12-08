package pagerank.phase1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import utils.string.StringUtils;

import com.google.gson.Gson;

public class DRInitMapper extends Mapper<Text, Text, Text, Text> {
	
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

		String urlDomain = StringUtils.getDomainFromUrl(blob.url.trim());
		Text url = new Text(urlDomain);

		// TODO - CHECK FOR MEMORY FOOTPRINT FOR DUMMY_ACC_PAGE
		
		StringBuffer strBuf = new StringBuffer();
		boolean isFirst = true;
		Set<String> isSeen = new HashSet<>();
		
		List<String> outLinks = (blob.outgoingLinks == null) 
			? new ArrayList<String>() : blob.outgoingLinks;
			
		for (String outLink : outLinks) {
			String linkDomain = StringUtils.getDomainFromUrl(outLink.trim());			
			if (linkDomain == null || linkDomain.isEmpty()) {
				continue;
			}
			if (isSeen.contains(linkDomain)) {
				continue;
			}
			isSeen.add(linkDomain);
			
			if (linkDomain.equals(urlDomain)) {
				continue;
			}
			
			if (!isFirst) {
				strBuf.append(" ");
			}
			strBuf.append(linkDomain);
			isFirst = false;
			
			Text domain = new Text(linkDomain);
			context.write(domain, new Text(DUMMY_ACC_PAGE));
			context.write(new Text(DUMMY_ACC_PAGE), domain);
		}
		
		// If there are no outgoing links
		String outLinksStr = strBuf.toString();
		if (outLinksStr.isEmpty()) {
			outLinksStr = DUMMY_ACC_PAGE;
		}		
	
		context.write(new Text(DUMMY_ACC_PAGE), url);
		context.write(url, new Text(outLinksStr));
	}
	
	
}
