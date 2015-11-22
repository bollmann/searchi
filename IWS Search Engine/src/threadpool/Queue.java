/*
 * Written by Shreejit Gangadharan
 */
package threadpool;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import errors.QueueFullException;

// TODO: Auto-generated Javadoc
/**
 * The Class Queue which is a generic blocking queue.
 *
 * @param <T>
 *            the generic type
 */
public class Queue<T> {

	/** The max queue size. */
	private int MAX_QUEUE_SIZE = 60000;

	/** The logger. */
	private static Logger logger = Logger.getLogger(Queue.class);

	/** The list. */
	private List<T> list;

	/**
	 * Instantiates a new queue.
	 */
	public Queue() {
		list = new ArrayList<T>();
	}
	
	public Queue(int size) {
		list = new ArrayList<T>(size);
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
	public void enqueue(T element) throws QueueFullException {
		logger.debug("Adding element to queue " + element.toString());
		if (list.size() < MAX_QUEUE_SIZE) {
			synchronized (list) {
				list.add(element);
			}
		} else {
			throw new QueueFullException("Queue is full!");
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
		synchronized (list) {
			try {
				return list.remove(list.size() - 1);
			} catch (IndexOutOfBoundsException e) {
				throw new IndexOutOfBoundsException("Queue is empty!");
			}
		}
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public int getSize() {
		synchronized (list) {
			return list.size();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (T item : list) {
			sb.append(item.toString() + " ");
		}

		return sb.toString();
	}
}
