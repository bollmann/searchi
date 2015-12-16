package searchengine.ranking;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import searchengine.query.QueryWord;

public final class RankerHeader extends Ranker {
	
	private final List<DocumentScore> docs;
	private double weight;
	private boolean normalize;
	
	private List<Double> ranks;
	
	public RankerHeader(List<DocumentScore> documentScores) {
		this.docs = documentScores;
		this.weight = 3;
		this.normalize = false;
	}
	
	@Override
	public String getRankingName() {
		return RankerInfo.RankerType.RANKER_HEADER.name();
	}
	
	@Override
	public void computeRank () {
		ranks = new ArrayList<>();
		
		double maxScore = Double.MIN_VALUE;
		double minScore = Double.MAX_VALUE;
		
		for (DocumentScore score : docs) {
			// logger.info("Looking at " + score.getDocId());
			
			double scoreValue = combineHeaderCounts(score.getWordFeatures());
			
			maxScore = Double.compare(scoreValue, maxScore) > 0 ? scoreValue : maxScore;
			minScore = Double.compare(scoreValue, minScore) < 0 ? scoreValue : minScore;
			
			ranks.add(scoreValue);
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

	private double combineHeaderCounts(Map<QueryWord, DocumentFeatures> wordFeatures) {
		double result = 0.0;
		double normalization = 1.0;
		
		for (QueryWord word : wordFeatures.keySet()) {
			DocumentFeatures features = wordFeatures.get(word);
			
			if (normalize && features.getTotalCount() > 0) {
				normalization = ((double)features.getEuclideanTermFrequency()) / features.getTotalCount();
			}
			result += features.getHeaderCount() * word.getWeight() * normalization;
		}
		return result;
	}	
	
}
