package db.dbo;

import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="URLMetaInfo")
public class URLMetaInfo {
	
	private String url;
	private Set<String> outgoingURLs;
	private String type;
	private Integer size;
	
	/**
	 * Do not delete. DynamoDBMapper needs this
	 */
	public URLMetaInfo() {
		
	}
	
	public URLMetaInfo(String url) {
		this.url = url;
	}
	
	@DynamoDBHashKey(attributeName="url")
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@DynamoDBAttribute(attributeName="outgoingUrls")
	public Set<String> getOutgoingURLs() {
		return outgoingURLs;
	}
	public void setOutgoingURLs(Set<String> outgoingURLs) {
		this.outgoingURLs = outgoingURLs;
	}
	
	@DynamoDBAttribute(attributeName="type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@DynamoDBAttribute(attributeName="size")
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}

}
