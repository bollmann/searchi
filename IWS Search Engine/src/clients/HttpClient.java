/*
 * Written by Shreejit Gangadharan
 */
package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;

import parsers.Parser;
import requests.HttpRequest;
import responses.Http10Response;
import responses.Http11Response;
import responses.HttpResponse;
import responses.Response;
import crawler.info.URLInfo;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpClient.
 */
public class HttpClient {

	/** The logger. */
	private static Integer bodyBufferSize = 1024 * 10;
	private static Logger logger = Logger.getLogger(HttpClient.class);

	public static String extractBody(int length, BufferedReader br)
			throws IOException {
		String body = "";
		int b, total = 0;

		StringBuilder buf = new StringBuilder(bodyBufferSize);

		while (total < length && (b = br.read()) != -1) {
			buf.append((char) b);
			total++;
			// logger.debug("Read:" + (char) b + " total:" + total + " length:"
			// + length);
		}
		body = buf.toString();
		logger.debug("Read body till :" + body.length()
				+ " out of buffer size:" + bodyBufferSize);
		return body;
	}

	public static void sendRequest(PrintWriter out, HttpRequest request)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getMarshalledHeaders());
		if (request.getMethod().equals("POST")) {
			sb.append(request.getBody());
		}
		out.write(sb.toString());
		out.flush();
		logger.debug("Wrote " + sb.toString()
				+ " to host. Waiting for reply...");
	}

	public static Http10Response receiveResponse(BufferedReader br,
			Http10Response response) throws IOException, ParseException {

		String inputLine, method = response.getMethod();
		List<String> fullInput = new ArrayList<String>();
		while (true) {
			inputLine = br.readLine();
			if (inputLine == null || Parser.LINE_DELIMS.contains(inputLine)) {
				// handle body outside this loop
				break;
			}
			logger.debug("Got this from socket: " + inputLine);
			if (inputLine.split(Parser.HEADER_SEPARATOR).length < 2) {
				logger.debug("Status line:" + inputLine);
				Response resp = Parser.parseResponseCode(inputLine);
				response.setResponse(resp.getResponseCode());
				if (resp.getResponseCode() != 200) {
					logger.error("Didn't get a proper response. Exiting...");
					break;
				}
				continue;
			}
			String header = inputLine.split(Parser.HEADER_SEPARATOR)[0].trim();
			String value = inputLine.split(Parser.HEADER_SEPARATOR)[1].trim();
			// adding support for cookies
			if (header.equals("Cookie")) {
				for (String ckv : value.split("; ")) {
					String cookieName = ckv.split("=")[0];
					String cookieValue = ckv.split("=")[1];
					response.addCookie(new Cookie(cookieName, cookieValue));
				}
			} else {
				response.addHeader(header, value);
			}
			fullInput.add(inputLine);
		}
		if (!method.equals("HEAD")) {
			String lengthHeader = response.getHeader("Content-Length");
			int length = Integer.MAX_VALUE;
			if (lengthHeader != null) {
				length = Integer.parseInt(lengthHeader);
			}

			String body = extractBody(length, br);
			if (response.getHeader("Conntent-Type") != null) {
				if (response.getHeader("Content-Type").equals("text/html")) {
					body = Parser.cleanHtmlContent(body);
				}
			}
			response.setBody(body.getBytes());
		}
		return response;
	}

	/**
	 * Gets the.
	 *
	 * @param url
	 *            the url
	 * @return the http10 response
	 * @throws UnknownHostException
	 *             the unknown host exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 */
	public static Http10Response get(String url, HttpRequest request)
			throws UnknownHostException, IOException, ParseException {
		Http10Response response = new Http10Response();
		request.setMethod("GET");
		response.setMethod("GET");
		URL pUrl = new URL(url);

		logger.info("Sending a GET request to url:" + request.getPath() + "?"
				+ pUrl.getQuery() + " parsed into:" + pUrl + " host:"
				+ pUrl.getHost() + " port:" + pUrl.getPort());
		HttpURLConnection conn = (HttpURLConnection) pUrl.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(false);
		HttpURLConnection.setFollowRedirects(false);
		conn.setUseCaches(false);
		conn.setRequestMethod("GET");
		if (request.getHeaderNames() != null) {
			List<String> headerNames = Collections.list(request
					.getHeaderNames());
			for (String headerName : headerNames) {
				conn.setRequestProperty(headerName,
						request.getHeader(headerName));
			}
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		conn.connect();
		logger.debug("GET connected!");
		Map<String, List<String>> map = conn.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			for (String value : entry.getValue()) {
				if (entry.getKey() != null) {
					response.setHeader(entry.getKey(), value);
				} else {
					Response resp = Parser.parseResponseCode(value);
					String version = Parser.parseResponseVersion(value);
					response.setResponse(resp.getResponseCode());
					response.setVersion(version);
				}
			}
			logger.debug("Key : " + entry.getKey() + " ,Value : "
					+ entry.getValue());
		}
		int length = Integer.MAX_VALUE;
		if (response.containsHeader("Content-Length")) {
			length = Integer.parseInt(response.getHeader("Content-Length"));
		}

		String body = extractBody(length, br);
		logger.debug("Https get body:" + body);
		response.setBody(body.getBytes());
		br.close();
		return response;
	}

	public static Http10Response post(String url, HttpRequest request)
			throws UnknownHostException, IOException, ParseException {
		Http10Response response = new Http10Response();
		request.setMethod("POST");
		response.setMethod("POST");
		URL pUrl = new URL(url);

		request.setPath(pUrl.getPath());
		request.setQueryString();
		int port = pUrl.getPort();
		String host = pUrl.getHost();
		logger.info("Sending a POST request to url:" + pUrl + " host:"
				+ pUrl.getHost() + " port:" + pUrl.getPort() + " with body " + request.getBody());
		Socket socket = null;
		if (port == -1) {
			socket = new Socket(host, 80);
		} else {
			socket = new Socket(host, port);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		sendRequest(out, request);
		response = receiveResponse(br, response);

		socket.close();
		return response;
	}

	public static Http10Response head(String url, HttpRequest request)
			throws UnknownHostException, IOException, ParseException {
		Http10Response response = new Http10Response();
		response.setMethod("HEAD");
		URL pUrl = new URL(url);
		logger.info("Sending a HEAD request to url:" + url + " parsed into:"
				+ pUrl + " host:" + pUrl.getHost() + " port:" + pUrl.getPort());
		HttpURLConnection conn = (HttpURLConnection) pUrl.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(false);
		HttpURLConnection.setFollowRedirects(false);
		conn.setUseCaches(false);
		conn.setRequestMethod("HEAD");

		if (request.getHeaderNames() != null) {
			List<String> headerNames = Collections.list(request
					.getHeaderNames());
			for (String headerName : headerNames) {
				conn.setRequestProperty(headerName,
						request.getHeader(headerName));
			}
		}

		if (request.getDateHeaderNames() != null) {
			List<String> dateHeaderNames = Collections.list(request
					.getDateHeaderNames());
			logger.debug("Header names:" + dateHeaderNames);
			for (String headerName : dateHeaderNames) {
				Date date = new Date(request.getDateHeader(headerName));
				logger.debug("Setting date header k:" + headerName + "="
						+ Parser.formatDate(date));
				conn.setRequestProperty(headerName, Parser.formatDate(date));
			}
		}
		conn.connect();
		logger.info("HEAD connected!");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		Map<String, List<String>> map = conn.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			for (String value : entry.getValue()) {
				logger.debug("Heads trying to put:" + entry.getKey() + "="
						+ value);
				if (entry.getKey() != null) {
					response.setHeader(entry.getKey(), value);
				} else {
					Response resp = Parser.parseResponseCode(value);
					String version = Parser.parseResponseVersion(value);
					response.setResponse(resp.getResponseCode());
					response.setVersion(version);
				}
			}
		}
		br.close();
		logger.debug("HEAD over");
		return response;
	}

	public static Http11Response heads(String url, HttpRequest request)
			throws IOException, ParseException {
		Http11Response response = new Http11Response();
		response.setMethod("HEAD");
		URL pUrl = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection) pUrl.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(false);
		HttpsURLConnection.setFollowRedirects(false);
		conn.setUseCaches(false);
		conn.setRequestMethod("HEAD");

		if (request.getHeaderNames() != null) {
			List<String> headerNames = Collections.list(request
					.getHeaderNames());
			for (String headerName : headerNames) {
				conn.setRequestProperty(headerName,
						request.getHeader(headerName));
			}
		}

		if (request.getDateHeaderNames() != null) {
			List<String> dateHeaderNames = Collections.list(request
					.getDateHeaderNames());
			logger.debug("Header names:" + dateHeaderNames);
			for (String headerName : dateHeaderNames) {
				Date date = new Date(request.getDateHeader(headerName));
				logger.debug("Setting date header k:" + headerName + "="
						+ Parser.formatDate(date));
				conn.setRequestProperty(headerName, Parser.formatDate(date));
			}
		}
		conn.connect();
		logger.info("HEAD connected!");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		Map<String, List<String>> map = conn.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			for (String value : entry.getValue()) {
				logger.debug("Heads trying to put:" + entry.getKey() + "="
						+ value);
				if (entry.getKey() != null) {
					response.setHeader(entry.getKey(), value);
				} else {
					Response resp = Parser.parseResponseCode(value);
					String version = Parser.parseResponseVersion(value);
					response.setResponse(resp.getResponseCode());
					response.setVersion(version);
				}
			}
		}
		br.close();
		return response;
	}

	public static Http11Response gets(String url, HttpRequest request)
			throws IOException, ParseException {
		Http11Response response = new Http11Response();
		response.setMethod("GET");
		URL pUrl = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection) pUrl.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(false);
		HttpsURLConnection.setFollowRedirects(false);
		conn.setUseCaches(false);
		conn.setRequestMethod("GET");
		List<String> headerNames = Collections.list(request.getHeaderNames());
		for (String headerName : headerNames) {
			conn.setRequestProperty(headerName, request.getHeader(headerName));
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		conn.connect();
		logger.debug("GET connected!");
		Map<String, List<String>> map = conn.getHeaderFields();
		for (Map.Entry<String, List<String>> entry : map.entrySet()) {
			for (String value : entry.getValue()) {
				if (entry.getKey() != null) {
					response.setHeader(entry.getKey(), value);
				} else {
					Response resp = Parser.parseResponseCode(value);
					String version = Parser.parseResponseVersion(value);
					response.setResponse(resp.getResponseCode());
					response.setVersion(version);
				}
			}
			logger.debug("Key : " + entry.getKey() + " ,Value : "
					+ entry.getValue());
		}
		int length = Integer.MAX_VALUE;
		if (response.containsHeader("Content-Length")) {
			length = Integer.parseInt(response.getHeader("Content-Length"));
		}

		String body = extractBody(length, br);
		logger.debug("Https get body:" + body);
		response.setBody(body.getBytes());
		br.close();
		return response;
	}

	public static HttpResponse genericGet(String url, HttpRequest request)
			throws IOException, ParseException {
		HttpResponse response = null;
		URLInfo urlInfo = new URLInfo(url);
		switch (urlInfo.getProtocol()) {
		case "http":
			response = get(url, request);
			break;
		case "https":
			response = gets(url, request);
			break;
		default:
			logger.error("Client got url with incorrect protocol");
		}
		return response;
	}

	public static HttpResponse genericHead(String url, HttpRequest request)
			throws IOException, ParseException {
		HttpResponse response = null;
		URLInfo urlInfo = new URLInfo(url);
		switch (urlInfo.getProtocol()) {
		case "http":
			response = head(url, request);
			break;
		case "https":
			response = heads(url, request);
			break;
		default:
			logger.error("Client got url with incorrect protocol");
		}
		return response;
	}

	public static HttpResponse genericPost(String url, HttpRequest request)
			throws IOException, ParseException {
		HttpResponse response = null;
		URLInfo urlInfo = new URLInfo(url);
		switch (urlInfo.getProtocol()) {
		case "http":
			response = post(url, request);
			break;
		case "https":
			// response = posts(url, request);
			break;
		default:
			logger.error("Client got url with incorrect protocol");
		}
		return response;
	}
}
