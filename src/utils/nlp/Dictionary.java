package utils.nlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class Dictionary {
	public static final InputStream JAR_RESOURCE = Dictionary.class.getResourceAsStream("/resources/dict/all-english");
	
	private static Dictionary dict = null;
	private Set<String> words;
	
	private Dictionary(InputStream resource) throws IOException {
		String dictWords = new Scanner(resource).useDelimiter("\\Z").next();
		List<String> lines = Arrays.asList(dictWords.split("\n"));
		words = new HashSet<>(lines);
	}
	
	public static Dictionary createInstance(InputStream source) throws IOException {
		if(dict == null)
			dict = new Dictionary(source);
		return dict;
	}
	
	public static Dictionary getInstance() {
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