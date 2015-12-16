package searchengine.servlets;

import indexer.DocumentScore;
import indexer.api.DocumentIDs;
import indexer.clients.InvertedIndexClient;
import indexer.db.dao.DocumentFeatures;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
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
import edu.stanford.nlp.util.StringUtils;

@SuppressWarnings("serial")
public class IndexerClientServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	DocumentIDs dId = null;
	QueryProcessor queryProcessor;
	InvertedIndexClient iic;
	PageRankAPI pageRankAPI;
	@Override
	public void init() {
		dId = (DocumentIDs) getServletContext().getAttribute("forwardIndex");
		if(dId == null) {
			dId = DocumentIDs.getInstance();
			getServletContext().setAttribute("forwardIndex", dId);
		}
		queryProcessor = QueryProcessor.getInstance();
		iic = InvertedIndexClient.getInstance();
		pageRankAPI = new PageRankAPI();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head><title>Interface-Search Engine Test</title></head>");
		buffer.append("<body><form action = \"/search\" method = \"post\">");
		buffer.append("<input size=50 name = \"query\">");
		buffer.append("<button>Searchi!</button>");
		buffer.append("</form></body></html>");
		out.append(buffer.toString());
		out.flush();
		out.close();
	}

	

	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head><title>Interface-Search Engine Test</title></head>");
		buffer.append("<body>");

		String queryStr = req.getParameter("query");
		buffer.append("<form action = \"/search\" method = \"post\">");
		buffer.append("<input size=50 name = \"query\">");
		buffer.append("<button>Searchi again!</button>");
		buffer.append("</form>");
		
		LinkedList<String> unigrams = new LinkedList<String>();
		queryStr = URLDecoder.decode(queryStr, "UTF-8");

		for (String word : queryStr.split(" "))
			unigrams.add(word);
		
		List<QueryWord> processedQuery = queryProcessor.getProcessedQuery(unigrams, 2);

		logger.info("Got search query:" + queryStr + " split into " + processedQuery);

		Map<String, Double> indexerScore = new HashMap<String, Double>(1000);
		try {
			/******************************** Indexer display *********************/

			
			buffer.append("<br>Using Indexer Ranking:");
			buffer.append("<ol>");

			
			List<DocumentScore> rankedDocs = SearchEngineUtils.getRankedIndexerResults(processedQuery);
			
			
			List<String> lookupList = new ArrayList<String>(1000);
			
			logger.info(rankedDocs.size());
			int resultCount = 0;
			for (DocumentScore doc : rankedDocs) {
				SearchResult sr = new SearchResult();
				// lookup id to get document
				String url = dId.getURLFor(doc.getDocId());
				sr.setUrl(url);
				sr.setScore(doc.getScore());
				sr.setSnippet(doc.toHtml());
				buffer.append("<li>" + sr.toHtml() + "</li>");
				indexerScore.put(url, (double) doc.getScore());
				lookupList.add(url);
				resultCount++;
				if (resultCount > 10) {
					break;
				}
			}
			buffer.append("</ol>");

			/******************************** Page Rank *********************/

			logger.info("Using Domain rank to rank pages");
			
			Map<String, Double> domainRankScore = new HashMap<>();

			Date startTime = Calendar.getInstance().getTime();
			
			for (String page : lookupList) {
				double score = pageRankAPI.getDomainRankCached(page);
				domainRankScore.put(page, score);
			}
			
			logger.info("Domain rank returned " + domainRankScore.size()
					+ " results");

			List<SearchResult> drResults = SearchEngineUtils
					.getSortedSearchResultUsingScores(domainRankScore);
			Date endTime = Calendar.getInstance().getTime();
			logger.info("Domain ranking took "+ SearchEngineUtils.printTimeDiff(startTime, endTime));
					
			buffer.append("<br>Using Page Ranking:");
			buffer.append("<ol>");
			for (SearchResult doc : drResults) {
				buffer.append("<li>" + doc.toHtml() + "</li>");
				lookupList.add(doc.getUrl());
				resultCount++;
				if (resultCount > 10) {
					break;
				}
			}
			buffer.append("</ol>");

			/******************************** Final display *********************/
			
			Double[] weights = { 0.98, 0.02 };
			List<SearchResult> result = SearchEngineUtils
					.weightedMergeScores(indexerScore, domainRankScore, weights);
			buffer.append("<br>Combined Results:<ol>");

			resultCount = 0;
			for (SearchResult doc : result) {
				buffer.append("<li>" + doc.toHtml() + "</li>");
				lookupList.add(doc.getUrl());
				resultCount++;
				if (resultCount > 10) {
					break;
				}
			}
			buffer.append("</ol>");
			
			buffer.append("</body></html>");
			out.write(buffer.toString());
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			buffer.append("<br>No results found!</body></html>");
			out.write(buffer.toString());
		} finally {

			out.flush();
			out.close();
		}
		
	}
}
