package indexer;

import indexer.dao.DocumentFeatures;
import indexer.dao.InvertedIndexRow;

import java.util.HashMap;
import java.util.Map;

public class DocumentScore implements Comparable<DocumentScore> {
	private String url;
	private double rank;

	/*
	 * Feature counts of words occurring in this document.
	 */
	private Map<String, Integer> totalCounts;
	private Map<String, Integer> linkCounts;
	private Map<String, Integer> headerCounts;

	public DocumentScore(String word, DocumentFeatures features) {
		this.url = new String(features.getUrl());
		this.rank = 0.0;
		this.totalCounts = new HashMap<String, Integer>();
		this.linkCounts = new HashMap<String, Integer>();
		this.headerCounts = new HashMap<String, Integer>();

		addFeatures(word, features);
	}

	public void addFeatures(String word, DocumentFeatures features) {
		this.totalCounts.put(word, features.getTotalCount());
		this.linkCounts.put(word, features.getLinkCount());
		this.headerCounts.put(word, features.getHeaderCount());
	}

	public String getUrl() {
		return url;
	}

	public void setRank(double r) {
		this.rank = r;
	}

	public double getRank() {
		return rank;
	}

	public Map<String, Integer> getTotalCounts() {
		return totalCounts;
	}

	public Map<String, Integer> getLinkCounts() {
		return linkCounts;
	}

	public Map<String, Integer> getHeaderCounts() {
		return headerCounts;
	}

	@Override
	public int compareTo(DocumentScore other) {
		return (-1) * Double.compare(this.rank, other.rank);
	}

	@Override
	public String toString() {
		StringBuffer fmt = new StringBuffer();
		fmt.append("URL %s ; rank=%f\n");
		fmt.append("totalWordCounts=%s\n");
		fmt.append("headerCounts=%s\n");
		fmt.append("linkCounts=%s\n");
		
		return String.format(fmt.toString(), this.url, this.rank,
				this.totalCounts, this.headerCounts, this.linkCounts);
	}

}
