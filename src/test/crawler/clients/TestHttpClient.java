package test.crawler.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.google.gson.Gson;

import crawler.clients.HttpClient;
import crawler.parsers.Parser;
import crawler.requests.Http10Request;
import crawler.responses.Http10Response;
import crawler.responses.HttpResponse;
import crawler.threadpool.MercatorQueue;

public class TestHttpClient extends TestCase {
	
	@Test
	public void testExtractBody() {
		String content = "some body\n";
		StringReader sr = new StringReader(content);
		BufferedReader br = new BufferedReader(sr);
		String readContent = null;
		try {
			readContent = HttpClient.extractBody(content.length(), br);
		} catch (IOException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		
		assertEquals(readContent, content);
	}
	
	@Test
	public void testSendRequest() {
		Http10Request request = new Http10Request();
		Date date = Calendar.getInstance().getTime();
		request.addDateHeader("Date", date.getTime());
		request.setMethod("GET");
		request.setPath("/abc");
		request.setHeader("User-Agent", "cis455crawler");
		StringWriter sr = new StringWriter();
		PrintWriter out = new PrintWriter(sr);
		try {
			HttpClient.sendRequest(out, request);
		} catch (IOException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		String exp = ("GET /abc HTTP/1.0\n"
				+ "User-Agent: cis455crawler\n"
				+ "Date: " + Parser.formatDate(date) + "\n"
						+ "\n").replaceAll("\n", Parser.LINE_DELIMS);
		assertEquals(request.getMarshalledHeaders(), sr.toString());
		assertEquals(exp, sr.toString());
		
	}
	
	@Test
	public void testSendRequestForPost() {
		Http10Request request = new Http10Request();
		Date date = Calendar.getInstance().getTime();
		request.addDateHeader("Date", date.getTime());
		request.setMethod("POST");
		request.setPath("/abc");
		request.setHeader("User-Agent", "cis455crawler");
		String body = "There's some body here";
		request.setBody(body);
		StringWriter sr = new StringWriter();
		PrintWriter out = new PrintWriter(sr);
		try {
			HttpClient.sendRequest(out, request);
		} catch (IOException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		String exp = ("POST /abc HTTP/1.0\n"
				+ "Content-Length: 22\n"
				+ "User-Agent: cis455crawler\n"
				+ "Date: " + Parser.formatDate(date) + "\n"
				+ "\n"
				+ body).replaceAll("\n", Parser.LINE_DELIMS);
		assertEquals(request.getMarshalledHeaders(), sr.toString().replace(body, ""));
		assertEquals(exp, sr.toString());
		
	}
	
	@Test
	public void testReceiveResponseGET() {
		Http10Response response = new Http10Response();
		Http10Response mock = new Http10Response();
		mock.setBody("some body".getBytes());
		mock.setMethod("GET");
		mock.setResponse(200);
		mock.addHeader("User-Agent", "cis455crawler");
		String toRead = new String(mock.toBytes());
		StringReader sr = new StringReader(toRead);
		BufferedReader br = new BufferedReader(sr);
		response.setMethod("GET");
		try {
			response = HttpClient.receiveResponse(br, response);
		} catch (IOException | ParseException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		assertEquals(mock.getResponse().getResponseCode(), response.getResponse().getResponseCode());
		assertEquals(mock.getHeader("User-Agent"), response.getHeader("User-Agent"));
		assertEquals(new String(mock.getBody()), new String(response.getBody()));
	}
	
	@Test
	public void testReceiveResponseHEAD() {
		Http10Response response = new Http10Response();
		Http10Response mock = new Http10Response();
		mock.setBody("some body".getBytes());
		mock.setMethod("HEAD");
		mock.setResponse(200);
		mock.addHeader("User-Agent", "cis455crawler");
		String toRead = new String(mock.toBytes());
		StringReader sr = new StringReader(toRead);
		BufferedReader br = new BufferedReader(sr);
		response.setMethod("HEAD");
		try {
			response = HttpClient.receiveResponse(br, response);
		} catch (IOException | ParseException e) {
			assertEquals("", e.getMessage());
			e.printStackTrace();
		}
		assertEquals(mock.getResponse().getResponseCode(), response.getResponse().getResponseCode());
		assertEquals(mock.getHeader("User-Agent"), response.getHeader("User-Agent"));
		assertNull(response.getBody());
	}
	
	@Test
	public void testExtractContentFromGzip() throws IOException, ParseException {
		Http10Request request = new Http10Request();
		String url = "http://projects.fivethirtyeight.com/stump-speech/";
		request.setPath(new URL(url).getPath());
		request.setMethod("GET");
		request.setHeader("User-Agent", "cis455crawler");
		request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		HttpResponse response = HttpClient.genericGet(url, request);
		String content = new String(response.getBody());
		System.out.println("Original:" + content);
		
		url = "https://en.wikipedia.org/wiki/Main_Page";
		request.setPath(new URL(url).getPath());
		request.setMethod("GET");
		request.setHeader("User-Agent", "cis455crawler");
		request.setHeader("Accept",
				"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		response = HttpClient.genericGet(url, request);
		content = new String(response.getBody());
		System.out.println("Original:" + content);
	}
	
	@Test
	public void testPost() {
		List<String> urls = new ArrayList<String>();
		urls.add("http://www.google.com");
		String content = new Gson().toJson(urls);
		Http10Request request = new Http10Request();
		request.setBody(content);
		
		try {
			HttpClient.post("http://" + "127.0.0.1:8080" + "/master/enqueueURLs",
					request);
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			 e.printStackTrace();
		}
	}
	
	@Test
	public void testGetToRobotsOnDomain() {
		String url = "http://www.google.com";
		Http10Request request = new Http10Request();
		try {
			String robotsUrl = MercatorQueue.getRobotsTxtUrl(url);
			Http10Response response = HttpClient.get(robotsUrl, request);
			assertEquals(200, (int) response.getResponse().getResponseCode());
			
			robotsUrl = MercatorQueue.getRobotsTxtUrl("http://google.com");
			response = HttpClient.get(robotsUrl, request);
			assertEquals(200, (int) response.getResponse().getResponseCode());
		} catch (URISyntaxException | UnknownHostException | MalformedURLException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
