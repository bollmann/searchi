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
import java.util.PriorityQueue;
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

	public static final String CREDENTIALS_PROFILE = "default";
	public static final String TABLE_NAME = "invertedIndex";
	
	private DynamoDBMapper db;
	
	public InvertedIndex() {
		AWSCredentials credentials = new ProfileCredentialsProvider(CREDENTIALS_PROFILE).getCredentials();
		AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentials);
		dbClient.setRegion(Region.getRegion(Regions.US_EAST_1));
		
		this.db = new DynamoDBMapper(dbClient);
		// TODO: obtain the real corpus size here at the beginning once!
	}
	
	public List<WordDocumentStatistics> getDocumentLocations(String word) {
		WordDocumentStatistics item = new WordDocumentStatistics();
		item.setWord(word);
		
		DynamoDBQueryExpression<WordDocumentStatistics> query = new DynamoDBQueryExpression<WordDocumentStatistics>()
				.withHashKeyValues(item);
		return db.query(WordDocumentStatistics.class, query);
	}
	
	public List<WordDocumentStatistics> getAllEntries(List<String> words) {
		List<Object> items = new LinkedList<Object>();
		for(String word: words) {
			WordDocumentStatistics item = new WordDocumentStatistics();
			item.setWord(word);
			items.add(item);
		}
		return (List<WordDocumentStatistics>)(List<?>) db.batchLoad(items).get(TABLE_NAME);
	}
	
	public void importData(String fromFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(fromFile)));
		String line = null;
		List<WordDocumentStatistics> items = new LinkedList<WordDocumentStatistics>();
		
		int rowCount = 0;
		while ((line = br.readLine()) != null) {
			++rowCount;
			try {
				String parts[] = line.split("\t");
				WordDocumentStatistics item = new WordDocumentStatistics();
				item.setWord(parts[0]);
				item.setUrl(parts[1]);
				item.setMaximumTermFrequency(Double.parseDouble(parts[2]));
				item.setEuclideanTermFrequency(Double.parseDouble(parts[3]));
				item.setWordCount(Integer.parseInt(parts[4]));
				//item.setLinkCount(Integer.parseInt(parts[5]));
				//item.setMetaTagCount(Integer.parseInt(parts[6]));
				//item.setHeaderCount(Integer.parseInt(parts[7]));
				
				items.add(item);
				if(items.size() >= 5000) {
					this.db.batchSave(items);
					items = new LinkedList<WordDocumentStatistics>();
					logger.info(String.format("imported %d records into DynamoDB's 'inverted-index' table.", rowCount));
				}
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
				logger.error(String.format("importing inverted index row '%s' failed.", line), e);
			}
		}
		this.db.batchSave(items);
		br.close();
	}
	
	public PriorityQueue<WordDocumentStatistics> rankDocuments(List<String> query) {
		// TODO: query URLMetaInfo dynamoDB table for corpus size??
		int corpusSize = 4000;
		
		Map<String, WordDocumentStatistics> ranks = new HashMap<String, WordDocumentStatistics>();
		for(String word: query) {
			// TODO: optimize based on different table layout, multi-thread requests, etc.
			List<WordDocumentStatistics> wordDocLocs = getDocumentLocations(word);
			for(WordDocumentStatistics wordDocLoc: wordDocLocs) {
				
				if(!ranks.containsKey(wordDocLoc.getUrl())) {
					wordDocLoc.setSimilarityRank(0);
					ranks.put(wordDocLoc.getUrl(), wordDocLoc);
				}
				
				WordDocumentStatistics rankedDoc = ranks.get(wordDocLoc.getUrl());
				double queryWeight = wordDocLoc.getMaximumTermFrequency() * Math.log((double) corpusSize / wordDocLocs.size());
				double docWeight = wordDocLoc.getEuclideanTermFrequency();
				rankedDoc.setSimilarityRank(rankedDoc.getSimilarityRank() + queryWeight * docWeight);
				rankedDoc.addWordVector(wordDocLoc.getWord());
			}
			logger.info(String.format("=> got %d documents for query word '%s'.", wordDocLocs.size(), word));
		}
		
		return new PriorityQueue<WordDocumentStatistics>(ranks.values());
	}
	
	public TreeSet<DocumentVector> lookupDocuments(List<String> query) {
		// TODO: query URLMetaInfo dynamoDB table for corpus size??
		int corpusSize = 4000;
		
		List<WordDocumentStatistics> candidates = new LinkedList<WordDocumentStatistics>();
		Map<String, Integer> dfs = new HashMap<String, Integer>();
		for(String word: query) {
			// TODO: optimize based on different table layout, multi-thread requests, etc.
			List<WordDocumentStatistics> wordCandidates = getDocumentLocations(word);
			candidates.addAll(wordCandidates);
			dfs.put(word, wordCandidates.size());
			logger.info(String.format("=> got %d documents for query word '%s'.", wordCandidates.size(), word));
		}
		
		// build TF-IDF information
		Map<String, Map<String, Double>> docs = new HashMap<String, Map<String, Double>>();
		for(WordDocumentStatistics candidate: candidates) {
			Map<String, Double> doc = docs.get(candidate.getUrl());
			
			if(doc == null) {
				doc = new HashMap<String, Double>();
				docs.put(candidate.getUrl(), doc);
			}
			
			doc.put(candidate.getWord(), candidate.getEuclideanTermFrequency());
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
				PriorityQueue<WordDocumentStatistics> newResults = idx.rankDocuments(query);
				
				Iterator<WordDocumentStatistics> iter = newResults.iterator();
				for(int i = 0; i < 10 && iter.hasNext(); ++i) {
					WordDocumentStatistics doc = iter.next();
					System.out.println(doc.toString());
				}
				
				System.out.println("============");
				System.out.println("old results:");
				System.out.println("============");
				TreeSet<DocumentVector> oldResults = idx.lookupDocuments(query);
				Iterator<DocumentVector> olditer = oldResults.descendingIterator();
				for(int i = 0; i < 10 && olditer.hasNext(); ++i) {
					DocumentVector doc = olditer.next();
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
