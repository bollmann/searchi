package pagerank.phase2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PRFinalAggMapper extends Mapper<Text, Text, Text, Text> {
	
	private static final String DELIM_PR_LINKS = "###";
	
	@Override
	public void map(Text key, Text value, Context context) 
		throws IOException, InterruptedException {
		
		String [] pageRankData = value.toString().split(DELIM_PR_LINKS);
		
		if (pageRankData.length > 1) {			
			String pageRankStr = pageRankData[0].trim();		
			context.write(key, new Text(pageRankStr));
		}
	}
}
