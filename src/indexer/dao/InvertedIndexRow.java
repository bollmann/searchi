package indexer.dao;

import java.util.ArrayList;
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
@DynamoDBTable(tableName="ii2")
public class InvertedIndexRow {
	private String word;
	private int page;
	private List<DocumentFeatures> features;


	public InvertedIndexRow() {
		features = new ArrayList<DocumentFeatures>();
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
	
	@DynamoDBAttribute(attributeName="features")
	@DynamoDBMarshalling(marshallerClass=DocumentFeaturesMarshaller.class)
	public List<DocumentFeatures> getFeatures() {
		return features;
	}
	public void setFeatures(List<DocumentFeatures> features) {
		this.features = features;
	}
	
	@DynamoDBIgnore
	public String toString() {
		return String.format("word = %s, page = %d, features = %s", word, page, features);
	}

//	@DynamoDBIgnore
//	public List<DocumentFeatures> getFeatures() {
//		return features;
//	}
//	
//	@DynamoDBIgnore
//	public void setFeatures(List<DocumentFeatures> fs) {
//		features = fs;
//	}
	
//	@DynamoDBAttribute(attributeName="docs")
//	public String marshallFeatures() {
//		String res = new HashSet<String>();
//		for(DocumentFeatures feature: features)
//			res.add(marshaller.marshall(feature));
//		return res;
//	}
//	@DynamoDBAttribute(attributeName="docs")
//	public void unmarshallFeatures(String rawFeatures) {
//		Class<List<DocumentFeatures>> clazz = (Class) List.class;
//		List<DocumentFeatures> features = marshaller.unmarshall(clazz, rawFeatures);
//		features = new ArrayList<DocumentFeatures>();
//		for(String rawFeature: rawFeatures)
//			features.add(marshaller.unmarshall(DocumentFeatures.class, rawFeature));
//	}
}