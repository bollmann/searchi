package crawler.dao;

import java.util.Date;
import java.util.List;


public class URLContent {

	String url;
	
	String content;
	
	String contentType;
	
	Date crawledOn;
	
	List<String> outgoingLinks;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String path) {
		this.url = path;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCrawledOn() {
		return crawledOn;
	}

	public void setCrawledOn(Date crawledOn) {
		this.crawledOn = crawledOn;
	}
	
	public List<String> getOutgoingLinks() {
		return outgoingLinks;
	}

	public void setOutgoingLinks(List<String> outgoingLinks) {
		this.outgoingLinks = outgoingLinks;
	}

	public URLContent(String absolutePath) {
		this.url = absolutePath;
	}
	
	public URLContent() {
		
	}

}
