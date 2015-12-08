package indexer;

import indexer.dao.DocumentFeatures;
import indexer.dao.InvertedIndexRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
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

	public static DynamoDBMapper connectDB() {
		AWSCredentials credentials = new ProfileCredentialsProvider(
				CREDENTIALS_PROFILE).getCredentials();

		AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentials);
		dbClient.setRegion(Region.getRegion(Regions.US_EAST_1));

		return new DynamoDBMapper(dbClient);
	}

	public List<InvertedIndexRow> getDocumentLocations(String word) {
		InvertedIndexRow item = new InvertedIndexRow();
		item.setWord(word);

		DynamoDBQueryExpression<InvertedIndexRow> query = new DynamoDBQueryExpression<InvertedIndexRow>()
				.withHashKeyValues(item);
		return db.query(InvertedIndexRow.class, query);
	}

	public static void importData(String fromFile, int batchSize)
			throws IOException {
		DynamoDBMapper db = connectDB();
		BufferedReader br = new BufferedReader(new FileReader(new File(fromFile)));
		String line = null;
		List<InvertedIndexRow> rows = new LinkedList<InvertedIndexRow>();
		
		Gson gson = new Gson();
		while ((line = br.readLine()) != null) {
			InvertedIndexRow row = gson.fromJson(line, InvertedIndexRow.class);
			rows.add(row);
			if (rows.size() >= batchSize) {
				db.batchSave(rows);
				logger.info(String.format("imported %d records into DynamoDB's 'inverted-index' table.",
								rows.size()));

				rows = new LinkedList<InvertedIndexRow>();
			}
		}
		db.batchSave(rows);
		br.close();
	}

	public Map<String, InvertedIndexRow> getInvertedIndexForQuery(
			List<String> query) {
		Map<String, InvertedIndexRow> wordDocumentInfoMap = new HashMap<String, InvertedIndexRow>();

		for (String word: query) {
			// TODO: optimize based on different table layout, multi-thread
			// requests, etc.
			List<InvertedIndexRow> rows = getDocumentLocations(word);
			for (InvertedIndexRow row : rows) {
				wordDocumentInfoMap.put(word, row);
			}
			// all urls of a word have been processed here

			logger.info(String.format(
					"=> got %d documents for query word '%s'.", rows.size(),
					word));
		}
		return wordDocumentInfoMap;
	}

	public List<DocumentFeatures> getDocumentsForWord(String word) {
		List<InvertedIndexRow> rows = getDocumentLocations(word);
		List<DocumentFeatures> docs = new ArrayList<DocumentFeatures>();
		for(InvertedIndexRow row: rows) {
			logger.info("row features: " + row.getFeatures());
			docs.addAll(row.getFeatures());
		}
		return docs;
	}
	
	public List<DocumentScore> rankDocumentsBy(List<String> query, Comparator<DocumentScore> ranking) {
		WordCounts queryCounts = new WordCounts(query);
		Map<String, DocumentScore> documentRanks = new HashMap<String, DocumentScore>();

		for (String word: query) {
			// TODO: optimize based on different table
			// layout, multi-thread requests, etc.
			List<InvertedIndexRow> rows = getDocumentLocations(word);
			List<DocumentFeatures> docs = new ArrayList<DocumentFeatures>();
			for(InvertedIndexRow row: rows) {
				docs.addAll(row.getFeatures());
			}

			for(DocumentFeatures features: docs) {
				DocumentScore rankedDoc = documentRanks.get(features.getUrl());
				if(rankedDoc == null) {
					rankedDoc = new DocumentScore(word, features);
					documentRanks.put(features.getUrl(), rankedDoc);
				} else {
					rankedDoc.addFeatures(word, features);
				}

				double queryWeight = queryCounts.getTFIDF(word, corpusSize, docs.size());
				double docWeight = features.getEuclideanTermFrequency(); // TODO: try other weighting functions!
				rankedDoc.setRank(rankedDoc.getRank() + queryWeight * docWeight);
			}
			logger.info(String.format(
					"=> got %d rows for query word '%s'.", rows.size(),
					word));
		}
		List<DocumentScore> results = new ArrayList<DocumentScore>(documentRanks.values());
		Collections.sort(results, ranking);
		return results;
	}
	
	public List<DocumentScore> rankDocuments(List<String> query) {
		Comparator<DocumentScore> ranking = new Comparator<DocumentScore>() {
			@Override
			public int compare(DocumentScore s1, DocumentScore s2) {
				Map<String, DocumentFeatures> s1Words = s1.getWordFeatures();
				Map<String, DocumentFeatures> s2Words = s2.getWordFeatures();
				
				if (s1Words.size() > s2Words.size())
					return -1;
				else if (s1Words.size() < s2Words.size())
					return 1;
				else 
					return (-1) * Double.compare(s1.getRank(), s2.getRank());
			}
		};
		return rankDocumentsBy(query, ranking);
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

				List<DocumentScore> newResults = idx.rankDocuments(query);

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