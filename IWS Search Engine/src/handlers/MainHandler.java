/*
 * Written by Shreejit Gangadharan
 */
package handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import parsers.Parser;
import responses.HttpResponse;
import servlets.SessionMap;
import threadpool.Queue;
import threadpool.ThreadPool;
import webserver.RunnerDaemon;

// TODO: Auto-generated Javadoc
/**
 * The Class MainHandler.
 */
public class MainHandler extends Thread {

	/** The job queue. */
	private Queue<Socket> jobQueue;

	/** The web root. */
	private String webRoot;

	/** The logger. */
	private static Logger logger = Logger.getLogger(MainHandler.class);

	/** The current url. */
	private String currentUrl;

	/** The servlets. */
	private Map<String, HttpServlet> servlets;

	/**
	 * Gets the current url.
	 *
	 * @return the current url
	 */
	public String getCurrentUrl() {
		return currentUrl;
	}

	/**
	 * Sets the current url.
	 *
	 * @param url
	 *            the new current url
	 */
	public void setCurrentUrl(String url) {
		currentUrl = url;
	}

	/**
	 * Instantiates a new main handler.
	 *
	 * @param jobQueue
	 *            the job queue
	 * @param webRoot
	 *            the web root
	 * @param servlets
	 *            the servlets
	 */
	public MainHandler(Queue<Socket> jobQueue, String webRoot,
			Map<String, HttpServlet> servlets) {
		this.jobQueue = jobQueue;
		this.webRoot = webRoot;
		this.servlets = servlets;
	}

	/**
	 * Parses the lines.
	 *
	 * @param in
	 *            the in
	 * @param out
	 *            the out
	 * @return the list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<String> parseLines(InputStream in, OutputStream out)
			throws IOException {
		String inputLine = null;
		List<String> fullInput = new ArrayList<String>();
		int linesRead = 0;
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		while (true) {
			inputLine = br.readLine();
			linesRead++;
			// TODO might be error prone
			if (linesRead == 1) {
				try {
					String version = Parser.parseRequestVersion(inputLine);
					if (version.equals("HTTP/1.1")
							|| version.equals("HTTP/1.2")) {
						logger.debug("Sending a 100 Continue response");
						String continueResponse = "HTTP/1.1 100 Continue"
								+ Parser.LINE_DELIMS;
						// TODO uncomment
						// out.write(continueResponse.getBytes());
					}
				} catch (Exception e) {
					break;
				}
			}
			if (inputLine == null || Parser.LINE_DELIMS.contains(inputLine)) {
				// handle body outside this loop
				break;
			}
			logger.debug("Got this from socket: " + inputLine);
			fullInput.add(inputLine);
		}

		try {
			String lengthHeader = Parser.parseHeader(fullInput,
					"Content-Length");
			if (lengthHeader != null) {
				int length = Integer.parseInt(lengthHeader);

				int b, total = 0;
				StringBuilder buf = new StringBuilder(1024);
				logger.debug("Reading request");
				while (total < length && (b = br.read()) != -1) {
					buf.append((char) b);
					total++;
//					logger.info("Read:" + (char) b + " total:" + total
//							+ " length:" + length);
				}
				logger.debug("=============Read body:" + buf.toString());
				fullInput.add("BODY-CONTENT: " + buf.toString());
				logger.debug("Full header input is:" + fullInput);
			}
		} catch (NumberFormatException e) {
			logger.error("Got error while parsing Content-Length header");
			e.printStackTrace();
		}

		return fullInput;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (!ThreadPool.getInstance().getShouldShutdown()) {
			Socket socket = null;
			synchronized (jobQueue) {
				try {
					logger.debug("Waiting for jobQueue");
					setCurrentUrl("Waiting");
					if (jobQueue.getSize() <= 0)
						jobQueue.wait();
				} catch (InterruptedException e) {
					logger.error("Thread got interrupt. Shutting down");
					e.printStackTrace();
					break;
				}

				logger.debug("Received notify. Waking up!");

				try {
					socket = jobQueue.dequeue();
				} catch (Exception e) {
					continue;
				}
			}
			RequestHandler handler;
			List<String> fullInput = null;
			if (socket != null) {
				// Might cause a problem with CR LF
				try {
					InputStream in = socket.getInputStream();
					OutputStream out = socket.getOutputStream();

					fullInput = parseLines(in, out);

				} catch (IOException e) {
					logger.error(e.getMessage());
					continue;
				}

				logger.debug("Got request header line" + fullInput);
				if(fullInput.size() < 1) {
					continue;
				}
				String path = Parser.parseRequestPath(fullInput.get(0));
				
				setCurrentUrl(path);
				String urlMatch = Parser.longestUrlMatch(path.split("\\?")[0],
						servlets.keySet());

				if (urlMatch != null) {
					logger.debug("Servlet found for " + urlMatch);
					// we have a match for a servlet
					HttpServlet servlet = servlets.get(urlMatch);
					handler = new ServletHandler(socket, servlet, urlMatch);

				} else {
					// is static path
					logger.debug("Resorting to static path");
					handler = new StaticRequestHandler(socket);
				}

				try {
					HttpResponse response = handler.handleRequest(fullInput,
							webRoot, jobQueue);
					logger.debug("Sending back "
							+ new String(response.toBytes()));
					handler.writeOutput(response.toBytes());

				
				} catch (Exception e) {
					logger.error("Mainhandler got exception in handling request. Got " + e.getMessage());
					continue;
				}
				if (path.equals("/shutdown")) {
					SessionMap.getInstance().destroy();
					for (Entry<String, HttpServlet> servlet : servlets
							.entrySet()) {
						servlet.getValue().destroy();
					}
					ThreadPool.getInstance().setShouldShutdown(true);
					logger.info("Shutting down threads. Set should shutdown to "
							+ ThreadPool.getInstance().getShouldShutdown());
					RunnerDaemon.shutdown();
				}
			} else {
				// exception
				logger.error("Socket is null!");
			}
		}
		logger.info("Thread " + getName() + " has stopped execution");
	}
}
