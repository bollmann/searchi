package pagerank.phase1;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class PRInitReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text> {

	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> out, Reporter reporter) throws IOException {
		
		String initPageRank = "1.0";
		StringBuilder outVal = new StringBuilder(initPageRank + "#");
		
		
		while (values.hasNext()) {
			outVal.append(" ");
			outVal.append(values.next().toString().trim());
		}
		
		out.collect(key, new Text(outVal.toString()));
	}
}
