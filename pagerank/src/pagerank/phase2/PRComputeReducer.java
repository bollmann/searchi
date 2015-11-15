package pagerank.phase2;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

public class PRComputeReducer extends MapReduceBase implements Reducer<Text, Text, Text, Text>{
	
	private static final String DELIM_SINGLE_HASH = "#";
	
	@Override
	public void reduce(Text key, Iterator<Text> values,
			OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
		
		double pageRank = 0.0;
		String outLinks = "";
	
		while (values.hasNext()) {
			
			Text value = values.next();			
			String [] incomingPRData = value.toString().split(DELIM_SINGLE_HASH);
			
			if (incomingPRData.length == 1) {
				outLinks = incomingPRData[0].trim();
				continue;
			}
			
			if (incomingPRData.length > 1) {
				double inPageRank = new Double(incomingPRData[0].trim());
				int countLinks = new Integer(incomingPRData[1].trim());
				
				pageRank = pageRank + (inPageRank/countLinks); 
			}
		}
		
		// Factor in the damping
		double dampFactor = 0.85;
		pageRank = (1-dampFactor) + dampFactor * pageRank;
		
		// Form the output value as string
		StringBuffer oVal = new StringBuffer(Double.toString(pageRank));
		oVal.append(DELIM_SINGLE_HASH);
		oVal.append(outLinks);
		
		output.collect(key, new Text(oVal.toString()));		
	}

}
