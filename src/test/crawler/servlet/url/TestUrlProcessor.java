package test.crawler.servlet.url;

import java.io.IOException;
import java.text.ParseException;

import junit.framework.TestCase;

import org.junit.Test;

import crawler.dao.URLContent;
import crawler.errors.QueueFullException;
import crawler.servlet.url.UrlProcessor;

public class TestUrlProcessor extends TestCase {

	@Test
	public void testGetNewContentForPdf() {
		String url = "http://www.rjs.eti.br/arq/TipsForSearchingInGoogleEN.pdf";
		UrlProcessor up = new UrlProcessor(null);
		URLContent content = null;
		try {
			content = up.getNewContent(url);
		} catch (IOException | ParseException | QueueFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(content.getContent().contains("Google"));
		assertTrue(content.getContent().contains("<html>"));
		assertTrue(content.getContent().contains("<body>"));

		System.out.println(content.getContent());
	}
	
	@Test
	public void testGetNewContentForHtml() {
		String url = "https://en.wikipedia.org/wiki/Main_Page";
		UrlProcessor up = new UrlProcessor(null);
		URLContent content = null;
		try {
			content = up.getNewContent(url);
		} catch (IOException | ParseException | QueueFullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(content.getContent().contains("Wikipedia"));
//		System.out.println(content);
	}
}
