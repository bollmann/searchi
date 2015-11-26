package indexer.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import indexer.DocumentVector;
import indexer.InvertedIndex;
import indexer.WordDocumentStatistics;

@SuppressWarnings("serial")
public class IndexerClientServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head><title>Interface-Search Engine Test</title></head>");
		buffer.append("<body><form action = \"/indexerclient\" method = \"post\">");
		buffer.append("<input size=50 name = \"query\">");
		buffer.append("<button>Searchi</button>");
		buffer.append("</form></body></html>");
		out.append(buffer.toString());
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head><title>Interface-Search Engine Test</title></head>");
		buffer.append("<body>Using lookup documents:<ol>");
		
		String queryStr = req.getParameter("query");
		LinkedList<String> query = new LinkedList<String>();
		for(String word: queryStr.split(" "))
			query.add(word);
		InvertedIndex iiObj = new InvertedIndex();
		for(DocumentVector doc: iiObj.lookupDocuments(query)){
			buffer.append("<li>" + doc.toString() + "</li>");
		}
		buffer.append("</ol><br>Using rankDocuments<ol>" );
		for(WordDocumentStatistics doc :iiObj.rankDocuments(query)){
			buffer.append("<li>" + doc.toString() + "</li>");
		}
		buffer.append("</ol></body></html>");
		out.append(buffer.toString());
		out.flush();
		out.close();
	}
	
	static public void main(String[] args){
		InvertedIndex iiObj = new InvertedIndex();
		LinkedList<String> query = new LinkedList<String>();
		for(String word: "astronomer".split(" "))
			query.add(word);
		TreeSet<DocumentVector> results = iiObj.lookupDocuments(query);
		PriorityQueue<WordDocumentStatistics> results2 = iiObj.rankDocuments(query);
			
		int count = 0;
		for(DocumentVector doc: results){
			if(count == 10)
				break;
			count++;
			System.out.println(doc.toString());
		}
		System.out.println("----------");
		count = 0;
		for(WordDocumentStatistics doc: results2){
			if(count == 10)
				break;
			count++;
			System.out.println(doc.toString());
		}
	}

}
