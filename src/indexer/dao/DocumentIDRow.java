package indexer.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="DocumentIDs")
public class DocumentIDRow {
	private int docId;
	private String url;
	
	public DocumentIDRow() { }
	
	public DocumentIDRow(int id, String u) {
		docId = id;
		url = u;
	}
	
	@DynamoDBHashKey(attributeName="docId")
	public int getDocId() { return docId; }
	public void setDocId(int docId) { this.docId = docId; }
	
	@DynamoDBAttribute(attributeName="url")
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
}
