package searchengine.ranking;

import indexer.DocumentScore;
import indexer.api.DocumentIDs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import searchengine.query.QueryWord;

public final class RankerUrlCount extends Ranker {
	private final Logger logger = Logger.getLogger(getClass());
	
	private final List<DocumentScore> docs;	
	private double weight;
	private boolean normalize;	
	private List<QueryWord> query;
	private List<Double> ranks;

	public RankerUrlCount(List<DocumentScore> documentScores, List<QueryWord> query) {
		this.docs = documentScores;
		this.weight = 1;
		this.normalize = true;
		this.query = query;
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
		DocumentIDs dId = DocumentIDs.getInstance();
				
		for (DocumentScore score : docs) {
			// logger.info("Looking at " + score.getDocId());
			double scoreValue = 0.0;
			String url = dId.getURLFor(score.getDocId());
			
			if (url != null) {
				scoreValue = combineUrlCounts(query, url.toLowerCase());
			}
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
	
	private double combineUrlCounts(List<QueryWord> query, String url) {
		double result = 0.0;
		for (QueryWord qword : query) {
			result += url.contains(qword.getWord()) ? qword.getWeight()  : 0;
		}
		return result;
	}

}
