package test.crawler.servlet.multinodal.producer;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import crawler.servlet.multinodal.producer.UrlPoster;
import crawler.servlet.multinodal.status.WorkerStatus;

public class TestUrlPoster extends TestCase {

	@Test
	public void testGetRandomWorker() {
		Map<String, WorkerStatus> workerStatusMap = new HashMap<String, WorkerStatus>();
		workerStatusMap.put("1", new WorkerStatus());
		workerStatusMap.put("2", new WorkerStatus());
		workerStatusMap.put("3", new WorkerStatus());
		UrlPoster p = new UrlPoster(null, null, workerStatusMap);
		String result = p.getNextWorker(workerStatusMap);
		System.out.println(result);
	}
}
