package indexer;

import indexer.dao.InvertedIndexRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.google.gson.Gson;

import db.wrappers.S3Wrapper;

public class InvertedIndex {
	private static Logger logger = Logger.getLogger(InvertedIndex.class);

	public static final String CREDENTIALS_PROFILE = "default";
	public static final String TABLE_NAME = "InvertedIndex";
	public static final String S3_CRAWL_SNAPSHOT = "cis455-url-content-snapshot2";

	private DynamoDBMapper db;
	private int corpusSize;

	public InvertedIndex() {
		this.db = connectDB();
		S3Wrapper s3 = S3Wrapper.getInstance();
		// TODO make this faster
		// this.corpusSize = s3.getNumberOfItemsInBucket(S3_CRAWL_SNAPSHOT);
		this.corpusSize = 113000;
	}

	public int getCorpusSize() {
		return corpusSize;
	}

	public static DynamoDBMapper connectDB() {
		AWSCredentials credentials = new ProfileCredentialsProvider(
				CREDENTIALS_PROFILE).getCredentials();

		AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentials);
		dbClient.setRegion(Region.getRegion(Regions.US_EAST_1));

		return new DynamoDBMapper(dbClient);
	}

	public static void importData(String fromFile, int batchSize)
			throws IOException {
		DynamoDBMapper db = connectDB();
		BufferedReader br = new BufferedReader(new FileReader(
				new File(fromFile)));
		String line = null;
		List<InvertedIndexRow> rows = new LinkedList<InvertedIndexRow>();

		Gson gson = new Gson();
		while ((line = br.readLine()) != null) {
			InvertedIndexRow row = gson.fromJson(line, InvertedIndexRow.class);
			rows.add(row);
			if (rows.size() >= batchSize) {
				db.batchSave(rows);
				logger.info(String
						.format("imported %d records into DynamoDB's 'inverted-index' table.",
								rows.size()));

				rows = new LinkedList<InvertedIndexRow>();
			}
		}
		db.batchSave(rows);
		br.close();
	}

	public static void main(String[] args) {
		try {
			if (args[0].equals("import")) {
				int batchSize = Integer.parseInt(args[2]);
				System.out.println("importing with batchSize " + batchSize
						+ "...");
				InvertedIndex.importData(args[1], Integer.parseInt(args[2]));
			} else if (args[0].equals("query")) {
				InvertedIndex idx = new InvertedIndex();

				List<String> query = Arrays.asList(Arrays.copyOfRange(args, 1,
						args.length));
				System.out.println("querying for words " + query + "...");

				List<DocumentScore> newResults = null;

				Iterator<DocumentScore> iter = newResults.iterator();
				for (int i = 0; i < 10 && iter.hasNext(); ++i) {
					DocumentScore doc = iter.next();
					System.out.println(doc.toString());
				}
			} else {
				System.out
						.println("usage: InvertedIndex import <fromdir> <batchSize>");
				System.out
						.println("       InvertedIndex query <word1> <word2> ... <wordN>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}