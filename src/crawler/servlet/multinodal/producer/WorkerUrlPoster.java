package crawler.servlet.multinodal.producer;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import crawler.clients.HttpClient;
import crawler.requests.Http10Request;
import crawler.responses.Http10Response;
import crawler.threadpool.Queue;

public class WorkerUrlPoster extends Thread {
	private final Logger logger = Logger.getLogger(getClass());
	String postUrl;
	Queue<List<String>> urlQueue;

	public WorkerUrlPoster(String postUrl, Queue<List<String>> urlQueue) {
		this.postUrl = postUrl;
		this.urlQueue = urlQueue;
	}

	@Override
	public void run() {
		while (true) {
			List<String> urls = null;
			synchronized (urlQueue) {
				try {
					logger.debug("WorkerUrlPoster waiting for jobQueue");
					if (urlQueue.getSize() <= 0)
						urlQueue.wait();
				} catch (InterruptedException e) {
					logger.error("WorkerUrlPoster got interrupt. Shutting down");
					e.printStackTrace();
					break;
				}
				logger.debug("WorkerUrlPoster received notify. Waking up!");
				urls = urlQueue.dequeue();
			}

			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			postUrls(postUrl, urls);

		}
	}

	public void postUrls(String postUrl, List<String> urls) {
		String content = new Gson().toJson(urls);
		Http10Request request = new Http10Request();
		request.setMethod("POST");
		request.setBody(content);
		try {
			logger.debug("Worker Url Poster sending url:" + postUrl
					+ " to worker");

			Http10Response response = HttpClient.post(postUrl, request);
			logger.debug("Worker Url Poster got response " + response.getResponse()
					+ "for url:" + postUrl + " body:"
					+ new String(response.getBody()));

		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			logger.error("Posting to server failed!");
			// e.printStackTrace();
		}
	}

}
