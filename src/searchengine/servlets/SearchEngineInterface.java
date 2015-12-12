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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import pagerank.api.PageRankAPI;
import searchengine.api.SearchAPI;
import searchengine.ranking.Ranker;
import searchengine.ranking.RankerInfo.RankerType;
import utils.searchengine.SearchEngineUtils;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class SearchEngineInterface extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	Gson gson = null;
	URL frontendIP = null;
	
	@Override
	public void init() {
		gson = new Gson();
		try {
			frontendIP = new URL(getFrontendIP());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private String getFrontendIP(){
		try{
			System.out.println(new File(".").getAbsolutePath());
			BufferedReader a = new BufferedReader(new FileReader(new File("conf/ip_config")));
			String line;
			while((line = a.readLine()) != null)
				if(line.startsWith("frontend")){
					System.out.println(line.split(" = ")[1].trim());
					return line.split(" = ")[1].trim();
				}
		} catch (IOException e){
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(req.getParameter("q") != null)
			doListing(req, resp);
		else
			logger.error("Search engine interface accessed with no query string");
	}
	
	private String printTimeDiff(Date startTime, Date endTime) {
		long timeDiff = (endTime.getTime() - startTime.getTime());
		return timeDiff / 1000 + "s," + timeDiff % 1000 + "ms:";
	}

	private void doListing(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		String queryStr = req.getParameter("q");
		LinkedList<String> query = new LinkedList<String>();
		queryStr = URLDecoder.decode(queryStr, "UTF-8");
		for(String word: queryStr.split(" "))
			query.add(word);
		InvertedIndexClient iic = InvertedIndexClient.getInstance();
		logger.info("Recieved query: " + query.toString());
		Map<String, Double> indexerScore = new HashMap<String, Double>(1000);
		Map<String, Double> pageRankScore = null;
		Map<String, Map<String, Map<String, String>>> searchResultMap = new HashMap<String, Map<String, Map<String, String>>>();
		PageRankAPI pra = new PageRankAPI();
		List<String> lookupList = new ArrayList<String>(1000);
		
		
		Date startTime = Calendar.getInstance().getTime();
		Map<String, List<DocumentFeatures>> invertedIndex = iic.getInvertedIndexForQueryMultiThreaded(query);
		Date endTime = Calendar.getInstance().getTime();
		
		
		Map<String, Integer> wordDfs = new HashMap<String, Integer>();
		for(Entry<String, List<DocumentFeatures>> entry : invertedIndex.entrySet()) {
			wordDfs.put(entry.getKey(), entry.getValue().size());
		}

		logger.info("Indexer fetch took "
				+ printTimeDiff(startTime, endTime));
		
		/****************************** Add rankers and combine them here *************/
		SearchAPI searchAPI = new SearchAPI(query, invertedIndex, iic.getCorpusSize());		
		searchAPI.formDocumentScoresForQueryFromInvertedIndex();
		List<DocumentScore> rankedDocs = null;
		try {
			searchAPI.addRanker(RankerType.RANKER_TFIDF, 1.0);
			searchAPI.addRanker(RankerType.RANKER_HEADER, 1.0);
			searchAPI.addRanker(RankerType.RANKER_LINKS, 1.0);
			searchAPI.addRanker(RankerType.RANKER_META, 1.0);
			searchAPI.addRanker(RankerType.RANKER_POSITION, -1.0);
			searchAPI.addRanker(RankerType.RANKER_QUERYMATCH, 1.0);
			searchAPI.addRanker(RankerType.RANKER_TOTALCOUNT, 1.0);
			searchAPI.addRanker(RankerType.RANKER_URLCOUNT, 10.0);
			
			List<Ranker> rankers = searchAPI.applyRankers();
			
			rankedDocs = searchAPI.combineRankings(rankers);
			Collections.sort(rankedDocs);
		} catch (Exception e) {
			//TODO Handle this exception;
			return;
		}
		
		/****************************** End of secret sauce ****************************/
		
		/******************************** Indexer results *********************/
		
		try {
			
			startTime = Calendar.getInstance().getTime();
			Map<String, Map<String, String>> indexerResultMap = new HashMap<String, Map<String, String>>();
			Map<String, String> timeMap = new HashMap<String, String>();
			
			DocumentIDs dId = new DocumentIDs();
			
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
				if (resultCount > 10) {
					break;
				}
			}
			endTime = Calendar.getInstance().getTime();
			timeMap.put("time",  String.valueOf(endTime.getTime() - startTime.getTime()));
			indexerResultMap.put("time", timeMap);
			
			/******************************** Page Rank results *********************/

			startTime = Calendar.getInstance().getTime();
			pageRankScore = pra.getPageRankBatch(lookupList);
			Map<String, Map<String, String>> pageRankResultMap = new HashMap<String, Map<String, String>>();
			
			List<SearchResult> pqueue = SearchEngineUtils
					.convertScoreMapToPriorityQueue(pageRankScore);

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
			timeMap = new HashMap<String, String>();
			timeMap.put("time",  String.valueOf(endTime.getTime() - startTime.getTime()));
			pageRankResultMap.put("time", timeMap);
			
			/******************************** Combined results *********************/
			startTime = Calendar.getInstance().getTime();
			Double[] weights = { 0.9, 0.1 };
			Map<String, Map<String, String>> combinedResultMap = new HashMap<String, Map<String, String>>();
			List<SearchResult> result = SearchEngineUtils
					.weightedMergeScores(indexerScore, pageRankScore, weights);
			
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
			timeMap.put("time",  String.valueOf(endTime.getTime() - startTime.getTime()));
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
//			System.out.println("Sending POST request");
//			System.out.println("to " + frontendIP.toString());
//			
			//Socket s = new Socket(frontendIP.getHost(), Integer.valueOf(frontendIP.getPort()));
			//PrintWriter out = new PrintWriter(s.getOutputStream(), true);
		    
//			out.append("POST / HTTP/1.0\r\n");
//			out.append("Host: 127.0.0.1\r\n");
//		    out.append("Content-Type: application/json\r\n");
//		    out.append("Content-Length: " + String.valueOf(searchResultsJSON.getBytes().length) + "\r\n");
//			out.append("Connection: close\r\n");
//		    out.append("\r\n");
//		    out.append(searchResultsJSON);
//		    out.flush();  //sends request
//		    out.close();
		}
	}
}