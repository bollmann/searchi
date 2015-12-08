package pagerank.phase2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PRFinalAggMapper extends Mapper<Text, Text, Text, Text> {
	
	private static final String DUMMY_ACC_PAGE = "DUMMY_ACC_PAGE";
	private static final String DELIM_PR_LINKS = "###";
	private static final String DELIM_SINGLE_SPACE = " ";
	
	@Override
	public void map(Text key, Text value, Context context) 
		throws IOException, InterruptedException {
		
		String [] pageRankData = value.toString().split(DELIM_PR_LINKS);
		
		String pageRankStr = pageRankData[0].trim();
		if (key.toString().equals(DUMMY_ACC_PAGE)) {
			String[] outLinks = pageRankData[1].trim().split(DELIM_SINGLE_SPACE);
			int outLinksCount = outLinks.length;
				
			double pageRankDist = new Double(pageRankStr) / outLinksCount; 
			for (int i = 0; i < outLinksCount; ++i) {
				context.write(new Text(outLinks[i]), new Text(Double.toString(pageRankDist)));
			}				
			
		} else {
			context.write(key, new Text(pageRankStr));
		}
	}
}
