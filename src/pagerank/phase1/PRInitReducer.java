package pagerank.phase1;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PRInitReducer extends Reducer<Text, Text, Text, Text> {
	
	private static final String DUMMY_ACC_PAGE = "DUMMY_ACC_PAGE";
	private static final String DELIM_PR_LINKS = "###";
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		
		String initPageRank = 
			key.toString().equals(DUMMY_ACC_PAGE) ? "0.0" : "1.0";
		
		StringBuilder outVal = new StringBuilder();		
		for (Text value : values) {
			if (DUMMY_ACC_PAGE.equals(value.toString())) {
				continue;
			}
			outVal.append(" ");
			outVal.append(value.toString().trim());
		}
		
		if (outVal.toString().isEmpty() && !key.toString().equals(DUMMY_ACC_PAGE)) {
			outVal.append(DUMMY_ACC_PAGE);
		}
		
		context.write(key, new Text(initPageRank + DELIM_PR_LINKS + outVal.toString()));
	}
}
