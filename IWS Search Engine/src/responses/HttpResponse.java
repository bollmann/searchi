/*
 * Written by Shreejit Gangadharan
 */
package responses;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import parsers.Parser;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpResponse.
 */
public abstract class HttpResponse implements HttpServletResponse {

	/** The version. */
	protected String version;

	/** The response. */
	protected Response response;

	/** The headers. */
	protected Map<String, List<String>> headers;

	/** The body. */
	protected byte[] body;

	/** The method. */
	protected String method;

	/** The cookies. */
	protected List<Cookie> cookies;

	/** The character encoding. */
	protected String characterEncoding = "ISO-8859-1";

	/** The content type. */
	protected String contentType = "text/html";

	/** The locale. */
	protected Locale locale;

	/** The buffer size. */
	protected int bufferSize = 1024;

	/** The is committed. */
	protected boolean isCommitted = false;

	/** The logger. */
	protected static Logger logger = Logger.getLogger(HttpResponse.class);

	/** The internal writer. */
	protected ServletWriter internalWriter;

	/** The internal string writer. */
	protected StringWriter internalStringWriter;

	/** The external writer. */
	protected PrintWriter externalWriter;

	/** The request. */
	protected HttpServletRequest request;

	/**
	 * Sets the request.
	 *
	 * @param request
	 *            the new request
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Gets the request.
	 *
	 * @return the request
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * The Class ServletWriter.
	 */
	class ServletWriter extends PrintWriter {

		/** The logger2. */
		private final Logger logger2 = Logger.getLogger(ServletWriter.class);

		/**
		 * Instantiates a new servlet writer.
		 *
		 * @param out
		 *            the out
		 */
		public ServletWriter(Writer out) {
			super(out);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.io.PrintWriter#flush()
		 */
		@Override
		public void flush() {
			try {
				flushBuffer();
			} catch (IOException e) {
				logger2.error("Error while trying to flush servlet writer");
				e.printStackTrace();
			}
		}
	}

	/** The content length. */
	private int contentLength;

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}

	/**
	 * Gets the headers.
	 *
	 * @return the headers
	 */
	public Map<String, List<String>> getHeaders() {
		return headers;
	}

	/**
	 * Sets the method.
	 *
	 * @param method
	 *            the new method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Gets the method.
	 *
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the version.
	 *
	 * @param version
	 *            the new version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public Response getResponse() {
		return response;
	}

	/**
	 * Sets the response.
	 *
	 * @param errorCode
	 *            the new response
	 */
	public abstract void setResponse(Integer errorCode);

	/**
	 * Sets the default body which is a simple html document with the
	 * appropriate error string corresponding to the error code.
	 */
	public abstract void setDefaultBody();

	/**
	 * Instantiates a new http response.
	 */
	public HttpResponse() {
		response = new Response();
		headers = new TreeMap<String, List<String>>(
				String.CASE_INSENSITIVE_ORDER);
		cookies = new ArrayList<Cookie>();
		internalStringWriter = new StringWriter();
		internalWriter = new ServletWriter(internalStringWriter);
	}

	/**
	 * Gets the marshalled headers.
	 *
	 * @return the marshalled headers
	 */
	public String getMarshalledHeaders() {
		StringBuilder marshalledResponse = new StringBuilder("");
		marshalledResponse.append(getVersion() + " " + getResponse() + "\n");
		for (Entry<String, List<String>> header : getHeaders().entrySet()) {
			for (String headerValue : header.getValue()) {
				marshalledResponse.append(header.getKey() + ": " + headerValue
						+ "\n");
			}
		}
		for (Cookie cookie : getCookies()) {
			marshalledResponse.append("Set-Cookie: ").append(cookie.getName())
					.append("=").append(cookie.getValue());
			if (cookie.getDomain() != null) {
				marshalledResponse.append("; Domain=").append(
						cookie.getDomain());
			}
			if (cookie.getPath() != null) {
				marshalledResponse.append("; Path=").append(cookie.getPath());
			}
			if (cookie.getSecure()) {
				marshalledResponse.append("; Secure; ");
			}
			if (cookie.getMaxAge() != -1) {
				marshalledResponse.append("; Max-Age=").append(
						String.valueOf(cookie.getMaxAge()));
			}
			marshalledResponse.append("\n");
		}
		marshalledResponse.append("\n");
		return marshalledResponse.toString().replaceAll("\n",
				Parser.LINE_DELIMS);
	}

