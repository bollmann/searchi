package searchengine.ranking;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import searchengine.query.QueryWord;

public final class RankerTotalCount extends Ranker {
	
	private final List<DocumentScore> docs;	
	private double weight;
	
	private List<Double> ranks;
	private boolean normalize;

	public RankerTotalCount(List<DocumentScore> documentScores) {
		this.docs = documentScores;
		this.weight = 1;
		this.normalize = false;
	}
	
	@Override
	public String getRankingName() {
		return RankerInfo.RankerType.RANKER_TOTALCOUNT.name();
	}
	
	@Override
	public void computeRank() {
		ranks = new ArrayList<>();
		
		double maxScore = Double.MIN_VALUE;
		double minScore = Double.MAX_VALUE;
		
		for (DocumentScore score : docs) {
			// logger.info("Looking at " + score.getDocId());
			double scoreValue = combineTotalCounts(score.getWordFeatures());
	
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

	public static double combineTotalCounts(
			Map<QueryWord, DocumentFeatures> wordFeatures) {
		double result = 0;
		
		for (Entry<QueryWord, DocumentFeatures> entry : wordFeatures.entrySet()) {
			result += entry.getValue().getTotalCount() * entry.getKey().getWeight();
		}
		return result;
	}


	


}
