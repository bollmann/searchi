package indexer;

import indexer.exceptions.IndexerNotReadyException;

import java.util.List;

import org.jsoup.nodes.Document;

public class Client {
	//private Lexicon lexicon;
	//private InvertedIndex invertedIndex;
	
	public Client() throws IndexerNotReadyException {
		// try to instantiate the lexicon as well as the invertedIndex from DynamoDB tables.
		// throw an exception, if the databases are not yet ready (i.e., map reduce jobs have not yet run)!
	}
	
	public static List<Document> getTFIDFScores(List<String> words) {
		return null;
		// access inverted index to retrieve matching documents.
		// return TFIDF and similarity scores for matches.
	}
}
