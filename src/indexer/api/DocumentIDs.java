package indexer.api;

import indexer.dao.DocumentIDRow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.aws.dynamo.DynamoDBUtils;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;

/**
 * Provides an in-memory, read-only view of the DocumentIDs table.
 */
public class DocumentIDs {
	private Map<Integer, String> docIDs;
	
	public DocumentIDs() {
		 docIDs = new HashMap<Integer, String>();
		 DynamoDBMapper db = DynamoDBUtils.connectDB();
		 
		 DynamoDBScanExpression scanExpr = new DynamoDBScanExpression();
		 List<DocumentIDRow> rows = db.scan(DocumentIDRow.class, scanExpr);
		 for(DocumentIDRow row: rows) {
			 docIDs.put(row.getDocId(), row.getUrl());
		 }
	}
	
	Map<Integer, String> getDocumentIDs() {
		return docIDs;
	}
	
	public String getURLFor(int docId) {
		return docIDs.get(docId);
	}
}
