package pagerank.phase2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

public final class PRComputeJob extends Configured implements Tool {

	private static final Logger logger = Logger.getLogger(PRComputeJob.class);
	
	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(PRComputeJob.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setJobName("PageRankCompute");
		job.setMapperClass(PRComputeMapper.class);
		job.setReducerClass(PRComputeReducer.class);

		job.setInputFormatClass(KeyValueTextInputFormat.class);
		
		job.setOutputFormatClass(TextOutputFormat.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static void main(String [] args) throws Exception {
		
		int numIter = 5;
		if (args.length > 2) {
			numIter = Integer.parseInt(args[2]);
		} 
		
		String input = args[0];
		String out = args[1];
		String tempOut;
		int res = 0;
		
		for (int i = 1; i <= numIter; ++i) {
			tempOut = out + "_" + Integer.toString(i); 
			String [] jobArgs = {input, tempOut};
			res = ToolRunner.run(new PRComputeJob(), jobArgs);
			if (res != 0) {				
				break;
			}
			input = tempOut;			
		}
		
		String [] jobArgs = {input, out};
		res = ToolRunner.run(new PRFinalAggJob(), jobArgs);
		
		System.exit(res);
	}

}
