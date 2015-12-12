package indexer.db.dao;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName=DocumentIndex.TABLE_NAME)
public class DocumentIndex {
	@DynamoDBIgnore
	public static final String TABLE_NAME = "DocumentIndex";
	
	@DynamoDBHashKey
	private int docId;
	@DynamoDBAttribute
	private String url;
	
	public DocumentIndex() { }
	
	public DocumentIndex(int id, String u) {
		docId = id;
		url = u;
	}
	
	public int getDocId() { return docId; }
	public void setDocId(int docId) { this.docId = docId; }
	
	public String getUrl() { return url; }
	public void setUrl(String url) { this.url = url; }
}
