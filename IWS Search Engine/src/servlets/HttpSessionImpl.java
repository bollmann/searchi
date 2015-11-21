/*
 * Written by Shreejit Gangadharan
 */
package servlets;

import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class HttpSessionImpl.
 */
public class HttpSessionImpl implements HttpSession {
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(HttpSessionImpl.class);

	/** The creation time. */
	private long creationTime;

	/** The id. */
	private String id;

	/** The last accessed time. */
	private long lastAccessedTime;

	/** The servlet context. */
	private ServletContext servletContext;

	/** The max inactive interval. */
	private int maxInactiveInterval;

	/** The attributes. */
	private Map<String, Object> attributes;

	/** The is valid. */
	private boolean isValid;

	/** The is new. */
	private boolean isNew;

	/**
	 * Instantiates a new http session impl.
	 */
	public HttpSessionImpl() {
		id = UUID.randomUUID().toString();
		logger.info("UUID is " + id);
		attributes = new HashMap<String, Object>();
		isValid = true;
		isNew = true;
		Calendar cal = Calendar.getInstance();
		setCreationTime(cal.getTimeInMillis());
		setLastAccessedTime(cal.getTimeInMillis());
		maxInactiveInterval = -1;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String arg0) {
		if (isValid) {
			return attributes.get(arg0);
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getAttributeNames()
	 */
	@Override
	public Enumeration<String> getAttributeNames() {
		return Collections.enumeration(attributes.keySet());
	}

	/**
	 * Sets the creation time.
	 *
	 * @param creationTime the new creation time
	 * @throws IllegalStateException the illegal state exception
	 */
	public void setCreationTime(long creationTime) throws IllegalStateException {
		this.creationTime = creationTime;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getCreationTime()
	 */
	@Override
	public long getCreationTime() {
		if (isValid) {
			return creationTime;
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getId()
	 */
	@Override
	public String getId() {
		if (isValid) {
			return id;
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}

	/**
	 * Sets the last accessed time.
	 *
	 * @param lastAccessedTime the new last accessed time
	 */
	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getLastAccessedTime()
	 */
	@Override
	public long getLastAccessedTime() {
		if (isValid) {
			return lastAccessedTime;
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
	 */
	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	/**
	 * Sets the servlet context.
	 *
	 * @param scx the new servlet context
	 */
	public void setServletContext(ServletContext scx) {
		this.servletContext = scx;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getServletContext()
	 */
	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getSessionContext()
	 */
	@Override
	public HttpSessionContext getSessionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
	 */
	@Override
	@Deprecated
	public Object getValue(String arg0) {
		if (isValid) {
			return null;
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getValueNames()
	 */
	@Override
	@Deprecated
	public String[] getValueNames() {
		if (isValid) {
			return null;
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#invalidate()
	 */
	@Override
	public void invalidate() {
		if (isValid) {
			isValid = false;
		} else {
			throw new IllegalStateException("Session is invalid!");
		}

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#isNew()
	 */
	@Override
	public boolean isNew() {
		//TODO
		if (isValid) {
			return isNew;
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
	 */
	@Override
	@Deprecated
	public void putValue(String arg0, Object arg1) {
		if (isValid) {
			
		} else {
			throw new IllegalStateException("Session is invalid!");
		}

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String arg0) {
		if (isValid) {
			attributes.remove(arg0);
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
	 */
	@Override
	@Deprecated
	public void removeValue(String arg0) {
		if (isValid) {
			
		} else {
			throw new IllegalStateException("Session is invalid!");
		}

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String arg0, Object arg1) {
		if (isValid) {
			attributes.put(arg0, arg1);
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
	 */
	@Override
	public void setMaxInactiveInterval(int interval) {
		if (isValid) {
			this.maxInactiveInterval = interval;
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}

	/**
	 * Checks if is valid.
	 *
	 * @return true, if is valid
	 */
	public boolean isValid() {
		return isValid;
	}

	/**
	 * Sets the checks if is new.
	 *
	 * @param isNew the new checks if is new
	 */
	public void setIsNew(boolean isNew) {
		if (isValid) {
			this.isNew = isNew;
		} else {
			throw new IllegalStateException("Session is invalid!");
		}
	}
}
