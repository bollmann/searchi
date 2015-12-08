package pagerank.phase2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PRFinalAggReducer extends Reducer<Text, Text, Text, Text> {
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
		throws IOException, InterruptedException {
		
		double pageRank = 0.0;
		
		for (Text value : values) {
			pageRank += new Double(value.toString());			
		}
		context.write(key, new Text(Double.toString(pageRank)));
	}

}
