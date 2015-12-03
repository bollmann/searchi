package indexer.servlets;

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
	
	public String toJSONString() {
		return "{\"url\": " + url + " \"rank\": " + rank + " \"snippet\": " + snippet + " }";
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
