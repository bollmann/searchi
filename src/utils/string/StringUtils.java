package utils.string;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public final class StringUtils {

	/** Converts the list of links to specified delimited string  */
	public static String listToString(List<String> outgoingLinks,
			String delimiter, List<String> filter) {

		StringBuffer strBuf = new StringBuffer();
		boolean isFirst = true;
		for (String link : outgoingLinks) {
			String normalizedLink = normalizeUrlToString(link.trim());
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
				
		if (url.endsWith("/")) {
			return url.substring(0, url.length() - 1 );
		}
		
		if (url.endsWith("/#")) {
			return url.substring(0, url.length() - 2 );
		}
		return url;
	}
	
	/** Strips the Url and returns just the domain 
	 * @throws MalformedURLException */
	public static String getDomainFromUrl(String urlStr) 
			throws MalformedURLException {
		if (urlStr == null || urlStr.isEmpty()) {
			return urlStr;
		}
		
		URL url = new URL(urlStr);
		return url.getHost();
	}

	/** Converts the list of links to specified delimited string
	 *  consisting of domains */
	public static String listToDomainString(List<String> outgoingLinks,
			String delimiter, List<String> filter) throws MalformedURLException {
		
		StringBuffer strBuf = new StringBuffer();
		boolean isFirst = true;
		for (String link : outgoingLinks) {
			String linkDomain = getDomainFromUrl(link.trim());
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
