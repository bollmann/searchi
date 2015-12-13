package db.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public final class ScanSegmentTask extends Thread {

	private static final Logger logger = Logger
			.getLogger(ScanSegmentTask.class);

	// DynamoDB table to scan
	private String tableName;

	// Total number of segments
	// Equals to total number of threads scanning the table in parallel
	private int totalSegments;

	// Segment that will be scanned with by this task
	private int segment;

	List<Map<String, AttributeValue>> attrList;

	private AmazonDynamoDBClient client;
	 
	public ScanSegmentTask(String tableName, int totalSegments,
			int segment, AmazonDynamoDBClient client) {
		this.tableName = tableName;		
		this.totalSegments = totalSegments;
		this.segment = segment;
		this.client = client;
		this.attrList = new ArrayList<>();
	}

	@Override
	public void run() {
		logger.debug("Scanning " + tableName + " segment " + segment
				+ " out of " + totalSegments + " segments ");
		scan();
	}

	public void scan() {

		Map<String, AttributeValue> exclusiveStartKey = null;
		
		while (true) {
			ScanRequest scanRequest = new ScanRequest()
				.withTableName(tableName)
				.withExclusiveStartKey(exclusiveStartKey)
				.withTotalSegments(totalSegments).withSegment(segment);

			ScanResult result = client.scan(scanRequest);
			attrList.addAll(result.getItems());

			exclusiveStartKey = result.getLastEvaluatedKey();
			if (exclusiveStartKey == null) {
				break;
			}
		}
	}
	
	public List<Map<String, AttributeValue>> getItems() {
		return attrList;
	}
}