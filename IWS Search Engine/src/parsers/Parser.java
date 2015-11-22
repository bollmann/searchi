/*
 * Written by Shreejit Gangadharan
 */
package parsers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.w3c.tidy.Tidy;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import responses.Response;
import servlets.ServletConfigImpl;
import servlets.ServletContextImpl;
import threadpool.MercatorNode;
import dao.URLContent;

// TODO: Auto-generated Javadoc
/**
 * The Class Parser.
 */
public class Parser {


	/** The logger. */
	private static Logger logger = Logger.getLogger(Parser.class);

	/** The line delims. */
	public static String LINE_DELIMS = (Character.toString((char) 13) + Character
			.toString((char) 10));

	/** The header separator. */
	public static String HEADER_SEPARATOR = ":[\t ]+";

	/** The methods. */
	public static String[] methods = { "GET", "HEAD", "POST" };

	/** The versions. */
	public static String[] versions = { "HTTP/1.1", "HTTP/1.0", "HTTP/1.2" };

	/** The input date formats. */
	public static String[] inputDateFormats = { "EEE, dd MMM yyyy HH:mm:ss z",
			"EEEEEE, dd-MMM-yy HH:mm:ss z", "EEE MMM dd HH:mm:ss yyyy", };

	/** The date format. */
	public static String dateFormat = "EEE, dd MMM yyyy HH:mm:ss z";
	
	public static String tDateFormat = "YYYY-MM-DD'T'hh:mm:ss";

	/** The absolute url regex. */
	public static String absoluteUrlRegex = ".*(http|https)://.*";
	
	public static String[] allowedCrawlCotentTypes = {"text/html.*", "text/xml.*", "application/xml.*", ".*\\+xml.*"};
	
	public static Integer maxFileSize = 10; // file size in mb. 10 by default
	
	public static String formEncoding = "application/x-www-form-urlencoded";
	/**
	 * The Class Handler.
	 */
	public static class Handler extends DefaultHandler {

		/** The m_state. */
		private int m_state = 0;

		/** The m_servlet name. */
		private String m_servletName;

		/** The m_param name. */
		private String m_paramName;

		/** The m_url_patterns. */
		private HashMap<String, String> m_url_patterns;

		/** The m_servlets. */
		private HashMap<String, String> m_servlets = new HashMap<String, String>();

		/** The m_context params. */
		Map<String, String> m_contextParams = new HashMap<String, String>();

		/** The m_servlet params. */
		private HashMap<String, HashMap<String, String>> m_servletParams = new HashMap<String, HashMap<String, String>>();

