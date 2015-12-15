package pagerank.phase2;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class PRComputeReducer extends Reducer<Text, Text, Text, Text>{
	
	private static final String DELIM_PR_LINKS = "###";
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context)
		throws IOException, InterruptedException {
		
		double pageRank = 0.0;
		String outLinks = "";
	
		for (Text value : values) {			
			String [] incomingPRData = value.toString().split(DELIM_PR_LINKS);
			
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
		oVal.append(DELIM_PR_LINKS);
		oVal.append(outLinks);
		
		context.write(key, new Text(oVal.toString()));		
	}

}
