package searchengine.ranking;

import indexer.DocumentScore;
import indexer.WordCounts;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public final class RankerTfIdf extends Ranker {
	private static final Logger logger = Logger.getLogger(RankerTfIdf.class);
	
	private final List<String> query;
	private final List<DocumentScore> docs;
	private final Map<String, Integer> wordDfs;
	private final int corpusSize;
	private double weight;
	
	private List<Double> ranks;

	private boolean normalize;
	
	public RankerTfIdf(List<DocumentScore> docs, List<String> query,
			int corpusSize, Map<String, Integer> wordDfs) {
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
		System.out.println("MAX tfidf " + maxScore  );
		System.out.println("MIN tfidf " + minScore  );
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
	
	private double combineTfIdfs(Map<String, DocumentFeatures> wordFeatures) {
		
		double result = 0.0;
		WordCounts queryCounts = new WordCounts(query);
		
		for (String queryWord : query) {
			DocumentFeatures feature = wordFeatures.get(queryWord);
			//logger.info("Combiner looking for word:" + queryWord + " and got feature:" + feature);
		
			if (feature != null) {
				
				double queryWeight = queryCounts.getTFIDF(queryWord,
						corpusSize, wordDfs.get(queryWord));
//				double docWeight = feature.getEuclideanTermFrequency();
				
				double docWeight = feature.getTfidf();
				
				result += queryWeight * docWeight;
				//logger.info("queryWeight is " + queryWeight + " and docweight is tfidf=" + docWeight + " and result is " + result);
			}
		}
		return result;
	}

}
