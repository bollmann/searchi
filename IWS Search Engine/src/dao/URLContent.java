package dao;

import java.util.Date;


public class URLContent {

	String absolutePath;
	
	String content;
	
	String contentType;
	
	Date crawledOn;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAbsolutePath() {
		return absolutePath;
	}

	public void setAbsolutePath(String path) {
		this.absolutePath = path;
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
	
	public URLContent(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	
	public URLContent() {
		
	}

}
