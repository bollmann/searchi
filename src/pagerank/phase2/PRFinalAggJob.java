package pagerank.phase2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;

public final class PRFinalAggJob extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {Configuration conf = new Configuration();
	
	Job job = Job.getInstance(conf);
	job.setJarByClass(PRComputeJob.class);

	FileInputFormat.addInputPath(job, new Path(args[0]));
	FileOutputFormat.setOutputPath(job, new Path(args[1]));

	job.setJobName("PageRankFinalAggregation");
	job.setMapperClass(PRFinalAggMapper.class);
	job.setReducerClass(PRFinalAggReducer.class);

	job.setInputFormatClass(KeyValueTextInputFormat.class);
	
	job.setOutputFormatClass(TextOutputFormat.class);

	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(Text.class);
	
	return job.waitForCompletion(true) ? 0 : 1;
	}

}
