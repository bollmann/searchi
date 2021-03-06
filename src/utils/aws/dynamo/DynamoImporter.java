package utils.aws.dynamo;


import indexer.db.dao.DocumentIndex;
import indexer.db.dao.ImageIndex;
import indexer.db.dao.InvertedIndex;
import indexer.db.imports.adapters.DocumentIndexAdapter;
import indexer.db.imports.adapters.FileToDatabaseAdapter;
import indexer.db.imports.adapters.InvertedIndexAdapter;
import indexer.db.imports.adapters.ImageIndexAdapter;

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

import pagerank.db.adapters.DRAdapter;
import pagerank.db.adapters.PRAdapter;
import pagerank.db.ddl.PRCreateTable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

import db.wrappers.DynamoDBWrapper;

public class DynamoImporter<T> implements Runnable {
	private static final Logger logger = Logger.getLogger(DynamoImporter.class);
	private static int NUMBER_THREADS = 3;
	private static Integer importedRows = 0;
	
	private File input;
	private int batchSize;
	private DynamoDBMapper db;
	private FileToDatabaseAdapter<T> adapter;

	
	
	public DynamoImporter(File input, int batchSize, DynamoDBMapper db,
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
			int importedFileRows = 0;
			while ((line = in.readLine()) != null) {
				entries.add(adapter.unserialize(line));

				if (entries.size() >= batchSize) {
					logger.info("saving " + batchSize
							+ " items to " + adapter.getTableName() + " table.");
					
					db.batchSave(entries);
					entries = new ArrayList<>();
				}
				++importedFileRows;
			}
			
			db.batchSave(entries);
			logger.info("Done with file " + input + ". Saved " + importedFileRows
					+ " rows in table " + adapter.getTableName() + ".");
			in.close();

			synchronized(importedRows) {
				importedRows += importedFileRows;
			}
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
		ExecutorService executor = Executors.newFixedThreadPool(NUMBER_THREADS);
		for (File file : inputDir.listFiles()) {
			logger.info("starting import on file " + file.getName());
			executor.execute(new DynamoImporter<T>(file, batchSize, wrapper
					.getMapper(), adapter));
		}
		executor.shutdown();
		executor.awaitTermination(8, TimeUnit.HOURS);

		logger.info("Done. Imported a total of " + importedRows + " into table "
				+ adapter.getTableName());
	}

	private static void usage() {
		System.out.println("usage: DynamoImporter <table-to-import> <inputdir> <batch-size> [<number-threads>]\n");
	}

	public static void main(String[] args) {
		try {
			String what = args[0];
			File inputDir = new File(args[1]);
			int batchSize = Integer.parseInt(args[2]);
			if(args.length == 4)
				NUMBER_THREADS = Integer.parseInt(args[3]);
			
			logger.info("importing files from " + inputDir + " into table " + what + "...");

			if (what.equals(DocumentIndex.TABLE_NAME)) {
				doImport(inputDir, batchSize, new DocumentIndexAdapter());
			} else if (what.equals(InvertedIndex.TABLE_NAME)) {
				doImport(inputDir, batchSize, new InvertedIndexAdapter());
			} else if (what.equals(ImageIndex.TABLE_NAME)) {
				doImport(inputDir, batchSize, new ImageIndexAdapter());
			} else if (what.equals(PRCreateTable.DR_TABLE_NAME)) {
				doImport(inputDir, batchSize, new DRAdapter());
				
			} else if (what.equals(PRCreateTable.PR_TABLE_NAME)) {
				doImport(inputDir, batchSize, new PRAdapter());
			} else {
				logger.info("table " + what + " is not supported by DynamoImporter!");
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			usage();
			System.exit(1);
		} catch (InterruptedException e) {
			logger.error("execution of tasks was interrupted:", e);
		}
	}
}
