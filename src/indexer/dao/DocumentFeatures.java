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
	private String url;
	
	private double maximumTermFrequency;
	private double euclideanTermFrequency;
	private int totalCount;
	private int linkCount;
	private int metaTagCount;
	private int headerCount;
	private Set<Integer> positions;
	
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
	
	public double getMaximumTermFrequency() { return maximumTermFrequency; }
	public void setMaximumTermFrequency(double maximumTermFrequency) {
		this.maximumTermFrequency = maximumTermFrequency;
	}
	
	public double getEuclideanTermFrequency() { return euclideanTermFrequency; }
	public void setEuclideanTermFrequency(double euclideanTermFrequency) {
		this.euclideanTermFrequency = euclideanTermFrequency;
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
		return String.format("url = %s, maxtf = %f, euclidtf = %f, totalCount = %d," + 
			"linkCount = %d, metaTagCount = %d, headerCount = %d, wordPositions = %s",
			url, maximumTermFrequency, euclideanTermFrequency, totalCount,
			linkCount, metaTagCount, headerCount, positions.toString());
	}
}