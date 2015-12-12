package indexer.db.dao;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
	private int headerCount;
	private int linkCount;
	private int metaTagCount;
	private int urlCount;
	private Set<Integer> positions;

	public int getUrlCount() {
		return urlCount;
	}

	public void setUrlCount(int urlCount) {
		this.urlCount = urlCount;
	}

	public DocumentFeatures() {
		this.positions = new HashSet<Integer>();
	}

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public float getMaximumTermFrequency() {
		return maximumTermFrequency;
	}

	public void setMaximumTermFrequency(float maximumTermFrequency) {
		this.maximumTermFrequency = maximumTermFrequency;
	}

	public float getEuclideanTermFrequency() {
		return euclideanTermFrequency;
	}

	public void setEuclideanTermFrequency(float euclideanTermFrequency) {
		this.euclideanTermFrequency = euclideanTermFrequency;
	}

	public float getTfidf() {
		return tfidf;
	}

	public void setTfidf(float tfidf) {
		this.tfidf = tfidf;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getLinkCount() {
		return linkCount;
	}

	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}

	public int getMetaTagCount() {
		return metaTagCount;
	}

	public void setMetaTagCount(int metaTagCount) {
		this.metaTagCount = metaTagCount;
	}

	public int getHeaderCount() {
		return headerCount;
	}

	public void setHeaderCount(int headerCount) {
		this.headerCount = headerCount;
	}

	public Set<Integer> getPositions() {
		return this.positions;
	}

	public void setPositions(Set<Integer> pos) {
		this.positions = new HashSet<>(pos);
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31)
			.append(docId)
			.append(maximumTermFrequency)
			.append(euclideanTermFrequency)
			.append(tfidf)
			.append(totalCount)
			.append(headerCount)
			.append(linkCount)
			.append(metaTagCount)
			.append(positions)
			.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DocumentFeatures))
			return false;
		if (this == o)
			return true;
		
		DocumentFeatures other = (DocumentFeatures) o;
		return new EqualsBuilder()
				.append(this.docId, other.docId)
				.append(this.maximumTermFrequency, other.maximumTermFrequency)
				.append(this.euclideanTermFrequency,
						other.euclideanTermFrequency)
				.append(this.tfidf, other.tfidf)
				.append(this.totalCount, other.totalCount)
				.append(this.headerCount, other.headerCount)
				.append(this.linkCount, other.linkCount)
				.append(this.metaTagCount, other.metaTagCount)
				.append(this.positions, other.positions).isEquals();
	}

	public String toString() {

		return String
				.format("{docId: %d, maxtf: %f, euclidtf: %f, tfidf: %f, totalCount: %d, "
						+ "linkCount: %d, metaTagCount: %d, headerCount: %d, wordPositions: %s}",
						docId, maximumTermFrequency, euclideanTermFrequency,
						tfidf, totalCount, linkCount, metaTagCount,
						headerCount, positions.toString());
	}
}