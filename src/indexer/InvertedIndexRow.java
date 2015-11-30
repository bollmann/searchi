package indexer;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/*
 * Models an Item (row) from the Inverted Index.
 */
@DynamoDBTable(tableName=InvertedIndex.TABLE_NAME)
public class InvertedIndexRow {
	private String word;
	private String url;
	private int wordCount;
	private double maximumTermFrequency;
	private double euclideanTermFrequency;
	private int linkCount;
	private int metaTagCount;
	private int headerCount;
	
	@DynamoDBHashKey(attributeName="word")
	public String getWord() { return word; }
	public void setWord(String w) { word = w; }
	
	@DynamoDBRangeKey(attributeName="url")
	public String getUrl() { return url; }
	public void setUrl(String pid) { url = pid; }
	
	@DynamoDBAttribute(attributeName="wordcount")
	public Integer getWordCount() { return wordCount; }
	public void setWordCount(Integer wc) { wordCount = wc; }

	@DynamoDBAttribute(attributeName="maxtf")
	public double getMaximumTermFrequency() { return maximumTermFrequency; }
	public void setMaximumTermFrequency(double tf) {
		this.maximumTermFrequency = tf;
	}
	
	@DynamoDBAttribute(attributeName="euclidtf")
	public double getEuclideanTermFrequency() {
		return euclideanTermFrequency;
	}
	public void setEuclideanTermFrequency(double euclideanTermFrequency) {
		this.euclideanTermFrequency = euclideanTermFrequency;
	}
	
	@DynamoDBAttribute(attributeName="linkcount")
	public int getLinkCount() {
		return linkCount;
	}
	public void setLinkCount(int linkCount) {
		this.linkCount = linkCount;
	}
	
	@DynamoDBAttribute(attributeName="metatagcount")
	public int getMetaTagCount() {
		return metaTagCount;
	}
	public void setMetaTagCount(int metaTagCount) {
		this.metaTagCount = metaTagCount;
	}
	
	@DynamoDBAttribute(attributeName="headercount")
	public int getHeaderCount() {
		return headerCount;
	}
	public void setHeaderCount(int headerCount) {
		this.headerCount = headerCount;
	}
}
