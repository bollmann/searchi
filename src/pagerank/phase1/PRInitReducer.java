package pagerank.phase1;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PRInitReducer extends Reducer<Text, Text, Text, Text> {

	private static final String DELIM_PR_LINKS = "###";
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		
		String initPageRank = "1.0";
		StringBuilder outVal = new StringBuilder(initPageRank + DELIM_PR_LINKS);
		
		for (Text value : values) {
			outVal.append(" ");
			outVal.append(value.toString().trim());
		}
		
		context.write(key, new Text(outVal.toString()));
	}
}
