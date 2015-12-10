package utils.aws;

import indexer.db.imports.DocumentIDsAdapter;
import indexer.db.imports.FileToDatabaseAdapter;
import indexer.db.imports.InvertedIndexAdapter;

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

import db.wrappers.DynamoDBWrapper;

public class S3ToDynamoImporter<T> implements Runnable {
	private static final Logger logger = Logger.getLogger(S3ToDynamoImporter.class);
	private static final int NUMBER_THREADS = 4;

	private File input;
	private int batchSize;
	private DynamoDBMapper db;
	private FileToDatabaseAdapter<T> adapter;

	public S3ToDynamoImporter(File input, int batchSize, DynamoDBMapper db,
			FileToDatabaseAdapter<T> adapter) {
		this.input = input;
		this.batchSize = batchSize;
		this.db = db;
		this.adapter = adapter;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(input));
			String line = null;

			List<T> entries = new ArrayList<>();
			int importedLines = 0;
			while ((line = in.readLine()) != null) {
				entries.add(adapter.unserialize(line));

				if (entries.size() >= batchSize) {
					logger.info("saving " + batchSize
							+ " items to " + adapter.getTableName() + " table.");
					db.batchSave(entries);
					entries = new ArrayList<>();
				}
				++importedLines;
			}
			db.batchSave(entries);
			logger.info("Done with file " + input + ". Saved " + importedLines
					+ " rows in table " + adapter.getTableName() + ".");
			in.close();
		} catch (FileNotFoundException e) {
			logger.error("file " + this.input.getName() + " not found: ", e);
		} catch (Exception e) {
			logger.error("error while reading file " + this.input.getName()
					+ ": ", e);
		}
	}

	private static <T> void doImport(File inputDir, int batchSize,
			FileToDatabaseAdapter<T> adapter) throws InterruptedException {
		DynamoDBWrapper wrapper = DynamoDBWrapper
				.getInstance(DynamoDBWrapper.US_EAST);
//		if (wrapper.describeTable(adapter.getTableName()) == null)
//			wrapper.createTable(adapter.getTableName(), 5, NUMBER_THREADS * 100, "docId", "N");
//		TableDescription tableInfo = wrapper.getDynamoDB()
//				.getTable(adapter.getTableName()).describe();
//		if (tableInfo.getProvisionedThroughput().getWriteCapacityUnits() != 100L)
//			wrapper.getClient().updateTable(adapter.getTableName(),
//					new ProvisionedThroughput(5L, 100L));

		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
		for (File file : inputDir.listFiles()) {
			logger.info("starting import on file " + file.getName());
			executor.execute(new S3ToDynamoImporter<T>(file, batchSize, wrapper
					.getMapper(), adapter));
		}
		executor.shutdown();
		executor.awaitTermination(8, TimeUnit.HOURS);

		int nrRows = wrapper.getNumberOfItemsInTable(adapter.getTableName());
		logger.info("imported a total of " + nrRows + " into table "
				+ adapter.getTableName());

//		tableInfo = wrapper.getDynamoDB().getTable(adapter.getTableName())
//				.describe();
//		if (tableInfo.getProvisionedThroughput().getWriteCapacityUnits() != 5L)
//			wrapper.getClient().updateTable(adapter.getTableName(),
//					new ProvisionedThroughput(5L, 5L));
	}

	private static void usage() {
		System.out.println("usage: DynamoImporter <table-to-import> <inputdir> <batch-size>\n");
	}

	public static void main(String[] args) {
		try {
			String what = args[0];
			File inputDir = new File(args[1]);
			int batchSize = Integer.parseInt(args[2]);

			if (what.equals("DocumentIDs")) {
				logger.info("importing from files into Dynamo's DocumentIDs table...");
				doImport(inputDir, batchSize, new DocumentIDsAdapter());
			} else if (what.equals("InvertedIndex")) {
				logger.info("importing from files into Dynamo's InvertedIndex table...");
				doImport(inputDir, batchSize, new InvertedIndexAdapter());
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			usage();
			System.exit(1);
		} catch (InterruptedException e) {
			logger.error("execution of tasks was interrupted:", e);
		}
	}
}
