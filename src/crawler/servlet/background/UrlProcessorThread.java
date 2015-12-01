package crawler.servlet.background;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import crawler.clients.HttpClient;
import crawler.errors.NoDomainConfigException;
import crawler.parsers.Parser;
import crawler.requests.Http10Request;
import crawler.requests.HttpRequest;
import crawler.responses.HttpResponse;
import crawler.servlet.multinodal.status.WorkerStatus;
import crawler.servlet.url.UrlProcessor;
import crawler.threadpool.DiskBackedQueue;
import crawler.threadpool.MercatorNode;
import crawler.threadpool.MercatorQueue;

public class UrlProcessorThread extends Thread {
	private final Logger logger = Logger.getLogger(getClass());
	private DiskBackedQueue<String> urlQueue = null;
	private Set<String> allowedDomains = null;
	private MercatorQueue mq = null;
	private WorkerStatus workerStatus = null;

	public UrlProcessorThread(MercatorQueue mq, Set<String> allowedDomains,
			DiskBackedQueue<String> urlQueue, WorkerStatus workerStatus) {
		this.mq = mq;
		this.allowedDomains = allowedDomains;
		this.urlQueue = urlQueue;
		this.workerStatus = workerStatus;
	}

	@Override
	public void run() {
		while (true) {
			String url = null;
			synchronized (urlQueue) {
				try {
					logger.debug("UrlProcessorThread waiting for jobQueue");
					if (urlQueue.getSize() <= 0) {
						logger.info("UrlProcessorThread is going to sleep!");
						urlQueue.wait();
					}
				} catch (InterruptedException e) {
					logger.error("UrlProcessorThread got interrupt. Shutting down");
					e.printStackTrace();
					break;
				}
				logger.debug("UrlProcessorThread received notify. Waking up!");
				url = urlQueue.dequeue();
			}
//			try {
//				Thread.sleep(300);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			logger.debug("UrlProcessorThread received notify. Woke up and dequeued:" + url);

			try {
				if (mq.isDomainPresentForUrl(url)) {
					// check if node is ready
					URI parsedUrl = new URI(url);
					String domain = Parser.getDomainForUrl(url);
					MercatorNode mn = mq.getDomainNodeMap().get(domain);
					if (!mn.isQueriable()) {
						enqueueUrl(url);
						continue;
					} else {
						if (mn.isAllowed(parsedUrl.getPath())
								&& !mq.isVisited(url)
								&& allowedDomains.contains(domain)) {
							logger.info("Allowing url " + url
									+ " to be sent to workers");
							synchronized (mn) {
								mn.setLastCrawledTime(Calendar.getInstance()
										.getTime());
							}
							mq.addUrlToFrontier(url);

						} else {
							logger.info("Discarded url " + url);
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

				// process url and extract links. save url if needed
				UrlProcessor p = new UrlProcessor(urlQueue);
				List<String> outgoingUrls = null;
				try {
					outgoingUrls = p.handleURL(url);
					workerStatus
							.setUrlProcessed(workerStatus.getUrlProcessed() + 1);
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					logger.error("Error in handling the url " + url);
				}
				logger.debug("Enqueueing extracted urls");
				List<String> filteredUrls = Parser.filterUrls(outgoingUrls);
				if (outgoingUrls == null) {
					continue;
				}
				if (filteredUrls.size() > 0) {
					for (String filteredUrl : filteredUrls) {
						enqueueUrl(filteredUrl);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				enqueueUrl(url);
				continue;
			}
		}
	}

	public void enqueueUrl(String url) {
		synchronized (urlQueue) {
			urlQueue.enqueue(url);
			urlQueue.notify();
		}
	}

	public MercatorNode createMercatorNode(String url)
			throws URISyntaxException {
		MercatorNode node = new MercatorNode(Parser.getDomainForUrl(url));

		String httpRobotsUrl = MercatorQueue.getRobotsTxtUrl(url);
		logger.info("Getting robots.txt at " + httpRobotsUrl);
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
				node = Parser.parseRobotsContent(
						Parser.getDomainForUrl(url),
						new String(response.getBody()));
				return node;
			} else {
				// make default mn
				return node;

			}
		} catch (IOException e1) {
			logger.error("Got an io exception with url:" + url);
			e1.printStackTrace();
		} catch (ParseException e1) {
			logger.error("Got a parse exception with url:" + url);
			e1.printStackTrace();
		} catch (Exception e) {
			logger.error("Got an exception with url:" + url);
		}
		return node;
	}

	public void addMercatorNodetoMercatorQueue(MercatorNode node, String url)
			throws NoDomainConfigException {
		synchronized (node) {
			node.setLastCrawledTime(Calendar.getInstance().getTime());
		}
		synchronized (mq) {
			mq.addNode(node);
		}
	}

}
