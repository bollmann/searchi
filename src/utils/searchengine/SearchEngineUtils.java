package utils.searchengine;

import indexer.DocumentScore;
import indexer.clients.InvertedIndexClient;
import indexer.db.dao.DocumentFeatures;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

import searchengine.SearchEngine;
import searchengine.query.QueryWord;
import searchengine.servlets.SearchResult;

public class SearchEngineUtils {
	private final static Logger logger = Logger
			.getLogger(SearchEngineUtils.class);

	public static List<SearchResult> weightedMergeScores(
			Map<String, Double> map1, Map<String, Double> map2,
			Double weights[]) {
		Date startTime = Calendar.getInstance().getTime();
		List<SearchResult> resultList = new ArrayList<SearchResult>(1000);

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
			// logger.info("Applying " + map1Weight + " to indexer score " +
			// map1Score
			// + " and " + map2Weight + " to pagerank score " + map2Score);
			Double finalScore = (map1Weight * map1Score)
					+ (map2Weight * map2Score);

			SearchResult sr = new SearchResult();
			sr.setUrl(entry.getKey());
			sr.setScore(finalScore);
			resultList.add(sr);
		}
		Collections.sort(resultList);
		Date endTime = Calendar.getInstance().getTime();
		logger.info("Combined ranking took "
				+ printTimeDiff(startTime, endTime));
		return resultList;
	}

	/** Returns sorted search results using the specified document scores */
	public static List<SearchResult> getSortedSearchResultUsingScores(
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

	public static String printTimeDiff(Date startTime, Date endTime) {
		long timeDiff = (endTime.getTime() - startTime.getTime());
		return timeDiff / 1000 + "s," + timeDiff % 1000 + "ms:";
	}

	public static List<DocumentScore> getRankedIndexerResults(
			List<QueryWord> query) {
		InvertedIndexClient iic = InvertedIndexClient.getInstance();

		Date startTime, endTime;

		startTime = Calendar.getInstance().getTime();
		// get gram data
		Map<QueryWord, List<DocumentFeatures>> invertedIndex = iic
				.getInvertedIndexForQueryMultiThreaded(query);

		endTime = Calendar.getInstance().getTime();

		logger.info("Indexer fetch for query took "
				+ printTimeDiff(startTime, endTime));

		/******************* Add rankers and combine them here - Secret sauce ******************/
		startTime = Calendar.getInstance().getTime();

		SearchEngine searchEngine = new SearchEngine(query, invertedIndex,
				iic.getCorpusSize());

		/****************************** End of secret sauce ****************************/

		List<DocumentScore> rankedDocs = searchEngine.getRankedIndexerResults();

		endTime = Calendar.getInstance().getTime();
		logger.info("Indexer ranking took " + printTimeDiff(startTime, endTime));

		return rankedDocs;
	}

	public static List<SearchResult> getRankedDomainRankResults(
			Map<String, Double> domainRankScore, List<String> lookupList) {
		// domainRankScore = pageRankAPI.getDomainRankBatch(lookupList);
		Date startTime = Calendar.getInstance().getTime();

		List<SearchResult> result = SearchEngineUtils
				.getSortedSearchResultUsingScores(domainRankScore);

		return result;
	}

	public static List<SearchResult> diversifyResults(
			List<SearchResult> resultList, int diversityLimit) {
		List<SearchResult> filteredList = new ArrayList<>();

		Map<String, Integer> domainCountMap = new HashMap<>();
		Set<String> baseUrlMap = new HashSet<>();

		for (SearchResult result : resultList) {
//			logger.info("Looking at result " + result.getUrl());
			String domain = null;
			URI pURI = null;
			try {
				pURI = new URI(result.getUrl());
				domain = pURI.getHost();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}

			String baseUrl = pURI.getHost() + pURI.getPath();
//			logger.info("Baseurl is " + baseUrl);
			if(baseUrlMap.contains(baseUrl)) {
//				logger.info("Skipping as found " + baseUrl + " earlier ");
				continue;
			} else {
				baseUrlMap.add(baseUrl);
			}
			
			// only put results into the result list if we have seen
			// at max diversityLimit from that domain
			if (domainCountMap.containsKey(domain)) {
				Integer count = domainCountMap.get(domain) + 1;
//				logger.info("Found " + count + " links for domain " + domain + ". Cut off at " + diversityLimit);
				if (count > diversityLimit) {
					continue;
				}
				domainCountMap.put(domain, count);
			} else {
				domainCountMap.put(domain, 1);
			}
			
			filteredList.add(result);

		}

		return filteredList;
	}
}
