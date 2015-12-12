package searchengine.ranking;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public final class RankerUrlCount extends Ranker {
	private final Logger logger = Logger.getLogger(getClass());
	private final List<DocumentScore> docs;	
	private double weight;
	private boolean normalize;
	private Map<Integer, String> docIdMap;
	private List<String> query;
	private List<Double> ranks;

	public RankerUrlCount(List<DocumentScore> documentScores,  List<String> query, Map<Integer, String> docIdMap) {
		this.docs = documentScores;
		this.weight = 1;
		this.normalize = true;
		this.query = query;
		this.docIdMap = docIdMap;
	}
	
	@Override
	public String getRankingName() {
		return RankerInfo.RankerType.RANKER_URLCOUNT.name();
	}

	@Override
	public void computeRank() {
		ranks = new ArrayList<>();
		
		double maxScore = Double.MIN_VALUE;
		double minScore = Double.MAX_VALUE;
				
		for (DocumentScore score : docs) {
			// logger.info("Looking at " + score.getDocId());
			String url = docIdMap.get(score.getDocId());
			double scoreValue = combineUrlCounts(query, url);
//			logger.info(score.getDocId() + " got a url count score of " + scoreValue);
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
	
	private double combineUrlCounts(List<String> query, String url) {
		double result = 0.0;
		for (String word : query) {
			result += url.contains(word) ? 1 : 0;
		}
		return result;
	}

}
