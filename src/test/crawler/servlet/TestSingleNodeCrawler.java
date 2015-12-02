package test.crawler.servlet;

import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.Test;

import crawler.servlet.SingleNodeCrawler;

public class TestSingleNodeCrawler extends TestCase {
	@Test
	public void testReadDomainConfigFile() {
		SingleNodeCrawler snc = new SingleNodeCrawler();
		Set<String> config = null;
		try {
			config = snc.readConfigFile("conf/test/domains.csv", 2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(config.contains("google.com"));
	}
}
