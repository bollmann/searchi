package utils.string;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class StringUtils {

	/** Converts the list of links to specified delimited string  */
	public static String listToString(List<String> outgoingLinks,
			String delimiter, List<String> filter) {

		StringBuffer strBuf = new StringBuffer();
		boolean isFirst = true;
		Set<String> isSeen = new HashSet<>();
		for (String link : outgoingLinks) {			
			String normalizedLink = normalizeUrlToString(link.trim());
			if (normalizedLink == null || normalizedLink.isEmpty()) {
				continue;
			}
			if (isSeen.contains(normalizedLink)) {
				continue;
			}
			isSeen.add(normalizedLink);
			if (filter != null && filter.contains(normalizedLink)) {
				continue;
			}
			if (!isFirst) {
				strBuf.append(delimiter);
			}
			strBuf.append(normalizedLink);
			isFirst = false;
		}
		return strBuf.toString();
	}
	
	/** Normalizes the Url to remove trailing slashes and # */
	public static String normalizeUrlToString(String url) {
		if (url == null || url.isEmpty()) {
			return url;
		}
		
		String newUrl = url;
		if (newUrl.toLowerCase().startsWith("https://")) {
			newUrl = newUrl.substring(8);
		}
		if (newUrl.toLowerCase().startsWith("http://")) {
			newUrl = newUrl.substring(7);
		}
		if (newUrl.toLowerCase().startsWith("www.")) {
			newUrl = newUrl.substring(4);
		}
				
		if (newUrl.endsWith("/")) {
			return newUrl.substring(0, newUrl.length() - 1 );
		}
		
		if (newUrl.endsWith("/#")) {
			return newUrl.substring(0, newUrl.length() - 2 );
		}
		return newUrl;
	}
	
	/** Strips the Url and returns just the domain 
	 * @throws MalformedURLException */
	public static String getDomainFromUrl(String urlStr) 
			throws MalformedURLException {
		if (urlStr == null || urlStr.isEmpty()) {
			return urlStr;
		}
		
		URL url = new URL(urlStr);
		return normalizeUrlToString(url.getHost());
	}

	/** Converts the list of links to specified delimited string
	 *  consisting of domains */
	public static String listToDomainString(List<String> outgoingLinks,
			String delimiter, List<String> filter) throws MalformedURLException {
		
		StringBuffer strBuf = new StringBuffer();
		boolean isFirst = true;
		Set<String> isSeen = new HashSet<>();
		for (String link : outgoingLinks) {
			String linkDomain = getDomainFromUrl(link.trim());
			
			if (linkDomain == null || linkDomain.isEmpty()) {
				continue;
			}
			if (isSeen.contains(linkDomain)) {
				continue;
			}
			isSeen.add(linkDomain);
			
			if (filter != null && filter.contains(linkDomain)) {
				continue;
			}
			if (!isFirst) {
				strBuf.append(delimiter);
			}
			strBuf.append(linkDomain);
			isFirst = false;
		}
		return strBuf.toString();
	}
}
