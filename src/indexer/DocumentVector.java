package indexer;

import java.util.Map;

public class DocumentVector implements Comparable<DocumentVector> {
	private String url;
	private Map<String, Double> wordFrequencies;
	private double similarityScore;
	
	public DocumentVector(Map<String, Double> wordFrequencies) {
		this.wordFrequencies = wordFrequencies;
	}
	
	@Override
	public int compareTo(DocumentVector other) {
		return Double.compare(this.similarityScore, other.similarityScore);
	}
	
	public static double cosineSimilarity(DocumentVector v1, DocumentVector v2) {
		double dotproduct = 0;
		
		for(String word: v1.wordFrequencies.keySet()) {
			if(v2.wordFrequencies.containsKey(word))
				dotproduct += v1.wordFrequencies.get(word) * v2.wordFrequencies.get(word);
		}
		
		return dotproduct;
	}
	
	public void setSimilarity(double sim) {
		this.similarityScore = sim;
	}
	
	public void setUrl(String u) {
		this.url = u;
	}
	
	public String toString() {
		return String.format("URL %s: cosine similarity=%f; tfidfs=%s", url, similarityScore, wordFrequencies);
	}
}