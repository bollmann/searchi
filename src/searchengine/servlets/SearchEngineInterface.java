package searchengine.servlets;

import indexer.DocumentScore;
import indexer.api.DocumentIDs;
import indexer.clients.InvertedIndexClient;
import indexer.db.dao.DocumentFeatures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import pagerank.api.PageRankAPI;
import searchengine.SearchEngine;
import searchengine.query.QueryWord;
import utils.nlp.QueryProcessor;
import utils.searchengine.SearchEngineUtils;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class SearchEngineInterface extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	Gson gson = null;
	URL frontendIP = null;
	DocumentIDs dId;
	InvertedIndexClient iic;
	PageRankAPI pageRank;
	QueryProcessor queryProcessor;

	@Override
	public void init() {
		Date startTime = Calendar.getInstance().getTime();
		gson = new Gson();
		dId = (DocumentIDs) getServletContext().getAttribute("forwardIndex");
		if (dId == null) {
			dId = new DocumentIDs();
			getServletContext().setAttribute("forwardIndex", dId);
		}
		iic = InvertedIndexClient.getInstance();
		pageRank = new PageRankAPI();
		queryProcessor = QueryProcessor.getInstance();
		try {
			frontendIP = new URL(getFrontendIP());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		Date endTime = Calendar.getInstance().getTime();
		logger.info("Init took " + printTimeDiff(startTime, endTime));
	}

	private String getFrontendIP() {
		try {
			System.out.println(new File(".").getAbsolutePath());
			BufferedReader a = new BufferedReader(new FileReader(new File(
					"conf/ip_config")));
			String line;
			while ((line = a.readLine()) != null)
				if (line.startsWith("frontend")) {
					System.out.println(line.split(" = ")[1].trim());
					return line.split(" = ")[1].trim();
				}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getPathInfo() != null) {
			if (req.getPathInfo().equals("/image")) {
				String result;
				// {"images" : [{"url": url}, {"url": url}]}

				Map<String, List<Map<String, String>>> resultMap = new HashMap<>();
				List<Map<String, String>> resultList = new ArrayList<>();
				resultMap.put("images", resultList);
				try {
					String queryStr = req.getParameter("q");
					queryStr = URLDecoder.decode(queryStr, "UTF-8");
					List<String> query = Arrays.asList(queryStr.split("\\s+"));

					Map<QueryWord, List<DocumentFeatures>> imageIndex = iic
							.getImageIndexForQueryMultiThreaded(query);

					List<QueryWord> processedQuery = queryProcessor
							.getProcessedQuery(query, 1);

					SearchEngine search = new SearchEngine(processedQuery,
							imageIndex, iic.getCorpusSize());
					List<DocumentScore> rankedDocs = search
							.formDocumentScoresForQueryFromInvertedIndex();
					for (DocumentScore doc : rankedDocs) {
						Map<String, String> map = new HashMap<>();
						map.put("url", dId.getURLFor(doc.getDocId()));
						resultList.add(map);
					}
				} catch (Exception e) {

				} finally {
					result = new Gson().toJson(resultMap);
					PrintWriter out = resp.getWriter();
					out.append(result);
					out.flush();
					out.close();
				}

			}
		} else {
			if (req.getParameter("q") != null) {
				logger.info("Processing request for query "
						+ req.getParameter("q"));
				doListing(req, resp);
			} else
				logger.error("Search engine interface accessed with no query string");
		}

	}

	private String printTimeDiff(Date startTime, Date endTime) {
		long timeDiff = (endTime.getTime() - startTime.getTime());
		return timeDiff / 1000 + "s," + timeDiff % 1000 + "ms:";
	}

	private void doListing(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		/***************************** Get Query and convert to list of words *************/

		String queryStr = req.getParameter("q");
		queryStr = URLDecoder.decode(queryStr, "UTF-8");
		List<String> query = Arrays.asList(queryStr.split("\\s+"));

		/***************************** Query Processing ****************************************/

		List<String> lookupList = new ArrayList<String>(1000);

		Map<String, Map<String, Map<String, String>>> searchResultMap = new HashMap<String, Map<String, Map<String, String>>>();

		// TODO _ List of QueryWords
		// List<QueryWord> processedQuery = queryProcessor.generateNGrams(query,
		// 2);
		List<QueryWord> processedQuery = queryProcessor.getProcessedQuery(
				query, 2);
		logger.info("Recieved query: " + query.toString()
				+ " and generated nGrams " + processedQuery);

		/********************* Get Inverted Index for Query words *************************/

		Date startTime = Calendar.getInstance().getTime();
		List<DocumentScore> rankedDocs = SearchEngineUtils
				.getRankedIndexerResults(processedQuery);

		Date endTime = Calendar.getInstance().getTime();

		logger.info("Indexer fetch took " + printTimeDiff(startTime, endTime));

		Map<String, Map<String, String>> indexerResultMap = new HashMap<String, Map<String, String>>();
		Map<String, String> timeMap = new HashMap<String, String>();
		timeMap.put("time",
				String.valueOf(endTime.getTime() - startTime.getTime()));
		indexerResultMap.put("time", timeMap);

		/******************************** Indexer results ******************************/
		Map<String, Double> indexerScore = new HashMap<String, Double>(1000);

		try {

			int resultCount = 0;
			for (DocumentScore doc : rankedDocs) {
				SearchResult sr = new SearchResult();
				// lookup for id
				String url = dId.getURLFor(doc.getDocId());
				sr.setUrl(url);
				sr.setScore(doc.getScore());
				sr.setSnippet(doc.toString());
				indexerResultMap.put(String.valueOf(resultCount), sr.toMap());
				indexerScore.put(url, (double) doc.getScore());
				lookupList.add(url);
				resultCount++;
				if (resultCount >= 10) {
					break;
				}
			}
			endTime = Calendar.getInstance().getTime();

			logger.info("Total indexer took "
					+ printTimeDiff(startTime, endTime));

			/******************************** Page Rank results ***************************/

			startTime = Calendar.getInstance().getTime();
			Map<String, Double> domainRankScore = new HashMap<>();

			for (String page : lookupList) {
				double score = pageRank.getDomainRank(page);
				domainRankScore.put(page, score);
			}
			// domainRankScore = pageRankAPI.getDomainRankBatch(lookupList);

			logger.info("Page rank returned " + domainRankScore.size()
					+ " results");
			List<SearchResult> pqueue = SearchEngineUtils
					.getSortedSearchResultUsingScores(domainRankScore);

			endTime = Calendar.getInstance().getTime();
			logger.info("Domain ranking took "
					+ printTimeDiff(startTime, endTime));

			Map<String, Map<String, String>> pageRankResultMap = new HashMap<String, Map<String, String>>();
			resultCount = 0;
			for (SearchResult doc : pqueue) {
				pageRankResultMap.put(String.valueOf(resultCount), doc.toMap());
				lookupList.add(doc.getUrl());
				resultCount++;
				if (resultCount > 10) {
					break;
				}
			}
			endTime = Calendar.getInstance().getTime();
			logger.info("Domain ranking took "
					+ printTimeDiff(startTime, endTime));
			timeMap = new HashMap<String, String>();
			timeMap.put("time",
					String.valueOf(endTime.getTime() - startTime.getTime()));
			pageRankResultMap.put("time", timeMap);

			/******************************** Combined results **************************/
			startTime = Calendar.getInstance().getTime();
			Double[] weights = { 0.9, 0.1 };
			Map<String, Map<String, String>> combinedResultMap = new HashMap<String, Map<String, String>>();
			List<SearchResult> result = SearchEngineUtils.weightedMergeScores(
					indexerScore, domainRankScore, weights);

			resultCount = 0;
			for (SearchResult doc : result) {
				combinedResultMap.put(String.valueOf(resultCount), doc.toMap());
				lookupList.add(doc.getUrl());
				indexerScore.put(doc.getUrl(), doc.getScore());
				resultCount++;
				if (resultCount > 10) {
					break;
				}
			}
			endTime = Calendar.getInstance().getTime();
			timeMap = new HashMap<String, String>();
			timeMap.put("time",
					String.valueOf(endTime.getTime() - startTime.getTime()));
			combinedResultMap.put("time", timeMap);

			searchResultMap.put("indexer", indexerResultMap);
			searchResultMap.put("pagerank", pageRankResultMap);
			searchResultMap.put("combined", combinedResultMap);

		} catch (IllegalArgumentException e) {
		} finally {
			String searchResultsJSON = gson.toJson(searchResultMap);
			PrintWriter out = resp.getWriter();
			out.append(searchResultsJSON);
			out.flush();
			out.close();
		}
	}
}