package pagerank.db.ddl;

import db.wrappers.DynamoDBWrapper;

public final class PRCreateTable {
	
	public static final String PR_TABLE_NAME = "PageRank";
	private static final String PARTITION_KEY = "Page";
	private static final String PARTITION_KEY_TYPE = "S";
	private static final int READ_CAP_UNITS = 1;
	private static final int WRITE_CAP_UNITS = 1;
	
	public static void main(String [] args) {
		
		DynamoDBWrapper dynamoWrapper = DynamoDBWrapper.getInstance(
			DynamoDBWrapper.US_EAST, DynamoDBWrapper.CLIENT_DEFAULT);
		
		dynamoWrapper.createTable(
			PR_TABLE_NAME,
			READ_CAP_UNITS,
			WRITE_CAP_UNITS,
			PARTITION_KEY,
			PARTITION_KEY_TYPE);		
	}
	
}
