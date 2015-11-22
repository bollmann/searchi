/*
 * Written by Shreejit Gangadharan
 */
package crawler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import parsers.Parser;
import threadpool.MercatorQueue;
import threadpool.Queue;
import threadpool.ThreadPool2;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crawler.shutdownHook.ShutdownHook;
import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;
import errors.QueueFullException;

/**
 * The Class XPathCrawler.
 */
public class XPathCrawler {
	private static Logger logger = Logger.getLogger(XPathCrawler.class);
	public static Integer maxUrls = Integer.MAX_VALUE;

	public static void main(String[] args) {

		Integer maxFileSize = Integer.parseInt(args[0]);
		Parser.setMaxFileSize(maxFileSize);

		maxUrls = Integer.parseInt(args[1]);

		logger.info("params: " + Arrays.toString(args));

		DynamoDBWrapper ddb = DynamoDBWrapper
				.getInstance(DynamoDBWrapper.URL_CONTENT_ENDPOINT);
		S3Wrapper s3 = S3Wrapper.getInstance();
		if (args[2].equals("yes")) {
			ddb.deleteTable("URLMetaInfo");
			s3.deleteBucket(s3.URL_BUCKET);
			s3.deleteBucket(s3.URL_QUEUE_BUCKET);
		}

		s3.createBucket(s3.URL_BUCKET);
		s3.createBucket(s3.URL_QUEUE_BUCKET);
		ddb.createTable("URLMetaInfo", 5, 5, "url", "S");

		// look for queue in s3. If not there, then initialize to new
		Queue<String> q = null;
		try {
			String queueContent = s3.getItem(s3.URL_QUEUE_BUCKET, "queueState");
			Type listType = new TypeToken<Queue<String>>() {
			}.getType();
			System.out.println("Reading queue for s3. Resuming saved state.");
			q = new Gson().fromJson(queueContent, listType);
		} catch (Exception e) {
			e.printStackTrace();
			q = new Queue<String>(1000);
		}

		MercatorQueue mq = new MercatorQueue();

		Runtime.getRuntime().addShutdownHook(new ShutdownHook(q));

		mq.setOutgoingJobQueue(q);

		List<String> seedUrls = new ArrayList<String>() {
			{
				add("https://en.wikipedia.org/wiki/Main_Page");
				add("https://www.reddit.com/");
			}
		};
		try {
			for (String url : seedUrls) {
				q.enqueue(url);
			}
		} catch (QueueFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		XPathProducer p = new XPathProducer(mq, q);

		ThreadPool2 tp = ThreadPool2.getInstance();

		for (int i = 0; i < ThreadPool2.MAX_SIZE - 1; i++) {
			tp.addThread(new XPathWorker(mq, q));
		}
		tp.addThread(new Thread(p));
		tp.start();
	}

	public static void usage() {
		System.out
				.println("java -cp lib/*:target/WEB-INF/crawler.XPathCrawler "
						+ "<max_file_size> <max_urls> <refresh_database?yes/no>");
		System.exit(1);
	}
}
