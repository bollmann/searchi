package indexer.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.JsonMarshaller;

/**
 * Models an Item (row) from the Inverted Index.
 */
@DynamoDBTable(tableName="ii2")
public class InvertedIndexRow {
	private String word;
	private int page;
	private List<DocumentFeatures> features;
	
	private JsonMarshaller<DocumentFeatures> marshaller;

	public InvertedIndexRow() {
		marshaller = new JsonMarshaller<DocumentFeatures>();
	}
	
	public InvertedIndexRow(String word, int page, List<DocumentFeatures> features) {
		this.word = word;
		this.page = page;
		this.features = features;
	}
	
	@DynamoDBHashKey(attributeName="word")
	public String getWord() { return word; }
	public void setWord(String w) { word = w; }
	
	@DynamoDBRangeKey(attributeName="page")
	public int getPage() { return page; }
	public void setPage(int pid) { page = pid; }
	
	@DynamoDBAttribute(attributeName="docs")
	public Set<String> marshallFeatures() {
		Set<String> res = new HashSet<String>();
		for(DocumentFeatures feature: features)
			res.add(marshaller.marshall(feature));
		return res;
	}
	
	public void unmarshallFeatures(Set<String> jsonFeatures) {
		features = new ArrayList<DocumentFeatures>();
		for(String jsonFeature: jsonFeatures)
			features.add(marshaller.unmarshall(DocumentFeatures.class, jsonFeature));
	}

//	@DynamoDBAttribute(attributeName="test")
//	@DynamoDBMarshalling(marshallerClass=DocumentFeaturesMarshaller.class)
//	public DocumentFeatures getTest() { return features.get(0); }
//	public void setTest(DocumentFeatures f) { features = new ArrayList<DocumentFeatures>(); features.add(f); }

	@DynamoDBIgnore
	public String toString() {
		return String.format("word = %s, page = %d, features = %s", word, page, features);
	}

	@DynamoDBIgnore
	public List<DocumentFeatures> getFeatures() {
		return features;
	}
	
	@DynamoDBIgnore
	public void setFeatures(List<DocumentFeatures> fs) {
		features = fs;
	}
}