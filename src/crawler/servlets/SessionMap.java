/*
 * Written by Shreejit Gangadharan
 */
package crawler.servlets;

import java.util.HashMap;
import java.util.Map;

// TODO: Auto-generated Javadoc
/**
 * The Class SessionMap.
 */
public class SessionMap {
	
	/** The session map. */
	private static SessionMap sessionMap;

	/** The map. */
	private Map<String, HttpSessionImpl> map;

	/**
	 * Instantiates a new session map.
	 */
	private SessionMap() {
		map = new HashMap<String, HttpSessionImpl>();
	}
	
	/**
	 * Gets the sessions.
	 *
	 * @return the sessions
	 */
	public Map<String, HttpSessionImpl> getSessions() {
		return map;
	}

	/**
	 * Gets the single instance of SessionMap.
	 *
	 * @return single instance of SessionMap
	 */
	public static synchronized SessionMap getInstance() {
		if (sessionMap == null) {
			sessionMap = new SessionMap();
		}
		return sessionMap;
	}

	/**
	 * Gets the session.
	 *
	 * @param sessionId the session id
	 * @return the session
	 */
	public HttpSessionImpl getSession(String sessionId) {
		synchronized (map) {
			HttpSessionImpl session = map.get(sessionId);
			if(session != null) {
				if(!session.isValid()) {
					// TODO performance
					map.remove(sessionId);
					session = null;
				}
			}
			return session;
		}
	}

	/**
	 * Adds the session.
	 *
	 * @param session the session
	 */
	public void addSession(HttpSessionImpl session) {
		synchronized (map) {
			map.put(session.getId(), session);
		}
	}
	
	/**
	 * Removes the session.
	 *
	 * @param session the session
	 */
	public void removeSession(HttpSessionImpl session) {
		synchronized (map) {
			map.remove(session.getId());
		}
	}
	
	/**
	 * Destroy.
	 */
	public void destroy() {
		map.clear();
	}

}
