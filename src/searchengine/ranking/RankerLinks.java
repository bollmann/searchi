package searchengine.ranking;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class RankerLinks extends Ranker {
	
	private final List<DocumentScore> docs;	
	private double weight;
	private boolean normalize;
	
	private List<Double> ranks;

	public RankerLinks(List<DocumentScore> documentScores) {
		this.docs = documentScores;
		this.weight = 1;
		this.normalize = false;
	}
	
	@Override
	public String getRankingName() {
		return RankerInfo.RankerType.RANKER_LINKS.name();
	}

	@Override
	public void computeRank() {
		ranks = new ArrayList<>();
		
		double maxScore = Double.MIN_VALUE;
		double minScore = Double.MAX_VALUE;
				
		for (DocumentScore score : docs) {
			// logger.info("Looking at " + score.getDocId());
			double scoreValue = combineLinkCounts(score.getWordFeatures());
			
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
	
	private double combineLinkCounts(Map<String, DocumentFeatures> wordFeatures) {
		double result = 0.0;
		double normalization = 1.0;
		
		for (Entry<String, DocumentFeatures> entry : wordFeatures.entrySet()) {
			DocumentFeatures features = entry.getValue();
			
			if (normalize && features.getTotalCount() > 0) {				
				normalization = ((double) features.getEuclideanTermFrequency()) / features.getTotalCount(); 
			}
			
			result += entry.getValue().getLinkCount() * normalization;
		}
		return result;
	}

}