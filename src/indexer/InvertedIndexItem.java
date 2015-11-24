package indexer;

import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/*
 * Interface to access the inverted index.
 */
@DynamoDBTable(tableName=InvertedIndex.TABLE_NAME)
public class InvertedIndexItem {
	private String word;
	private String url;
	private Integer wordCount;
	private Double termFrequency;
	
	@DynamoDBHashKey(attributeName="word")
	public String getWord() { return word; }
	public void setWord(String w) { word = w; }
	
	@DynamoDBRangeKey(attributeName="url")
	public String getUrl() { return url; }
	public void setUrl(String pid) { url = pid; }
	
	@DynamoDBAttribute(attributeName="wordcount")
	public Integer getWordCount() { return wordCount; }
	public void setWordCount(Integer wc) { wordCount = wc; }
	
	@DynamoDBAttribute(attributeName="tf")
	public Double getTf() { return termFrequency; }
	public void setTf(Double tf) { this.termFrequency = tf; }
}
