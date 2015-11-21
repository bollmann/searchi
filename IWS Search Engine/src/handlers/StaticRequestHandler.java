/*
 * Written by Shreejit Gangadharan
 */
package handlers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import org.apache.log4j.Logger;

import parsers.Parser;
import requests.Http10Request;
import requests.Http11Request;
import requests.HttpRequest;
import responses.Http11Response;
import responses.HttpResponse;
import threadpool.Queue;

// TODO: Auto-generated Javadoc
/**
 * The Class StaticRequestHandler.
 */
public class StaticRequestHandler extends RequestHandler {

	/** The logger. */
	private static Logger logger = Logger.getLogger(MainHandler.class);

	/**
	 * Instantiates a new static request handler.
	 *
	 * @param socket the socket
	 */
	public StaticRequestHandler(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Handle request.
	 *
	 * @param fullInput            the full input
	 * @param webRoot            the web root
	 * @param jobQueue            the job queue
	 * @return the byte[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public HttpResponse handleRequest(List<String> fullInput, String webRoot,
			Queue<Socket> jobQueue) throws IOException {
		String firstLine = fullInput.get(0);
		HttpRequest request;
		HttpResponse response;

		String version = null;
		try {
			version = Parser.parseRequestVersion(firstLine);
		} catch (IllegalArgumentException e) {
			logger.error("Got a bad request!");
			response = new Http11Response();
			response.setResponse(400);
			response.setDefaultBody();
			response.addHeader("Host", socket.getLocalAddress().getHostName());
			return response;
		}
		try {
			switch (version) {
			case "HTTP/1.0":
				logger.info("This is a HTTP/1.0 request");
				request = new Http10Request();
				request.setSocketInputStream(socket.getInputStream());
				response = request.generateResponse(fullInput, webRoot,
						jobQueue);
				return response;
			case "HTTP/1.1":
				logger.info("This is a HTTP/1.1 request");
				request = new Http11Request();
				request.setSocketInputStream(socket.getInputStream());
				response = request.generateResponse(fullInput, webRoot,
						jobQueue);
				return response;
			case "HTTP/1.2":
				logger.info("This is a HTTP/1.2 request");
				request = new Http11Request();
				request.setSocketInputStream(socket.getInputStream());
				response = request.generateResponse(fullInput, webRoot,
						jobQueue);
				return response;
			default:
				logger.info("This has an unimplemented version");
				response = new Http11Response();
				response.setResponse(400);
				response.setVersion("HTTP/1.1");
				response.setDefaultBody();
				return response;
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("IO exception!");
			response = new Http11Response();
			response.setResponse(500);
			response.setDefaultBody();
			return response;
		}
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis.cis455.handlers.RequestHandler#writeOutput(byte[])
	 */
	@Override
	public void writeOutput(byte[] output) throws IOException {
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		out.write(new String(output));
		out.flush();
		socket.close();
	}
}
