/*
 * Written by Shreejit Gangadharan
 */
package crawler.threadpool;

import java.util.List;

import org.apache.log4j.Logger;

import crawler.handlers.MainHandler;
import crawler.webserver.RunnerDaemon;

// TODO: Auto-generated Javadoc
/**
 * The singleton ThreadPool used to maintain the workers.
 */
public class ThreadPool {
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(ThreadPool.class);
	
	/** The runner. */
	public RunnerDaemon runner;
	
	/** The thread pool. */
	private static ThreadPool threadPool;
	
	/** The Constant MAX_THREADS. */
	public static final Integer MAX_THREADS = 20;

	/** The thread list. */
	private List<MainHandler> threadList;
	
	/** The should shutdown. */
	private Boolean shouldShutdown = false;
	
	/**
	 * Gets the thread list.
	 *
	 * @return the thread list
	 */
	public List<MainHandler> getThreadList() {
		return threadList;
	}

	/**
	 * Sets the should shutdown.
	 *
	 * @param shouldIt the new should shutdown
	 */
	public void setShouldShutdown(boolean shouldIt) {
		synchronized (shouldShutdown) {
			shouldShutdown = shouldIt;
		}
	}

	/**
	 * Gets the should shutdown.
	 *
	 * @return the should shutdown
	 */
	public boolean getShouldShutdown() {
		synchronized (shouldShutdown) {
			return shouldShutdown;
		}
	}

	/**
	 * Gets the max size.
	 *
	 * @return the max size
	 */
	public static Integer getMaxSize() {
		return MAX_THREADS;
	}

	/**
	 * Instantiates a new thread pool.
	 *
	 * @param threadList the thread list
	 */
	private ThreadPool(List<MainHandler> threadList) {
		// threadList = new ArrayList();
		// Class myClass = Class.forName(className);
		// Constructor constructor = myClass.getConstructor();
		// for(int i=0;i<MAX_THREADS;i++) {
		// threadList.add();
		// }
		// Prune
		if (threadList.size() > MAX_THREADS) {
			for (int i = MAX_THREADS; i < threadList.size(); i++) {
				threadList.remove(i);
			}
		}
		this.threadList = threadList;
	}

	/**
	 * Gets the single instance of ThreadPool.
	 *
	 * @param threadList the thread list
	 * @return the singleton threadpool instance
	 */
	public static ThreadPool getInstance(List<MainHandler> threadList) {
		if (threadPool == null) {
			logger.info("Creating new threadpool with list size:" + threadList.size());
			threadPool = new ThreadPool(threadList);
		}
		return threadPool;
	}
	
	/**
	 * Destroys the threadpool.
	 */
	public static void destroyThreadPool() {
		if(threadPool != null)
			threadPool.threadList.clear();
		threadPool = null;
	}
	
	/**
	 * Gets the single instance of ThreadPool.
	 *
	 * @return the current threadpool instance
	 */
	public static ThreadPool getInstance() {
		return threadPool;
	}

	/**
	 * Starts all the threads in the pool.
	 */
	public void start() {
		for (int i = 0; i < MAX_THREADS; i++) {
			threadList.get(i).start();
		}
	}

	/**
	 * Gets the statuses.
	 *
	 * @return the statuses
	 */
	public String[] getStatuses() {
		StringBuilder sb = new StringBuilder();
		for(Thread t : threadList) {
			sb.append(t.getName() + " " + t.isAlive());
		}
		return new String[1];
	}
}
