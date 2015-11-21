/*
 * Written by Shreejit Gangadharan
 */
package crawler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;
import parsers.Parser;
import threadpool.MercatorQueue;
import threadpool.Queue;
import threadpool.ThreadPool2;
import errors.QueueFullException;

/**
 * The Class XPathCrawler.
 */
public class XPathCrawler {
	private static Logger logger = Logger.getLogger(XPathCrawler.class);
	public static Integer maxUrls = Integer.MAX_VALUE;

	public static void main(String[] args) {

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Exiting gracefully");
				// save mercator queue state
			}
		});

		Integer maxFileSize = Integer.parseInt(args[0]);
		Parser.setMaxFileSize(maxFileSize);

		maxUrls = Integer.parseInt(args[1]);

		logger.info("params: " + Arrays.toString(args));

		DynamoDBWrapper ddb = DynamoDBWrapper.getInstance("http://localhost:8000");
		ddb.createTable("URLMetaInfo", 100, 100, "url", "S");
		
		S3Wrapper s3 = S3Wrapper.getInstance();
		s3.createBucket(s3.URL_BUCKET);
		
		
		MercatorQueue mq = new MercatorQueue();
		Queue<String> q = new Queue<String>(1000);
		mq.setOutgoingJobQueue(q);

		List<String> seedUrls = new ArrayList<String>() {
			{
				add("https://en.wikipedia.org/wiki/Main_Page");
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
						+ "<max_file_size> <max_urls>");
		System.exit(1);
	}
}
