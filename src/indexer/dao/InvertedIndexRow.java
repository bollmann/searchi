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
	@DynamoDBIgnore
	private ArrayList<DocumentFeatures> features;
	private String something;

	@DynamoDBAttribute(attributeName="something")
	public String getSomething() {
		return something;
	}

	public void setSomething(String something) {
		this.something = something;
	}

	public InvertedIndexRow() {
		features = new ArrayList<DocumentFeatures>();
	}
	
	public InvertedIndexRow(String word, int page, ArrayList<DocumentFeatures> features) {
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
	
//	@DynamoDBAttribute(attributeName="features")
//	@DynamoDBMarshalling(marshallerClass=DocumentFeaturesMarshaller.class)
	public ArrayList<DocumentFeatures> getFeatures() {
		return features;
	}
	public void setFeatures(ArrayList<DocumentFeatures> features) {
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