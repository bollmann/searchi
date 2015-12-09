package pagerank.db.ddl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import pagerank.db.dao.PRDao;
import db.wrappers.DynamoDBWrapper;

public final class PRLoadData {
	private static final Logger logger = Logger.getLogger(PRLoadData.class);
	
	public static void main(String [] args) {
		if (args.length != 1) {
			logger.error("Invalid ards: Refer to USAGE");
			usage();
			System.exit(0);
		}
		
		String filePath = args[0].trim();		
		PRLoadData loader = new PRLoadData();
		loader.load(filePath);		
	}

	/** Load the data from a file onto the db */
	public void load(String filePath) {
		if (filePath == null || filePath.isEmpty()) {
			logger.error("Empty file path. Cannot load data");
			return;
		}
		
		File file = new File(filePath);
		if (!file.exists()) {
			logger.error("No file exists by this name");
			return;
		}		
		
		DynamoDBWrapper dynamo = DynamoDBWrapper.getInstance(
				DynamoDBWrapper.US_EAST, DynamoDBWrapper.CLIENT_PROFILE);
		
		List<Object> items = new LinkedList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = "";
			int itemCount = 0;
			while ((line = br.readLine()) != null) {
				++itemCount;
				try {
					String parts[] = line.split("\t");
					PRDao item = new PRDao();
					item.setPage(parts[0].trim());
					item.setPageScore(Double.parseDouble(parts[1].trim()));
					
					items.add(item);
					if(items.size() >= 2000) {
						dynamo.putItemBatch(items);
						items = new LinkedList<>();
						logger.info(String.format("imported %d records into DynamoDB's 'PageRank' table.", itemCount));
					}
					
				} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
					logger.error(String.format("Loading page rank item '%s' failed.", line), e);
				}				
			}
			dynamo.putItemBatch(items);			
		} catch (Exception e) {
			logger.error("Couldn't load the page rank data into the table");
		}
		
	}

	/** Usage of LoadData class as a runnable unit */	
	private static void usage() {
		logger.info("USAGE:\n");
		logger.info("PRLoadData <filePath>");		
	}
}
