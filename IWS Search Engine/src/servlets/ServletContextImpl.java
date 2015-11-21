/*
 * Written by Shreejit Gangadharan
 */
package servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class ServletContextImpl.
 */
public class ServletContextImpl implements ServletContext {
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(ServletContextImpl.class);

	/**
	 * Instantiates a new servlet context impl.
	 */
	public ServletContextImpl() {
		this.initParams = new HashMap<String, String>();
		this.attributes = new HashMap<String, Object>();
		this.servletContextName = "root context";
	}

	/** The init params. */
	private Map<String, String> initParams;
	
	/** The attributes. */
	private Map<String, Object> attributes;
	
	/** The servlet context name. */
	private String servletContextName;
	
	/** The web root. */
	private String webRoot;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String arg0) {
		synchronized (attributes) {
			return attributes.get(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getAttributeNames()
	 */
	@Override
	public Enumeration getAttributeNames() {
		synchronized (attributes) {
			Vector<String> attribNames = new Vector<String>(attributes.keySet());
			return attribNames.elements();
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getContext(java.lang.String)
	 */
	@Override
	public synchronized ServletContext getContext(String arg0) {
		return this;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
	 */
	@Override
	public String getInitParameter(String arg0) {
		synchronized (initParams) {
			return initParams.get(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getInitParameterNames()
	 */
	@Override
	public Enumeration getInitParameterNames() {
		synchronized (initParams) {
			Vector<String> initParamNames = new Vector<String>(
					initParams.keySet());
			return initParamNames.elements();
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getMajorVersion()
	 */
	@Override
	public int getMajorVersion() {
		return 2;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
	 */
	@Override
	public String getMimeType(String arg0) {
		Path filePath = Paths.get(arg0);
		String contentType = null;
		if (Files.exists(filePath)) {
			try {
				contentType = Files.probeContentType(filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("Content Type not available!");
				e.printStackTrace();
			}
		}
		return contentType;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getMinorVersion()
	 */
	@Override
	public int getMinorVersion() {
		return 4;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
	 */
	@Override
	public RequestDispatcher getNamedDispatcher(String arg0) {
		// Don't need to do
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
	 */
	@Override
	public String getRealPath(String arg0) {
		// Don't need to do
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getRequestDispatcher(java.lang.String)
	 */
	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// Don't need to do
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String resource) throws MalformedURLException {
		URL url = null;
		if (resource.startsWith("/")) {
			File file = new File(webRoot + resource);
			if(!file.isDirectory()) {
				url = new URL("htp://localhost:8080" + resource);
			}
		}
		return url;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
	 */
	@Override
	public InputStream getResourceAsStream(String arg0) {
		File file = new File(arg0);
		InputStream inputStream = null;
		if (file.exists()) {
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				logger.error("File not found when converting to input stream!");
				e.printStackTrace();
			}
		}
		return inputStream;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
	 */
	@Override
	public Set getResourcePaths(String filePath) {
		Set<String> resources = null;
		if (filePath.startsWith("/")) {
			File file = new File(webRoot + "/" + filePath);
			resources = new HashSet<String>();
			if (file.exists()) {
				if (file.isDirectory()) {
					for (File fileEntry : file.listFiles()) {
						resources.add(fileEntry.getPath());
					}
				}
			}
		}
		return resources;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServerInfo()
	 */
	@Override
	public String getServerInfo() {
		return "Shreejit's server/1.2";
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServlet(java.lang.String)
	 */
	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Sets the servlet context name.
	 *
	 * @param servletContextName the new servlet context name
	 */
	public void setServletContextName(String servletContextName) {
		synchronized (this.servletContextName) {
			this.servletContextName = servletContextName;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletContextName()
	 */
	@Override
	public String getServletContextName() {
		synchronized (this.servletContextName) {
			return servletContextName;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServletNames()
	 */
	@Override
	public Enumeration getServletNames() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#getServlets()
	 */
	@Override
	public Enumeration getServlets() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#log(java.lang.String)
	 */
	@Override
	public void log(String arg0) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
	 */
	@Override
	public void log(Exception arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
	 */
	@Override
	public void log(String arg0, Throwable arg1) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String arg0) {
		synchronized (attributes) {
			attributes.remove(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String arg0, Object arg1) {
		synchronized (attributes) {
			attributes.put(arg0, arg1);
		}
	}

	/**
	 * Sets the init parameter.
	 *
	 * @param param the param
	 * @param string the string
	 */
	public void setInitParameter(String param, String string) {
		synchronized (initParams) {
			initParams.put(param, string);
		}
	}

	/**
	 * Gets the web root.
	 *
	 * @return the web root
	 */
	public String getWebRoot() {
		return webRoot;
	}

	/**
	 * Sets the web root.
	 *
	 * @param webRoot the new web root
	 */
	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;

	}

}
