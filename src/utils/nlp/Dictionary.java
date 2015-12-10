package utils.nlp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import crawler.policies.FilePolicy;

public class Dictionary {
	private static final String DIR_PATH = "resources/dict/all-english";	
	private static Dictionary dict = null;
	
	private Set<String> words;
	
	private Dictionary() throws IOException {
		String dictWords = FilePolicy.readFile(DIR_PATH);
		List<String> lines = Arrays.asList(dictWords.split("\n"));
		words = new HashSet<>(lines);
	}
	
	public static Dictionary getInstance() throws IOException {
		if (dict == null) {
			dict = new Dictionary();
		}
		return dict;
	}
	
	public Set<String> getAllWords() {
		return this.words;
	}
	
	public boolean contains(String word ) {
		if (word == null || word.isEmpty()) {
			return false;			
		}
		return words.contains(word);
	}

}