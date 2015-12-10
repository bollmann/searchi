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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import utils.file.FilePolicy;

import crawler.parsers.Parser;
import crawler.responses.Http10Response;
import crawler.responses.HttpResponse;
import crawler.threadpool.Queue;

// TODO: Auto-generated Javadoc
/**
 * The Class Http10Request.
 */
public class Http10Request extends HttpRequest implements HttpServletRequest {
	/**
	 * Instantiates a new http10 request.
	 */
	public Http10Request() {
		super();
		setVersion("HTTP/1.0");
	}

	/* (non-Javadoc)
	 * @see edu.upenn.cis.cis455.requests.HttpRequest#fillHeaders(java.util.List, edu.upenn.cis.cis455.responses.HttpResponse)
	 */
	public void fillHeaders(List<String> fullInput, HttpResponse response) {
		String firstLine = fullInput.get(0);
		response.addHeader("Server", "Shreejit/1.2");

		String method = null;
		try {
			method = Parser.parseRequestMethod(firstLine);
		} catch (ParseException e) {
			response.setResponse(501);
			response.setVersion(version);
			response.setDefaultBody();
			throw new IllegalArgumentException();
		}

		response.setMethod(method);

		String path = null;
		if (Parser.isAbsolutePathRequest(firstLine)) {
			response.setResponse(400);
			String responseString = "<html><body>HTTP/1.0 doesn't support absolute urls!</body></html>";
			response.setBody(responseString.getBytes());
			response.addHeader("Content-Type", "text/html");
			throw new IllegalArgumentException();
		} else {
			path = Parser.parseRequestPath(firstLine);
		}

		setMethod(method);
		setPath(path);
		response.setMethod(method);

		List<String> parsedHeaderLines = Parser.parserHeaderLines(fullInput);

		logger.info(parsedHeaderLines);
		try {
			for (int i = 0; i < parsedHeaderLines.size(); i++) {
				String headerLine = parsedHeaderLines.get(i);
				String header = headerLine.split(Parser.HEADER_SEPARATOR)[0]
						.trim();
				String value = headerLine.split(Parser.HEADER_SEPARATOR)[1]
						.trim();
				// adding support for cookies
				if(header.equals("Cookie")) {
					for(String ckv : value.split("; ")) {
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
			response.setMethod(method);
			response.setResponse(400);
			String responseString = "<html><body>Headers aren't in the correct format!</body></html>";
			response.setBody(responseString.getBytes(StandardCharsets.UTF_8));
			response.addHeader("Content-Type", "text/html");
			throw new IllegalArgumentException();
		}
		response.setResponse(200);
	}

	/**
	 * To bytes.
	 *
	 * @return the byte[]
	 */
	public byte[] toBytes() {
		byte[] byteResponse;
		
	    byte[] bodyBytes = null;
	    if(method == null) {
	    	method = "";
	    }
	    if(body == null) {
	    	bodyBytes = "".getBytes(StandardCharsets.UTF_8);
	    } else {
	    	bodyBytes = body.getBytes();
	    }
	    setHeader("Content-Length", String.valueOf(bodyBytes.length));
	    String headers = getMarshalledHeaders();
	    byte[] headerB = headers.getBytes(StandardCharsets.UTF_8);
	    byteResponse = new byte[headerB.length + bodyBytes.length];
	    System.arraycopy(headerB, 0, byteResponse, 0, headerB.length);
	    //TODO what if no headers
	    if(!method.equals("HEAD")) {
	    	System.arraycopy(bodyBytes, 0, byteResponse, headerB.length, bodyBytes.length);
	    }
	    return byteResponse;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.upenn.cis.cis455.requests.HttpRequest#generateResponse(java.util.
	 * List, java.lang.String, edu.upenn.cis.cis455.threadpool.Queue)
	 */
	@Override
	public Http10Response generateResponse(List<String> fullInput,
			String webRoot, Queue<?> jobQueue) {
		Http10Response response = new Http10Response();
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
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
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

		logger.info("Trying to access " + filePath.toString());

		if (!FilePolicy.exists(filePath)) {
			logger.error("File doesn't exist!");
			response.setResponse(404);
			response.setDefaultBody();
			return;
		}

		if (!FilePolicy.isReadable(filePath)) {
			logger.error("File isn't readable!");
			response.setResponse(401);
			response.setDefaultBody();
			return;
		}

		if (!FilePolicy.isAccessible(filePath, webRootPath)) {
			logger.error("Trying to access something above webroot!");
			response.setResponse(403);
			response.setDefaultBody();
			return;
		}

		File file = new File(filePath.toString());
		long lastModified = file.lastModified();

		logger.info("Last modified " + lastModified);
		Date lastModifiedDate = new Date(lastModified);
		response.addHeader("Last-Modified", Parser.formatDate(lastModifiedDate));

		try {

			if (!FilePolicy.isDirectory(filePath)) {
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
