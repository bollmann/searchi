package crawler.threadpool;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

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

	public void init(String queueName) {
		inputList = new ArrayList<T>();
		outputList = new ArrayList<T>();
		ddb = DynamoDBWrapper.getInstance(DynamoDBWrapper.US_EAST);
		ddb.createTable("QueueInfo", 5, 5, "name", "S");
		logger.info("Looking for queueName:" + queueName);
		queueInfo = (QueueInfo) ddb.getItem(queueName, QueueInfo.class);
		if (queueInfo == null) {
			logger.info("Creating new disk backed queue");
			queueInfo = new QueueInfo(queueName);
		} else {
			logger.info("Using existing queue");
		}
		s3 = S3Wrapper.getInstance();
	}
	/**
	 * Instantiates a new queue.
	 */
	public DiskBackedQueue() {
		init(UUID.randomUUID().toString());
		
	}
	
	public DiskBackedQueue(String prefix) {
		init(prefix);
	}
	
	public DiskBackedQueue(int size) {
		init(UUID.randomUUID().toString());
		inputList = new ArrayList<T>(size);
		outputList = new ArrayList<T>(size);
	}

	public QueueInfo getQueueInfo() {
		return queueInfo;
	}

	public Integer getPushToDiskLimit() {
		return pushToDiskLimit;
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
	
	public String getWriteItemKey() {
		return queueInfo.getName() + "-" + queueInfo.getToWrite();
	}
	
	public String getReadItemKey() {
		return queueInfo.getName() + "-" + queueInfo.getToRead();
	}

	public void pushQueueToDisk(List<T> list) {
		String content = new Gson().toJson(inputList);
		logger.info("Pushing inputList of size " + inputList.size()
				+ "  to queue!");
		synchronized (queueInfo) {
			String writeItemKey = getWriteItemKey();
			s3.putItem(s3.URL_QUEUE_BUCKET, writeItemKey, content);
			
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
		boolean queueFileExists = queueInfo.getToRead() > 0 && queueInfo.getToRead() < queueInfo.getToWrite();
		logger.debug("Dequeueing for DBQ. Info:" + queueInfo + " outputList size:" + outputList.size() 
				+ " inputList size:" + inputList.size());
		if (outputList.size() < 1) {
			
			if (queueFileExists) {
				logger.debug("Found queue element in disk for queueInfo "
						+ queueInfo);
				// retrieve queue file and make it outputList
				synchronized (queueInfo) {
					
					String content = s3.getItem(s3.URL_QUEUE_BUCKET,
							getReadItemKey());
					s3.deleteItem(s3.URL_QUEUE_BUCKET,
							getReadItemKey());
					Type listType = new TypeToken<List<T>>() {
					}.getType();
					outputList = new Gson().fromJson(content, listType);
					logger.debug("Retrieved a list of size" + outputList.size());
					queueInfo.setToRead(queueInfo.getToRead() + 1);
					ddb.putItem(queueInfo);
				}
			} else {
				logger.debug("Checking with inputList");
				if(inputList.size() > 0) {
					outputList.addAll(inputList);
					inputList.clear();
				} else {
					logger.debug("Both input and outlist are empty!");
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
