package searchengine.servlets;

import indexer.DocumentScore;
import indexer.InvertedIndexFetcher;
import indexer.api.DocumentIDs;
import indexer.clients.InvertedIndexClient;
import indexer.db.dao.DocumentFeatures;
import searchengine.api.SearchAPI;
import searchengine.ranking.Ranker;
import searchengine.ranking.RankerImpl;
import searchengine.ranking.RankerInfo.RankerType;
import searchengine.ranking.RankerTfIdf;

import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
	DocumentIDs dId = null;
	@Override
	public void init() {
		dId = new DocumentIDs();
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

		Map<String, Double> indexerScore = new HashMap<String, Double>(1000);
		try {
			/******************************** Indexer display *********************/

			
			buffer.append("<br>Using Indexer Ranking:");
			buffer.append("<ol>");

			InvertedIndexClient iic = InvertedIndexClient.getInstance();
			Date startTime = Calendar.getInstance().getTime();
			Map<String, List<DocumentFeatures>> invertedIndex = iic.getInvertedIndexForQueryMultiThreaded(query);
			Date endTime = Calendar.getInstance().getTime();
			
			logger.info("Indexer fetch took "
					+ printTimeDiff(startTime, endTime));
			
		
			/******************* Add rankers and combine them here - Secret sauce ******************/
//			startTime = Calendar.getInstance().getTime();
//			
//			int numOfRankers = 5;
//			Ranker rankerTfIdf = new RankerTfIdf(documentList, query, iic.getCorpusSize(), wordDfs);
//			
//			
//			ExecutorService es = Executors.newFixedThreadPool(numOfRankers);
//			es.execute(rankerTfIdf);
//			
//			es.shutdown();			
//			try {
//				boolean finshed = es.awaitTermination(1, TimeUnit.MINUTES);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			Map<Integer, DocumentScore> tfIdfRankedDocs = RankerImpl.rankDocumentsOnTfIdf(documentList, query, iic.getCorpusSize(), wordDfs);
//			Map<Integer, DocumentScore> headerCountRankedDocs = RankerImpl.rankDocumentsOnHeaderCount(documentList);
////			Map<Integer, DocumentScore> linkCountRankedDocs = Ranker.rankDocumentsOnLinkCount(documentList);
////			Map<Integer, DocumentScore> metaCountRankedDocs = Ranker.rankDocumentsOnMetaCount(documentList);
//			Map<Integer, DocumentScore> totalCountRankedDocs = RankerImpl.rankDocumentsOnTotalCount(documentList);
//			Map<Integer, DocumentScore> queryWordPresenceRankedDocs = RankerImpl.rankDocumentsOnQueryWordPresenceCount(documentList, query);
//			Map<Integer, DocumentScore> positionRankedDocs = RankerImpl.rankDocumentsOnPosition(documentList, query);

			
			SearchAPI searchAPI = new SearchAPI(query, invertedIndex, iic.getCorpusSize());			
			searchAPI.formDocumentScoresForQueryFromInvertedIndex();
			List<DocumentScore> rankedDocs = null;
			try {
				searchAPI.setRanker(RankerType.RANKER_TFIDF, 1.0);
				searchAPI.setRanker(RankerType.RANKER_HEADER, 1.0);
				searchAPI.setRanker(RankerType.RANKER_LINKS, 1.0);
				searchAPI.setRanker(RankerType.RANKER_META, 1.0);
				searchAPI.setRanker(RankerType.RANKER_POSITION, -1.0);
				searchAPI.setRanker(RankerType.RANKER_QUERYMATCH, 1.0);
				searchAPI.setRanker(RankerType.RANKER_TOTALCOUNT, 1.0);

				List<Ranker> rankers = searchAPI.applyRankers();
				
				rankedDocs = searchAPI.combineRankings(rankers);
				Collections.sort(rankedDocs);
			} catch (Exception e) {
				//TODO Handle this exception;
				return;
			}
			
			
//			List<Map<Integer, DocumentScore>> rankedLists = new ArrayList<>();
//			rankedLists.add(tfIdfRankedDocs);
//			rankedLists.add(headerCountRankedDocs);
////			rankedLists.add(linkCountRankedDocs);
////			rankedLists.add(metaCountRankedDocs);
//			rankedLists.add(totalCountRankedDocs);
//			rankedLists.add(queryWordPresenceRankedDocs);
//			rankedLists.add(positionRankedDocs);
//			
//			List<Double> rankWeights = new ArrayList<>();
//			rankWeights.add(1.0); // tfidf
//			rankWeights.add(1.0); // headerCounts
////			rankWeights.add(1.0); // linkCounts
////			rankWeights.add(1.0); // metaCounts
//			rankWeights.add(1.0); // totalCounts
//			rankWeights.add(1.0); // query word counts
//			rankWeights.add(1.0); // positions
//			
//			List<DocumentScore> combinedRankedDocs = RankerImpl.combineRankedListsWithWeights(rankedLists, rankWeights);

			endTime = Calendar.getInstance().getTime();
			logger.info("Indexer ranking took "
					+ printTimeDiff(startTime, endTime));
			/****************************** End of secret sauce ****************************/
			
			List<String> lookupList = new ArrayList<String>(1000);
			
			startTime = Calendar.getInstance().getTime();			
			logger.info(rankedDocs.size());
			int resultCount = 0;
			for (DocumentScore doc : rankedDocs) {
				
				logger.info("Score " + doc.getScore());
				SearchResult sr = new SearchResult();
				// lookup id to get document
				String url = dId.getURLFor(doc.getDocId());
				sr.setUrl(url);
				sr.setScore(doc.getScore());
				sr.setSnippet(doc.toString());
				buffer.append("<li>" + sr.toHtml() + "</li>");
				indexerScore.put(url, (double) doc.getScore());
				lookupList.add(url);
				resultCount++;
				if (resultCount > 10) {
					break;
				}
			}
			buffer.append("</ol>");
			endTime = Calendar.getInstance().getTime();
			logger.info("DucumentScore copy took "
					+ printTimeDiff(startTime, endTime));

			/******************************** Page Rank display *********************/

			logger.info("Using Domain rank to rank pages");
			Map<String, Double> domainRankScore = new HashMap<>();
			PageRankAPI pageRankAPI = new PageRankAPI();
			
			startTime = Calendar.getInstance().getTime();
			
			for (String page : lookupList) {
				double score = pageRankAPI.getDomainRank(page);
				domainRankScore.put(page, score);
			}
			//domainRankScore = pageRankAPI.getDomainRankBatch(lookupList);

			logger.info("Page rank returned " + domainRankScore.size()
					+ " results");
			List<SearchResult> pqueue = SearchEngineUtils
				.convertScoreMapToPriorityQueue(domainRankScore);

			endTime = Calendar.getInstance().getTime();
			logger.info("Domain ranking took "
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
