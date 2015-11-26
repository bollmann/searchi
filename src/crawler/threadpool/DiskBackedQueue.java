package crawler.threadpool;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import crawler.errors.QueueFullException;
import db.dbo.QueueInfo;
import db.wrappers.DynamoDBWrapper;
import db.wrappers.S3Wrapper;

public class DiskBackedQueue<T> { 

	/** The max queue size. */
	private int MAX_QUEUE_SIZE = 100000;

	private Integer pushToDiskLimit = 10000; // the size of inputList when it is
											// pushed to s3.
	// this is exponentially grown till it reaches MAX_QUEUE_SIZE

	/** The logger. */
	private static Logger logger = Logger.getLogger(DiskBackedQueue.class);

	/** The list. */
	private List<T> inputList;

	private List<T> outputList;

	DynamoDBWrapper ddb;
	S3Wrapper s3;
	QueueInfo queueInfo;

	public static String QUEUE_NAME = "queueStatus";

	/**
	 * Instantiates a new queue.
	 */
	public DiskBackedQueue() {
		inputList = new ArrayList<T>();
		outputList = new ArrayList<T>();
		ddb = DynamoDBWrapper.getInstance(DynamoDBWrapper.US_EAST);
		ddb.createTable("QueueInfo", 5, 5, "name", "S");
		queueInfo = (QueueInfo) ddb.getItem(QUEUE_NAME, QueueInfo.class);
		if (queueInfo == null) {
			queueInfo = new QueueInfo();
		}
		s3 = S3Wrapper.getInstance();
	}

	public QueueInfo getQueueInfo() {
		return queueInfo;
	}

	public Integer getPushToDiskLimit() {
		return pushToDiskLimit;
	}

	public DiskBackedQueue(int size) {
		inputList = new ArrayList<T>(size);
		outputList = new ArrayList<T>();
	}

	/**
	 * Sets the max queue size.
	 *
	 * @param maxQueueSize
	 *            the new max queue size
	 */
	public void setMaxQueueSize(int maxQueueSize) {
		this.MAX_QUEUE_SIZE = maxQueueSize;
	}

	/**
	 * Enqueue.
	 *
	 * @param element
	 *            the element
	 * @throws QueueFullException
	 *             the queue full exception
	 */
	
	public boolean enqueue(T element) {
		logger.debug("Adding element to queue " + element.toString());

		synchronized (inputList) {
			inputList.add(element);
			synchronized(queueInfo) {
				queueInfo.incrSize();
			}
			if (inputList.size() < pushToDiskLimit) {
				return false;
			} else {
				// push inputList to disk. empty out inputList. then enqueue
				pushQueueToDisk(inputList);
				inputList = new ArrayList<T>();
				pushToDiskLimit = (int) Math.min(2 * pushToDiskLimit,
						MAX_QUEUE_SIZE);
				return true;
			}
		}

	}

	public void pushQueueToDisk(List<T> list) {
		String content = new Gson().toJson(inputList);
		logger.info("Pushing inputList of size " + inputList.size()
				+ "  to queue!");
		synchronized (queueInfo) {
			s3.putItem(s3.URL_QUEUE_BUCKET,
					String.valueOf(queueInfo.getToWrite()), content);
			queueInfo.setToWrite(queueInfo.getToWrite() + 1);
			if (queueInfo.getToRead() < 1) {
				queueInfo.setToRead(queueInfo.getToRead() + 1);
			}
			ddb.putItem(queueInfo);
		}
	}

	/**
	 * Dequeue.
	 *
	 * @return the t
	 * @throws IndexOutOfBoundsException
	 *             the index out of bounds exception
	 */
	
	public T dequeue() throws IndexOutOfBoundsException {
		logger.debug("Removing element from queue");
		boolean queueFileExists = queueInfo.getToRead() > 0 && queueInfo.getToRead() < queueInfo.getToWrite();
		if (outputList.size() < 1) {
			logger.info("Refilling queue from disk");
			if (queueFileExists) {
				logger.info("Found queue element in disk for queueInfo "
						+ queueInfo);
				// retrieve queue file and make it outputList
				synchronized (queueInfo) {
					
					String content = s3.getItem(s3.URL_QUEUE_BUCKET,
							String.valueOf(queueInfo.getToRead()));
					s3.deleteItem(s3.URL_QUEUE_BUCKET,
							String.valueOf(queueInfo.getToRead()));
					queueInfo.setToRead(queueInfo.getToRead());
					Type listType = new TypeToken<List<T>>() {
					}.getType();
					outputList = new Gson().fromJson(content, listType);
					logger.info("Retrieved a list of size" + outputList.size());
					queueInfo.setToRead(queueInfo.getToRead() + 1);
					ddb.putItem(queueInfo);
				}
			} else {
				if(inputList.size() > 0) {
					outputList.addAll(inputList);
					inputList.clear();
				}
			}
		}
		
		synchronized(queueInfo) {
			queueInfo.decrSize();
		}

		return outputList.remove(0);
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		return queueInfo.getSize();
	}

	public List<T> getInputList() {
		return inputList;
	}

	public List<T> getOutputList() {
		return outputList;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (T item : outputList) {
			sb.append(item.toString() + " ");
		}

		return sb.toString();
	}
}
