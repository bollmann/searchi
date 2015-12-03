package indexer;

import indexer.dao.DocumentFeatures;
import indexer.dao.InvertedIndexRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

import db.wrappers.S3Wrapper;

public class InvertedIndex {
	private static Logger logger = Logger.getLogger(InvertedIndex.class);

	public static final String CREDENTIALS_PROFILE = "default";
	public static final String TABLE_NAME = "InvertedIndex";
	public static final String S3_CRAWL_SNAPSHOT = "cis455-url-content-snapshot5";
	
	private DynamoDBMapper db;
	private int corpusSize;
	
	public InvertedIndex() {
		this.db = connectDB();
		S3Wrapper s3 = S3Wrapper.getInstance();
		this.corpusSize = s3.getNumberOfItemsInBucket(S3_CRAWL_SNAPSHOT);
	}
	
	public static DynamoDBMapper connectDB() {
		AWSCredentials credentials = new ProfileCredentialsProvider(CREDENTIALS_PROFILE).getCredentials();
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
	
//	public static void importData(String fromFile, int batchSize) throws IOException {
//		DynamoDBMapper db = connectDB();
//		BufferedReader br = new BufferedReader(new FileReader(new File(fromFile)));
//		String line = null;
//		List<InvertedIndexRow> items = new LinkedList<InvertedIndexRow>();
//		
//		while ((line = br.readLine()) != null) {
//			try {
//				String parts[] = line.split("\t");
//				InvertedIndexRow item = new InvertedIndexRow();
//				item.setWord(parts[0]);
//				item.setUrl(parts[1]);
//				item.setMaximumTermFrequency(Double.parseDouble(parts[2]));
//				item.setEuclideanTermFrequency(Double.parseDouble(parts[3]));
//				item.setWordCount(Integer.parseInt(parts[4]));
//				item.setLinkCount(Integer.parseInt(parts[5]));
//				item.setMetaTagCount(Integer.parseInt(parts[6]));
//				item.setHeaderCount(Integer.parseInt(parts[7]));
//				
//				items.add(item);
//				if(items.size() >= batchSize) {
//					db.batchSave(items);
//					logger.info(String.format("imported %d records into DynamoDB's 'inverted-index' table.", items.size()));
//
//					items = new LinkedList<InvertedIndexRow>();
//				}
//			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
//				logger.error(String.format("importing inverted index row '%s' failed.", line), e);
//			}
//		}
//		db.batchSave(items);
//		br.close();
//	}
	
	public PriorityQueue<DocumentScore> rankDocuments(List<String> query) {
		WordCounts queryCounts = new WordCounts(query);
		Map<String, DocumentScore> documentRanks = new HashMap<String, DocumentScore>();
		for(String word: query) {
			// TODO: optimize based on different table layout, multi-thread requests, etc.
			List<InvertedIndexRow> rows = getDocumentLocations(word);
			Set<DocumentFeatures> docs = new HashSet<DocumentFeatures>();
			for(InvertedIndexRow row: rows)
				docs.addAll(row.getFeatures());

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
			logger.info(String.format("=> got %d documents for query word '%s'.", rows.size(), word));
		}
		return new PriorityQueue<DocumentScore>(documentRanks.values());
	}
	
//	public PriorityQueue<DocumentVector> lookupDocuments(List<String> query) {
//		List<InvertedIndexRow> candidates = new LinkedList<InvertedIndexRow>();
//		Map<String, Integer> dfs = new HashMap<String, Integer>();
//		for(String word: query) {
//			List<InvertedIndexRow> wordCandidates = getDocumentLocations(word);
//			candidates.addAll(wordCandidates);
//			dfs.put(word, wordCandidates.size()); 
//			logger.info(String.format("=> got %d documents for query word '%s'.", wordCandidates.size(), word));
//		}
//		
//		// build candidate document vectors
//		Map<String, Map<String, Double>> docs = new HashMap<String, Map<String, Double>>();
//		for(InvertedIndexRow candidate: candidates) {
//			Map<String, Double> doc = docs.get(candidate.getUrl());
//			if(doc == null) {
//				doc = new HashMap<String, Double>();
//				docs.put(candidate.getUrl(), doc);
//			}
//			doc.put(candidate.getWord(), candidate.getEuclideanTermFrequency());
//		}
//		
//		// compute document similarity
//		DocumentVector queryVector = getQueryVector(query, corpusSize, dfs);
//		PriorityQueue<DocumentVector> ranks = new PriorityQueue<>();
//		for(String doc: docs.keySet()) {
//			DocumentVector docVec = new DocumentVector(docs.get(doc));
//			docVec.setUrl(doc);
//			docVec.setSimilarity(DocumentVector.cosineSimilarity(docVec, queryVector));
//			ranks.add(docVec);
//		}
//		return ranks;
//	}
//	
//	private DocumentVector getQueryVector(List<String> query, int corpusSize, Map<String, Integer> dfs) {
//		WordCounts queryCounts = new WordCounts(query);
//		Map<String, Double> queryVector = new HashMap<String, Double>();
//		for(String queryWord: queryCounts) {
//			// FIXME: what do we do, if the queryWord is not found in the corpus at all?
//			// i.e., it is an 'UNK' word to the corpus?
//			double idf = Math.log((double) corpusSize / dfs.get(queryWord));
//			queryVector.put(queryWord, queryCounts.getMaximumTermFrequency(queryWord) * idf);
//		}
//		return new DocumentVector(queryVector);
//	}
	
	public static void main(String[] args) {
		try {			
			if(args[0].equals("import")) {
				int batchSize = Integer.parseInt(args[2]);
				System.out.println("importing with batchSize " + batchSize + "...");
				System.out.println("DEPRECATED! Is done by the InvertedIndexJob now!");
				//InvertedIndex.importData(args[1], Integer.parseInt(args[2]));
			} else if(args[0].equals("query")) {
				InvertedIndex idx = new InvertedIndex();

				List<String> query = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
				System.out.println("querying for words " + query + "...");

				PriorityQueue<DocumentScore> newResults = idx.rankDocuments(query);
				
				Iterator<DocumentScore> iter = newResults.iterator();
				for(int i = 0; i < 10 && iter.hasNext(); ++i) {
					DocumentScore doc = iter.next();
					System.out.println(doc.toString());
				}
				
//				System.out.println("============");
//				System.out.println("old results:");
//				System.out.println("============");
//				PriorityQueue<DocumentVector> oldResults = idx.lookupDocuments(query);
//				Iterator<DocumentVector> olditer = oldResults.iterator();
//				for(int i = 0; i < 10 && olditer.hasNext(); ++i) {
//					DocumentVector doc = olditer.next();
//					System.out.println(doc.toString());
//				}
			} else {
				System.out.println("usage: InvertedIndex import <fromdir> <batchSize>");
				System.out.println("       InvertedIndex query <word1> <word2> ... <wordN>");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}