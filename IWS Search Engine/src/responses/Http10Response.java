/*
 * Written by Shreejit Gangadharan
 */
package responses;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import requests.Http10Request;

// TODO: Auto-generated Javadoc
/**
 * The Class Http10Response.
 */
public class Http10Response extends HttpResponse {
	
	/** The error code defns. */
	private static Map<Integer, String> errorCodeDefns = new HashMap<Integer, String>() {{
		put(200, "OK");
		put(201, "Created");
		put(202, "Accepted");
		put(204, "No Content");
		
		put(301, "Moved Permanently");
		put(302, "Moved Temporarily");
		put(303, "See Other");
		put(304, "Not Modified");
		
		
		put(400, "Bad Request");
		put(401, "Unauthorized");
		put(403, "Forbidden");
		put(404, "Not Found");
		
		put(500, "Internal Server Error");
		put(501, "Method Not Implemented");
		put(502, "Bad Gateway");
		put(503, "Service Unavailable");
	}};
	
	/**
	 * Gets the error code defns.
	 *
	 * @return the error code defns
	 */
	public static Map<Integer, String> getErrorCodeDefns() {
		return errorCodeDefns;
	}
	
	/**
	 * Inits the.
	 */
	private void init() {
		setVersion("HTTP/1.0");
		addHeader("Server", "Shreejit's server/1.2");
		addHeader("Connection", "Close");
	}
	
	/**
	 * Instantiates a new http10 response.
	 */
	public Http10Response() {
		super();
		init();
	}
	
	/**
	 * Instantiates a new http10 response.
	 *
	 * @param request the request
	 */
	public Http10Response(Http10Request request) {
		super();
		init();
		this.request = request;
	}
	
	/* (non-Javadoc)
	 * @see edu.upenn.cis.cis455.responses.HttpResponse#setResponse(java.lang.Integer)
	 */
	@Override
	public void setResponse(Integer errorCode) {
		response.setResponseCode(errorCode);
		response.setResponseString(errorCodeDefns.get(errorCode));
	}
	
	/* (non-Javadoc)
	 * @see edu.upenn.cis.cis455.responses.HttpResponse#setDefaultBody()
	 */
	@Override
	public void setDefaultBody() {
		String body = "<html><body>" + errorCodeDefns.get(response.getResponseCode()) + "</body></html>";
		setBody(body.getBytes(StandardCharsets.UTF_8));
		setHeader("Content-Type", "text/html");
	}
	
	
	/* (non-Javadoc)
	 * @see edu.upenn.cis.cis455.responses.HttpResponse#toBytes()
	 */
	@Override
	public byte[] toBytes() {
		byte[] byteResponse;
		String headers = getMarshalledHeaders();
	    byte[] headerB = headers.getBytes(StandardCharsets.UTF_8);
	    if(method == null) {
	    	method = "";
	    }
	    if(body == null) {
	    	body = "".getBytes(StandardCharsets.UTF_8);
	    }
	    byteResponse = new byte[headerB.length + body.length];
	    System.arraycopy(headerB, 0, byteResponse, 0, headerB.length);
	    //TODO what if no headers
	    if(!method.equals("HEAD")) {
	    	System.arraycopy(body, 0, byteResponse, headerB.length, body.length);
	    }
	    return byteResponse;
	}
}
