package crawler.shutdownHook;

import com.google.gson.Gson;

import crawler.threadpool.Queue;
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
				.getInstance(DynamoDBWrapper.US_EAST);
		S3Wrapper s3 = S3Wrapper.getInstance();
		String queueContent = new Gson().toJson(urlQueue);
		s3.putItem(s3.URL_QUEUE_BUCKET, "queueState", queueContent);
		ddb.displaySaveStatistics();
		// save mercator queue state
	}

}
