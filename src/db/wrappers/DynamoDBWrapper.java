package db.wrappers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

public class DynamoDBWrapper {
	private final Logger logger = Logger.getLogger(DynamoDBWrapper.class);
	private static DynamoDBWrapper wrapper;
	private AmazonDynamoDBClient client;
	private String endPoint;
	private DynamoDB dynamoDB;
	private DynamoDBMapper mapper;
	private long entriesWritten = 0L;
	private long entriesRead = 0L;
	private Date startTime;

	public static final String US_EAST = "http://dynamodb.us-east-1.amazonaws.com";
	public static final String CLIENT_PROFILE = "default";

	public DynamoDBMapper getMapper() {
		return mapper;
	}
	
	public DynamoDB getDynamoDB() {
		return dynamoDB;
	}
	
	public AmazonDynamoDBClient getClient() {
		return client;
	}

	public String getEndPoint() {
		return endPoint;
	}

	public void displaySaveStatistics() {
		Date endTime = Calendar.getInstance().getTime();
		String message = "Entries read:" + entriesRead + " entries written:"
				+ entriesWritten + " time elapsed: "
				+ (endTime.getTime() - startTime.getTime()) / 1000 + "s, "
				+ (endTime.getTime() - startTime.getTime()) % 1000 + "ms";
		System.out.println(message);
		logger.warn(message);
	}

	private DynamoDBWrapper(String endPoint, String client) {
		this.client = new AmazonDynamoDBClient(new ProfileCredentialsProvider(
				client));
		this.endPoint = endPoint;
		this.startTime = Calendar.getInstance().getTime();
		this.client.setEndpoint(this.endPoint);

		dynamoDB = new DynamoDB(this.client);
		mapper = new DynamoDBMapper(this.client);
	}

	/**
	 * Get dynamo instance with default (Shreejit's client)
	 * 
	 * @param endPoint
	 */
	public static DynamoDBWrapper getInstance(String endPoint) {
		if (wrapper != null) {
			return wrapper;
		}

		Logger.getLogger(DynamoDBWrapper.class).warn(
				"Setting endpoint to " + endPoint);

		return getInstance(endPoint, null);
	}

	/**
	 * Get dynamo instance with specified client
	 * 
	 * @param endPoint
	 */
	public static DynamoDBWrapper getInstance(String endPoint, String client) {
		if (wrapper != null) {
			return wrapper;
		}

		if (client == null) {
			Logger.getLogger(DynamoDBWrapper.class).warn(
					"Setting client to " + CLIENT_PROFILE);
			client = CLIENT_PROFILE;
		}

		wrapper = new DynamoDBWrapper(endPoint, client);
		return wrapper;
	}

