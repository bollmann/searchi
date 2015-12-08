package pagerank.phase1;

import java.nio.file.Paths;

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

import utils.file.FileUtils;

public final class PRInitJob extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = new Configuration();		
		// No need - It's default
		//conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator", "\t");
		
        Job job = Job.getInstance(conf);
        job.setJarByClass(PRInitJob.class);
         
        job.setJobName("PageRankInit");
        //job.setMapperClass(PRInitMapper.class);
        //job.setMapperClass(PRInitMapper2.class);
        job.setMapperClass(DRInitMapper.class);
        job.setReducerClass(PRInitReducer.class);

        job.setInputFormatClass(KeyValueTextInputFormat.class);
        //job.setInputFormatClass(URLInputFormat.class);
        //job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        Path in = new Path(args[0]);
        Path out = new Path(args[1]);
        FileInputFormat.addInputPath(job, in);
        FileOutputFormat.setOutputPath(job, out);
        
        return job.waitForCompletion(true) ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		// Delete output directory if it exists
		FileUtils.deleteIfExists(Paths.get(args[1]));
		
		int res = ToolRunner.run(new PRInitJob(), args);
		System.exit(res);
	}
}
