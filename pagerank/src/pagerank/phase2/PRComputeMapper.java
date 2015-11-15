package pagerank.phase2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class PRComputeMapper extends MapReduceBase implements Mapper<Text, Text, Text, Text> {
	
	private static final String DELIM_SINGLE_SPACE = " ";
	private static final String DELIM_SINGLE_HASH = "#";

	@Override
	public void map(Text key, Text value, OutputCollector<Text, Text> output,
			Reporter reporter) throws IOException {
		
		String [] pageRankData = value.toString().split(DELIM_SINGLE_HASH);
		
		if (pageRankData.length > 1) {
			String pageRankStr = pageRankData[0].trim();
			
			String [] outLinks = pageRankData[1].trim().split(DELIM_SINGLE_SPACE);
			int outLinksCount = outLinks.length;
			
			for(int i = 0; i < outLinksCount; i++) {
				
				StringBuffer oVal = new StringBuffer(pageRankStr);
				oVal.append(DELIM_SINGLE_HASH);
				oVal.append(Integer.toString(outLinksCount));
				output.collect(new Text(outLinks[i].trim()), new Text(oVal.toString()));
			}
			output.collect(key, new Text(pageRankData[1].trim()));
		}
		
	}

}
