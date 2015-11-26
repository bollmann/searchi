/*
 * Written by Shreejit Gangadharan
 */
package crawler.requests;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import crawler.handlers.MainHandler;
import crawler.parsers.Parser;
import crawler.responses.HttpResponse;
import crawler.servlets.HttpSessionImpl;
import crawler.servlets.SessionMap;
import crawler.threadpool.Queue;
import crawler.threadpool.ThreadPool;
import crawler.webserver.HttpServer;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpRequest. Is the abstract parent of the request types that are
 * handled by the server
 */
public abstract class HttpRequest implements HttpServletRequest {

	/** The cookies. */
	protected Map<String, Cookie> cookies;

	/** The parameters. */
	protected Map<String, List<String>> parameters;
	/** The logger. */
	protected static Logger logger = Logger.getLogger(HttpRequest.class);

	/** The allowed headers. */
	protected static Map<String, Boolean> allowedHeaders = new TreeMap<String, Boolean>(
			String.CASE_INSENSITIVE_ORDER) {
		{
			put("Accept", true);
			put("User-Agent", true);
			put("Content-Type", true);
			put("Date", true);
			put("Expires", true);
			put("From", true);
			put("If-Modified-Since", true);
			put("Last-Modified", true);
			put("Location", true);
			put("Referer", true);
			put("Server", true);
		}
	};

	/** The method. */
	protected String method;

	/** The version. */
	protected String version;

	/** The path. */
	protected String path;

	/** The headers. */
	// protected Map<String, String> headers;
	protected Map<String, List<String>> headers;

	/** The body. */
	protected String body;

	/** The path info. */
	protected String pathInfo;

	/** The context path. */
	protected String contextPath;

	/** The query string. */
	protected String queryString;

	/** The request uri. */
	protected String requestURI;

	/** The request url. */
	protected String requestURL;

	/** The server name. */
	protected String serverName;

	/** The server port. */
	protected int serverPort;

	/** The protocol. */
	protected String protocol = "http";

	/** The servlet path. */
	protected String servletPath;

	/** The session. */
	protected HttpSessionImpl session;

	/** The attributes. */
	protected Map<String, Object> attributes;

	/** The character encoding. */
	protected String characterEncoding = "ISO-8859-1";

	/** The content length. */
	protected int contentLength;

	/** The content type. */
	protected String contentType = "text/html";

	/** The remote host. */
	private String remoteHost;

	/** The remote port. */
	private int remotePort;

	/** The local port. */
	private int localPort;

	/** The locale. */
	private Locale locale;

	/** The socket input stream. */
	private InputStream socketInputStream;
	
	protected Map<String, List<Long>> dateHeaders;

	private String remoteAddr;

	/**
	 * Instantiates a new http request.
	 */
	public HttpRequest() {
		headers = new HashMap<String, List<String>>();
		parameters = new HashMap<String, List<String>>();
		cookies = new HashMap<String, Cookie>();
		attributes = new HashMap<String, Object>();
		dateHeaders = new HashMap<String, List<Long>>();
	}

	/**
	 * Gets the http method.
	 *
	 * @return the method
	 */
	@Override
	public String getMethod() {
		return method;
	}

	/**
	 * Sets the http method.
	 *
	 * @param method
	 *            the new method
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * Gets the http version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the http version.
	 *
	 * @param version
	 *            the new version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Gets the requested path.
	 *
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the path.
	 *
	 * @param path
	 *            the new path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Sets the body.
	 *
	 * @param body
	 *            the new body
	 */
	public void setBody(String body) {
		this.body = body;
		addHeader("Content-Length", String.valueOf(body.length()));
	}

	/**
	 * Adds the header to a case insensitive map of headers.
	 *
	 * @param header
	 *            the header
	 * @param value
	 *            the value
	 */
	public void addHeader(String header, String value) {
		if (headers.containsKey(header)) {
			List<String> headerValues = headers.get(header);
			headerValues.add(value);
		} else {
			setHeader(header, value);
		}
	}

	public void addDateHeader(String header, long date) {
		if (dateHeaders.containsKey(header)) {
			dateHeaders.get(header).add(date);
		} else {
			setDateHeader(header, date);
		}
	}

