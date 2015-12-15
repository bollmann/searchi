package pagerank.db.dao;

import pagerank.db.ddl.PRCreateTable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIgnore;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName=PRCreateTable.DR_TABLE_NAME)
public class DRDao implements Comparable<DRDao> {
	
	private String domain;
	private double domainScore;
	
	public DRDao() { }
	public DRDao(String dom, double score) {
		this.domain = dom;
		this.domainScore = score;
	}
	
	@DynamoDBHashKey(attributeName="Domain")
	public String getDomain() { 
		return domain;
	}
	
	public void setDomain(String dom) { 
		domain = dom;
	}

	@DynamoDBAttribute(attributeName="DomainScore")
	public double getDomainScore() {
		return domainScore;		
	}
	
	public void setDomainScore(double score) {
		domainScore = score;		
	}	
		
	@DynamoDBIgnore
	@Override
	public int compareTo(DRDao o) {
		return (-1)*Double.compare(this.domainScore, o.domainScore);
	}
	
	@DynamoDBIgnore
	@Override
	public String toString() {
		return String.format("Domain: %s has score=%f", domain, domainScore);
	}
}
