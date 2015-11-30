package db.dbo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "QueueInfo")
public class QueueInfo {
	
	private String name = "queueState";
	private int toRead = 0;
	private int toWrite = 1;
	private int size = 0;
	
	public QueueInfo() {
		
	}
	
	@DynamoDBHashKey(attributeName = "name")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@DynamoDBAttribute(attributeName = "toRead")
	public int getToRead() {
		return toRead;
	}
	public void setToRead(int toRead) {
		this.toRead = toRead;
	}
	
	public void incrToRead() {
		this.toRead+=1;
	}
	
	@DynamoDBAttribute(attributeName = "toWrite")
	public int getToWrite() {
		return toWrite;
	}
	public void setToWrite(int toWrite) {
		this.toWrite = toWrite;
	}
	
	public void incrToWrite() {
		this.toWrite+=1;
	}
	
	@DynamoDBAttribute(attributeName = "size")
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public void incrSize() {
		this.size+=1;
	}
	
	public void decrSize() {
		this.size-=1;
	}

	public String toString() {
		return name + " toRead:" + toRead + " toWrite:" + toWrite + " size:" + size;
	}

}
