package pagerank.phase2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PRComputeMapper extends Mapper<Text, Text, Text, Text> {
	
	private static final String DELIM_SINGLE_SPACE = " ";
	private static final String DELIM_SINGLE_HASH = "#";

	@Override
	public void map(Text key, Text value, Context context)
		throws IOException, InterruptedException {
		
		String [] pageRankData = value.toString().split(DELIM_SINGLE_HASH);
		
		if (pageRankData.length > 1) {
			String pageRankStr = pageRankData[0].trim();
			
			String [] outLinks = pageRankData[1].trim().split(DELIM_SINGLE_SPACE);
			int outLinksCount = outLinks.length;
			
			for(int i = 0; i < outLinksCount; i++) {
				
				StringBuffer oVal = new StringBuffer(pageRankStr);
				oVal.append(DELIM_SINGLE_HASH);
				oVal.append(Integer.toString(outLinksCount));
				context.write(new Text(outLinks[i].trim()), new Text(oVal.toString()));
			}
			context.write(key, new Text(pageRankData[1].trim()));
		}		
	}
}
