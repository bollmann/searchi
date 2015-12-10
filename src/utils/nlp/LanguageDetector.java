package utils.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;

public class LanguageDetector {

	private static final Logger logger = Logger
			.getLogger(LanguageDetector.class);
	

	@SuppressWarnings("serial")
	private static final List<String> mostCommonEnglishWords = new ArrayList<String>() {
		{
			add("the");
			add("be");
            add("are");
            add("is");
            add("were");
            add("was");
			add("to");
			add("of");
			add("and");
			// add("a");
			add("in");
			add("that");
			add("have");
            add("it");
			add("I");
		}
	};

    private static final Map<String, String> baseWord = new HashMap<>();
    static {
        baseWord.put("are","be");
        baseWord.put("is","be");
        baseWord.put("were","be");
        baseWord.put("was","be");
        baseWord.put("be","be");
        baseWord.put("the","the");
        baseWord.put("to","to");
        baseWord.put("of","of");
        baseWord.put("and","and");
        baseWord.put("in","in");
        baseWord.put("that","that");
        baseWord.put("have","have");
        baseWord.put("it","it");
        baseWord.put("I","I");
    }

	public static int englishWordCountHeuristic = 4;

	public static boolean hasEnglishHeader(String content) {

		if (content.contains("<meta http-equiv=\"Content-Language\"")) {
			if (content
					.contains("<meta http-equiv=\"Content-Language\" content=\"en\"")) {
				return true;
			} else {
				return false;
			}
		}
		int ind = content.indexOf("lang=\"");
		while(ind >= 0) {
			if (!content.substring(ind, ind + 15).contains("lang=\"en\"")) {
//				logger.info("Content contains lang=\"en\"");
				return false;
			}
				
		     ind = content.indexOf("lang=\"", ind+1);
		}
		return true;
	}

	public static boolean isEnglish(String page) {
		String content = Jsoup.parse(page).select("body").text();
		int englishWordCount = 0;
		Set<String> seenWords = new HashSet<String>();
        String contentLower = content.toLowerCase();
		for (String englishWord : mostCommonEnglishWords) {

			if (contentLower.contains(" " + englishWord + " ")
					&& !seenWords.contains(baseWord.get(englishWord))) {
				englishWordCount++;
				seenWords.add(englishWord);
				 logger.info("Found " + englishWord +
				   " in content. Total count " + englishWordCount);
				continue;
			}
            if (englishWordCount >= englishWordCountHeuristic) {
                return hasEnglishHeader(content);
            }
		}

		return false;
	}

}
