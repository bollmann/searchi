package crawler.servlet.multinodal.producer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;

import crawler.clients.HttpClient;
import crawler.errors.NoDomainConfigException;
import crawler.info.URLInfo;
import crawler.parsers.Parser;
import crawler.requests.Http10Request;
import crawler.requests.HttpRequest;
import crawler.responses.Http10Response;
import crawler.responses.HttpResponse;
import crawler.servlet.multinodal.status.WorkerStatus;
import crawler.threadpool.DiskBackedQueue;
import crawler.threadpool.MercatorNode;
import crawler.threadpool.MercatorQueue;

public class UrlPoster extends Thread {
	private final Logger logger = Logger.getLogger(getClass());
	private MercatorQueue mq;
	private DiskBackedQueue<String> urlQueue;
	private Map<String, WorkerStatus> workerStatusMap;

	public UrlPoster(MercatorQueue mq, DiskBackedQueue<String> urlQueue,
			Map<String, WorkerStatus> workerStatusMap) {
		this.mq = mq;
		this.urlQueue = urlQueue;
		this.workerStatusMap = workerStatusMap;
	}

	@Override
	public void run() {
		int mqCleanupInterval = 5000;
		Date timeMercatorQueueCleaned = Calendar.getInstance().getTime();
		while (true) {
			Date timeNow = Calendar.getInstance().getTime();
			if ((timeNow.getTime() - timeMercatorQueueCleaned.getTime()) / 1000 > mqCleanupInterval) {
				logger.info("Cleaning up mercator queue");
				timeMercatorQueueCleaned = Calendar.getInstance().getTime();
				mq.cleanUp();
			}
			String url = null;
			synchronized (urlQueue) {
				try {
					logger.debug("UrlPoster waiting for jobQueue");
					if (urlQueue.getSize() <= 0) {
						logger.debug("UrlPoster is going to sleep!");
						urlQueue.wait();
					}
				} catch (InterruptedException e) {
					logger.error("UrlPoster got interrupt. Shutting down");
					e.printStackTrace();
					break;
				}
				logger.debug("UrlPoster received notify. Waking up!");
				url = urlQueue.dequeue();
			}

			// get the mercator node for this url. if it is ready, send it to a
			// worker
			// otherwise just enqueue back to the queue. if there is no mercator
			// node,
			// then create the node.

			try {
				if (mq.isDomainPresentForUrl(url)) {
					// check if node is ready
					URL parsedUrl = new URL(url);
					MercatorNode mn = mq.getDomainNodeMap().get(
							parsedUrl.getHost());
					if (!mn.isQueriable()) {
						enqueueUrl(url);
						continue;
					} else {
						if (mn.isAllowed(parsedUrl.getPath()) && !mq.isVisited(url)) {
							logger.debug("Allowing url " + url
									+ " to be sent to workers");
							synchronized (mn) {
								mn.setLastCrawledTime(Calendar.getInstance()
										.getTime());
							}
							mq.addUrlToFrontier(url);

						} else {
							logger.debug("Discared url " + url);
							continue;
						}
					}

				} else {
					// create a domain for this
					MercatorNode mn = createMercatorNode(url);
					addMercatorNodetoMercatorQueue(mn, url);
					enqueueUrl(url);
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
				enqueueUrl(url);
				continue;
			}

			if (workerStatusMap.size() > 0) {
				// we have workers! put them to work
				String ipPort = getNextWorker(workerStatusMap);
				logger.debug("Giving url " + url + " to worker " + ipPort);
				Http10Request request = new Http10Request();
				request.setHeader("Content-Type", Parser.formEncoding);
				request.setBody("url=" + url);
				request.setMethod("POST");
				try {
					logger.info("Url Poster sending url:" + url + " to worker");
					Http10Response response = HttpClient.post("http://"
							+ ipPort + "/worker", request);
					logger.debug("Url Poster got response "
							+ response.getResponse() + "for url:" + url
							+ " body:" + new String(response.getBody()));
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				logger.debug("Dequeued url " + url + " and sent to " + ipPort);
			} else {
				// no workers. just put the url back into the queue
				logger.debug("Putting url " + url + " back in the queue");
				enqueueUrl(url);
			}

			// try {
			// Thread.sleep(100);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	public void enqueueUrl(String url) {
		synchronized (urlQueue) {
			urlQueue.enqueue(url);
			urlQueue.notify();
		}
	}

	public MercatorNode createMercatorNode(String url) {
		URLInfo urlInfo = new URLInfo(url);
		MercatorNode node = new MercatorNode(urlInfo.getHostName());
		if (urlInfo.getProtocol() == null) {
			return null;
		}
		String httpRobotsUrl = urlInfo.getProtocol() + "://"
				+ urlInfo.getHostName() + "/robots.txt";
		logger.info("Trying " + urlInfo.getProtocol() + " connection to:"
				+ urlInfo.getProtocol() + "://" + urlInfo.getHostName()
				+ "/robots.txt");
		try {
			HttpRequest request = new Http10Request();
			request.setPath(new URL(url).getPath());
			request.setMethod("HEAD");
			request.setHeader("User-Agent", "cis455crawler");
			HttpResponse response = HttpClient.genericHead(httpRobotsUrl,
					request);
			logger.debug("Got head");
			if (response.getResponse().getResponseCode() == 200) {
				request = new Http10Request();
				request.setPath(new URL(url).getPath());
				request.setMethod("GET");
				request.setHeader("User-Agent", "cis455crawler");
				response = HttpClient.genericGet(httpRobotsUrl, request);
				node = Parser.parseRobotsContent(urlInfo.getHostName(),
						new String(response.getBody()));
				return node;
			} else {
				// make default mn
				return node;

			}
		} catch (MalformedURLException e1) {
			logger.error("There's something wrong even after adding to MQ the new url hasn't been added to the apt queue!");
			e1.printStackTrace();
		} catch (IOException e1) {
			logger.error("Got an io exception with url:" + url);
			e1.printStackTrace();
		} catch (ParseException e1) {
			logger.error("Got a parse exception with url:" + url);
			e1.printStackTrace();
		}
		return node;
	}

	public void addMercatorNodetoMercatorQueue(MercatorNode node, String url)
			throws MalformedURLException, NoDomainConfigException {
		synchronized (node) {
			node.setLastCrawledTime(Calendar.getInstance().getTime());
		}
		synchronized (mq) {
			mq.addNode(node);
		}
	}

	public String getNextWorker(Map<String, WorkerStatus> workerStatusMap) {
		Set<String> set = workerStatusMap.keySet();
		List<String> list = new ArrayList<String>();
		list.addAll(set);
		int size = list.size();
		Random random = new Random();
		int idx = random.nextInt(size);
		return list.get(idx);
	}

}
