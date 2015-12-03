package indexer.servlets;

public class SearchResult implements Comparable<SearchResult> {
	private String url;
	private String snippet;
	private Double score;
	
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
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	
	@Override
	public String toString() {
		return "url:" + url + "  rank:" + score +  "\nsnippet:" + snippet;
	}
	
	public String toHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<p>");
		sb.append("<a href='"+ url + "'>" + url + "</a> : " + score);
		sb.append("<br>" + snippet);
		sb.append("</p>");
		return sb.toString();
	}
	
	@Override
	public int compareTo(SearchResult other) {
		return (-1) * Double.compare(score, other.score);
	}

}
