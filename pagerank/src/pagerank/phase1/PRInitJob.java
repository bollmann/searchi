package pagerank.phase1;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public final class PRInitJob extends Configured implements Tool {

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = getConf();
        JobConf job = new JobConf(conf, PRInitJob.class);
        
        Path in = new Path(args[0]);
        Path out = new Path(args[1]);
        FileInputFormat.setInputPaths(job, in);
        FileOutputFormat.setOutputPath(job, out);
        
        job.setJobName("PageRankInit");
        job.setMapperClass(PRInitMapper.class);
        job.setReducerClass(PRInitReducer.class);

        job.setInputFormat(KeyValueTextInputFormat.class);
        job.set("key.value.separator.in.input.line", "\t");

        job.setOutputFormat(TextOutputFormat.class);
        
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        
        JobClient.runJob(job);
		return 0;
	}
	
	public static void main(String[] args) throws Exception {
		int res = ToolRunner.run(new Configuration(), new PRInitJob(), args);
		System.exit(res);
	}
}
