package indexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

public class InvertedIndex {
	private static Logger logger = Logger.getLogger(InvertedIndex.class);

	public static final String CREDENTIALS_PROFILE = "dominik";
	public static final String TABLE_NAME = "invertedIndex";
	
	private DynamoDBMapper db;
	
	public InvertedIndex() {
		AWSCredentials credentials = new ProfileCredentialsProvider(CREDENTIALS_PROFILE).getCredentials();
		AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentials);
		dbClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		
		this.db = new DynamoDBMapper(dbClient);
		// TODO: obtain the real corpus size here at the beginning once!
	}
	
	public List<InvertedIndexItem> getEntries(String word) {
		InvertedIndexItem item = new InvertedIndexItem();
		item.setWord(word);
		
		DynamoDBQueryExpression<InvertedIndexItem> query = new DynamoDBQueryExpression<InvertedIndexItem>()
				.withHashKeyValues(item);
		return db.query(InvertedIndexItem.class, query);
	}
	
	public List<InvertedIndexItem> getAllEntries(List<String> words) {
		List<Object> items = new LinkedList<Object>();
		for(String word: words) {
			InvertedIndexItem item = new InvertedIndexItem();
			item.setWord(word);
			items.add(item);
		}
		return (List<InvertedIndexItem>)(List<?>) db.batchLoad(items).get(TABLE_NAME);
	}
	
	public void importData(String fromFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(fromFile)));
		String line = null;
		List<InvertedIndexItem> items = new LinkedList<InvertedIndexItem>();
		
		int rowCount = 0;
		while ((line = br.readLine()) != null) {
			++rowCount;
			try {
				String parts[] = line.split("\t");
				InvertedIndexItem item = new InvertedIndexItem();
				item.setWord(parts[0]);
				item.setUrl(parts[1]);
				item.setWordCount(Integer.parseInt(parts[2]));
				item.setTf(Double.parseDouble(parts[3]));
			
				items.add(item);
				if(items.size() >= 5000) {
					this.db.batchSave(items);
					items = new LinkedList<InvertedIndexItem>();
					logger.info(String.format("imported %d records into DynamoDB's 'inverted-index' table.", rowCount));
				}
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
				logger.error(String.format("importing inverted index row '%s' failed.", line), e);
			}
		}
		this.db.batchSave(items);
		br.close();
	}
	
	public TreeSet<DocumentVector> lookupDocuments(List<String> query) {
		// TODO: query URLMetaInfo dynamoDB table for corpus size??
		int corpusSize = 4000;
		
		List<InvertedIndexItem> candidates = new LinkedList<InvertedIndexItem>();
		Map<String, Integer> dfs = new HashMap<String, Integer>();
		for(String word: query) {
			// TODO: optimize based on different table layout, multi-thread requests, etc.
			List<InvertedIndexItem> wordCandidates = getEntries(word);
			candidates.addAll(wordCandidates);
			dfs.put(word, wordCandidates.size());
			logger.info(String.format("=> got %d documents for query word '%s'.", wordCandidates.size(), word));
		}
		
		// build TF-IDF information
		Map<String, Map<String, Double>> docs = new HashMap<String, Map<String, Double>>();
		for(InvertedIndexItem candidate: candidates) {
			Map<String, Double> doc = docs.get(candidate.getUrl());
			
			if(doc == null) {
				doc = new HashMap<String, Double>();
				docs.put(candidate.getUrl(), doc);
			}
			
			double idf = Math.log(corpusSize / dfs.get(candidate.getWord()));
			doc.put(candidate.getWord(), candidate.getTf() * idf);
		}
		
		logger.info(String.format("extracted TF-IDF info for %d documents", docs.size()));
		
		Map<String, Double> queryTFIDFs = TFIDF.computeTFIDFs(query, corpusSize, dfs);
		DocumentVector queryVec = new DocumentVector(queryTFIDFs);
		return createDocumentVectors(docs, queryVec);
	}
	
	private TreeSet<DocumentVector> createDocumentVectors(Map<String, Map<String, Double>> docs, 
			DocumentVector queryVector) {
		TreeSet<DocumentVector> set = new TreeSet<DocumentVector>();
		for(String doc: docs.keySet()) {
			DocumentVector docVec = new DocumentVector(docs.get(doc));
			docVec.setUrl(doc);
			docVec.setSimilarity(DocumentVector.cosineSimilarity(docVec, queryVector));
			set.add(docVec);
		}
		return set;
	}
	
	public static void main(String[] args) {
		try {
			InvertedIndex idx = new InvertedIndex();
			
			if(args[0].equals("import")) {
				idx.importData(args[1]);
			} else if(args[0].equals("query")) {
				List<String> query = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
				TreeSet<DocumentVector> results = idx.lookupDocuments(query);
				
				Iterator<DocumentVector> iter = results.descendingIterator();
				for(int i = 0; i < 10 && iter.hasNext(); ++i) {
					DocumentVector doc = iter.next();
					System.out.println(doc.toString());
				}
			} else {
				System.out.println("usage: InvertedIndex import <fromdir>");
				System.out.println("       InvertedIndex query <word1> <word2> ... <wordN>");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
