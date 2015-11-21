/*
 * Written by Shreejit Gangadharan
 */
package webserver;

import handlers.MainHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import threadpool.Queue;
import threadpool.ThreadPool;
import errors.QueueFullException;

// TODO: Auto-generated Javadoc
/**
 * The Class RunnerDaemon.
 */
public class RunnerDaemon extends Thread {
	
	/** The socket. */
	private static ServerSocket socket = null;
	
	/** The job queue. */
	private static Queue<Socket> jobQueue = null;
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(RunnerDaemon.class);
	
	/** The port. */
	private int port;
	
	/** The web root. */
	private String webRoot;

	/** The servlets. */
	private Map<String, HttpServlet> servlets;
	
	/**
	 * Instantiates a new runner daemon.
	 *
	 * @param port the port
	 * @param webRoot the web root
	 * @param webDotXml the web dot xml
	 * @param servlets the servlets
	 */
	public RunnerDaemon(int port, String webRoot, String webDotXml, Map<String, HttpServlet> servlets) {
		this.port = port;
		this.webRoot = webRoot;
		this.servlets = servlets;
	}

	/**
	 * Shutdown.
	 */
	public static void shutdown() {
		logger.error("Main runner is shutting down!");
		ThreadPool pool = ThreadPool.getInstance();
		int threadsShut = 0;
		for (Thread t : pool.getThreadList()) {
			try {
				t.interrupt();
				logger.debug("Waiting for " + t.getName());
				t.join();
				threadsShut++;
				logger.debug("Finished waiting for " + t.getName() + ". "
						+ threadsShut + " threads stopped.");
			} catch (InterruptedException e) {
				logger.error("Couldn't gracefully wait for thread stops!" + e.getMessage());
				e.printStackTrace();
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			logger.error("Couldn't close server socket properly" + e.getMessage());
			e.printStackTrace();
		}
		System.exit(0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		jobQueue = new Queue<Socket>();

		List<MainHandler> threadList = new ArrayList<MainHandler>();
		for (int i = 0; i < ThreadPool.getMaxSize(); i++) {
			MainHandler newHandler = new MainHandler(jobQueue, webRoot, servlets);
			newHandler.setName("MainHandler-" + i);
			threadList.add(newHandler);
		}
		ThreadPool pool = ThreadPool.getInstance(threadList);
		pool.start();
		
		try {
			socket = new ServerSocket(port);
		} catch (IOException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		logger.debug("Starting main accept loop in RunnerDaemon");
		synchronized (jobQueue) {
			if (jobQueue.getSize() > 0) {
				jobQueue.notify();
			}
		}
		while (!ThreadPool.getInstance().getShouldShutdown()) {
			logger.debug("Listening for another socket");

			Socket clientSock = null;
			try {
				clientSock = socket.accept();
			} catch (IOException e) {
				logger.error("Couldn't accept on server socket:" + e.getMessage());
				e.printStackTrace();
				break;
			}
			
			logger.debug("Client socket shifted to " + clientSock.getLocalPort()
					+ " " + clientSock.getPort());

			try {
				synchronized (jobQueue) {
					logger.debug("Enqueuing clientSock");
//					clientSock.setSoTimeout(300);
					jobQueue.enqueue(clientSock);
					jobQueue.notify();
				}
			} catch (QueueFullException 
//					| SocketException
					e) {
				logger.error(e.getMessage());
			}
		}
		logger.info("Server is shutting down. RunnerDaemon out of loop.");
		try {
			socket.close();
		} catch (IOException e) {
			logger.error("Couldn't close socket!");
			e.printStackTrace();
		}
	}
}
