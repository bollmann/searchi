package dao;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import xpathengine.XPathEngineFactory;
import xpathengine.XPathHandler;

@Entity
public class Channel {

	@PrimaryKey
	private String name;

	private String[] xPaths;

	private Set<String> matchedPages;

	private String userName;
	
	public Channel() {
		matchedPages = new HashSet<String>();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getxPaths() {
		return xPaths;
	}

	public void setxPaths(String[] xPaths2) {
		this.xPaths = xPaths2;
	}

	public boolean isMatch(String content) {
		boolean isMatch = false;
		InputStream is = new ByteArrayInputStream(content.getBytes());
		XPathHandler h = XPathEngineFactory.getSAXHandler();
		h.setXPaths(xPaths);
		boolean[] results = h.evaluateSAX(is, h);
		for(boolean result: results) {
			if(result) {
				return true;
			}
		}
		return isMatch;
	}
	
	public void addUrl(String url) {
		matchedPages.add(url);
	}
	
	public Set<String> getMatchedPages() {
		return matchedPages;
	}


}
