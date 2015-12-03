package indexer.servlets;

import java.util.HashMap;

public class SearchResult implements Comparable<SearchResult> {
	private String url;
	private String snippet;
	private Double rank;
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSnippet() {
		return snippet;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}
	public Double getRank() {
		return rank;
	}
	public void setRank(Double score) {
		this.rank = score;
	}
	
	@Override
	public String toString() {
		return "url:" + url + "  rank:" + rank +  "\nsnippet:" + snippet;
	}
	
	public HashMap<String, String> toMap() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("url", url);
		map.put("rank", String.valueOf(rank));
		map.put("snippet", snippet);
		return map;
	}
	
	public String toHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<p>");
		sb.append("<a href='"+ url + "'>" + url + "</a> : " + rank);
		sb.append("<br>" + snippet);
		sb.append("</p>");
		return sb.toString();
	}
	
	@Override
	public int compareTo(SearchResult other) {
		return (-1) * Double.compare(rank, other.rank);
	}

}
