package indexer;

import indexer.dao.DocumentFeatures;

import java.util.HashMap;
import java.util.Map;

public class DocumentScore implements Comparable<DocumentScore> {
	private String url;
	private float score;

	/*
	 * Feature counts of words occurring in this document.
	 */
	private Map<String, DocumentFeatures> wordFeatures;

	public DocumentScore(String url) {
		this.url = new String(url);
		this.wordFeatures = new HashMap<String, DocumentFeatures>();
	}
	
	public DocumentScore(String word, DocumentFeatures features) {
		this.url = new String(features.getUrl());
		this.wordFeatures = new HashMap<String, DocumentFeatures>();
		addFeatures(word, features);
	}

	public void addFeatures(String word, DocumentFeatures features) {
		wordFeatures.put(word, features);
	}

	public String getUrl() {
		return url;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public float getScore() {
		return score;
	}
	
	public Map<String, DocumentFeatures> getWordFeatures() {
		return wordFeatures;
	}

	@Override
	public int compareTo(DocumentScore other) {
		return (-1) * Float.compare(this.score, other.score);
	}

	@Override
	public String toString() {
		StringBuffer fmt = new StringBuffer();
		fmt.append(String.format("URL %s ; score=%f\n", this.url, this.score));
		for(String word: wordFeatures.keySet())
			fmt.append(String.format("%s features=%s\n", word, wordFeatures.get(word)));
		
		return fmt.toString();
	}

}
