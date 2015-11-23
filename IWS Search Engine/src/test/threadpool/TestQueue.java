/*
 * Written by Shreejit Gangadharan
 */
package test.threadpool;

import junit.framework.TestCase;

import org.junit.Assert;
import org.junit.Test;

import threadpool.Queue;
import errors.QueueFullException;

// TODO: Auto-generated Javadoc
/**
 * The Class TestQueue.
 */
public class TestQueue extends TestCase {
	
	/**
	 * Test enqueue.
	 */
	@Test
	public void testEnqueue() {
		Queue<String> queue = new Queue<String>();
		queue.setMaxQueueSize(10);

		try {
			for (int i = 0; i < 10; i++) {
				queue.enqueue("abc");
			}
		} catch (QueueFullException e) {
			Assert.fail();
		}

		try {
			queue.enqueue("abc");
		} catch (QueueFullException e) {
			assertEquals("Queue is full!", e.getMessage());
		}

	}

	/**
	 * Test de queue.
	 */
	@Test
	public void testDeQueue() {
		Queue<String> queue = new Queue<String>();
		queue.setMaxQueueSize(10);

		try {
			for (int i = 0; i < 10; i++) {
				queue.enqueue("abc");
			}
		} catch (QueueFullException e) {
			Assert.fail();
		}

		try {
			for (int i = 0; i < 10; i++) {
				queue.dequeue();
			}
		} catch (IndexOutOfBoundsException e) {
			Assert.fail();
		}
		
		try {
			queue.dequeue();
		} catch (IndexOutOfBoundsException e) {
			assertEquals("Queue is empty!", e.getMessage());
		}

	}

}
