package utils.string;

import java.util.List;

public final class StringUtils {

	/** Converts the list of links to space delimited string  */
	public static String listToString(List<String> outgoingLinks,
			String delimiter, List<String> filter) {

		StringBuffer strBuf = new StringBuffer();
		boolean isFirst = true;
		for (String link : outgoingLinks) {
			if (filter != null && filter.contains(link.trim())) {
				continue;
			}
			if (!isFirst) {
				strBuf.append(delimiter);
			}
			strBuf.append(link);
			isFirst = false;
		}
		return strBuf.toString();
	}
}