	public void setDateHeader(String header, long date) {
		List<Long> list = new ArrayList<Long>();
		list.add(date);
		dateHeaders.put(header, list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("");

		sb.append("Method: " + method + "\n");
		sb.append("Path: " + path + "\n");
		sb.append("Version: " + version + "\n");
		sb.append("Headers: " + headers + "\n");
		sb.append("Body :" + body);

		return sb.toString();
	}

	/**
	 * Fill headers.
	 *
	 * @param fullInput
	 *            the full input
	 * @param response
	 *            the response
	 */
	public abstract void fillHeaders(List<String> fullInput,
			HttpResponse response);

	/**
	 * Generate response.
	 *
	 * @param fullInput
	 *            the full input
	 * @param webRoot
	 *            the web root
	 * @param jobQueue
	 *            the job queue
	 * @return the http response
	 */
	public abstract HttpResponse generateResponse(List<String> fullInput,
			String webRoot, Queue<?> jobQueue);

	/**
	 * Handle file access.
	 *
	 * @param response
	 *            the response
	 * @param path
	 *            the path
	 * @param webRoot
	 *            the web root
	 */
	public abstract void handleFileAccess(HttpResponse response, String path,
			String webRoot);

	/**
	 * Handle shutdown.
	 *
	 * @param response
	 *            the response
	 * @param version
	 *            the version
	 */
	public void handleShutdown(HttpResponse response, String version) {
		response.setResponse(200);
		response.setVersion(version);
		String body = "<h1>Server is shutting down</h1>";
		response.setBody(body.getBytes());
		response.addHeader("Content-Type", "text/html");

	}

	/**
	 * Handle control.
	 *
	 * @param response
	 *            the response
	 * @return the http response
	 */
	public HttpResponse handleControl(HttpResponse response) {
		if (response == null) {
			return null;
		}
		response.setVersion(version);
		response.setResponse(200);
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><h1>Server Control Page</h1><br/>\n")
				.append("<h3>Server created by Shreejit Gangadharan, pennid: shreejit</h3><br/>")
				.append("\n<table>\n")
				.append("<thead><th style=\"150px\">ThreadName</th><th>Status/URL</th></thead>");
		for (MainHandler t : ThreadPool.getInstance().getThreadList()) {
			sb.append("<tr><td>").append(t.getName()).append("</td><td>");
			if (t.getState().equals("WAITING")) {
				sb.append(t.getState()).append("</td><td></tr>\n");
			} else {
				sb.append(t.getCurrentUrl()).append("</td></td></tr>\n");
			}
		}
		String logs = HttpServer.consoleWriter.getBuffer().toString();

		sb.append("</table>\n")
				.append("<a href=\"/shutdown\">Shutdown</a><br/>")
				.append("<h2>Logs</h2>").append(logs).append("</body></html>");
		response.setBody(sb.toString().getBytes());
		response.addHeader("Content-Type", "text/html");
		response.addHeader("Content-Length",
				String.valueOf(sb.toString().length()));
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getAuthType()
	 */
	@Override
	public String getAuthType() {
		return BASIC_AUTH;
	}

	/**
	 * Sets the context path from servlet path.
	 *
	 * @param servletPath
	 *            the new context path from servlet path
	 */
	public void setContextPathFromServletPath(String servletPath) {
		String newPath = getPath();
		/*
		 * path may be /../<url matched with servlet pattern>. need to return
		 * ... part
		 */
		String[] sections = getPath().split("/");
		newPath = "/".concat(sections[1]);
		Pattern pattern = Pattern.compile(servletPath.replace("/*", ".*"));
		Matcher matcher = pattern.matcher(newPath);
		logger.debug("Matching path:" + newPath + " with servletPath:"
				+ servletPath);

		if (matcher.matches()) {
			// no context
			newPath = "";
		} else {
			// this is the context
		}
		contextPath = newPath;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getContextPath()
	 */
	@Override
	public String getContextPath() {
		return contextPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getCookies()
	 */
	@Override
	public Cookie[] getCookies() {
		if (cookies.size() == 0) {
			return null;
		}
		Cookie[] cookieList = new Cookie[cookies.size()];
		int i = 0;
		for (Entry<String, Cookie> entry : cookies.entrySet()) {
			cookieList[i++] = entry.getValue();
		}
		return cookieList;
	}

	/**
	 * Adds the cookie.
	 *
	 * @param cookie
	 *            the cookie
	 */
	public void addCookie(Cookie cookie) {
		cookies.put(cookie.getName(), cookie);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
	 */
	@Override
	public long getDateHeader(String header) throws IllegalArgumentException {
		if (dateHeaders.containsKey(header)) {
			return (dateHeaders.get(header).get(0));
		}
		return -1;
	}

	/**
	 * Sets the header.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setHeader(String key, String value) {
		List<String> hv = new ArrayList<String>();
		hv.add(value);
		headers.put(key, hv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
	 */
	@Override
	public String getHeader(String arg0) {
		String header = null;
		if (headers.containsKey(arg0)) {
			header = headers.get(arg0).get(0);
		}
		return header;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
	 */
	@Override
	public Enumeration<String> getHeaderNames() {
		Enumeration<String> headerNames = null;
		if (!headers.keySet().isEmpty()) {
			headerNames = Collections.enumeration(headers.keySet());
		}
		return headerNames;
	}
	
	public Enumeration<String> getDateHeaderNames() {
		Enumeration<String> headerNames = null;
		if (!dateHeaders.keySet().isEmpty()) {
			headerNames = Collections.enumeration(dateHeaders.keySet());
		}
		return headerNames;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
	 */
	@Override
	public Enumeration<String> getHeaders(String arg0) {
		Enumeration<String> headerValues = null;
		if (headers.containsKey(arg0)) {
			headerValues = Collections.enumeration(headers.get(arg0));
		}
		return headerValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
	 */
	@Override
	public int getIntHeader(String arg0) throws NumberFormatException {
		String headerValue = null;
		int intValue = -1;
		if (headers.containsKey(arg0)) {
			headerValue = headers.get(arg0).get(0);
			try {
				intValue = Integer.parseInt(headerValue);
			} catch (NumberFormatException e) {
				logger.error("Int header not in correct format"
						+ e.getMessage());
				throw new NumberFormatException(
						"Int header isn't in the correct format!");
			}
		}
		return intValue;
	}

	/**
	 * Sets the path info from servlet path.
	 *
	 * @param servletPath
	 *            the new path info from servlet path
	 */
	public void setPathInfoFromServletPath(String servletPath) {
		/*
		 * servlet path may be in the form of /url/* or /url. In the first case
		 * remove the ending and check against /url and /url/* to avoid mapping
		 * to file names. Else there shouldn't be any path info as its an exact
		 * match
		 */

		String newPath = getPath();
		if (newPath.contains("?")) {
			newPath = newPath.split("\\?")[0];
		}
		if (newPath.endsWith("/")) {
			newPath = newPath.substring(0, newPath.length() - 1);
		}
		if (servletPath.contains("/*")) {
			servletPath = servletPath.replace("/*", "");
		} else if (servletPath.endsWith("/")) {
			servletPath = servletPath.substring(0, servletPath.length() - 1);
		}

		if (newPath.equals(servletPath)) {
			newPath = null;
		} else {
			newPath = newPath.replace(servletPath, "");
		}
		logger.debug("New path is now:" + newPath
				+ " after replacing servletPath:" + servletPath);
		pathInfo = newPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getPathInfo()
	 */
	@Override
	public String getPathInfo() {
		return pathInfo;
	}

	/**
	 * Sets the path translated.
	 */
	public void setPathTranslated() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
	 */
	@Override
	public String getPathTranslated() {
		// Don't need to do
		return null;
	}

	/**
	 * Sets the query string.
	 */
	public void setQueryString() {
		String newPath = getPath();
		logger.debug("Set queryString to " + newPath + " is query?"
				+ newPath.contains("?"));
		if (newPath.contains("?") && newPath.split("\\?").length > 1) {
			newPath = newPath.split("\\?")[1];
		} else {
			newPath = null;
		}
		queryString = newPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getQueryString()
	 */
	@Override
	public String getQueryString() {
		return queryString;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
	 */
	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sets the request uri.
	 */
	public void setRequestURI() {
		requestURI = getPath().split("\\?")[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRequestURI()
	 */
	@Override
	public String getRequestURI() {
		return requestURI;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRequestURL()
	 */
	@Override
	public StringBuffer getRequestURL() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(protocol).append("://").append(serverName).append(":")
				.append(serverPort).append(requestURI);
		return buffer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
	 */
	@Override
	public String getRequestedSessionId() {
		if (session != null && session.isValid()) {
			return session.getId();
		} else {
			return null;
		}
	}

	/**
	 * Sets the servlet path with servlet path.
	 *
	 * @param servletPath
	 *            the new servlet path with servlet path
	 */
	public void setServletPathWithServletPath(String servletPath) {
		if (servletPath.contains("/*")) {
			this.servletPath = "";
		} else {
			this.servletPath = servletPath;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getServletPath()
	 */
	@Override
	public String getServletPath() {
		return servletPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getSession()
	 */
	@Override
	public HttpSession getSession() {
		return getSession(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
	 */
	@Override
	public HttpSession getSession(boolean arg0) {
		// find if within the cookies you have a sessionId cookie
		Calendar cal = Calendar.getInstance();

		if (cookies.get("sessionId") != null) {
			session = SessionMap.getInstance().getSession(
					cookies.get("sessionId").getValue());
			if (session != null) {
				session.setIsNew(false);
				logger.info("Checking with last accessed time:"
						+ session.getLastAccessedTime()
						+ " now time:"
						+ cal.getTimeInMillis()
						+ " and diff:"
						+ (cal.getTimeInMillis() - session
								.getLastAccessedTime()) / 1000
						+ " against max inactive interval:"
						+ session.getMaxInactiveInterval());
				if (session.getMaxInactiveInterval() < 0) {
					session.setLastAccessedTime(cal.getTimeInMillis());
				} else if ((cal.getTimeInMillis() - session
						.getLastAccessedTime()) > (session
						.getMaxInactiveInterval() * 1000)) {
					SessionMap.getInstance().removeSession(session);
					session = null;
				} else {
					session.setLastAccessedTime(cal.getTimeInMillis());
				}
			}
			logger.debug("Found sessionId header:" + cookies.get("sessionId")
					+ " and found session:" + session);
		}
		if (session == null && arg0) {
			// creating a new session
			session = new HttpSessionImpl();
			SessionMap.getInstance().addSession(session);
			Cookie cookie = new Cookie("sessionId", session.getId());
			logger.debug("Created new cookie with session id:" + session.getId());
			addCookie(cookie);
		}

		logger.info("Request cookies now:" + cookies);
		return session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	@Override
	public Principal getUserPrincipal() {
		// Don't need to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
	 */
	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
	 */
	@Override
	public boolean isRequestedSessionIdFromURL() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
	 */
	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
	 */
	@Override
	public boolean isRequestedSessionIdValid() {
		Calendar cal = Calendar.getInstance();
		if (cookies.get("sessionId") != null) {
			session = SessionMap.getInstance().getSession(
					cookies.get("sessionId").getValue());
			if (session != null) {
				logger.info("Checking with last accessed time:"
						+ session.getLastAccessedTime()
						+ " now time:"
						+ cal.getTimeInMillis()
						+ " and diff:"
						+ (cal.getTimeInMillis() - session
								.getLastAccessedTime()) / 1000
						+ " against max inactive interval:"
						+ session.getMaxInactiveInterval());
				if (session.getMaxInactiveInterval() < 0) {
					return true;
				} else if ((cal.getTimeInMillis() - session
						.getLastAccessedTime()) > (session
						.getMaxInactiveInterval() * 1000)) {
					return false;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
	 */
	@Override
	public boolean isUserInRole(String arg0) {
		// Don't need to do
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String arg0) {
		return attributes.get(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getAttributeNames()
	 */
	@Override
	public Enumeration getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getCharacterEncoding()
	 */
	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getContentLength()
	 */
	@Override
	public int getContentLength() {
		return contentLength;
	}

	/**
	 * Sets the content type.
	 *
	 * @param contentType
	 *            the new content type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getContentType()
	 */
	@Override
	public String getContentType() {
		return contentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getInputStream()
	 */
	@Override
	public ServletInputStream getInputStream() throws IOException {
		// Don't need to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocalAddr()
	 */
	@Override
	public String getLocalAddr() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocalName()
	 */
	@Override
	public String getLocalName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sets the local port.
	 *
	 * @param localPort
	 *            the new local port
	 */
	public void setLocalPort(int localPort) {
		this.localPort = localPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocalPort()
	 */
	@Override
	public int getLocalPort() {
		return localPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocale()
	 */
	@Override
	public Locale getLocale() {
		return locale;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocales()
	 */
	@Override
	public Enumeration getLocales() {
		// Don't need to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(String key) {
		if (parameters.containsKey(key)) {
			return parameters.get(key).get(0);
		} else {
			return null;
		}
	}

	/**
	 * Sets the paramater.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void setParamater(String key, String value) {
		List<String> hv = new ArrayList<String>();
		hv.add(value);
		parameters.put(key, hv);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterMap()
	 */
	@Override
	public Map getParameterMap() {
		return parameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterNames()
	 */
	@Override
	public Enumeration getParameterNames() {
		return Collections.enumeration(parameters.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
	 */
	@Override
	public String[] getParameterValues(String key) {
		if (parameters.containsKey(key)) {
			String[] paramArray = new String[parameters.get(key).size()];
			return parameters.get(key).toArray(paramArray);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getProtocol()
	 */
	@Override
	public String getProtocol() {
		return protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getReader()
	 */
	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new StringReader(getBody()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
	 */
	@Override
	public String getRealPath(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRemoteAddr()
	 */
	@Override
	public String getRemoteAddr() {
		return remoteAddr;
	}

	/**
	 * Sets the remote host.
	 *
	 * @param remoteHost
	 *            the new remote host
	 */
	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRemoteHost()
	 */
	@Override
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * Sets the remote port.
	 *
	 * @param remotePort
	 *            the new remote port
	 */
	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRemotePort()
	 */
	@Override
	public int getRemotePort() {
		return remotePort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
	 */
	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// Don't need to do
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getScheme()
	 */
	@Override
	public String getScheme() {
		return "http";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getServerName()
	 */
	@Override
	public String getServerName() {
		return serverName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getServerPort()
	 */
	@Override
	public int getServerPort() {
		return serverPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#isSecure()
	 */
	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String arg0) {
		attributes.remove(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public void setAttribute(String arg0, Object arg1) {
		attributes.put(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
	 */
	@Override
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		this.characterEncoding = arg0;

	}

	/**
	 * Sets the server port.
	 *
	 * @param serverPort
	 *            the new server port
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * Sets the server name.
	 *
	 * @param serverName
	 *            the new server name
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * Sets the protocol.
	 *
	 * @param protocol
	 *            the new protocol
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * Sets the socket input stream.
	 *
	 * @param in
	 *            the new socket input stream
	 */
	public void setSocketInputStream(InputStream in) {
		this.socketInputStream = in;
	}

	/**
	 * Gets the socket input stream.
	 *
	 * @return the socket input stream
	 */
	public InputStream getSocketInputStream() {
		return socketInputStream;
	}

	/**
	 * Gets the marshalled headers.
	 *
	 * @return the marshalled headers
	 */
	public String getMarshalledHeaders() {
		StringBuilder marshalledRequest = new StringBuilder("");
		boolean isQueryInUrl = (getMethod().equals("GET") || getMethod().equals("HEAD")) && getQueryString() != null;
		String requestPath = getPath() + (isQueryInUrl ? ("?" + getQueryString()) : "");
		marshalledRequest.append(getMethod() + " " + requestPath + " "
				+ getVersion() + "\n");
		for (Entry<String, List<String>> header : headers.entrySet()) {
			for (String headerValue : header.getValue()) {
				
					marshalledRequest.append(header.getKey() + ": "
							+ headerValue + "\n");
			}
		}
		
		for (Entry<String, List<Long>> header : dateHeaders.entrySet()) {
			for (Long headerValue : header.getValue()) {
				Date date = new Date(headerValue);
				marshalledRequest.append(header.getKey() + ": "
						+ Parser.formatDate(date) + "\n");
			}
		}
		
		
		if (getCookies() != null) {
			for (Cookie cookie : getCookies()) {
				marshalledRequest.append("Set-Cookie: ")
						.append(cookie.getName()).append("=")
						.append(cookie.getValue());
				if (cookie.getDomain() != null) {
					marshalledRequest.append("; Domain=").append(
							cookie.getDomain());
				}
				if (cookie.getPath() != null) {
					marshalledRequest.append("; Path=")
							.append(cookie.getPath());
				}
				if (cookie.getSecure()) {
					marshalledRequest.append("; Secure; ");
				}
				if (cookie.getMaxAge() != -1) {
					marshalledRequest.append("; Max-Age=").append(
							String.valueOf(cookie.getMaxAge()));
				}
				marshalledRequest.append("\n");
			}
		}
		marshalledRequest.append("\n");
		return marshalledRequest.toString()
				.replaceAll("\n", Parser.LINE_DELIMS);
	}
}
