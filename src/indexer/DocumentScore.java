package indexer;

import indexer.db.dao.DocumentFeatures;

import java.util.HashMap;
import java.util.Map;

public class DocumentScore implements Comparable<DocumentScore> {
	private int docId;
	private float score;

	/*
	 * Feature counts of words occurring in this document.
	 */
	private Map<String, DocumentFeatures> wordFeatures;

	public DocumentScore(int docId) {
		this.docId = docId;
		this.wordFeatures = new HashMap<String, DocumentFeatures>();
	}
	
	public DocumentScore(String word, DocumentFeatures features) {
		this.docId = features.getDocId();
		this.wordFeatures = new HashMap<String, DocumentFeatures>();
		addFeatures(word, features);
	}

	public void addFeatures(String word, DocumentFeatures features) {
		wordFeatures.put(word, features);
	}

	public int getDocId() {
		return docId;
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
	
	public void setWordFeatures(Map<String, DocumentFeatures> wordFeatures) {
		this.wordFeatures = wordFeatures;
	}

	@Override
	public int compareTo(DocumentScore other) {
		return (-1) * Float.compare(this.score, other.score);
	}

	@Override
	public String toString() {
		StringBuffer fmt = new StringBuffer();
		fmt.append(String.format("DocID %d ; score=%f\n", this.docId, this.score));
		for(String word: wordFeatures.keySet())
			fmt.append(String.format("%s features=%s\n", word, wordFeatures.get(word)));
		
		return fmt.toString();
	}

}
