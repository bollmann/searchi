/*
 * Written by Shreejit Gangadharan
 */
package crawler.requests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import utils.file.FileUtils;

import crawler.parsers.Parser;
import crawler.responses.Http11Response;
import crawler.responses.HttpResponse;
import crawler.threadpool.Queue;

// TODO: Auto-generated Javadoc
/**
 * The Class Http11Request.
 */
public class Http11Request extends HttpRequest implements HttpServletRequest {

	/** The logger. */
	private static Logger logger = Logger.getLogger(Http11Request.class);

	/**
	 * Instantiates a new http11 request.
	 */
	public Http11Request() {
		super();
		setVersion("HTTP/1.1");
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis.cis455.requests.HttpRequest#fillHeaders(java.util.List, edu.upenn.cis.cis455.responses.HttpResponse)
	 */
	public void fillHeaders(List<String> fullInput, HttpResponse response) {
		String firstLine = fullInput.get(0);
		String method = null;
		try {
			method = Parser.parseRequestMethod(firstLine);
		} catch (ParseException e) {
			response.setResponse(501);
			response.setDefaultBody();
			throw new IllegalArgumentException();
		}

		response.setMethod(method);

		String path = null;
		try {
			path = Parser.parseRequestPath(firstLine);
			logger.debug("Looking at " + firstLine + " is absolute? "
					+ Parser.isAbsolutePathRequest(firstLine));
			if (Parser.isAbsolutePathRequest(firstLine)) {

				String newPath = path
						.replaceFirst(Parser.absoluteUrlRegex, "/");
				logger.debug("Absolute path changed to " + newPath);
				path = newPath;
			}
		} catch (Exception e) {
			response.setResponse(400);
			response.setDefaultBody();
			throw new IllegalArgumentException();
		}

		setMethod(method);
		setPath(path);

		List<String> parsedHeaderLines = Parser.parserHeaderLines(fullInput);

		logger.debug(parsedHeaderLines);
		try {
			for (int i = 0; i < parsedHeaderLines.size(); i++) {
				String headerLine = parsedHeaderLines.get(i);
				String header = headerLine.split(Parser.HEADER_SEPARATOR)[0]
						.trim();
				String value = headerLine.split(Parser.HEADER_SEPARATOR)[1]
						.trim();
				// adding support for cookies
				if (header.equals("Cookie")) {
					for (String ckv : value.split("; ")) {
						String cookieName = ckv.split("=")[0];
						String cookieValue = ckv.split("=")[1];
						addCookie(new Cookie(cookieName, cookieValue));
					}
				} else if (header.equals("BODY-CONTENT")) {
					setBody(value);
				} else {
					addHeader(header, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setMethod(method);
			response.setResponse(400);
			String responseString = "<html><body>Headers aren't in the correct format!</body></html>";
			response.setBody(responseString.getBytes(StandardCharsets.UTF_8));
			response.addHeader("Content-Type", "text/html");
			response.addHeader("Connection", "Close");
			throw new IllegalArgumentException();
		}

		if (getHeaderNames() == null) {
			response.setResponse(400);
			String body = "<html><body>Client didn't send a host header!</body></html>";
			response.setBody(body.getBytes());
			response.addHeader("Content-Type", "text/html");
			throw new IllegalArgumentException();
		} else {
			Set<String> headerNames = new HashSet<String>(
					Collections.list(getHeaderNames()));
			if (!headerNames.contains("Host")) {
				response.setResponse(400);
				String body = "<html><body>Client didn't send a host header!</body></html>";
				response.setBody(body.getBytes());
				response.addHeader("Content-Type", "text/html");
				throw new IllegalArgumentException();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.upenn.cis.cis455.requests.HttpRequest#generateResponse(java.util.
	 * List, java.lang.String, edu.upenn.cis.cis455.threadpool.Queue)
	 */
	public Http11Response generateResponse(List<String> fullInput,
			String webRoot, Queue<?> jobQueue) {
		Http11Response response = new Http11Response();

		try {
			fillHeaders(fullInput, response);
			if (path.equals("/shutdown")) {
				handleShutdown(response, version);
				return response;
			}
			
			Pattern pattern = Pattern.compile("/control.*");
			Matcher matcher = pattern.matcher(path);
			if (matcher.matches()) {
				handleControl(response);
				return response;
			}
			response.setResponse(200);
			handleFileAccess(response, path, webRoot);
			logger.debug("Sending response " + new String(response.toBytes()));
		} catch (IllegalArgumentException e) {

		}

		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.upenn.cis.cis455.requests.HttpRequest#handleFileAccess(edu.upenn.
	 * cis.cis455.responses.HttpResponse, java.lang.String, java.lang.String)
	 */
	@Override
	public void handleFileAccess(HttpResponse response, String path,
			String webRoot) {
		// get resource
		Path webRootPath = Paths.get(webRoot);
		Path filePath = Paths.get(webRoot + "/" + path);

		logger.debug("Trying to access " + filePath.toString());

		if (!FileUtils.exists(filePath)) {
			logger.error("File doesn't exist!");
			response.setResponse(404);
			response.setDefaultBody();
			return;
		}

		if (!FileUtils.isReadable(filePath)) {
			logger.error("File isn't readable!");
			response.setResponse(401);
			response.setDefaultBody();
			return;
		}

		if (!FileUtils.isAccessible(filePath, webRootPath)) {
			logger.error("Trying to access something above webroot!");
			response.setResponse(403);
			response.setDefaultBody();
			return;
		}

		File file = new File(filePath.toString());
		long lastModified = file.lastModified();

		logger.debug("Last modified " + lastModified);
		Date lastModifiedDate = new Date(lastModified);
		response.addHeader("Last-Modified", Parser.formatDate(lastModifiedDate));

		String ifModifiedSinceHeader = "If-Modified-Since";
		String ifUnmodifiedSinceHeader = "If-Unmodified-Since";

		if (headers.containsKey(ifModifiedSinceHeader)) {
			String ifModifiedSince = getHeader(ifModifiedSinceHeader);
			Date date = null;
			try {
				// date = df.parse(ifModifiedSince);
				date = Parser.parseDate(ifModifiedSince);
			} catch (ParseException e) {
				logger.info("Wrong date format for " + ifModifiedSince);
				e.printStackTrace();
				response.setResponse(400);
				String responseString = "<html><body><h2>Wrong date format for "
						+ "If-Modified-Since</h2></body></html>";
				response.addHeader("Content-Type", "text/html");
				response.setBody(responseString.getBytes());
				return;
			}
			if (date.compareTo(new Date(lastModified)) < 0) {
				// given date is less than last modified. so send
			} else {
				response.setResponse(304);
				response.setDefaultBody();
				return;
			}
		} else if (headers.containsKey(ifUnmodifiedSinceHeader)) {

			String ifUnmodifiedSince = getHeader(ifUnmodifiedSinceHeader);
			Date date = null;
			try {
				date = Parser.parseDate(ifUnmodifiedSince);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				logger.info("Wrong date format for " + ifUnmodifiedSince);
				e.printStackTrace();
				response.setResponse(400);
				String responseString = "<html><body><h2>Wrong date format for "
						+ "If-Modified-Since</h2></body></html>";
				response.setBody(responseString.getBytes());
				response.addHeader("Content-Type", "text/html");
				return;
			}
			if (date != null) {
				if (date.compareTo(new Date(lastModified)) < 0) {
					// given date is less than last modified. so don't send
					response.setResponse(412);
					response.setDefaultBody();
					return;
				} else {
				}
			}
		}

		try {

			if (!FileUtils.isDirectory(filePath)) {
				// simple file
				logger.info("Reading file");
				FileInputStream fis = new FileInputStream(file);
				String contentType = Files.probeContentType(filePath);
				response.addHeader("Content-Type", contentType);
				byte[] fileContent = new byte[(int) file.length()];
				int j = 0;

				int c;
				while ((c = fis.read()) != -1) {

					fileContent[j] = (byte) c;
					j++;
				}
				fis.close();
				response.setBody(fileContent);
			} else {
				// read directory
				logger.info("Listing directory");
				StringBuilder sb = new StringBuilder();
				sb.append("<html><head><style>li {height:3px}</style></head>\n<body>\n");
				sb.append("The contents of the directory are as follows:<br/>");
				sb.append("<ul>");
				for (File fileEntry : file.listFiles()) {
					sb.append("<li><a href="
							+ fileEntry.toString().replace(webRoot, "") + ">"
							+ fileEntry.toString().replace(webRoot, "")
							+ "</a></li><br/>\n");
				}
				sb.append("</ul>\n");
				sb.append("</body>\n</html>");
				byte[] dirListing = sb.toString().getBytes(
						Charset.forName("UTF-8"));
				response.setBody(dirListing);
				response.setResponse(200);
				response.addHeader("Content-Type", "text/html");
				return;
			}

		} catch (IOException e) {
			response.setResponse(500);
			response.setDefaultBody();
			return;
		}
		return;
	}

}
