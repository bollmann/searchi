package searchengine.ranking;

import indexer.DocumentScore;
import indexer.db.dao.DocumentFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import searchengine.query.QueryWord;

public final class RankerPosition extends Ranker {

	private final List<DocumentScore> docs;
	private final List<QueryWord> query;
	
	private double weight;
	
	private List<Double> ranks;
	private boolean normalize;
	
	public RankerPosition(List<DocumentScore> documentScores,
		List<QueryWord>query) {
		
		this.docs = documentScores;
		this.query = query;
		this.weight = -1;
		this.normalize = false;
	}
	
	@Override
	public String getRankingName() {
		return RankerInfo.RankerType.RANKER_POSITION.name();
	}
	
	@Override
	public void computeRank() {
		ranks = new ArrayList<>();
		
		double maxScore = Double.MIN_VALUE;
		double minScore = Double.MAX_VALUE;
		
		for (DocumentScore score : docs) {
			// logger.info("Looking at " + score.getDocId());
			double scoreValue = combinePositions(score.getWordFeatures());
		
			maxScore = Double.compare(scoreValue, maxScore) > 0 ? scoreValue : maxScore;
			minScore = Double.compare(scoreValue, minScore) < 0 ? scoreValue : minScore;
			
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
	
	private int combinePositions(Map<QueryWord, DocumentFeatures> wordFeatures) {
		int score = 0;
		int range = 2;
		final int defaultPosScore = 500000;
		
		for (int i = 0; i < query.size()-1; i++) {
			if (containSpace(query.get(i+1).getWord()) && !containSpace(query.get(i).getWord())) {
				// Break in UNIGRAM and BIGRAMS
				continue;
			}
			
			DocumentFeatures feat1 = wordFeatures.get(query.get(i));
			if (feat1 == null) {
				score += defaultPosScore;
				continue;
			}			
			for (int j = i+1; j <= i+range-1; ++j) {
				
				DocumentFeatures feat2 = wordFeatures.get(query.get(j));
				if (feat2 == null) {
					score += defaultPosScore;
					continue;
				}
				
				int minDifference = defaultPosScore;
				for(int posA : feat1.getPositions()) {
					for(int posB : feat2.getPositions()) {
						if (posB < posA) continue;
						
						int diff = posB - posA;
						if(diff < minDifference) {
							minDifference  = diff;
						}
					}
				}
				
				score+= minDifference;
			}
		}
		return score;		
	}
	
	private boolean containSpace(String word) {
		if (word.indexOf(' ') >= 0) {
			return true;
		}
		return false;
	}
}

