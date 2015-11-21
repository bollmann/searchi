/*
 * Written by Shreejit Gangadharan
 */
package servlets;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class ServletMap.
 */
public class ServletMap {
	
	/** The servlet map. */
	private static ServletMap servletMap;
	
	/** The map. */
	Map<String, HttpServlet> map;
	
	/**
	 * Instantiates a new servlet map.
	 */
	private ServletMap() {
		map = new HashMap<String, HttpServlet>();
	}
	
	/**
	 * Gets the single instance of ServletMap.
	 *
	 * @return single instance of ServletMap
	 */
	public static ServletMap getInstance() {
		if(servletMap == null) {
			servletMap = new ServletMap();
		}
		return servletMap;
	}
	
	/**
	 * Gets the.
	 *
	 * @param url the url
	 * @return the http servlet
	 */
	public synchronized HttpServlet get(String url) {
		return map.get(url);
	}
	
	/**
	 * Put.
	 *
	 * @param url the url
	 * @param servlet the servlet
	 */
	public synchronized void put(String url, HttpServlet servlet) {
		map.put(url, servlet);
	}
	
	/**
	 * Sets the map.
	 *
	 * @param map the map
	 */
	public synchronized void setMap(Map<String, HttpServlet> map) {
		this.map = map;
	}
	
	/**
	 * Contains key.
	 *
	 * @param url the url
	 * @return true, if successful
	 */
	public synchronized boolean containsKey(String url) {
		return map.containsKey(url);
	}

	/**
	 * Key set.
	 *
	 * @return the sets the
	 */
	public synchronized Set<String> keySet() {
		return map.keySet();
	}
	
	/**
	 * Entry set.
	 *
	 * @return the sets the
	 */
	public synchronized Set<Entry<String, HttpServlet>> entrySet() {
		return map.entrySet();
	}
}
