/*
 * Written by Shreejit Gangadharan
 */
package edu.upenn.cis455.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.upenn.cis.cis455.clients.HttpClient;
import edu.upenn.cis.cis455.requests.Http10Request;
import edu.upenn.cis.cis455.requests.HttpRequest;
import edu.upenn.cis.cis455.responses.Http10Response;
import edu.upenn.cis455.xpathengine.XPathEngineFactory;
import edu.upenn.cis455.xpathengine.XPathHandler;

// TODO: Auto-generated Javadoc
/**
 * The Class XPathServlet.
 */
@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {
	
	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass());

	/* TODO: Implement user interface for XPath engine here */

	/* You may want to override one or both of the following methods */

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		/* TODO: Implement user interface for XPath engine here */
		logger.info("Got parameters: " + request.getParameterNames());
		String url = null;
		try {
			url = URLDecoder.decode(request.getParameter("url"), "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		InputStream stream = null;
		Http10Response resp = null;
		try {
			HttpRequest req = new Http10Request();
			req.setMethod("GET");
			req.setPath(url);
			req.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			req.setHeader("Host", "localhost");
			resp = HttpClient.get(url, req);
			stream = new ByteArrayInputStream(resp.getBody());
		} catch (IOException | ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String xPath = "";
		try {
			xPath = URLDecoder.decode(request.getParameter("xPath"), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String[] xPaths = xPath.split(";");
		logger.info("After url decoding url:" + url + " xPaths:" + xPaths);

		XPathHandler handler = XPathEngineFactory.getSAXHandler();
		handler.setXPaths(xPaths);

		StringBuilder sb = new StringBuilder();
		sb.append("<html><body>");
		sb.append("<h2>Validity Status</h2><br/>");
		sb.append("<table><tr><th>XPath</th><th>Valid?</th><tr>");
		for (int i = 0; i < xPaths.length; i++) {
			sb.append("<tr><td>" + xPaths[i] + "</td><td>" + handler.isValid(i)
					+ "</td></tr>");
		}
		sb.append("</table><br/>");
		sb.append("<br/><h2>Match Status</h2><br/>");
		sb.append("<table><tr><th>XPath</th><th>Valid?</th><tr>");
		boolean[] op = handler.evaluateSAX(stream, handler);
		for (int i = 0; i < xPaths.length; i++) {
			sb.append("<tr><td>" + xPaths[i] + "</td><td>" + op[i]
					+ "</td></tr>");
		}
		sb.append("</table><br/>");
		
//		sb.append("For document:<br/><pre><code>");
//		logger.info("Document content: " + document);
//		sb.append(HttpClient.encodeHTML(document));
//		sb.append("</code></pre><br/>");
		
		sb.append("</body></html>");

		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writer.write(sb.toString());

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		/* TODO: Implement user interface for XPath engine here */
		response.setContentType("text/html");
		String toWrite = "<html><title>Shreejit's XPath servlet</title><body>"
				+ "<h2>Name:   Shreejit Gangadharan</h2><br/>"
				+ "<h2>Seas Login: shreejit</h2><br/>"
				+ "<form name='xpath-form' action='/xPathServlet' method='post' accept-charset='utf-8'>"
				+ "<label name='xPath'>XPath expression (multiple xpaths need to be separated by ';'):  </label><input type='text'/ name='xPath'><br/>"
				+ "<label name'url'>URL:  </label><input type='text' name='url'><br/>"
				+ "<input type='submit' value='Submit'>"
				+ "</form>"
				+ "<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css\">"
				+ "</body></html>";
		PrintWriter out = new PrintWriter(response.getWriter());
		out.write(toWrite);

	}

}
