/*
 * Written by Shreejit Gangadharan
 */
package handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.ParseException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import parsers.Parser;
import requests.Http10Request;
import requests.Http11Request;
import requests.HttpRequest;
import responses.Http10Response;
import responses.Http11Response;
import responses.HttpResponse;
import threadpool.Queue;

// TODO: Auto-generated Javadoc
/**
 * The Class ServletHandler.
 */
public class ServletHandler extends RequestHandler {

	/** The logger. */
	private final Logger logger = Logger.getLogger(getClass());

	/** The servlet. */
	private HttpServlet servlet;

	/** The url match. */
	private String urlMatch;

	/**
	 * Instantiates a new servlet handler.
	 *
	 * @param socket
	 *            the socket
	 * @param servlet
	 *            the servlet
	 * @param urlMatch
	 *            the url match
	 */
	public ServletHandler(Socket socket, HttpServlet servlet, String urlMatch) {
		this.socket = socket;
		this.servlet = servlet;
		this.urlMatch = urlMatch;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.upenn.cis.cis455.handlers.RequestHandler#handleRequest(java.util.
	 * List, java.lang.String, edu.upenn.cis.cis455.threadpool.Queue)
	 */
	@Override
	public HttpResponse handleRequest(List<String> fullInput, String webRoot,
			Queue<Socket> jobQueue) throws IOException {
		String firstLine = fullInput.get(0);
		HttpRequest request = null;
		HttpResponse response;
		String version = null;
		try {
			version = Parser.parseRequestVersion(firstLine);
		} catch (Exception e) {
			logger.error("Got a bad request!");
			response = new Http11Response();
			response.setRequest(request);
			response.setResponse(400);
			response.setDefaultBody();
			response.setVersion("HTTP/1.1");
			response.addHeader("Host", socket.getLocalAddress().getHostName());
			return response;
		}
		try {
			switch (version) {
			case "HTTP/1.0":
				logger.debug("This is a HTTP/1.0 request");
				request = new Http10Request();
				response = new Http10Response();
				request.setSocketInputStream(socket.getInputStream());

				fillRequestAndResponse(firstLine, request, response, fullInput);
				request.setPathInfoFromServletPath(urlMatch);
				service(request, response);
				return response;
			case "HTTP/1.1":
				logger.debug("This is a HTTP/1.1 request");
				request = new Http11Request();
				response = new Http11Response();

				request.setSocketInputStream(socket.getInputStream());
				fillRequestAndResponse(firstLine, request, response, fullInput);
				request.setPathInfoFromServletPath(urlMatch);
				service(request, response);
				return response;
			case "HTTP/1.2":
				logger.debug("This is a HTTP/1.2 request");
				request = new Http11Request();
				response = new Http11Response();

				request.setSocketInputStream(socket.getInputStream());
				fillRequestAndResponse(firstLine, request, response, fullInput);
				request.setPathInfoFromServletPath(urlMatch);
				service(request, response);
				return response;
			default:
				logger.debug("This is a bad request");
				response = new Http11Response();
				response.setRequest(request);
				response.setResponse(400);
				response.setExternalWriter(new PrintWriter(socket
						.getOutputStream()));
				response.setVersion("HTTP/1.1");
				response.setDefaultBody();
				response.addHeader("Connection", "Close");
				response.addHeader("Server", "Shreejit/1.2");
				response.flushBuffer();
				return response;
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			response = new Http11Response();
			response.setRequest(request);
			response.setExternalWriter(new PrintWriter(socket.getOutputStream()));
			response.setResponse(400);
			response.setDefaultBody();
			response.setVersion("HTTP/1.1");
			response.addHeader("Host", socket.getLocalAddress().getHostName());
			response.flushBuffer();
			return response;
		} catch (ParseException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
			response = new Http11Response();
			response.setRequest(request);
			response.setExternalWriter(new PrintWriter(socket.getOutputStream()));
			response.setResponse(400);
			response.setDefaultBody();
			response.setVersion("HTTP/1.1");
			response.addHeader("Host", socket.getLocalAddress().getHostName());
			response.flushBuffer();
			return response;
		}
	}

	/**
	 * Fill request and response.
	 *
	 * @param firstLine
	 *            the first line
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @param fullInput
	 *            the full input
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParseException
	 *             the parse exception
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 */
	public void fillRequestAndResponse(String firstLine, HttpRequest request,
			HttpResponse response, List<String> fullInput) throws IOException,
			ParseException, IllegalArgumentException {
		logger.debug("Filling headers");
		request.fillHeaders(fullInput, response);
		String method = Parser.parseRequestMethod(firstLine);
		request.setMethod(method);
		String path = Parser.parseRequestPath(firstLine);
		request.setPath(path);
		request.setQueryString();
		String paramString = null;
		if (method.equals("GET") || method.equals("HEAD")) {
			paramString = request.getQueryString();
		} else {
			// handle post
			paramString = request.getBody();
		}
		if (paramString != null) {
			if (request.getMethod().equals("GET")
					|| request.getMethod().equals("HEAD")
					|| (request.getHeader("Content-Type") != null && request
							.getHeader("Content-Type").equals(
									Parser.formEncoding))) {
				for (String kv : paramString.trim().split("&")) {
					String key = kv.split("=")[0];
					String value = kv.split("=")[1];
					request.setParamater(key, value);
				}
			}

		}
		logger.debug("Query parameters now:" + request.getParameterMap());
		request.setContextPathFromServletPath(urlMatch);
		InetSocketAddress socka = (InetSocketAddress) socket
				.getRemoteSocketAddress();
		request.setRemoteHost(socka.getHostName());
		request.setRemotePort(socka.getPort());
		request.setRequestURI();
		request.setServerName(socket.getLocalAddress().getCanonicalHostName());
		request.setServerPort(socket.getPort());
		request.setServletPathWithServletPath(urlMatch);

		response.setExternalWriter(new PrintWriter(socket.getOutputStream()));
		response.addHeader("Host", socket.getLocalAddress().getHostName());
		logger.debug("Response cookies now:" + response.getCookies());
	}

	/**
	 * Service.
	 *
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	public void service(HttpRequest request, HttpResponse response) {
		try {
			logger.info("Now calling service method for:" + servlet);
			response.setRequest(request);
			servlet.service(request, response);

			if (!response.isCommitted()) {
				response.flushBuffer();
			}
		} catch (ServletException | IOException e) {
			response.setResponse(500);
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error("Fatal error in servlet handler!" + e.getMessage());
			response.setResponse(500);
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.upenn.cis.cis455.handlers.RequestHandler#writeOutput(byte[])
	 */
	@Override
	public void writeOutput(byte[] output) throws IOException {
		// PrintWriter out = new PrintWriter(socket.getOutputStream());
		// out.write(new String(output));
		// out.flush();
		logger.debug("Not writing anything");
		socket.close();
	}
}
