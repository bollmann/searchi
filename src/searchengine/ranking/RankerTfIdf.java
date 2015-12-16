package searchengine.ranking;

import indexer.DocumentScore;
import indexer.WordCounts;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import searchengine.query.QueryWord;

public final class RankerTfIdf extends Ranker {
	private static final Logger logger = Logger.getLogger(RankerTfIdf.class);
	
	private final List<QueryWord> query;
	private final List<DocumentScore> docs;
	private final Map<QueryWord, Integer> wordDfs;
	private final int corpusSize;
	private double weight;
	
	private List<Double> ranks;

	private boolean normalize;
	
	public RankerTfIdf(List<DocumentScore> docs, List<QueryWord> query,
			int corpusSize, Map<QueryWord, Integer> wordDfs) {
		this.docs = docs;
		this.query = query;
		this.corpusSize= corpusSize;
		this.wordDfs = wordDfs;
		
		this.weight = 5;
		this.normalize = false;
	}
	
	@Override
	public String getRankingName() {
		return RankerInfo.RankerType.RANKER_TFIDF.name();
	}
	
	@Override
	public void computeRank() {
		ranks = new ArrayList<>();
		int i = 0;
		
		double maxScore = Double.MIN_VALUE;
		double minScore = Double.MAX_VALUE;
		
		for (DocumentScore score : docs) {
			i++;
			logCond(i);
			double scoreValue = combineTfIdfs(score.getWordFeatures());
			
			maxScore = Double.compare(scoreValue, maxScore ) > 0 ? scoreValue : maxScore;
			minScore = Double.compare(scoreValue, minScore ) < 0 ? scoreValue : minScore;
			
			ranks.add(new Double(scoreValue));
		}
		
		ranks = normalize(ranks, minScore, maxScore);
	}
	
	@Override
	public List<Double> getRanks() {
		return ranks;
	}
	
	private void logCond(int num) {
		if (num % 500 == 0) {
			logger.info("Ranked " + num + " documents");	
		}
	}

	@Override
	public void setWeight(double weight) {
		this.weight = weight;
	}

	@Override
	public double getWeight() {
		return weight;
	}
	
	@Override
	public void doNormalization(boolean bool) {
		this.normalize = bool;
	}
	
	private double combineTfIdfs(Map<QueryWord, DocumentFeatures> wordFeatures) {
		
		double result = 0.0;
		List<String> words = new ArrayList<>();
		for (QueryWord qword: query) {
			words.add(qword.getWord());
		}
		WordCounts queryCounts = new WordCounts(words);
		
		for (QueryWord qword : query) {
			DocumentFeatures feature = wordFeatures.get(qword);
			//logger.info("Combiner looking for word:" + queryWord + " and got feature:" + feature);
		
			if (feature != null) {
				
				double queryWeight = queryCounts.getTFIDF(qword.getWord(),
						corpusSize, wordDfs.get(qword));
//				double docWeight = feature.getEuclideanTermFrequency();
				
				double docWeight = feature.getTfidf();
				
				result += queryWeight * docWeight;
			//	logger.info("queryWeight is " + queryWeight + " and docweight is tfidf=" + docWeight + " and result is " + result);
			}
		}
		return result;
	}

}
