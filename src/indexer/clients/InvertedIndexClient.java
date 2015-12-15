package indexer.clients;

import indexer.db.dao.DocumentFeatures;
import indexer.db.dao.ImageIndex;
import indexer.db.dao.InvertedIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import searchengine.query.QueryWord;
import utils.LRUCache;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;

public class InvertedIndexClient {
	private final Logger logger = Logger.getLogger(getClass());
	public static final String CREDENTIALS_PROFILE = "default";
	public static final String TABLE_NAME = "InvertedIndex";
	public static final String S3_CRAWL_SNAPSHOT = "cis455-url-content-snapshot2";

	private static InvertedIndexClient instance;
	private DynamoDBMapper db;
	private int corpusSize;
	private LRUCache<String, List<InvertedIndex>> cache;

	public static synchronized InvertedIndexClient getInstance() {
		if (instance == null) {
			instance = new InvertedIndexClient();
		}
		return instance;
	}

	public LRUCache getCache() {
		return cache;
	}

	private InvertedIndexClient() {
		this.db = connectDB();
		// TODO make this faster
		// this.corpusSize = s3.getNumberOfItemsInBucket(S3_CRAWL_SNAPSHOT);
		this.corpusSize = 113000;
		cache = new LRUCache<String, List<InvertedIndex>>();
	}

	public int getCorpusSize() {
		return corpusSize;
	}

	public List<ImageIndex> getImageLocations(String imageWord) {
		ImageIndex image = new ImageIndex();
		image.setImageWord(imageWord);
		DynamoDBQueryExpression<ImageIndex> query = new DynamoDBQueryExpression<ImageIndex>()
				.withHashKeyValues(image);
		List<ImageIndex> result = new ArrayList<>();
		for (ImageIndex row : db.query(ImageIndex.class, query)) {
			result.add(row);
		}
		return result;
	}

	public List<InvertedIndex> getDocumentLocations(String word) {
		List<InvertedIndex> result = null;
		if (cache.containsKey(word)) {
			logger.info("Getting entries for " + word + " from cache");
			result = cache.get(word);
		} else {
			InvertedIndex item = new InvertedIndex();
			item.setWord(word);

			DynamoDBQueryExpression<InvertedIndex> query = new DynamoDBQueryExpression<InvertedIndex>()
					.withHashKeyValues(item);
			result = new ArrayList<>();
			for (InvertedIndex row : db.query(InvertedIndex.class, query)) {
				result.add(row);
			}
			logger.info("Caching " + result.size() + " entries for " + word);
			cache.put(word, result);
		}
		// return db.query(InvertedIndexRow.class, query);
		return result;
	}

	public Map<QueryWord, List<DocumentFeatures>> getInvertedIndexForQueryMultiThreaded(
			List<QueryWord> query) {
		Map<QueryWord, List<DocumentFeatures>> wordDocumentInfoMap = new HashMap<>();
		logger.info("Starting an es of size " + query.size());
		if (query.size() <= 0) {
			return wordDocumentInfoMap;
		}
		ExecutorService es = Executors.newFixedThreadPool(query.size());
		for (QueryWord qword : query) {
			InvertedIndexFetcher f = new InvertedIndexFetcher(
					wordDocumentInfoMap, qword.getWord());
			es.execute(f);
		}
		es.shutdown();

		try {
			boolean finshed = es.awaitTermination(1, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return wordDocumentInfoMap;
	}

	public static DynamoDBMapper connectDB() {
		AWSCredentials credentials = new ProfileCredentialsProvider(
				CREDENTIALS_PROFILE).getCredentials();

		AmazonDynamoDBClient dbClient = new AmazonDynamoDBClient(credentials);
		dbClient.setRegion(Region.getRegion(Regions.US_EAST_1));

		return new DynamoDBMapper(dbClient);
	}

	public Map<QueryWord, List<DocumentFeatures>> getImageIndexForQueryMultiThreaded(
			List<String> query) {
		// TODO Auto-generated method stub
		return null;
	}

}