	/**
	 * Adds the headers.
	 *
	 * @param header
	 *            the header
	 */
	public void addHeaders(Map<String, List<String>> header) {
		this.headers.putAll(header);
	}

	/**
	 * Adds the header to the existing hashmap of headers.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void addHeader(String key, String value) {
		if (headers.containsKey(key)) {
			headers.get(key).add(value);
		} else {
			List<String> hv = new ArrayList<String>();
			hv.add(value);
			headers.put(key, hv);
		}
	}

	/**
	 * Sets the body as a bytestream.
	 *
	 * @param body
	 *            the new body
	 */
	public void setBody(byte[] body) {
		internalWriter.write(new String(body));
		this.body = body;
		setHeader("Content-Length", String.valueOf(body.length));
	}

	/**
	 * To bytes.
	 *
	 * @return the byte[]
	 */
	public abstract byte[] toBytes();

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#flushBuffer()
	 */
	@Override
	public void flushBuffer() throws IOException {
		logger.debug("Flushbuffer called! Committed?" + isCommitted);

		logger.debug("Copying over cookies from request to response");
		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				addCookie(cookie);
			}
		}
		if (request.getRequestedSessionId() != null) {
			addCookie(new Cookie("sessionId", request.getRequestedSessionId()));
		}
		if (getResponse() == null || getResponse().getResponseCode() == null) {
			setResponse(200);
		}

		String body = internalStringWriter.toString();
		internalStringWriter.flush();
		// internalWriter.flush();
		contentLength = body.length();
		if (getContentType() == null) {
			setContentType("text/html");
		}
		setHeader("Content-Type", getContentType());
		setHeader("Content-Length", String.valueOf(contentLength));
		String headers = getMarshalledHeaders();
		StringBuilder sb = new StringBuilder();
		sb.append(headers);
		sb.append(body);
		logger.debug("Writing " + sb.toString() + " to the output writer");
		externalWriter.write(sb.toString());
		externalWriter.flush();
		isCommitted = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#getBufferSize()
	 */
	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
	 */
	@Override
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#getCharacterEncoding()
	 */
	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#getContentType()
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// Dont need to do
		return null;
	}

	/**
	 * Sets the external writer.
	 *
	 * @param writer
	 *            the new external writer
	 */
	public void setExternalWriter(PrintWriter writer) {
		this.externalWriter = writer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	@Override
	public PrintWriter getWriter() throws IOException {
		return internalWriter;
	}

	/**
	 * Sets the committed.
	 *
	 * @param isCommitted
	 *            the new committed
	 */
	public void setCommitted(boolean isCommitted) {
		this.isCommitted = isCommitted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#isCommitted()
	 */
	@Override
	public boolean isCommitted() {
		return isCommitted;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#reset()
	 */
	@Override
	public void reset() {
		resetBuffer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#resetBuffer()
	 */
	@Override
	public void resetBuffer() {
		internalStringWriter.flush();
		logger.debug("Internal writer flushed. Current content:"
				+ internalStringWriter);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#setBufferSize(int)
	 */
	@Override
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#setContentLength(int)
	 */
	@Override
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
	 */
	@Override
	public void setContentType(String contentType) {
		this.contentType = contentType;
		setHeader("Content-Type", contentType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
	 */
	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;

	}

	/**
	 * Gets the cookies.
	 *
	 * @return the cookies
	 */
	public List<Cookie> getCookies() {
		return cookies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie
	 * )
	 */
	@Override
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String,
	 * long)
	 */
	@Override
	public void addDateHeader(String arg0, long arg1) {
		if (headers.containsKey(arg0)) {
			headers.get(arg0).add(String.valueOf(arg1));
		} else {
			setDateHeader(arg0, arg1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String,
	 * int)
	 */
	@Override
	public void addIntHeader(String arg0, int arg1) {
		if (headers.containsKey(arg0)) {
			headers.get(arg0).add(String.valueOf(arg1));
		} else {
			List<String> hv = new ArrayList<String>();
			hv.add(String.valueOf(arg1));
			headers.put(arg0, hv);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
	 */
	@Override
	public boolean containsHeader(String arg0) {
		return headers.containsKey(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String
	 * )
	 */
	@Override
	public String encodeRedirectURL(String arg0) {
		// Don't need to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String
	 * )
	 */
	@Override
	public String encodeRedirectUrl(String arg0) {
		// Don't need to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
	 */
	@Override
	public String encodeURL(String arg0) {
		// Don't need to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
	 */
	@Override
	public String encodeUrl(String arg0) {
		// Don't need to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletResponse#sendError(int)
	 */
	@Override
	public void sendError(int responseCode) throws IOException {
		logger.error("Send error called. code:" + responseCode);
		resetBuffer();
		setResponse(responseCode);
		setDefaultBody();
		flushBuffer();
		isCommitted = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletResponse#sendError(int,
	 * java.lang.String)
	 */
	@Override
	public void sendError(int responseCode, String messg) throws IOException {
		logger.error("Send error called. code:" + responseCode + " with messg:"
				+ messg);
		resetBuffer();
		setResponse(responseCode);
		setBody(messg.getBytes());
		flushBuffer();
		isCommitted = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
	 */
	@Override
	public void sendRedirect(String arg0) throws IOException {
		setResponse(302);
		String loc = "";
		if (arg0.startsWith("/")) {
			loc = request.getProtocol() + "://" + request.getServerName() + ":"
					+ request.getServerPort() + arg0;
		} else {
			loc = request.getProtocol() + "://" + request.getServerName() + ":"
					+ request.getServerPort() + request.getRequestURI() + "/"
					+ arg0;
		}
		setHeader("Location", loc);
		String body = "<html><body>Moved to <a href='" + arg0 + "'>" + arg0
				+ "</a></body></html>";
		setBody(body.getBytes());
		flushBuffer();
		isCommitted = true;
	}

	/**
	 * Gets the date header.
	 *
	 * @param headerName
	 *            the header name
	 * @return the date header
	 */
	public long getDateHeader(String headerName) {
		if (headers.containsKey(headerName)) {
			return Long.parseLong(headers.get(headerName).get(0));
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String,
	 * long)
	 */
	@Override
	public void setDateHeader(String arg0, long arg1) {
		setHeader(arg0, String.valueOf(arg1));
	}

	/**
	 * Gets the headers.
	 *
	 * @param key
	 *            the key
	 * @return the headers
	 */
	public Enumeration<String> getHeaders(String key) {
		if (headers.containsKey(key)) {
			return Collections.enumeration(headers.get(key));
		}
		return Collections.enumeration(new ArrayList<String>());
	}

	/**
	 * Gets the header.
	 *
	 * @param key
	 *            the key
	 * @return the header
	 */
	public String getHeader(String key) {
		if (headers.containsKey(key)) {
			return headers.get(key).get(0);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setHeader(String key, String value) {
		List<String> hv = new ArrayList<String>();
		hv.add(value);
		headers.put(key, hv);
	}

	/**
	 * Gets the int headers.
	 *
	 * @param key
	 *            the key
	 * @return the int headers
	 */
	public Enumeration<Integer> getIntHeaders(String key) {
		List<Integer> intHeaders = new ArrayList<Integer>();
		if (headers.containsKey(key)) {
			for (String value : headers.get(key)) {
				try {
					intHeaders.add(Integer.parseInt(value));
				} catch (NumberFormatException e) {
					logger.error("Error converting int header:" + key
							+ " value:" + value);
					intHeaders.clear();
				}
			}
		}

		return Collections.enumeration(intHeaders);
	}

	/**
	 * Gets the int header.
	 *
	 * @param arg0
	 *            the arg0
	 * @return the int header
	 */
	public int getIntHeader(String arg0) {
		if (headers.containsKey(arg0)) {
			return Integer.parseInt(headers.get(arg0).get(0));
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String,
	 * int)
	 */
	@Override
	public void setIntHeader(String arg0, int arg1) {
		List<String> hv = new ArrayList<String>();
		hv.add(String.valueOf(arg1));
		headers.put(arg0, hv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
	 */
	@Override
	public void setStatus(int responseCode) {
		response.setResponseCode(responseCode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int,
	 * java.lang.String)
	 */
	@Override
	public void setStatus(int responseCode, String messg) {
		response.setResponseCode(responseCode);
		response.setResponseString(messg);

	}
}
