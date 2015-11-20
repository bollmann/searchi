package pagerank.phase1;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

public class PRInitMapper extends MapReduceBase implements Mapper<Text, Text, Text, Text> {

	@Override
	public void map(Text key, Text value, OutputCollector<Text, Text> out,
			Reporter reporter) throws IOException {
		
		/* Map output in format L1		L2 L3 L5 L7 */		
		out.collect(key, value);		
	}
}
