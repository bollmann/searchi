package crawler.servlet.multinodal.producer;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import crawler.servlet.multinodal.status.WorkerStatus;
import crawler.threadpool.DiskBackedQueue;
import crawler.threadpool.MercatorQueue;
import crawler.threadpool.ThreadPool2;
import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;

public class UrlProducer extends Thread {
	private final Logger logger = Logger.getLogger(UrlProducer.class);
	private MercatorQueue mq;
	private DiskBackedQueue<String> q;
	private Integer maxUrls = 100;
	Map<String, WorkerStatus> workerStatusMap;
	
	public UrlProducer(MercatorQueue mq, DiskBackedQueue<String> q, Integer maxUrls, Map<String, WorkerStatus> workerStatusMap) {
		this.mq = mq;
		this.q = q;
		this.maxUrls = maxUrls;
		this.workerStatusMap = workerStatusMap;
	}
	
	public Integer getUrlsProcessed(Map<String, WorkerStatus> workerStatusMap) {
		Integer sum = 0;
		for(Entry<String, WorkerStatus> entry : workerStatusMap.entrySet()) {
			sum += entry.getValue().getUrlProcessed();
		}
		
		if(sum % (maxUrls / 20) == 0) {
			logger.warn("Checked workers and found a total of " + sum + "urls processed out of " + maxUrls);
		}
		return sum;
	}
	
	public void shutdown() {
		System.out.println("Exiting gracefully");
		S3Wrapper s3 = S3Wrapper.getInstance();
		String queueContent = new Gson().toJson(q);
		s3.putItem(s3.URL_QUEUE_BUCKET, "queueState", queueContent);
		System.exit(0);
	}

	public void run() {
		logger.debug("UrlProducer started!");
		int timesZero = 0, maxTimesZeroInARow = 10, maxSecsEmpty = 30;
		Date emptyStartTime = Calendar.getInstance().getTime();
		while (!ThreadPool2.getInstance().isShouldShutdown()) {

			mq.checkAndNotifyQueues();

			int urlCount = getUrlsProcessed(workerStatusMap);
			
			if (urlCount >= maxUrls) {
				System.out.println("Shutting down as encountered " + urlCount
						+ " urls.");
				shutdown();
				return;
			}
			
			if(mq.getSize() > 0) {
				timesZero = 0;
				emptyStartTime = Calendar.getInstance().getTime();
			} else {
				Date nowTime = Calendar.getInstance().getTime();
				long secsEmpty = (nowTime.getTime() - emptyStartTime.getTime()) / 1000;
				if(secsEmpty > maxSecsEmpty) {
					System.out.println("Queue has been empty for a while. Quitting...");
					shutdown();
					return;
				}
			}
			/*
			 * if(timesZero == maxTimesZeroInARow) {
				System.out.println("Queue has been empty for a while. Quitting...");
				shutdown();
				return;
			}
			*/

			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Producer shutting down");
	}

}
