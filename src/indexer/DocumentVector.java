package indexer;

import java.util.Map;

public class DocumentVector implements Comparable<DocumentVector> {
	private String url;
	private Map<String, Double> wordTFIDFs;
	private double similarityScore;
	
	public DocumentVector(Map<String, Double> wordTFIDFs) {
		this.wordTFIDFs = wordTFIDFs;
	}
	
	@Override
	public int compareTo(DocumentVector other) {
		return Double.compare(this.similarityScore, other.similarityScore);
	}
	
	public static double cosineSimilarity(DocumentVector v1, DocumentVector v2) {
		double dotproduct = 0;
		double sizeV1 = 0;
		double sizeV2 = 0;
		
		for(String word: v1.wordTFIDFs.keySet()) {
			if(v2.wordTFIDFs.containsKey(word))
				dotproduct += v1.wordTFIDFs.get(word) * v2.wordTFIDFs.get(word);
			
			sizeV1 += v1.wordTFIDFs.get(word) * v1.wordTFIDFs.get(word);
		}
		
		for(String word: v2.wordTFIDFs.keySet())
			sizeV2 += v2.wordTFIDFs.get(word) * v2.wordTFIDFs.get(word);
		
		//return (dotproduct / Math.sqrt(sizeV1 * sizeV2));
		return dotproduct;
	}
	
	public void setSimilarity(double sim) {
		this.similarityScore = sim;
	}
	
	public void setUrl(String u) {
		this.url = u;
	}
	
	public String toString() {
		return String.format("URL %s: cosine similarity=%f; tfidfs=%s", url, similarityScore, wordTFIDFs);
	}
}