		/** The m_servlet_context_name. */
		private String m_servlet_context_name;

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) {
			// logger.info("mstate " + m_state + " qname " + qName);
			if (qName.compareTo("servlet-name") == 0) {
				m_state = (m_state == 0) ? 1 : 6;
			} else if (qName.compareTo("servlet-class") == 0) {
				m_state = 2;
			} else if (qName.compareTo("context-param") == 0) {
				m_state = 3;
			} else if (qName.compareTo("init-param") == 0) {
				m_state = 4;
			} else if (qName.compareTo("param-name") == 0) {
				m_state = (m_state == 3) ? 10 : 20;
			} else if (qName.compareTo("param-value") == 0) {
				m_state = (m_state == 10) ? 11 : 21;
			} else if (qName.compareTo("servlet-mapping") == 0) {
				m_state = 5;
			} else if (qName.compareTo("url-pattern") == 0) {
				m_state = (m_state == 6) ? 12 : 22;
			} else if (qName.compareTo("display-name") == 0) {
				m_state = 7;
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
		 */
		public void characters(char[] ch, int start, int length) {
			String value = new String(ch, start, length);
			if (m_state == 1) {
				m_servletName = value;
				m_state = 0;
			} else if (m_state == 6) {
				m_servletName = value;
			} else if (m_state == 2) {
				getM_servlets().put(m_servletName, value);
				m_state = 0;
			} else if (m_state == 10 || m_state == 20) {
				m_paramName = value;
			} else if (m_state == 11) {
				if (m_paramName == null) {
					System.err.println("Context parameter value '" + value
							+ "' without name");
					System.exit(-1);
				}
				m_contextParams.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 21) {
				if (m_paramName == null) {
					System.err.println("Servlet parameter value '" + value
							+ "' without name");
					System.exit(-1);
				}
				HashMap<String, String> p = getM_servletParams().get(
						m_servletName);
				if (p == null) {
					p = new HashMap<String, String>();
					getM_servletParams().put(m_servletName, p);
				}
				p.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 12) {
				// System.out.println("Got url pattern " + value);
				if (m_servletName == null) {
					System.err.println("Servlet url pattern '" + value
							+ "' without name");
					System.exit(-1);
				}
				if (m_url_patterns == null) {
					m_url_patterns = new HashMap<String, String>();
				}
				// System.out.println("Now adding " + value + " to map " +
				// m_url_patterns);
				m_url_patterns.put(m_servletName, value);
				m_state = 0;
			} else if (m_state == 7) {
				setM_servlet_context_name(value);
			}
		}

		/**
		 * Gets the m_servlets.
		 *
		 * @return the m_servlets
		 */
		public HashMap<String, String> getM_servlets() {
			return m_servlets;
		}

		/**
		 * Sets the m_servlets.
		 *
		 * @param m_servlets
		 *            the m_servlets
		 */
		public void setM_servlets(HashMap<String, String> m_servlets) {
			this.m_servlets = m_servlets;
		}

		/**
		 * Gets the m_servlet params.
		 *
		 * @return the m_servlet params
		 */
		public HashMap<String, HashMap<String, String>> getM_servletParams() {
			return m_servletParams;
		}

		/**
		 * Sets the m_servlet params.
		 *
		 * @param m_servletParams
		 *            the m_servlet params
		 */
		public void setM_servletParams(
				HashMap<String, HashMap<String, String>> m_servletParams) {
			this.m_servletParams = m_servletParams;
		}

		/**
		 * Gets the m_state.
		 *
		 * @return the m_state
		 */
		public int getM_state() {
			return m_state;
		}

		/**
		 * Sets the m_state.
		 *
		 * @param m_state
		 *            the new m_state
		 */
		public void setM_state(int m_state) {
			this.m_state = m_state;
		}

		/**
		 * Gets the m_servlet name.
		 *
		 * @return the m_servlet name
		 */
		public String getM_servletName() {
			return m_servletName;
		}

		/**
		 * Sets the m_servlet name.
		 *
		 * @param m_servletName
		 *            the new m_servlet name
		 */
		public void setM_servletName(String m_servletName) {
			this.m_servletName = m_servletName;
		}

		/**
		 * Gets the m_param name.
		 *
		 * @return the m_param name
		 */
		public String getM_paramName() {
			return m_paramName;
		}

		/**
		 * Sets the m_param name.
		 *
		 * @param m_paramName
		 *            the new m_param name
		 */
		public void setM_paramName(String m_paramName) {
			this.m_paramName = m_paramName;
		}

		/**
		 * Gets the m_url_patterns.
		 *
		 * @return the m_url_patterns
		 */
		public HashMap<String, String> getM_url_patterns() {
			return m_url_patterns;
		}

		/**
		 * Sets the m_url_patterns.
		 *
		 * @param m_url_patterns
		 *            the m_url_patterns
		 */
		public void setM_url_patterns(HashMap<String, String> m_url_patterns) {
			this.m_url_patterns = m_url_patterns;
		}

		/**
		 * Gets the m_context params.
		 *
		 * @return the m_context params
		 */
		public Map<String, String> getM_contextParams() {
			return m_contextParams;
		}

		/**
		 * Sets the m_context params.
		 *
		 * @param m_contextParams
		 *            the m_context params
		 */
		public void setM_contextParams(Map<String, String> m_contextParams) {
			this.m_contextParams = m_contextParams;
		}

		/**
		 * Gets the m_servlet_context_name.
		 *
		 * @return the m_servlet_context_name
		 */
		public String getM_servlet_context_name() {
			return m_servlet_context_name;
		}

		/**
		 * Sets the m_servlet_context_name.
		 *
		 * @param m_servlet_context_name
		 *            the new m_servlet_context_name
		 */
		public void setM_servlet_context_name(String m_servlet_context_name) {
			this.m_servlet_context_name = m_servlet_context_name;
		}

	}

	/**
	 * Creates the context.
	 *
	 * @param h
	 *            the h
	 * @return the servlet context impl
	 */
	public static ServletContextImpl createContext(Handler h) {
		ServletContextImpl fc = new ServletContextImpl();
		for (String param : h.getM_contextParams().keySet()) {
			fc.setInitParameter(param, h.getM_contextParams().get(param));
		}
		return fc;
	}

	/**
	 * Creates the servlets.
	 *
	 * @param h
	 *            the h
	 * @param fc
	 *            the fc
	 * @return the hash map
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws ServletException
	 *             the servlet exception
	 */
	public static HashMap<String, HttpServlet> createServlets(Handler h,
			ServletContextImpl fc) throws IOException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, ServletException {
		HashMap<String, HttpServlet> servlets = new HashMap<String, HttpServlet>();
		for (String servletName : h.getM_servlets().keySet()) {
			ServletConfigImpl config = new ServletConfigImpl(servletName, fc);
			String className = h.getM_servlets().get(servletName);
			String urlPattern = h.getM_url_patterns().get(servletName);
			Class servletClass = Class.forName(className);
			HttpServlet servlet = (HttpServlet) servletClass.newInstance();
			HashMap<String, String> servletParams = h.getM_servletParams().get(
					servletName);
			if (servletParams != null) {
				for (String param : servletParams.keySet()) {
					config.setInitParam(param, servletParams.get(param));
				}
			}
			servlet.init(config);
			servlets.put(urlPattern, servlet);
		}
		return servlets;
	}

	/**
	 * Parses the webdotxml.
	 *
	 * @param webdotxml
	 *            the webdotxml
	 * @return the handler
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ParserConfigurationException
	 *             the parser configuration exception
	 */
	public static Handler parseWebdotxml(String webdotxml) throws SAXException,
			IOException, ParserConfigurationException {
		Handler h = new Handler();
		File file = new File(webdotxml);
		if (!file.exists()) {
			System.err.println("error: cannot find " + file.getPath());
			System.exit(-1);
		}
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(file, h);

		return h;
	}

	/**
	 * Longest url match.
	 *
	 * @param toMatch
	 *            the to match
	 * @param toMatchIn
	 *            the to match in
	 * @return the string
	 */
	public static String longestUrlMatch(String toMatch, Set<String> toMatchIn) {
		int maxLength = 0;
		String matchedString = null;
		Map<String, String> map = new HashMap<String, String>();
		List<String> newMatchIn = new ArrayList<String>();
		for (String string : toMatchIn) {
			String stringMatcher = string;
			if (stringMatcher.endsWith("/*")) {
				stringMatcher = stringMatcher.substring(0,
						stringMatcher.length() - 2);
				newMatchIn.add(stringMatcher);
				map.put(stringMatcher, string);
				newMatchIn.add(stringMatcher + "/.*");
				map.put(stringMatcher + "/.*", string);
			} else {
				stringMatcher = string.replace("*", ".*");
				newMatchIn.add(stringMatcher);
				map.put(stringMatcher, string);
			}

		}
		logger.debug("Newmatches now" + newMatchIn);

		for (String string : newMatchIn) {
			String stringMatcher = string;
			logger.debug("Trying to match " + toMatch + " against:"
					+ stringMatcher);
			Pattern pattern = Pattern.compile(stringMatcher);
			Matcher matcher = pattern.matcher(toMatch);
			if (matcher.matches() && !string.equals("/")
					&& (string.length() > maxLength)) {
				maxLength = string.length();
				matchedString = string;
			}
		}

		if (matchedString != null) {
			return map.get(matchedString);
		} else {
			return null;
		}
	}


	/**
	 * Checks if is allowed method.
	 *
	 * @param method
	 *            the method
	 * @return true, if is allowed method
	 */
	public static boolean isAllowedMethod(String method) {
		for (String m : methods) {
			if (m.equals(method.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is allowed version.
	 *
	 * @param version
	 *            the version
	 * @return true, if is allowed version
	 */
	public static boolean isAllowedVersion(String version) {
		for (String m : versions) {
			if (m.equals(version.toUpperCase())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Parses the method.
	 *
	 * @param firstLine
	 *            the first line
	 * @return the string
	 * @throws ParseException
	 *             the parse exception
	 */
	public static String parseRequestMethod(String firstLine)
			throws ParseException {
		String method;
		StringTokenizer tokenizer = new StringTokenizer(firstLine);
		List<String> firstLineTokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			firstLineTokens.add(tokenizer.nextToken());
		}
		method = firstLineTokens.get(0);

		if (isAllowedMethod(method)) {
			return method;
		} else {
			throw new ParseException("Wrong method!", 0);
		}
	}

	/**
	 * Parses the version.
	 *
	 * @param firstLine
	 *            the first line
	 * @return the string
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 */
	public static String parseRequestVersion(String firstLine)
			throws IllegalArgumentException {
		String version;
		StringTokenizer tokenizer = new StringTokenizer(firstLine);
		List<String> firstLineTokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			firstLineTokens.add(tokenizer.nextToken());
		}
		version = firstLineTokens.get(2);
		if (isAllowedVersion(version)) {
			return version;
		} else {
			throw new IllegalArgumentException("Wrong version!");
		}
	}

	/**
	 * Checks if is absolute path request.
	 *
	 * @param firstLine
	 *            the first line
	 * @return true, if is absolute path request
	 */
	public static boolean isAbsolutePathRequest(String firstLine) {
		Pattern pattern = Pattern.compile(absoluteUrlRegex);
		Matcher matcher = pattern.matcher(firstLine);
		return matcher.matches();
	}

	/**
	 * Parses the path.
	 *
	 * @param firstLine
	 *            the first line
	 * @return the string
	 */
	public static String parseRequestPath(String firstLine) {
		String path = null;
		StringTokenizer tokenizer = new StringTokenizer(firstLine);
		List<String> firstLineTokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			firstLineTokens.add(tokenizer.nextToken());
		}
		path = firstLineTokens.get(1);
		return path;
	}

	/**
	 * Convert time with time zone.
	 *
	 * @param time
	 *            the time
	 * @return the string
	 */
	public static String convertTimeWithTimeZone(long time) {
		Date date = new Date(time);
		DateFormat formatter = new SimpleDateFormat(Parser.dateFormat);
		formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		String dateFormatted = formatter.format(date);
		return dateFormatted;
	}

	/**
	 * Parses the date.
	 *
	 * @param dateString
	 *            the date string in 3 acceptable HTTP date formats
	 * @return the date
	 * @throws ParseException
	 *             the parse exception
	 */
	public static Date parseDate(String dateString) throws ParseException {
		DateFormat df;
		Date date = null;
		for (String format : inputDateFormats) {
			try {
				df = new SimpleDateFormat(format, Locale.US);
				df.setTimeZone(TimeZone.getTimeZone("GMT"));
				date = df.parse(dateString);
			} catch (ParseException p) {
				logger.error("Date parse failed with string:" + dateString);
				continue;
			}
			if (date != null) {
				return date;
			}
		}
		if (date == null) {
			throw new ParseException("Couldn't parse date", 0);
		}
		return date;
	}

	/**
	 * Format date.
	 *
	 * @param date
	 *            the date
	 * @return the string representation of the date in GMT as per HTTP
	 *         standards
	 */
	public static String formatDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(Parser.dateFormat,
				Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}

	/**
	 * Parser header lines.
	 *
	 * @param fullInput
	 *            the full input
	 * @return the list
	 */
	public static List<String> parserHeaderLines(List<String> fullInput) {
		List<String> parsedHeaderLines = new ArrayList<String>();

		for (int i = 1; i < fullInput.size(); i++) {
			if (fullInput.get(i).length() > 1) {
				if (fullInput.get(i).startsWith(" ")
						|| fullInput.get(i).startsWith("\t")) {
					// is continuation of previous header
					/*
					 * System.out .println("Concatting " +
					 * parsedHeaderLines.get(parsedHeaderLines .size() - 1) +
					 * " with " + fullInput.get(i) + (int) '\n' + " " +
					 * Parser.LINE_DELIMS.charAt(0) + " " +
					 * Parser.LINE_DELIMS.charAt(1));
					 */
					String prevHeader = parsedHeaderLines
							.remove(parsedHeaderLines.size() - 1);
					prevHeader = prevHeader.concat(fullInput.get(i).trim())
							.trim();
					prevHeader = prevHeader.replace(
							Parser.LINE_DELIMS.charAt(0), ' ');
					prevHeader = prevHeader.replace(
							Parser.LINE_DELIMS.charAt(1), ' ');
					parsedHeaderLines.add(prevHeader);
				} else {
					// new header
					parsedHeaderLines.add(fullInput.get(i));
				}

			} else {
				// blank line
			}
		}
		return parsedHeaderLines;
	}

	/**
	 * Parses the header.
	 *
	 * @param fullInput
	 *            the full input
	 * @param headerName
	 *            the header name
	 * @return the string
	 */
	public static String parseHeader(List<String> fullInput, String headerName) {
		String result = null;
		for (String input : fullInput) {
			if (input.startsWith(headerName)) {
				result = input.split(Parser.HEADER_SEPARATOR)[1];
			}
		}
		return result;
	}

	/**
	 * Removes the spaces outside quotes.
	 *
	 * @param string
	 *            the string
	 * @return the string
	 */
	public static String removeSpacesOutsideQuotes(String string) {
		StringBuilder sb = new StringBuilder();
		boolean skipSpaces = true;
		Pattern pattern = Pattern.compile("\\s+");

		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			Matcher matcher = pattern.matcher(String.valueOf(ch));
			if (matcher.matches()) {
				if (skipSpaces) {
					continue;
				}
			} else if (ch == '"') {
				if (skipSpaces == true)
					skipSpaces = false;
				else
					skipSpaces = true;
			}
			sb.append(string.charAt(i));
		}

		return sb.toString();
	}

	/**
	 * Parses the response code.
	 *
	 * @param firstLine
	 *            the first line
	 * @return the response
	 * @throws ParseException
	 *             the parse exception
	 */
	public static Response parseResponseCode(String firstLine)
			throws ParseException {
		String code, codeString;
		StringTokenizer tokenizer = new StringTokenizer(firstLine);
		List<String> firstLineTokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			firstLineTokens.add(tokenizer.nextToken());
		}
		logger.info("First line tokens:" + firstLineTokens);
		code = firstLineTokens.get(1);
		codeString = firstLineTokens.get(2);
		int codeInt;
		try {
			codeInt = Integer.parseInt(code);
		} catch (NumberFormatException e) {
			throw new ParseException("", 1);
		}

		Response response = new Response();
		response.setResponseCode(codeInt);
		response.setResponseString(codeString);

		return response;

	}

	/**
	 * Parses the response version.
	 *
	 * @param firstLine
	 *            the first line
	 * @return the string
	 * @throws IllegalArgumentException
	 *             the illegal argument exception
	 */
	public static String parseResponseVersion(String firstLine)
			throws IllegalArgumentException {
		String version;
		StringTokenizer tokenizer = new StringTokenizer(firstLine);
		List<String> firstLineTokens = new ArrayList<String>();
		while (tokenizer.hasMoreTokens()) {
			firstLineTokens.add(tokenizer.nextToken());
		}
		version = firstLineTokens.get(0);
		if (isAllowedVersion(version)) {
			return version;
		} else {
			throw new IllegalArgumentException("Wrong version!");
		}
	}

	/**
	 * Checks if is valid x path node name.
	 *
	 * @param name
	 *            the name
	 * @return true, if is valid x path node name
	 */
	public static boolean isValidXPathNodeName(String name) {
		boolean result = true;
		if (name.length() > 1) {
			if (name.contains("::")) {
				return false;
			}
			Pattern p1 = Pattern.compile("[-.0-9]");
			Matcher m = p1.matcher(name.subSequence(0, 1));
			if (m.matches()) {
				return false;
			}
		}
		return result;
	}

	/**
	 * Clean html content.
	 *
	 * @param data
	 *            the data
	 * @return the string
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public static String cleanHtmlContent(String data)
			throws UnsupportedEncodingException {
		Tidy tidy = new Tidy();
		tidy.setInputEncoding("UTF-8");
		tidy.setOutputEncoding("UTF-8");
		tidy.setWraplen(Integer.MAX_VALUE);
		tidy.setPrintBodyOnly(false);
		tidy.setXmlOut(true);
		tidy.setSmartIndent(true);
//		tidy.setForceOutput(true);
		tidy.setQuiet(true);
		tidy.setShowErrors(0);
		tidy.setErrout(null);
		tidy.setOnlyErrors(true);
		ByteArrayInputStream in = new ByteArrayInputStream(
				data.getBytes("UTF-8"));
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		tidy.parseDOM(in, out);
		return out.toString("UTF-8");
	}

	/**
	 * Parser the robots.txt content and creates a MercatorNode with the data.
	 * It searches for User-agent: * or User-agent: cis455-crawler and gives
	 * priority to the directives for cis455-crawler
	 * 
	 * @param domain
	 *            the domain
	 * @param content
	 *            the content of the robots.txt
	 * @return
	 */
	public static MercatorNode parseRobotsContent(String domain, String content) {
		boolean isSpecific = false;

		// first search for User-agent: cis455-crawler. If not present, then use
		// User-agent: *
		Pattern p = Pattern.compile("(?s).*User-agent:[\t ]+cis455crawler(?s).*");
		Matcher m = p.matcher(content);

		if (m.matches()) {
			isSpecific = true;
			logger.info("This robots.txt has specific info for cis455-crawler");
		}

		MercatorNode node = new MercatorNode(domain);
		boolean gatherInfo = false;
		for (String line : content.split("\n")) {
			logger.debug("Robots.txt processing:" + line + " isNewLine?:" + line.equals("") + " gatherInfo:" + gatherInfo);
			if (line.equals("")) { // newline
				if(gatherInfo) {
					gatherInfo = false; // end of section
				}
				continue;
			}
			if(line.startsWith("#") || line.split(Parser.HEADER_SEPARATOR).length < 2) {
				continue; // comment
			}
			String key = line.split(Parser.HEADER_SEPARATOR)[0].trim();
			String value = line.split(Parser.HEADER_SEPARATOR)[1].trim();

			if (isSpecific) {
				if (key.equals("User-agent") && value.equals("cis455crawler")) {
					gatherInfo = true;
					logger.debug("Gathering info at:" + line + " because isSpecific:" + isSpecific);
				}
			} else {
				if (key.equals("User-agent") && value.equals("*")) {
					gatherInfo = true;
					logger.debug("Gathering info at:" + line + " because isSpecific:" + isSpecific);
				}
			}

			if (gatherInfo) {
				if (key.equals("Disallow")) {
					node.addDisallowPath(value);
				} else if (key.equals("Allow")) {
					node.addAllowPath(value);
				} else if(key.equals("Crawl-delay")) {
					node.setCrawlDelay(Float.parseFloat(value));
				}
			}
		}
		return node;
	}
	
	public static boolean isAllowedCrawlContentType(String contentType) {
		for(String allowedContentType : allowedCrawlCotentTypes) {
			Pattern p = Pattern.compile(allowedContentType);
			Matcher m = p.matcher(contentType);
			if(m.matches()) {
				return true;
			}
		}
		return false;
	}

	public static void setMaxFileSize(Integer newMaxFileSize) {
		maxFileSize = newMaxFileSize;
		
	}
	
	/**
	 * Encode html.
	 *
	 * @param s
	 *            the s
	 * @return the string
	 */
	public static String encodeHTML(String s) {
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 127 || c == '"' || c == '<' || c == '>') {
				out.append("&#" + (int) c + ";");
			} else {
				out.append(c);
			}
		}
		return out.toString();
	}
	
	public static String formatTDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(Parser.tDateFormat,
				Locale.US);
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		return dateFormat.format(date);
	}
	
	public static String formatURLContent(URLContent content) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div>");
		sb.append("<label>Crawled On:" + formatTDate(content.getCrawledOn()) + "</label><br/>");
		sb.append("<label>Location:" + content.getUrl() + "</label><br/>");
		sb.append("<div>" + encodeHTML(content.getContent()) + "</div>");
		sb.append("</div>");
		return sb.toString();
	}
	
	public static Integer convertByesToMBytes(Integer byteLength) {
		return byteLength/(1024 * 1024);
	}
}
