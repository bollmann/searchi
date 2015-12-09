package indexer.dao;

import java.util.HashSet;
import java.util.Set;

/**
 * The counts of some word within the given document.
 */
public final class DocumentFeatures {
	/**
	 * the document.
	 */
	private int docId;
	
	private float maximumTermFrequency;
	private float euclideanTermFrequency;
	private float tfidf;
	private int totalCount;
	private int linkCount;
	private int metaTagCount;
	private int headerCount;
	private Set<Integer> positions;
	
	public int getDocId() { return docId; }
	public void setDocId(int docId) { this.docId = docId; }
	
	public float getMaximumTermFrequency() { return maximumTermFrequency; }
	public void setMaximumTermFrequency(float maximumTermFrequency) {
		this.maximumTermFrequency = maximumTermFrequency;
	}
	
	public float getEuclideanTermFrequency() { return euclideanTermFrequency; }
	public void setEuclideanTermFrequency(float euclideanTermFrequency) {
		this.euclideanTermFrequency = euclideanTermFrequency;
	}
	
	public float getTfidf() {
		return tfidf;
	}
	public void setTfidf(float tfidf) {
		this.tfidf = tfidf;
	}
	public int getTotalCount() { return totalCount; }
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	
	public int getLinkCount() { return linkCount; }
	public void setLinkCount(int linkCount) { this.linkCount = linkCount; }
	
	public int getMetaTagCount() { return metaTagCount; }
	public void setMetaTagCount(int metaTagCount) { this.metaTagCount = metaTagCount; }
	
	public int getHeaderCount() { return headerCount; }
	public void setHeaderCount(int headerCount) {
		this.headerCount = headerCount;
	}
	
	public Set<Integer> getPositions() { return this.positions; }
	public void setPositions(Set<Integer> pos) {
		this.positions = new HashSet<>(pos);
	}

	public String toString() {
		return String.format("{docId: %d, maxtf: %f, euclidtf: %f, totalCount: %d," + 
			"linkCount: %d, metaTagCount: %d, headerCount: %d, wordPositions: %s}",
			docId, maximumTermFrequency, euclideanTermFrequency, totalCount,
			linkCount, metaTagCount, headerCount, positions.toString());
	}
}