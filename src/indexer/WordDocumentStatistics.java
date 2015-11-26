package indexer;

import java.util.LinkedList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/*
 * Models an Item (row) from the Inverted Index.
 */
@DynamoDBTable(tableName=InvertedIndex.TABLE_NAME)
public class WordDocumentStatistics implements Comparable<WordDocumentStatistics> {
	private String word;
	private String url;
	private int wordCount;
	private double maximumTermFrequency;
	private double euclideanTermFrequency;
	private int linkCount;
	private int metaTagCount;
	private int headerCount;
	
	/*
	 * The document's cosine similarity rank wrt some query vector.
	 */
	private double rank;
	private List<String> words = new LinkedList<String>();
	
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
	
	@DynamoDBIgnore
	public double getSimilarityRank() { return rank; }
	public void setSimilarityRank(double r) { rank = r; }
	

	@DynamoDBIgnore
	@Override
	public int compareTo(WordDocumentStatistics other) {
		return (-1)*Double.compare(this.rank, other.rank);
	}
	
	@DynamoDBIgnore
	@Override
	public String toString() {
		return String.format("url: %s. cosine similarity=%f, words=%s", url, rank, words);
	}
	
	@DynamoDBIgnore
	public void addWordVector(String w) {
		words.add(w);
	}
}
