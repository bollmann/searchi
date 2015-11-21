/*
 * Written by Shreejit Gangadharan
 */
package crawler.info;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class RobotsTxtInfo.
 */
public class RobotsTxtInfo {
	
	/** The disallowed links. */
	private HashMap<String,ArrayList<String>> disallowedLinks;
	
	/** The allowed links. */
	private HashMap<String,ArrayList<String>> allowedLinks;
	
	/** The crawl delays. */
	private HashMap<String,Integer> crawlDelays;
	
	/** The sitemap links. */
	private ArrayList<String> sitemapLinks;
	
	/** The user agents. */
	private ArrayList<String> userAgents;
	
	/**
	 * Instantiates a new robots txt info.
	 */
	public RobotsTxtInfo(){
		disallowedLinks = new HashMap<String,ArrayList<String>>();
		allowedLinks = new HashMap<String,ArrayList<String>>();
		crawlDelays = new HashMap<String,Integer>();
		sitemapLinks = new ArrayList<String>();
		userAgents = new ArrayList<String>();
	}
	
	/**
	 * Adds the disallowed link.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void addDisallowedLink(String key, String value){
		if(!disallowedLinks.containsKey(key)){
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(value);
			disallowedLinks.put(key, temp);
		}
		else{
			ArrayList<String> temp = disallowedLinks.get(key);
			if(temp == null)
				temp = new ArrayList<String>();
			temp.add(value);
			disallowedLinks.put(key, temp);
		}
	}
	
	/**
	 * Adds the allowed link.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void addAllowedLink(String key, String value){
		if(!allowedLinks.containsKey(key)){
			ArrayList<String> temp = new ArrayList<String>();
			temp.add(value);
			allowedLinks.put(key, temp);
		}
		else{
			ArrayList<String> temp = allowedLinks.get(key);
			if(temp == null)
				temp = new ArrayList<String>();
			temp.add(value);
			allowedLinks.put(key, temp);
		}
	}
	
	/**
	 * Adds the crawl delay.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void addCrawlDelay(String key, Integer value){
		crawlDelays.put(key, value);
	}
	
	/**
	 * Adds the sitemap link.
	 *
	 * @param val the val
	 */
	public void addSitemapLink(String val){
		sitemapLinks.add(val);
	}
	
	/**
	 * Adds the user agent.
	 *
	 * @param key the key
	 */
	public void addUserAgent(String key){
		userAgents.add(key);
	}
	
	/**
	 * Contains user agent.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean containsUserAgent(String key){
		return userAgents.contains(key);
	}
	
	/**
	 * Gets the disallowed links.
	 *
	 * @param key the key
	 * @return the disallowed links
	 */
	public ArrayList<String> getDisallowedLinks(String key){
		return disallowedLinks.get(key);
	}
	
	/**
	 * Gets the allowed links.
	 *
	 * @param key the key
	 * @return the allowed links
	 */
	public ArrayList<String> getAllowedLinks(String key){
		return allowedLinks.get(key);
	}
	
	/**
	 * Gets the crawl delay.
	 *
	 * @param key the key
	 * @return the crawl delay
	 */
	public int getCrawlDelay(String key){
		return crawlDelays.get(key);
	}
	
	/**
	 * Prints the.
	 */
	public void print(){
		for(String userAgent:userAgents){
			System.out.println("User-Agent: "+userAgent);
			ArrayList<String> dlinks = disallowedLinks.get(userAgent);
			if(dlinks != null)
				for(String dl:dlinks)
					System.out.println("Disallow: "+dl);
			ArrayList<String> alinks = allowedLinks.get(userAgent);
			if(alinks != null)
					for(String al:alinks)
						System.out.println("Allow: "+al);
			if(crawlDelays.containsKey(userAgent))
				System.out.println("Crawl-Delay: "+crawlDelays.get(userAgent));
			System.out.println();
		}
		if(sitemapLinks.size() > 0){
			System.out.println("# SiteMap Links");
			for(String sitemap:sitemapLinks)
				System.out.println(sitemap);
		}
	}
	
	/**
	 * Crawl contain agent.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	public boolean crawlContainAgent(String key){
		return crawlDelays.containsKey(key);
	}
}
