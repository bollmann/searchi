package utils.searchengine;

import indexer.servlets.SearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class SearchEngineUtils {
	private final static Logger logger = Logger.getLogger(SearchEngineUtils.class);
	public static List<SearchResult> weightedMergeScores(
			Map<String, Double> map1, Map<String, Double> map2,
			Double weights[]) {
		List<SearchResult> resultList = new ArrayList<SearchResult>(
				1000);

		for (Entry<String, Double> entry : map1.entrySet()) {
			Double map1Score = entry.getValue();
			Double map2Score = map2.get(entry.getKey());

			Double map1Weight = weights[0];
			Double map2Weight = weights[1];
			if (map2Score == null) {
				map1Weight = 1.0;
				map2Weight = 0.0;
				map2Score = 0.0;
			}
			logger.info("Applying " + map1Weight + " to indexer score " + map1Score 
					+ " and " + map2Weight + " to pagerank score " + map2Score);
			Double finalScore = (map1Weight * map1Score) + (map2Weight * map2Score);

			SearchResult sr = new SearchResult();
			sr.setUrl(entry.getKey());
			sr.setScore(finalScore);
			resultList.add(sr);
		}
		Collections.sort(resultList);
		return resultList;
	}

	public static List<SearchResult> convertScoreMapToPriorityQueue(
			Map<String, Double> scoreMap) {
		List<SearchResult> list = new ArrayList<SearchResult>();
		for (Entry<String, Double> entry : scoreMap.entrySet()) {
			SearchResult sr = new SearchResult();
			sr.setUrl(entry.getKey());
			sr.setScore(entry.getValue());
			list.add(sr);
		}
		Collections.sort(list);
		return list;
	}
}
