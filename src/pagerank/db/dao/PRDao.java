package pagerank.db.dao;

import pagerank.db.ddl.PRCreateTable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName=PRCreateTable.PR_TABLE_NAME)
public class PRDao implements Comparable<PRDao> {
	
	private String page;
	private double pageScore;
		
	public PRDao() { }
	public PRDao(String p, double score) {
		this.page = p;
		this.pageScore = score;
	}
	
	
	@DynamoDBHashKey(attributeName="Page")
	public String getPage() { 
		return page;
	}
	
	public void setPage(String pg) { 
		page = pg;
	}

	@DynamoDBAttribute(attributeName="PageScore")
	public double getPageScore() {
		return pageScore;		
	}
	
	public void setPageScore(double score) {
		pageScore = score;		
	}	
		
	@DynamoDBIgnore
	@Override
	public int compareTo(PRDao o) {
		return (-1)*Double.compare(this.pageScore, o.pageScore);
	}
	
	@DynamoDBIgnore
	@Override
	public String toString() {
		return String.format("Page: %s has score=%f", page, pageScore);
	}
}
