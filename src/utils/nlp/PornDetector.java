package utils.nlp;

import java.util.Arrays;
import java.util.List;

public final class PornDetector {
	
	private static final List<String> TAGS;
	static {
		TAGS = Arrays.asList(
			"porn", " sex ", "-sex ", " sex-", "xxx", "incest", "boobs", "pussy", " cunt ",
			"blowjob", "blow-job", "blow job", "3some", "threesome", "asshole", "fuck",
			" anal "," anal-", "-anal ", " dick ", " cum ", "masturbate", "horny");
	};
	
	public static boolean isPorn(String text) { 
		if (text == null || text.isEmpty()) {
			return false;
		}
		
		for (String tag : TAGS) {
			if (text.toLowerCase().contains(tag)) {				
				return true;
			}
		}
		return false;		
	}
}
