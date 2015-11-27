package test.crawler.threadpool;

import junit.framework.TestCase;

import org.junit.Test;

import crawler.threadpool.DiskBackedQueue;
import db.wrappers.DynamoDBWrapper;

public class TestDiskBackedQueue extends TestCase {

	@Test
	public void testCreate() {
		DiskBackedQueue<String> q = new DiskBackedQueue<String>();
		assertEquals(0, q.getQueueInfo().getToRead());
		assertEquals(1, q.getQueueInfo().getToWrite());
	}

	@Test
	public void testEnqueue() {
		DynamoDBWrapper ddb = DynamoDBWrapper
				.getInstance(DynamoDBWrapper.US_EAST);
		ddb.deleteTable("QueueInfo");
		DiskBackedQueue<String> q = new DiskBackedQueue<String>();
		assertEquals(1, (int) q.getPushToDiskLimit());
		boolean pushed = q.enqueue("abc");
		assertTrue(pushed);
		assertEquals(0, q.getInputList().size());
		assertEquals(2, (int) q.getPushToDiskLimit());

		q.enqueue("pqr");
		pushed = q.enqueue("pqr");
		assertTrue(pushed);
		assertEquals(0, q.getInputList().size());
		assertEquals(4, (int) q.getPushToDiskLimit());

		pushed = q.enqueue("pqr");
		pushed = q.enqueue("pqr");
		pushed = q.enqueue("pqr");
		assertFalse(pushed);
		pushed = q.enqueue("pqr");
		assertTrue(pushed);
		assertEquals(8, (int) q.getPushToDiskLimit());
		assertEquals(0, q.getInputList().size());
	}

	@Test
	public void testEnqueueAndDequeue() {
		DynamoDBWrapper ddb = DynamoDBWrapper
				.getInstance(DynamoDBWrapper.US_EAST);
		ddb.deleteTable("QueueInfo");
		DiskBackedQueue<String> q = new DiskBackedQueue<String>();
		assertEquals(1, (int) q.getPushToDiskLimit());
		boolean pushed = q.enqueue("abc");
		assertTrue(pushed);
		assertEquals(0, q.getInputList().size());
		assertEquals(2, (int) q.getPushToDiskLimit());

		String a = q.dequeue();
		assertEquals("abc", a);
	}

	@Test
	public void testEandDfor1000() {
		DynamoDBWrapper ddb = DynamoDBWrapper
				.getInstance(DynamoDBWrapper.US_EAST);
		ddb.deleteTable("QueueInfo");
		DiskBackedQueue<String> q = new DiskBackedQueue<String>();
		for (int i = 0; i < 1000; i++) {
			q.enqueue(String.valueOf(i));
		}

		for (int i = 0; i < 1000; i++) {
			System.out.println("Trying to dequeue " + i);
			String a = q.dequeue();
			assertEquals(String.valueOf(i), a);
		}
	}
}
