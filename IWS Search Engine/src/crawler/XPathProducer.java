package crawler;

import org.apache.log4j.Logger;

import threadpool.MercatorQueue;
import threadpool.Queue;
import threadpool.ThreadPool2;

public class XPathProducer extends Thread {
	private static Logger logger = Logger.getLogger(XPathProducer.class);
	private MercatorQueue mq;
	Queue<String> q;

	public XPathProducer(MercatorQueue mq, Queue<String> q) {
		this.mq = mq;
		this.q = q;
	}

	/**
	 * Shutdown.
	 */
	public static void shutdown() {
		logger.error("Crawler is shutting down!");
		ThreadPool2 pool = ThreadPool2.getInstance();
		pool.shutdown();
		System.exit(0);
	}

	public void run() {
		logger.debug("XPathProducer started!");
		int timesZero = 0, maxTimesZeroInARow = 10;
		while (!ThreadPool2.getInstance().isShouldShutdown()) {

			mq.checkAndNotifyQueues();

			int urlCount = mq.getUrlsProcessed();
			if (urlCount >= XPathCrawler.maxUrls) {
				System.out.println("Shutting down as encountered " + urlCount
						+ " urls.");
				XPathProducer.shutdown();
			}
			
			if(mq.getSize() > 0) {
				timesZero = 0;
			} else {
				timesZero++;
			}
			if(timesZero == maxTimesZeroInARow) {
				System.out.println("Queue has been empty for a while. Quitting...");
				XPathProducer.shutdown();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logger.info("Producer shutting down");
	}

}
