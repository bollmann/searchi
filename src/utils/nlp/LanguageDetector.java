package utils.nlp;

import indexer.offline.Tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.champeau.ld.UberLanguageDetector;

import org.apache.log4j.Logger;

public class LanguageDetector {

	private static final Logger logger = Logger
			.getLogger(LanguageDetector.class);
	private static Integer TOKEN_HEURISTIC = 10;
	private static Double TOKEN_HEURISTIC_THRESHHOLD = 70.0;

	public static List<String> mostCommonEnglishWords = new ArrayList<String>() {
		{
			add("the");
			add("be");
			add("to");
			add("of");
			add("and");
			add("a");
			add("in");
			add("that");
			add("have");
			add("I");
		}
	};

	public static int englishWordCountHeuristic = 4;

	public static boolean isEnglish(String content) {
		int englishWordCount = 0;

		for (String englishWord : mostCommonEnglishWords) {
			if (content.contains(" " + englishWord + " ")) {
				englishWordCount++;
				logger.info("Found " + englishWord + " in content. Total count " + englishWordCount);
				continue;
			}
		}

		return (englishWordCount >= englishWordCountHeuristic);
	}

}