	/**
	 * Describe the meta properties of the specified table
	 * 
	 * @param tableName
	 * @return
	 */
	public DescribeTableResult describeTable(String tableName) {
		DescribeTableResult result = null;
		try {
			result = client.describeTable(tableName);
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Retrieves the object from dynamodb as identified by the itemId. This
	 * assumes that you have annotated the class that you want to retrieve with
	 * this get method with the appropriate dynamodb annotations. The class also
	 * SHOULD have a default constructor, otherwise it cannot be instantiated by
	 * dynamodb mapper
	 * 
	 * @param itemId
	 *            is the record identifier
	 * @param clazz
	 *            the dynamodb annotated record class
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getItem(String itemId, Class clazz) {
		Object obj = mapper.load(clazz, itemId);
		entriesRead++;
		return obj;
	}

	public Map<String, List<Object>> getBatchItem(List<Object> itemIds) {
		entriesRead += itemIds.size();
		return mapper.batchLoad(itemIds);
	}

	/**
	 * @param tableName
	 * @param toSave
	 *            this assumes that you have annotated the class object with the
	 *            appropriate annotations that allows the mapper to make object
	 *            bindings
	 */
	public void putItem(Object toSave) {
		entriesWritten++;
		mapper.save(toSave);
	}

	public void putItemBatch(List<Object> toSave) {
		entriesWritten += toSave.size();
		mapper.batchSave(toSave);
	}

	public void deleteTable(String tableName) {
		Table table = dynamoDB.getTable(tableName);
		try {
			logger.warn("Issuing DeleteTable request for " + tableName);
			table.delete();
			logger.warn("Waiting for " + tableName
					+ " to be deleted...this may take a while...");
			table.waitForDelete();

		} catch (Exception e) {
			System.err.println("DeleteTable request failed for " + tableName);
			System.err.println(e.getMessage());
		}
	}

	public void createTable(String tableName, long readCapacityUnits,
			long writeCapacityUnits, String partitionKeyName,
			String partitionKeyType) {

		if (describeTable(tableName) == null) {
			createTable(tableName, readCapacityUnits, writeCapacityUnits,
					partitionKeyName, partitionKeyType, null, null);
		} else {
			logger.error("DynamoDB table " + tableName
					+ " already exists! Not creating it");
		}
	}

	public void createTable(String tableName, long readCapacityUnits,
			long writeCapacityUnits, String partitionKeyName,
			String partitionKeyType, String sortKeyName, String sortKeyType) {

		try {

			ArrayList<KeySchemaElement> keySchema = new ArrayList<KeySchemaElement>();
			keySchema.add(new KeySchemaElement().withAttributeName(
					partitionKeyName).withKeyType(KeyType.HASH)); // Partition
																	// key

			ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
			attributeDefinitions.add(new AttributeDefinition()
					.withAttributeName(partitionKeyName).withAttributeType(
							partitionKeyType));

			if (sortKeyName != null) {
				keySchema.add(new KeySchemaElement().withAttributeName(
						sortKeyName).withKeyType(KeyType.RANGE)); // Sort key
				attributeDefinitions.add(new AttributeDefinition()
						.withAttributeName(sortKeyName).withAttributeType(
								sortKeyType));
			}

			CreateTableRequest request = new CreateTableRequest()
					.withTableName(tableName)
					.withKeySchema(keySchema)
					.withProvisionedThroughput(
							new ProvisionedThroughput().withReadCapacityUnits(
									readCapacityUnits).withWriteCapacityUnits(
									writeCapacityUnits));

			request.setAttributeDefinitions(attributeDefinitions);

			logger.warn("Issuing CreateTable request for " + tableName);
			Table table = dynamoDB.createTable(request);
			logger.warn("Waiting for " + tableName
					+ " to be created...this may take a while...");
			table.waitForActive();

		} catch (Exception e) {
			System.err.println("CreateTable request failed for " + tableName);
			System.err.println(e.getMessage());
		}
	}
	
	/***
	 *  Scans the table using multiple threads and segments
	 * @param tableName
	 * @param totalSegments
	 * @return ScanSegmentTask containing list of items scanned
	 */
	public List<ScanSegmentTask> scanParallel(String tableName, int totalSegments) {
		ScanSegmentTask task = null;
		
		ExecutorService executor = Executors.newFixedThreadPool(totalSegments);
		List<ScanSegmentTask> tasks = new ArrayList<>();
		for (int segment = 0; segment < totalSegments; segment++) {
			// Runnable task that will only scan one segment
			task = new ScanSegmentTask(tableName, totalSegments, segment, client);
			executor.execute(task);
			
			tasks.add(task);
		}
		
		executor.shutdown();
	    try 
	    {
	        if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
	            executor.shutdownNow();
	        }
	    } catch (InterruptedException e) {
	        executor.shutdownNow();
	        Thread.currentThread().interrupt();
	    }
	    
	    return tasks;
		
	}

	/** Get number of items in specified dynamo table */
	public Integer getNumberOfItemsInTable(String tableName) {
		Integer rowCount = 0;
		Map<String, AttributeValue> lastKeyEvaluated = null;
		do {
		    ScanRequest scanRequest = new ScanRequest()
		        .withTableName(tableName)
		        .withExclusiveStartKey(lastKeyEvaluated);

		    ScanResult result = client.scan(scanRequest);
		    for (Map<String, AttributeValue> item : result.getItems()){
//		        System.out.println((item));
//		    	item.size();
		        rowCount += 1;
		    }
		    lastKeyEvaluated = result.getLastEvaluatedKey();
		} while (lastKeyEvaluated != null);
		return rowCount;
	}

}
