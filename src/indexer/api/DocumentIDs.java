package indexer.api;

import indexer.db.dao.DocumentIndex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.aws.dynamo.DynamoDBUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

/**
 * Provides an in-memory, read-only view of the DocumentIDs table.
 */
public final class DocumentIDs {
	private static DocumentIDs dId;
	
	private Map<Integer, String> docIDs;
	
	private DocumentIDs() {
		 docIDs = new HashMap<Integer, String>();
		 DynamoDBMapper db = DynamoDBUtils.connectDB();
		 
		 DynamoDBScanExpression scanExpr = new DynamoDBScanExpression();
		 List<DocumentIndex> rows = db.scan(DocumentIndex.class, scanExpr);
		 for(DocumentIndex row: rows) {
			 docIDs.put(row.getDocId(), row.getUrl());
		 }
	}
	
	public static DocumentIDs getInstance() {
		if  (dId == null) {
			dId = new DocumentIDs();			
		}
		return dId;
	}
	
	public Map<Integer, String> getDocumentIDs() {
		return docIDs;
	}
	
	public String getURLFor(int docId) {
		return docIDs.get(docId);
	}
}
