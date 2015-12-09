package indexer.servlets;

import indexer.DocumentScore;
import indexer.InvertedIndex;
import indexer.api.DocumentIDs;
import indexer.dao.DocumentFeatures;
import indexer.ranking.Ranker;

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
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import pagerank.api.PageRankAPI;
import utils.searchengine.SearchEngineUtils;

@SuppressWarnings("serial")
public class IndexerClientServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());
	InvertedIndex iiObj = null;

	@Override
	public void init() {
		iiObj = new InvertedIndex();
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

	private String printTimeDiff(Date startTime, Date endTime) {
		long timeDiff = (endTime.getTime() - startTime.getTime());
		return timeDiff / 1000 + "s," + timeDiff % 1000 + "ms:";
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
		LinkedList<String> query = new LinkedList<String>();

		queryStr = URLDecoder.decode(queryStr, "UTF-8");

		for (String word : queryStr.split(" "))
			query.add(word);

		logger.info("Got search query:" + queryStr + " split into " + query);

		// for (DocumentVector doc : iiObj.lookupDocuments(query)) {
		// buffer.append("<li>" + doc.toString() + "</li>");
		//
		// }

		Map<String, Double> indexerScore = new HashMap<String, Double>(1000);
		Map<String, Double> pageRankScore = null;

		PageRankAPI pra = new PageRankAPI();
		List<String> lookupList = new ArrayList<String>(1000);

		try {
			/******************************** Indexer display *********************/

			
			buffer.append("<br>Using Indexer Ranking:");
			buffer.append("<ol>");

			Date startTime = Calendar.getInstance().getTime();
			Map<String, List<DocumentFeatures>> invertedIndex = iiObj.getInvertedIndexForQueryMultiThreaded(query);
			Date endTime = Calendar.getInstance().getTime();
			logger.info("Indexer fetch took "
					+ printTimeDiff(startTime, endTime));
			Map<String, Integer> wordDfs = new HashMap<String, Integer>();
			for(Entry<String, List<DocumentFeatures>> entry : invertedIndex.entrySet()) {
				wordDfs.put(entry.getKey(), entry.getValue().size());
			}
			List<DocumentScore> documentList = Ranker.getDocumentScoresForQueryAndInvertedIndex(query, invertedIndex);
			
			/****************************** Add rankers and combine them here *************/
			startTime = Calendar.getInstance().getTime();
			Map<Integer, DocumentScore> tfIdfRankedDocs = Ranker.rankDocumentsOnTfIdf(documentList, query, iiObj.getCorpusSize(), wordDfs);
			Map<Integer, DocumentScore> headerCountRankedDocs = Ranker.rankDocumentsOnHeaderCount(documentList);
			Map<Integer, DocumentScore> linkCountRankedDocs = Ranker.rankDocumentsOnLinkCount(documentList);
			Map<Integer, DocumentScore> metaCountRankedDocs = Ranker.rankDocumentsOnMetaCount(documentList);
			Map<Integer, DocumentScore> totalCountRankedDocs = Ranker.rankDocumentsOnTotalCount(documentList);
			Map<Integer, DocumentScore> queryWordPresenceRankedDocs = Ranker.rankDocumentsOnQueryWordPresenceCount(documentList, query);
			
			List<Map<Integer, DocumentScore>> rankedLists = new ArrayList<>();
			rankedLists.add(tfIdfRankedDocs);
			rankedLists.add(headerCountRankedDocs);
			rankedLists.add(linkCountRankedDocs);
			rankedLists.add(metaCountRankedDocs);
			rankedLists.add(totalCountRankedDocs);
			rankedLists.add(queryWordPresenceRankedDocs);
			
			List<Double> rankWeights = new ArrayList<>();
			rankWeights.add(1.0); // tfidf
			rankWeights.add(1.0); // headerCounts
			rankWeights.add(1.0); // linkCounts
			rankWeights.add(1.0); // metaCounts
			rankWeights.add(1.0); // totalCounts
			rankWeights.add(4.0); // query word counts
			List<DocumentScore> combinedRankedDocs = Ranker.combineRankedListsWithWeights(rankedLists, rankWeights);
			
			endTime = Calendar.getInstance().getTime();
			logger.info("Indexer ranking took "
					+ printTimeDiff(startTime, endTime));
			/****************************** End of secret sauce ****************************/
			
			DocumentIDs dId = new DocumentIDs();
			
			int resultCount = 0;
			for (DocumentScore doc : combinedRankedDocs) {
				SearchResult sr = new SearchResult();
				// lookup id to get document
				String url = dId.getUrlFor(doc.getDocId());
				sr.setUrl(url);
				sr.setScore(doc.getScore());
				sr.setSnippet(tfIdfRankedDocs.get(url).toString());
				buffer.append("<li>" + sr.toHtml() + "</li>");
				indexerScore.put(url, (double) doc.getScore());
				lookupList.add(url);
				resultCount++;
				if (resultCount > 40) {
					break;
				}
			}
			buffer.append("</ol>");
			

			/******************************** Page Rank display *********************/

			startTime = Calendar.getInstance().getTime();
			pageRankScore = pra.getPageRankBatch(lookupList);

			logger.info("Page rank returned " + pageRankScore.size()
					+ " results");
			List<SearchResult> pqueue = SearchEngineUtils
					.convertScoreMapToPriorityQueue(pageRankScore);

			endTime = Calendar.getInstance().getTime();
			logger.info("Page ranking took "
					+ printTimeDiff(startTime, endTime));
			resultCount = 0;
			buffer.append("<br>Using Page Ranking:");
			buffer.append("<ol>");
			for (SearchResult doc : pqueue) {
				buffer.append("<li>" + doc.toHtml() + "</li>");
				lookupList.add(doc.getUrl());
				resultCount++;
				if (resultCount > 10) {
					break;
				}
			}
			buffer.append("</ol>");

			

			/******************************** Final display *********************/
			startTime = Calendar.getInstance().getTime();
			Double[] weights = { 0.9, 0.1 };
			List<SearchResult> result = SearchEngineUtils
					.weightedMergeScores(indexerScore, pageRankScore, weights);
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
			endTime = Calendar.getInstance().getTime();
			logger.info("Combined ranking took "
					+ printTimeDiff(startTime, endTime));
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
