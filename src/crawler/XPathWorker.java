package crawler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.apache.log4j.Logger;

import com.google.gson.JsonSyntaxException;

import crawler.threadpool.MercatorQueue;
import crawler.threadpool.Queue;
import crawler.threadpool.ThreadPool2;

public class XPathWorker extends Thread {
	private final Logger logger = Logger.getLogger(getClass());
	private MercatorQueue mq;
	private Queue<String> q;

	public XPathWorker(MercatorQueue mq, Queue<String> q) {
		this.mq = mq;
		this.q = q;
	}

	public void run() {
		logger.debug("XPathWorker started!");
		while (!ThreadPool2.getInstance().isShouldShutdown()) {
			String url = null;
			synchronized (q) {
				try {
					logger.info("Thread " + getName() + " waiting for jobQueue");
					if (q.getSize() <= 0)
						q.wait();
				} catch (InterruptedException e) {
					logger.error("Thread got interrupt. Shutting down");
					e.printStackTrace();
					break;
				}
				logger.info("Thread " + getName() + " received notify. Waking up!");
				url = q.dequeue();
				
			}

			logger.debug("Dequeued url " + url);
			// now process url
			URLHandler uh = new URLHandler(mq, q);
			try {
				uh.handleURL(url);
			} catch (IOException | ParseException | JsonSyntaxException | URISyntaxException e) {

				e.printStackTrace();
				continue;
			}
			
			// end try processing of url
		} // end inf while
		logger.info("Worker  shutting down.");
	}
}
