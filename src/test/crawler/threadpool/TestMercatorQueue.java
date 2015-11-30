package test.crawler.threadpool;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import crawler.errors.NoDomainConfigException;
import crawler.threadpool.MercatorNode;
import crawler.threadpool.MercatorQueue;

public class TestMercatorQueue extends TestCase {
	
	@Test
	public void testAddNode() {
		MercatorQueue queue = new MercatorQueue();
		
		assertNull(queue.getHead());
		
		MercatorNode node = new MercatorNode("/1");
		node.setCrawlDelay(1);
		queue.addNode(node);
		
		assertEquals(node, queue.getHead());
		assertEquals(true, queue.getHead().getDomain().equals("/1"));
		assertEquals(1, Math.round(queue.getHead().getCrawlDelay()));
		assertEquals(true, queue.getDomainNodeMap().containsKey("/1"));
		
		node = new MercatorNode("/2");
		node.setCrawlDelay(2);
		queue.addNode(node);
		
		assertEquals(node, queue.getHead().getNext());
		assertEquals(true, queue.getHead().getNext().getDomain().equals("/2"));
		assertEquals(2, Math.round(queue.getHead().getNext().getCrawlDelay()));
		assertEquals(true, queue.getDomainNodeMap().containsKey("/2"));
	}
	
	@Test
	public void testRemoveNode() {
		MercatorNode node11 = new MercatorNode("/1");
		MercatorNode node12 = new MercatorNode("/2");
		MercatorNode node13 = new MercatorNode("/3");
		MercatorNode node21 = new MercatorNode("/1");
		MercatorNode node22 = new MercatorNode("/2");
		MercatorNode node23 = new MercatorNode("/3");
		MercatorNode node31 = new MercatorNode("/1");
		MercatorNode node32 = new MercatorNode("/2");
		MercatorNode node33 = new MercatorNode("/3");
		
		MercatorQueue queue1 = new MercatorQueue(); // head
		MercatorQueue queue2 = new MercatorQueue(); // middle
		MercatorQueue queue3 = new MercatorQueue(); // last
		
		queue1.addNode(node11);
		queue1.addNode(node12);
		queue1.addNode(node13);
		
		queue2.addNode(node21);
		queue2.addNode(node22);
		queue2.addNode(node23);
		
		queue3.addNode(node31);
		queue3.addNode(node32);
		queue3.addNode(node33);
		
		assertEquals(node11, queue1.getHead());
		assertEquals(node21, queue2.getHead());
		assertEquals(node31, queue3.getHead());
		
		
		queue1.removeNode("/1");
		queue2.removeNode("/2");
		queue3.removeNode("/3");
		
		assertEquals(node12, queue1.getHead());
		assertEquals(node13, queue1.getHead().getNext());
		assertNull(queue1.getHead().getNext().getNext());
		
		assertEquals(node21, queue2.getHead());
		assertEquals(node23, queue2.getHead().getNext());
		assertNull(queue2.getHead().getNext().getNext());
		
		assertEquals(node31, queue3.getHead());
		assertEquals(node32, queue3.getHead().getNext());
		assertNull(queue3.getHead().getNext().getNext());
	}
	
	@Test
	public void testEnqueueUrl() {
		MercatorQueue queue = new MercatorQueue();
		MercatorNode node = new MercatorNode("www.abc.com");
		queue.addNode(node);
		
		assertEquals(0, node.getUrls().getSize());
		String url1 = "http://www.abc.com/a";
		try {
			queue.enqueueUrl(url1);
		} catch (MalformedURLException | NoDomainConfigException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		
		assertEquals(1, node.getUrls().getSize());
		assertEquals("http://www.abc.com/a", node.getUrls().dequeue());
		assertEquals(true, queue.isVisited(url1));
		
		MercatorNode node2 = new MercatorNode("www.pqr.com");
		queue.addNode(node2);
		
		assertEquals(0, node2.getUrls().getSize());
		
		String url2 = "http://www.pqr.com/a";
		try {
			queue.enqueueUrl(url2);
		} catch (MalformedURLException | NoDomainConfigException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		
		assertEquals(1, node2.getUrls().getSize());
		assertEquals(url2, node2.getUrls().dequeue()); // dequeue will remove it
		assertEquals(true, queue.isVisited(url2));
		
		String url3 = "http://www.pqr.com/a/b";
		try {
			queue.enqueueUrl(url3);
		} catch (MalformedURLException | NoDomainConfigException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		
		assertEquals(1, node2.getUrls().getSize()); // dequeued previous one, so is gone
		assertEquals(url3, node2.getUrls().dequeue());
		assertEquals(true, queue.isVisited(url3));
	}
	
	@Test
	public void testEnqueueAndDequeueOrder() {
		MercatorQueue queue = new MercatorQueue();
		MercatorNode node = new MercatorNode("www.abc.com");
		queue.addNode(node);
		
		assertEquals(0, node.getUrls().getSize());
		String url1 = "http://www.abc.com/a";
		String url2 = "http://www.abc.com/b";
		String url3 = "http://www.abc.com/c";
		try {
			queue.enqueueUrl(url1);
			queue.enqueueUrl(url2);
			queue.enqueueUrl(url3);
		} catch (MalformedURLException | NoDomainConfigException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		assertEquals(3, node.getUrls().getSize());
		assertEquals(url1, node.getUrls().dequeue());
	}
	
	
	@Test
	public void testCleanUp() {
		MercatorNode node11 = new MercatorNode("/1");
		MercatorNode node12 = new MercatorNode("/2");
		MercatorNode node13 = new MercatorNode("/3");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, -1);
		cal.add(Calendar.MINUTE, -1);
		Date oneHourAgo = cal.getTime();
		node12.setLastCrawledTime(oneHourAgo);
		
		MercatorQueue queue1 = new MercatorQueue(); // head
		queue1.addNode(node11);
		queue1.addNode(node12);
		queue1.addNode(node13);
		
		queue1.cleanUp();
		assertEquals(node11, queue1.getHead());
		assertEquals(node13, queue1.getHead().getNext());
	}

}
