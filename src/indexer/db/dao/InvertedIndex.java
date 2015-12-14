package indexer.db.dao;


import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * Models an Item (row) from the Inverted Index.
 */
@DynamoDBTable(tableName=InvertedIndex.TABLE_NAME)
public class InvertedIndex {
	@DynamoDBIgnore
	public static final String TABLE_NAME = "InvertedIndex";
	
	@DynamoDBHashKey
	private String word;
	@DynamoDBRangeKey
	private int page;
	@DynamoDBAttribute
	@DynamoDBMarshalling(marshallerClass=DocumentFeaturesMarshaller.class)
	private List<DocumentFeatures> features;
	
	public InvertedIndex() { }
	public InvertedIndex(String word, int page, List<DocumentFeatures> features) {
		this.word = word;
		this.page = page;
		this.features = features;
	}
	
	public String getWord() { return word; }
	public void setWord(String w) { word = w; }
	
	public int getPage() { return page; }
	public void setPage(int pid) { page = pid; }
	
	public List<DocumentFeatures> getFeatures() {
		return features;
	}
	public void setFeatures(List<DocumentFeatures> features) {
		this.features = features;
	}
	
	public String toString() {
		return String.format("word = %s, page = %d, features = %s", word, page, features);
	}
}