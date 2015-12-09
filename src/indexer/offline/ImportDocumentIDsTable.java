package indexer.offline;

import indexer.dao.DocumentIDEntry;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.google.gson.Gson;

import crawler.dao.URLContent;
import db.wrappers.DynamoDBWrapper;


public class ImportDocumentIDsTable implements Runnable {
	private static final Logger logger = Logger.getLogger(ImportDocumentIDsTable.class);
	private static final int NUMBER_THREADS = 4;
	public static final String DOCUMENT_ID_TABLE = "DocumentIDs";
	
	private File input;
	private int batchSize;
	private DynamoDBMapper db;
	
	public ImportDocumentIDsTable(File input, int batchSize, DynamoDBMapper db) {
		this.input = input;
		this.batchSize = batchSize;
		this.db = db;
	}
	
	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(input));
			String line = null;
			
			List<DocumentIDEntry> entries = new ArrayList<>();
			int importedLines = 0;
			while( (line = in.readLine()) != null) {
				String parts[] = line.split("\t");
				int docId = Integer.parseInt(parts[0]);
				URLContent page = new Gson().fromJson(parts[1], URLContent.class);
				
				entries.add(new DocumentIDEntry(docId, page.getUrl()));
				
				if(entries.size() >= batchSize) {
					logger.info("saving " + batchSize + " items to DocumentIDs table.");
					db.batchSave(entries);
					entries = new ArrayList<>();
				}
				++importedLines;
			}
			logger.info("Done. Saved " + importedLines + " rows in table " + DOCUMENT_ID_TABLE + ".");
			in.close();
		} catch (FileNotFoundException e) {
			logger.error("file " + this.input.getName() + " not found: ", e);
		} catch (Exception e) {
			logger.error("error while reading file " + this.input.getName() + ": ", e);
		}
	}
	
	public static void usage() {
		System.out.println("usage: ImportDocumentIDsTable <inputdir> <batch-size>\n");
	}
	
	public static void main(String[] args) {
		try {
			File inputDir = new File(args[0]);
			int batchSize = Integer.parseInt(args[1]);
			
			DynamoDBWrapper wrapper = DynamoDBWrapper.getInstance(DynamoDBWrapper.US_EAST);
			if(wrapper.describeTable(DOCUMENT_ID_TABLE) == null)
				wrapper.createTable(DOCUMENT_ID_TABLE, 5, 100, "docId", "N");
			wrapper.getClient().updateTable(DOCUMENT_ID_TABLE, new ProvisionedThroughput(5L, 5L));
			
			ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
			for(File file: inputDir.listFiles()) {
				logger.info("starting import on file " + file.getName());
				executor.execute(new ImportDocumentIDsTable(file, batchSize, wrapper.getMapper()));
			}
			
			executor.shutdown();
			executor.awaitTermination(1, TimeUnit.HOURS);
			
			wrapper.getClient().updateTable(DOCUMENT_ID_TABLE, new ProvisionedThroughput(5L, 5L));
		} catch(ArrayIndexOutOfBoundsException e) {
			usage();
			System.exit(1);
		} catch (InterruptedException e) {
			logger.error("execution of tasks was interrupted:", e);
		}
	}
}
