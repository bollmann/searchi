package crawler.shutdownHook;

import threadpool.Queue;

import com.google.gson.Gson;

import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;

public class ShutdownHook extends Thread {
	private Queue<String> urlQueue;

	public ShutdownHook(Queue<String> urlQueue) {
		this.urlQueue = urlQueue;
	}

	@Override
	public void run() {
		System.out.println("Exiting gracefully");
		DynamoDBWrapper ddb = DynamoDBWrapper
				.getInstance(DynamoDBWrapper.URL_CONTENT_ENDPOINT);
		S3Wrapper s3 = S3Wrapper.getInstance();
		String queueContent = new Gson().toJson(urlQueue);
		s3.putItem(s3.URL_QUEUE_BUCKET, "queueState", queueContent);
		ddb.displaySaveStatistics();
		// save mercator queue state
	}

}
