/*
 * Written by Shreejit Gangadharan
 */
package servlets;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

// TODO: Auto-generated Javadoc
/**
 * The Class ServletConfigImpl.
 */
public class ServletConfigImpl implements ServletConfig {

	/** The servlet name. */
	private String servletName = "";
	
	/** The servlet context. */
	private ServletContext servletContext;
	
	/** The init params. */
	private Map<String, String> initParams;

	/**
	 * Instantiates a new servlet config impl.
	 *
	 * @param name the name
	 * @param context the context
	 */
	public ServletConfigImpl(String name, ServletContext context) {
		this.servletName = name;
		this.servletContext = context;
		initParams = new HashMap<String, String>();
	}

	/**
	 * Instantiates a new servlet config impl.
	 */
	public ServletConfigImpl() {
		servletName = "Random Name";
		initParams = new HashMap<String, String>();
		servletContext = new ServletContextImpl();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameter(java.lang.String)
	 */
	@Override
	public String getInitParameter(String arg0) {
		synchronized (initParams) {
			return initParams.get(arg0);
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getInitParameterNames()
	 */
	@Override
	public Enumeration<String> getInitParameterNames() {
		synchronized (initParams) {
			return Collections.enumeration(initParams.keySet());
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletContext()
	 */
	@Override
	public ServletContext getServletContext() {
		synchronized (servletContext) {
			return servletContext;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletConfig#getServletName()
	 */
	@Override
	public String getServletName() {
		synchronized (servletName) {
			return servletName;
		}
	}

	/**
	 * Sets the init param.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void setInitParam(String key, String value) {
		synchronized (initParams) {
			initParams.put(key, value);
		}
	}

	/**
	 * Sets the servlet name.
	 *
	 * @param servletName the new servlet name
	 */
	public void setServletName(String servletName) {
		synchronized (this.servletName) {
			this.servletName = servletName;
		}
	}

	/**
	 * Sets the servlet context.
	 *
	 * @param scx the new servlet context
	 */
	public void setServletContext(ServletContextImpl scx) {
		synchronized (this.servletContext) {
			this.servletContext = scx;
		}
	}
}
