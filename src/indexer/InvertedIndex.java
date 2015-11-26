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
	
	public List<InvertedIndexRow> getDocumentLocations(String word) {
		InvertedIndexRow item = new InvertedIndexRow();
		item.setWord(word);
		
		DynamoDBQueryExpression<InvertedIndexRow> query = new DynamoDBQueryExpression<InvertedIndexRow>()
				.withHashKeyValues(item);
		return db.query(InvertedIndexRow.class, query);
	}
	
	public List<InvertedIndexRow> getAllEntries(List<String> words) {
		List<Object> items = new LinkedList<Object>();
		for(String word: words) {
			InvertedIndexRow item = new InvertedIndexRow();
			item.setWord(word);
			items.add(item);
		}
		return (List<InvertedIndexRow>)(List<?>) db.batchLoad(items).get(TABLE_NAME);
	}
	
	public void importData(String fromFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(fromFile)));
		String line = null;
		List<InvertedIndexRow> items = new LinkedList<InvertedIndexRow>();
		
		int rowCount = 0;
		while ((line = br.readLine()) != null) {
			++rowCount;
			try {
				String parts[] = line.split("\t");
				InvertedIndexRow item = new InvertedIndexRow();
				item.setWord(parts[0]);
				item.setUrl(parts[1]);
				item.setMaximumTermFrequency(Double.parseDouble(parts[2]));
				item.setEuclideanTermFrequency(Double.parseDouble(parts[3]));
				item.setWordCount(Integer.parseInt(parts[4]));
				item.setLinkCount(Integer.parseInt(parts[5]));
				item.setMetaTagCount(Integer.parseInt(parts[6]));
				item.setHeaderCount(Integer.parseInt(parts[7]));
				
				items.add(item);
				if(items.size() >= 5000) {
					this.db.batchSave(items);
					items = new LinkedList<InvertedIndexRow>();
					logger.info(String.format("imported %d records into DynamoDB's 'inverted-index' table.", rowCount));
				}
			} catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
				logger.error(String.format("importing inverted index row '%s' failed.", line), e);
			}
		}
		this.db.batchSave(items);
		br.close();
	}
	
	// FIXME: this method is actually doing a wrong computation!
	public PriorityQueue<InvertedIndexRow> rankDocuments(List<String> query) {
		// TODO: query URLMetaInfo dynamoDB table for corpus size??
		int corpusSize = 4000;
		
		Map<String, InvertedIndexRow> ranks = new HashMap<String, InvertedIndexRow>();
		for(String word: query) {
			// TODO: optimize based on different table layout, multi-thread requests, etc.
			List<InvertedIndexRow> wordDocLocs = getDocumentLocations(word);
			for(InvertedIndexRow wordDocLoc: wordDocLocs) {
				
				if(!ranks.containsKey(wordDocLoc.getUrl())) {
					wordDocLoc.setSimilarityRank(0);
					ranks.put(wordDocLoc.getUrl(), wordDocLoc);
				}
				
				InvertedIndexRow rankedDoc = ranks.get(wordDocLoc.getUrl());
				// wrong! it needs to be the tf wrt the query, not the wordDocLoc!
				double queryWeight = wordDocLoc.getMaximumTermFrequency() * Math.log((double) corpusSize / wordDocLocs.size());
				double docWeight = wordDocLoc.getEuclideanTermFrequency();
				rankedDoc.setSimilarityRank(rankedDoc.getSimilarityRank() + queryWeight * docWeight);
				rankedDoc.addWordVector(wordDocLoc.getWord());
			}
			logger.info(String.format("=> got %d documents for query word '%s'.", wordDocLocs.size(), word));
		}
		
		return new PriorityQueue<InvertedIndexRow>(ranks.values());
	}
	
	public PriorityQueue<DocumentVector> lookupDocuments(List<String> query) {
		// TODO: query URLMetaInfo dynamoDB table for real corpus size!
		int corpusSize = 4000;
		
		List<InvertedIndexRow> candidates = new LinkedList<InvertedIndexRow>();
		Map<String, Integer> dfs = new HashMap<String, Integer>();
		for(String word: query) {
			List<InvertedIndexRow> wordCandidates = getDocumentLocations(word);
			candidates.addAll(wordCandidates);
			dfs.put(word, wordCandidates.size()); 
			logger.info(String.format("=> got %d documents for query word '%s'.", wordCandidates.size(), word));
		}
		
		// build candidate document vectors
		Map<String, Map<String, Double>> docs = new HashMap<String, Map<String, Double>>();
		for(InvertedIndexRow candidate: candidates) {
			Map<String, Double> doc = docs.get(candidate.getUrl());
			if(doc == null) {
				doc = new HashMap<String, Double>();
				docs.put(candidate.getUrl(), doc);
			}
			doc.put(candidate.getWord(), candidate.getEuclideanTermFrequency());
		}
		
		// compute document similarity
		DocumentVector queryVector = getQueryVector(query, corpusSize, dfs);
		PriorityQueue<DocumentVector> ranks = new PriorityQueue<>();
		for(String doc: docs.keySet()) {
			DocumentVector docVec = new DocumentVector(docs.get(doc));
			docVec.setUrl(doc);
			docVec.setSimilarity(DocumentVector.cosineSimilarity(docVec, queryVector));
			ranks.add(docVec);
		}
		return ranks;
	}
	
	private DocumentVector getQueryVector(List<String> query, int corpusSize, Map<String, Integer> dfs) {
		WordCounts queryCounts = new WordCounts(query);
		Map<String, Double> queryVector = new HashMap<String, Double>();
		for(String queryWord: queryCounts) {
			// FIXME: what do we do, if the queryWord is not found in the corpus at all?
			// i.e., it is an 'UNK' word to the corpus?
			double idf = Math.log((double) corpusSize / dfs.get(queryWord));
			queryVector.put(queryWord, queryCounts.getMaximumTermFrequency(queryWord) * idf);
		}
		return new DocumentVector(queryVector);
	}
	
	public static void main(String[] args) {
		try {
			InvertedIndex idx = new InvertedIndex();
			
			if(args[0].equals("import")) {
				idx.importData(args[1]);
			} else if(args[0].equals("query")) {
				List<String> query = Arrays.asList(Arrays.copyOfRange(args, 1, args.length));
				PriorityQueue<InvertedIndexRow> newResults = idx.rankDocuments(query);
				
				Iterator<InvertedIndexRow> iter = newResults.iterator();
				for(int i = 0; i < 10 && iter.hasNext(); ++i) {
					InvertedIndexRow doc = iter.next();
					System.out.println(doc.toString());
				}
				
				System.out.println("============");
				System.out.println("old results:");
				System.out.println("============");
				PriorityQueue<DocumentVector> oldResults = idx.lookupDocuments(query);
				Iterator<DocumentVector> olditer = oldResults.iterator();
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
