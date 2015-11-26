package crawler.threadpool;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ThreadPool2 {
	private final Logger logger = Logger.getLogger(getClass());
	private static ThreadPool2 instance;
	
	private List<Thread> threadList;
	
	public static Integer MAX_SIZE = 15;
	
	private Boolean shouldShutdown = false;
	
	public synchronized boolean isShouldShutdown() {
		return shouldShutdown;
	}

	public synchronized void setShouldShutdown(boolean shouldShutdown) {
		this.shouldShutdown = shouldShutdown;
	}

	public static ThreadPool2 getInstance() {
		if(instance == null) {
			instance = new ThreadPool2();
		}
		return instance;
	}
	
	private ThreadPool2() {
		threadList = new ArrayList<Thread>(MAX_SIZE);
	}
	
	public void shutdown() {
		shouldShutdown = true;
		int threadsShut = 0;
		for (Thread t : threadList) {
			try {
				t.interrupt();
				logger.info("Waiting for " + t.getName() + ". "
						+ threadsShut + " threads stopped.");
				t.join();
				threadsShut++;
				logger.info("Finished waiting for " + t.getName() + ". "
						+ threadsShut + " threads stopped.");
			} catch (InterruptedException e) {
				logger.error("Couldn't gracefully wait for thread stops!" + e.getMessage());
				e.printStackTrace();
			}
		}
		System.exit(0);
	}
	
	public void addThread(Thread thread) {
		logger.info("Adding thread " + thread.getName() + " to pool");
		threadList.add(thread);
	}
	
	public void start() {
		for(Thread thread : threadList) {
			thread.start();
		}
	}
}
