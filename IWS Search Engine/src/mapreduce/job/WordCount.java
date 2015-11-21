package mapreduce.job;

import java.util.Arrays;

import mapreduce.Context;
import mapreduce.Job;

import org.apache.log4j.Logger;

public class WordCount implements Job {
	private Logger logger = Logger.getLogger(getClass());

	public void map(String key, String value, Context context) {
		logger.info("Word count map job got key:" + key + " value:" + value);
		for(String word : value.split(" ")) {
			context.write(word, "1");
		}
	}

	public void reduce(String key, String[] values, Context context) {
		long sum = 0;
		logger.info("Word count reduce job got key:" + key + " value:" + Arrays.toString(values));
		for (String value : values) {
			int intValue = Integer.parseInt(value);
			sum += intValue;
		}
		logger.info("Word count reduce job writing " + key + ":" + sum);
		context.write(key, String.valueOf(sum));
	}

}
