/*
 * Written by Shreejit Gangadharan
 */
package crawler.webserver;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.HTMLLayout;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.xml.sax.SAXException;

import crawler.parsers.Parser;
import crawler.parsers.Parser.Handler;
import crawler.servlets.ServletContextImpl;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpServer.
 */
public class HttpServer {

	/** The Constant CONSOLE_APPENDER. */
	public static final String CONSOLE_APPENDER = "CONSOLE_APPENDER";
	
	/** The appender. */
	public static WriterAppender appender = null;
	
	/** The console writer. */
	public static StringWriter consoleWriter = new StringWriter();

	/** The logger. */
	private static Logger logger = Logger.getLogger(HttpServer.class);

	/**
	 * The main method.
	 *
	 * @param args
	 *            the arguments
	 */
	public static void main(String args[]) {
		setMemoryAppender();
		if (args.length == 0) {
			System.out.println("Shreejit Gangadharan shreejit");
			System.exit(0);
		} else if (args.length == 1) {
			System.out
					.println("You have activated Skynet. Searching John Connor....");
			System.out.println(" ░░░░░░░░░░░░▄▄\n" 
					+ "░░░░░░░░░░░█░░█\n"
					+ "░░░░░░░░░░░█░░█\n" 
					+ "░░░░░░░░░░█░░░█\n"
					+ "░░░░░░░░░█░░░░█\n" 
					+ "███████▄▄█░░░░░██████▄\n"
					+ "▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n" 
					+ "▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n"
					+ "▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n" 
					+ "▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n"
					+ "▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n" 
					+ "▓▓▓▓▓▓█████░░░░░░░░░█\n"
					+ "██████▀░░░░▀▀██████▀\n");
			System.exit(1);
		}
		if (args.length < 3) {
			System.out
					.println("Usage: java -cp .:bin/. edu.upenn.cis.cis455.webserver.HttpServer"
							+ " <port> <web root> <path to web.xml>");
			System.exit(1);
		}
		logger.info("Listening at port " + args[0] + " with webroot "
				+ args[1]);
		int port = 0;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception e) {
			System.out.println("Incorrect port format!");
			System.exit(1);
		}
		if (port > 65535) {
			System.out.println("Port out of range!");
			System.exit(1);
		}
		String webRoot = args[1];
		Path webRootPath = Paths.get(webRoot);
		if (!Files.exists(webRootPath) || !Files.isDirectory(webRootPath)) {
			logger.error("Invalid path given for web root:" + webRoot);
			System.out.println("Wrong path!");
			System.exit(1);
		}

		String webDotXml = args[2];
		Path webDotXmlPath = Paths.get(webDotXml);
		if (!Files.exists(webDotXmlPath)) {
			logger.error("Invalid path given for web dot xml:" + webDotXmlPath
					+ " " + Files.exists(webDotXmlPath));
			System.out.println("Invalid path for web.xml!");
			System.exit(1);
		}

		Handler h = null;
		try {
			h = Parser.parseWebdotxml(webDotXml);
		} catch (IOException | SAXException | ParserConfigurationException e) {
			System.out.println("Couldn't parse web.xml");
			logger.error("Couldn't parse web.xml:" + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		Map<String, HttpServlet> servlets = null;
		try {
			servlets = getServlets(h, webRoot);
		} catch (IOException | ClassNotFoundException | InstantiationException
				| IllegalAccessException | ServletException e) {
			System.out.println("Couldn't create servlets mentioned in web.xml");
			logger.error("Couldn't create servlets mentioned in web.xml:"
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}


		RunnerDaemon daemon = new RunnerDaemon(port, webRoot, webDotXml,
				servlets);
		daemon.setDaemon(true);
		daemon.run();
	}
	
	/**
	 * Gets the servlets.
	 *
	 * @param h the h
	 * @param webRoot the web root
	 * @return the servlets
	 * @throws ClassNotFoundException the class not found exception
	 * @throws InstantiationException the instantiation exception
	 * @throws IllegalAccessException the illegal access exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	public static Map<String, HttpServlet> getServlets(Handler h, String webRoot) throws ClassNotFoundException, 
	InstantiationException, IllegalAccessException, IOException, ServletException {
		ServletContextImpl fc = Parser.createContext(h);
		fc.setWebRoot(webRoot);
		Map<String, HttpServlet> servlets = Parser.createServlets(h, fc);
		return servlets;
	}

	/**
	 * Sets the memory appender.
	 */
	public static void setMemoryAppender() {
		appender = new WriterAppender(
		// new PatternLayout("%d{ISO8601} %p - %m%n<br/>"),
				new HTMLLayout(), consoleWriter);
		appender.setName(CONSOLE_APPENDER);
		appender.setThreshold(org.apache.log4j.Level.WARN);
		Logger root = Logger.getRootLogger();
		root.addAppender(appender);
	}
}
