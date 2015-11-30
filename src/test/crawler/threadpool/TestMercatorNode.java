package test.crawler.threadpool;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.junit.Test;

import crawler.parsers.Parser;
import crawler.threadpool.MercatorNode;

public class TestMercatorNode extends TestCase {

	@Test
	public void testAddDisallowAndAllowPaths() {
		String domain = "https://somedomain.com";
		MercatorNode node = new MercatorNode(domain);
		node.addAllowPath(domain + "/somePath");
		node.addDisallowPath(domain + "/somePath");
		assertEquals(false, node.isAllowed(domain + "/somePath"));

		node = new MercatorNode("/");
		node.addDisallowPath(domain + "/somePath");
		node.addAllowPath(domain + "/somePath");
		assertEquals(false, node.isAllowed(domain + "/somePath"));
	}

	@Test
	public void testIsAllowed() {
		String domain = "http://somedomain.com";
		MercatorNode node = new MercatorNode(domain);
		assertEquals(true, node.isAllowed(domain + "/somePath"));
		node.addAllowPath("/somePath");
		assertEquals(true, node.isAllowed(domain + "/somePath"));
		node.addDisallowPath(domain + "/somePath");
		assertEquals(false, node.isAllowed(domain + "/somePath"));
	}

	@Test
	public void testWithRobotsTxt() {
		String robotsContent = "# These defaults shouldn't apply to your crawler\n"
				+ "User-agent: *\n"
				+ "Disallow: /crawltest/marie/\n"
				+ "Crawl-delay: 10\n\n"

				+ "# Below is the directive your crawler should use:\n"
				+ "User-agent: cis455crawler\n"
				+ "Disallow: /crawltest/marie/private/\n"
				+ "Disallow: /crawltest/foo/\n"
				+ "Disallow: /infrastructure/\n"
				+ "Disallow: /maven/\n"
				+ "Disallow: /ppod/\n"
				+ "Crawl-delay: 5\n\n"

				+ "# This should be ignored by your crawler\n"
				+ "User-agent: evilcrawler\n" + "Disallow: /\n";
		String domain = "https://dbappserv.cis.upenn.edu";
		MercatorNode node = Parser.parseRobotsContent(domain, robotsContent);
		URL url1 = null, url2 = null;
		try {
			url1 = new URL(domain + "/crawltest/marie/private/");
			url2 = new URL(domain + "/crawltest/marie/private");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertEquals(false, node.isAllowed(url1.getPath()));
		assertEquals(false, node.isAllowed(url2.getPath()));
	}
	
	@Test
	public void testIsQueriable() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -3);
		Date threeSecondsAgo = cal.getTime();
		MercatorNode mn1 = new MercatorNode("a");
		mn1.setCrawlDelay(2.0f);
		mn1.setLastCrawledTime(threeSecondsAgo);
		assertTrue(mn1.isQueriable());
		
		cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, -1);
		Date oneSecondAgo = cal.getTime();
		MercatorNode mn2 = new MercatorNode("a");
		mn2.setCrawlDelay(2.0f);
		mn2.setLastCrawledTime(oneSecondAgo);
		assertFalse(mn2.isQueriable());
	}

}
