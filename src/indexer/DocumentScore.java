package indexer;

import indexer.db.dao.DocumentFeatures;

import java.util.HashMap;
import java.util.Map;

import searchengine.query.QueryWord;

public class DocumentScore implements Comparable<DocumentScore> {
	private int docId;
	private double score;

	/*
	 * Feature counts of words occurring in this document.
	 */
	private Map<QueryWord, DocumentFeatures> wordFeatures;

	public DocumentScore(int docId) {
		this.docId = docId;
		this.wordFeatures = new HashMap<QueryWord, DocumentFeatures>();
	}
	
	public DocumentScore(QueryWord word, DocumentFeatures features) {
		this.docId = features.getDocId();
		this.wordFeatures = new HashMap<QueryWord, DocumentFeatures>();
		addFeatures(word, features);
	}

	public void addFeatures(QueryWord word, DocumentFeatures features) {
		wordFeatures.put(word, features);
	}

	public int getDocId() {
		return docId;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public double getScore() {
		return score;
	}
	
	public Map<QueryWord, DocumentFeatures> getWordFeatures() {
		return wordFeatures;
	}
	
	public void setWordFeatures(Map<QueryWord, DocumentFeatures> wordFeatures) {
		this.wordFeatures = wordFeatures;
	}

	@Override
	public int compareTo(DocumentScore other) {
		return (-1) * Double.compare(this.score, other.score);
	}

	@Override
	public String toString() {
		StringBuffer fmt = new StringBuffer();
		fmt.append(String.format("DocID %d ; score=%f\n", this.docId, this.score));
		for(QueryWord qword: wordFeatures.keySet())
			fmt.append(String.format("%s features=%s\n", qword.getWord(), wordFeatures.get(qword)));
		
		return fmt.toString();
	}
	
	public String toHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<p>DocID " + this.docId + "; " + this.score + "<br />");
		for(QueryWord word: wordFeatures.keySet())
			sb.append(word + " features=" + wordFeatures.get(word) + "<br />");
		sb.append("</p>");
		return sb.toString();
	}
}
