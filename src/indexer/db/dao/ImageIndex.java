package indexer.db.dao;

import java.util.Set;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName="ImageIndex")
public class ImageIndex {
	@DynamoDBHashKey
	private String imageWord;
	@DynamoDBAttribute
	private Set<String> imageUrls;
	
	public String getImageWord() {
		return imageWord;
	}
	public void setImageWord(String imageWord) {
		this.imageWord = imageWord;
	}
	public Set<String> getImageUrls() {
		return imageUrls;
	}
	public void setImageUrls(Set<String> urls) {
		this.imageUrls = urls;
	}
}
