/*
 * Written by Shreejit Gangadharan
 */
package crawler;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Logger;

import parsers.Parser;
import storage.DBWrapper;
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
		
		if (args.length < 3) {
			usage();
		}
		String url = args[0];
		String dbPathString = args[1];
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("Exiting gracefully");
				DBWrapper.close();
			}
		});
		File dbDir = new File(dbPathString);
		logger.info("Looking for persistent store at:" + dbDir.getAbsolutePath());
		if(!dbDir.exists()) {
			System.out.println("Creating " + dbDir.getAbsolutePath());
			if(dbDir.mkdirs()) {
				
			} else {
				System.out.println("Couldn't create Berkeley store. Exiting");
				System.exit(1);
			}
		}
		DBWrapper.initialize(dbDir.getAbsolutePath());
		Integer maxFileSize = Integer.parseInt(args[2]);
		Parser.setMaxFileSize(maxFileSize);
		
		if(args.length == 4) {
			maxUrls = Integer.parseInt(args[3]);
		}
		
		logger.info("params: " + Arrays.toString(args));

		MercatorQueue mq = new MercatorQueue();
		Queue<String> q = new Queue<String>(1000);
		mq.setOutgoingJobQueue(q);

		try {
			q.enqueue(url);
		} catch (QueueFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		XPathProducer p = new XPathProducer(mq, q);

		ThreadPool2 tp = ThreadPool2.getInstance();
		
		for(int i=0;i<ThreadPool2.MAX_SIZE-1;i++) {
			tp.addThread(new XPathWorker(mq, q, dbPathString));
		}
		tp.addThread(new Thread(p));
		tp.start();
	}

	public static void usage() {
		System.out
				.println("java -cp lib/*:target/WEB-INF/classes edu.upenn.cis455.crawler.XPathCrawler "
						+ "<starting_url> <path_to_berkeley_db> <max_file_size>");
		System.exit(1);
	}
}
