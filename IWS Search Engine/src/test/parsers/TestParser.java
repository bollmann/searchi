/*
 * Written by Shreejit Gangadharan
 */
package test.parsers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import parsers.Parser;
import parsers.Parser.Handler;
import policies.FilePolicy;
import responses.Response;
import servlets.ServletContextImpl;
import threadpool.MercatorNode;
import webserver.HttpServer;

// TODO: Auto-generated Javadoc
/**
 * The Class TestParser.
 */
public class TestParser extends TestCase {

	/**
	 * Initialize logger.
	 */
	@Before
	public void initializeLogger() {
		org.apache.log4j.BasicConfigurator.configure();
	}

	/**
	 * Test parsedate.
	 */
	@Test
	public void testParsedate() {
		String date1 = "Fri, 31 Dec 1999 23:59:59 GMT";
		TimeZone timeZone = TimeZone.getTimeZone("GMT");
		Date date;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				Parser.dateFormat, Locale.US);
		simpleDateFormat.setTimeZone(timeZone);
		Calendar cal;
		try {
			date = Parser.parseDate(date1);
			cal = Calendar.getInstance(timeZone);
			cal.setTime(date);
			// System.out.println("line1" +
			// simpleDateFormat.format(cal.getTime()));
			assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
			assertEquals(6, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(11, cal.get(Calendar.MONTH)); // Jan is 0
			assertEquals(1999, cal.get(Calendar.YEAR));
			assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(59, cal.get(Calendar.MINUTE));
			assertEquals(59, cal.get(Calendar.SECOND));
		} catch (Exception e) {
			assertEquals("", e.getMessage());

		}

		try {
			date1 = "Friday, 31-Dec-99 23:59:59 GMT";

			date = Parser.parseDate(date1);
			cal = Calendar.getInstance(timeZone);
			cal.setTime(date);
			System.out
					.println("line2" + simpleDateFormat.format(cal.getTime()));
			assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
			assertEquals(6, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(11, cal.get(Calendar.MONTH)); // Jan is 0
			assertEquals(1999, cal.get(Calendar.YEAR));
			assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(59, cal.get(Calendar.MINUTE));
			assertEquals(59, cal.get(Calendar.SECOND));
		} catch (Exception e) {
			assertEquals("", e.getMessage());
		}
		date1 = "Fri Dec 31 23:59:59 1999";

		try {
			date = Parser.parseDate(date1);
			cal = Calendar.getInstance(timeZone);
			cal.setTime(date);
			// System.out.println("line3" +
			// simpleDateFormat.format(cal.getTime()));
			assertEquals(31, cal.get(Calendar.DAY_OF_MONTH));
			assertEquals(6, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(11, cal.get(Calendar.MONTH)); // Jan is 0
			assertEquals(1999, cal.get(Calendar.YEAR));
			assertEquals(23, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(59, cal.get(Calendar.MINUTE));
			assertEquals(59, cal.get(Calendar.SECOND));
		} catch (Exception e) {
			assertEquals("", e.getMessage());

		}

		date1 = "Thu, 17 Sep 2015 01:33:58 EDT";

		try {
			date = Parser.parseDate(date1);
			cal = Calendar.getInstance(timeZone);
			cal.setTime(date);
			
			System.out
					.println("line4" + simpleDateFormat.format(cal.getTime()));
			assertEquals(17, cal.get(Calendar.DAY_OF_MONTH));
			assertEquals(5, cal.get(Calendar.DAY_OF_WEEK));
			assertEquals(8, cal.get(Calendar.MONTH)); // Jan is 0
			assertEquals(2015, cal.get(Calendar.YEAR));
			assertEquals(5, cal.get(Calendar.HOUR_OF_DAY));
			assertEquals(33, cal.get(Calendar.MINUTE));
			assertEquals(58, cal.get(Calendar.SECOND));
		} catch (Exception e) {
			assertEquals("", e.getMessage());
		}
	}

	/**
	 * Test is allowed method.
	 */
	@Test
	public void testIsAllowedMethod() {
		boolean result = Parser.isAllowedMethod("GET");
		assertEquals(true, result);

		result = Parser.isAllowedMethod("get");
		assertEquals(true, result);

		result = Parser.isAllowedMethod("HEAD");
		assertEquals(true, result);

		result = Parser.isAllowedMethod("POST");
		assertEquals(true, result);

		result = Parser.isAllowedMethod("PUT");
		assertEquals(false, result);

		result = Parser.isAllowedMethod("DELETE");
		assertEquals(false, result);
	}

	/**
	 * Test is allowed version.
	 */
	@Test
	public void testIsAllowedVersion() {
		boolean result = Parser.isAllowedVersion("HTTP/1.0");
		assertEquals(true, result);

		result = Parser.isAllowedVersion("HTTP/1.1");
		assertEquals(true, result);

		result = Parser.isAllowedVersion("HTTP/1.2");
		assertEquals(true, result);

		result = Parser.isAllowedVersion("HTTP/1.9");
		assertEquals(false, result);
	}

	/**
	 * Test parse method.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testParseMethod() throws Exception {
		String firstLine = "GET / HTTP/1.0";
		String method = Parser.parseRequestMethod(firstLine);

		assertEquals("GET", method);

		firstLine = "GET	/	HTTP/1.0";
		method = Parser.parseRequestMethod(firstLine);

		assertEquals("GET", method);

		firstLine = "GOT	/	HTTP/1.0";
		try {
			method = Parser.parseRequestMethod(firstLine);
		} catch (Exception e) {
			assertEquals("Wrong method!", e.getMessage());
		}

		firstLine = "	/	HTTP/1.0";
		try {
			method = Parser.parseRequestMethod(firstLine);
		} catch (Exception e) {
			assertEquals("Wrong method!", e.getMessage());
		}
	}

	/**
	 * Test absolute paths.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testAbsolutePaths() throws Exception {
		String firstLine = "GET http://www.html.com/path HTTP/1.1";
		boolean result = Parser.isAbsolutePathRequest(firstLine);
		assertEquals(true, result);

		firstLine = "GET /path HTTP/1.1";
		result = Parser.isAbsolutePathRequest(firstLine);
		assertEquals(false, result);

		firstLine = "GET http://www.abc.com/ HTTP/1.1";
		result = Parser.isAbsolutePathRequest(firstLine);
		assertEquals(true, result);
	}

	/**
	 * Test parse version.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testParseVersion() throws Exception {
		String firstLine = "GET / HTTP/1.0";
		String method = Parser.parseRequestVersion(firstLine);

		assertEquals("HTTP/1.0", method);

		firstLine = "GET / HTTP/1.1";
		method = Parser.parseRequestVersion(firstLine);

		assertEquals("HTTP/1.1", method);

		firstLine = "GET / HTTP/1.2";
		try {
			method = Parser.parseRequestVersion(firstLine);
		} catch (Exception e) {
			assertEquals("Wrong version!", e.getMessage());
		}

	}

	/**
	 * Test parse path.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testParsePath() throws Exception {
		String firstLine = "GET /abc.html HTTP/1.0";
		String path = Parser.parseRequestPath(firstLine);

		assertEquals("/abc.html", path);

		firstLine = "GET  /abc.html  	HTTP/1.1";
		path = Parser.parseRequestPath(firstLine);

		assertEquals("/abc.html", path);
	}

	/**
	 * Test format date.
	 */
	@Test
	public void testFormatDate() {
		String date1 = "Fri, 31 Dec 1999 23:59:59 GMT";

		DateFormat df = new SimpleDateFormat(Parser.dateFormat, Locale.ENGLISH);
		Date date = null;
		try {
			date = df.parse(date1);
		} catch (Exception e) {

		}
		String formattedDate = Parser.formatDate(date);
		assertEquals(date1, formattedDate);

		/*
		 * date1 = "Fri, 32 Dec 1999 23:59:59 GMT";
		 * 
		 * df = new SimpleDateFormat(Parser.dateFormat, Locale.ENGLISH); date =
		 * null; try { date = df.parse(date1); } catch (Exception e) {
		 * assertEquals("", e.getMessage()); } formattedDate =
		 * Parser.formatDate(date); assertEquals(date1, formattedDate);
		 */

	}

	
	/**
	 * Test matches.
	 */
	@Test
	public void testMatches() {
		Set<String> toMatchIn = new HashSet<String>() {{
			add("/");
			add("/someUrl");
			add("/servletUrl/*");
		}};
				
		String toMatch = "/someUrl/abc";
		assertEquals(null, Parser.longestUrlMatch(toMatch, toMatchIn));
		
		toMatch = "/someUrl";
		assertEquals("/someUrl", Parser.longestUrlMatch(toMatch, toMatchIn));
		
		toMatch = "/servletUrl/abc";
		assertEquals("/servletUrl/*", Parser.longestUrlMatch(toMatch, toMatchIn));
		
		toMatch = "/servletUrl/abc/abc";
		assertEquals("/servletUrl/*", Parser.longestUrlMatch(toMatch, toMatchIn));
		
		toMatch = "/servletUrl";
		assertEquals("/servletUrl/*", Parser.longestUrlMatch(toMatch, toMatchIn));
	}
	
	/**
	 * Test longest match.
	 */
	@Test
	public void testLongestMatch() {
		Set<String> toMatchIn = new HashSet<String>() {{
			add("/");
			add("/someUrl/*");
			add("/someUrl/subUrl/*");
		}};
		String toMatch = "/someUrl/abc";
		assertEquals("/someUrl/*", Parser.longestUrlMatch(toMatch, toMatchIn));
		
		toMatch = "/someUrl";
		assertEquals("/someUrl/*", Parser.longestUrlMatch(toMatch, toMatchIn));
		
		toMatch = "/someUrl/subUrl";
		assertEquals("/someUrl/subUrl/*", Parser.longestUrlMatch(toMatch, toMatchIn));
		
		toMatch = "/";
		assertEquals(null, Parser.longestUrlMatch(toMatch, toMatchIn));
		
		toMatch = "/someUrl/subUrl/abc";
		assertEquals("/someUrl/subUrl/*", Parser.longestUrlMatch(toMatch, toMatchIn));
		
		toMatch = "/someUrl/subUrl/abc?abc=abc&abc=pqr";
		assertEquals("/someUrl/subUrl/*", Parser.longestUrlMatch(toMatch, toMatchIn));
	}
	
	/**
	 * Test create context.
	 */
	@Test
	public void testCreateContext() {
		HttpServer server = new HttpServer();
		Handler h = new Handler();
		Map<String, String> context = new HashMap<String, String>();
		context.put("param1", "value1");
		context.put("param2", "value2");
		h.setM_contextParams(context);
		ServletContextImpl fc = Parser.createContext(h);
		assertEquals(true, fc.getInitParameter("param1").equals("value1"));
		assertEquals(true, fc.getInitParameter("param2").equals("value2"));
	}
	

	
	/**
	 * Test remove spaces outside spaces.
	 */
	@Test
	public void testRemoveSpacesOutsideSpaces() {
		String toTest = "abc";
		String rToTest = Parser.removeSpacesOutsideQuotes(toTest);
		assertEquals("abc", rToTest);
		
		toTest = "      a   bc       ";
		rToTest = Parser.removeSpacesOutsideQuotes(toTest);
		assertEquals("abc", rToTest);
		
		toTest = "      a   \" bc \"      ";
		rToTest = Parser.removeSpacesOutsideQuotes(toTest);
		assertEquals("a\" bc \"", rToTest);
		
		toTest = "      a   \" bc \"  \"  nc sdf \"    ";
		rToTest = Parser.removeSpacesOutsideQuotes(toTest);
		assertEquals("a\" bc \"\"  nc sdf \"", rToTest);
		
		toTest = "  <a>   \" bc \"  \"  nc sdf \"   </a> ";
		rToTest = Parser.removeSpacesOutsideQuotes(toTest);
		assertEquals("<a>\" bc \"\"  nc sdf \"</a>", rToTest);
		
		toTest = "contains(    	text(), 			\" oh no this is pretty bad\"";
		rToTest = Parser.removeSpacesOutsideQuotes(toTest);
		assertEquals("contains(text(),\" oh no this is pretty bad\"", rToTest);
	}
	
	/**
	 * Test parse response code.
	 */
	@Test
	public void testParseResponseCode() {
		String firstLine = "HTTP/1.0 200 OK";
		Response response = null;
		try {
			response = Parser.parseResponseCode(firstLine);
		} catch (ParseException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		assertEquals(200, (int)response.getResponseCode());
		assertEquals("OK", response.getResponseString());
	}
	
	/**
	 * Test parse response version.
	 */
	@Test
	public void testParseResponseVersion() {
		String firstLine = "HTTP/1.0 200 OK";
		String version = Parser.parseResponseVersion(firstLine);
		assertEquals(version, "HTTP/1.0");
	}
	
	/**
	 * Test is valid x path node name.
	 */
	@Test
	public void testIsValidXPathNodeName() {
		String n1 = "-abc";
		assertEquals(false, Parser.isValidXPathNodeName(n1));
		n1 = ".abc";
		assertEquals(false, Parser.isValidXPathNodeName(n1));
		n1 = "0abc";
		assertEquals(false, Parser.isValidXPathNodeName(n1));
	}
	
	@Test
	public void testParseRobotsContent() {
		String domain = "https://dbappserv.cis.upenn.edu";
		String content = "User-agent: *\n"
				+ "Disallow: /path\n"
				+ "Disallow: /secondPath\n"
				+ "\n";
		MercatorNode node = Parser.parseRobotsContent(domain, content);
		assertEquals(domain, node.getDomain());
		assertEquals(false, node.isAllowed("/path"));
		assertEquals(false, node.isAllowed("/secondPath"));
		assertEquals(true, node.isAllowed("/"));
		content = "User-agent: *\n"
				+ "Disallow: /path\n"
				+ "Disallow: /secondPath\n"
				+ "\n"
				+ "User-agent: cis455crawler\n"
				+ "Disallow: /path/someInnerPath\n"
				+ "Disallow: /path/someSpecialPath";
		domain = "www.abc.com";
		node = Parser.parseRobotsContent(domain, content);
		assertEquals(true, node.getDomain().equals("www.abc.com"));
		assertEquals(true, node.isAllowed("/path"));
		assertEquals(true, node.isAllowed("/secondPath"));
		assertEquals(false, node.isAllowed("/path/someInnerPath"));
		assertEquals(false, node.isAllowed("/path/someInnerPath"));
	}
	
	@Test
	public void testAllowedContentTypes() {
		String contentType = "text/html; abc";
		assertEquals(true, Parser.isAllowedCrawlContentType(contentType));
		contentType = "text/xml; abc";
		assertEquals(true, Parser.isAllowedCrawlContentType(contentType));
		contentType = "application/xml";
		assertEquals(true, Parser.isAllowedCrawlContentType(contentType));
		contentType = "application/xhtml+xml";
		assertEquals(true, Parser.isAllowedCrawlContentType(contentType));
		contentType = "mimetype/xhtml+xml";
		assertEquals(true, Parser.isAllowedCrawlContentType(contentType));
		contentType = "image/gif";
		assertEquals(false, Parser.isAllowedCrawlContentType(contentType));
	}
	
	@Test
	public void testParseRobotsTxt() {
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
		System.out.println(node);
		URL url = null;
		try {
			url = new URL(domain + "/crawltest/marie/private");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		assertEquals(false, node.isAllowed(url.getPath()));
	}
	
	@Test
	public void testCleanHtml() {
		String content = "<html><body></body></html>";
		String out = null;
		try {
			out = Parser.cleanHtmlContent(content);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertNotSame(content, out);
	}

	@Test
	public void testIsCrawableUrl() {
		String url1 = "http://abc.html";
		String url2 = "abc.php";
		String url3 = "abc.gif";
		String url4 = "abc.css";
		String url5 = "abc.js";
		
		assertEquals(true, Parser.isCrawlableUrl(url1));
		assertEquals(false, Parser.isCrawlableUrl(url2));
		assertEquals(false, Parser.isCrawlableUrl(url3));
		assertEquals(false, Parser.isCrawlableUrl(url4));
		assertEquals(false, Parser.isCrawlableUrl(url5));
	}
}
