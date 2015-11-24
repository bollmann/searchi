package indexer.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import indexer.DocumentVector;
import indexer.InvertedIndex;

@SuppressWarnings("serial")
public class IndexerClientServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		StringBuffer buffer = new StringBuffer();
		buffer.append("<html><head><title>Interface-Search Engine Test</title></head>");
		buffer.append("<body><form action = \"/indexerclient\" method = \"post\">");
		buffer.append("Enter your search query: <input size=50 name = \"query\">");
		buffer.append("<button>Search</button>");
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
		buffer.append("<body><ol>");
		
		String queryStr = req.getParameter("query");
		LinkedList<String> query = new LinkedList<String>();
		for(String word: queryStr.split(" "))
			query.add(word);
		InvertedIndex iiObj = new InvertedIndex();
		TreeSet<DocumentVector> results = iiObj.lookupDocuments(query);
		while(results.isEmpty() == false){
			DocumentVector doc = results.first();
			results.remove(doc);
			buffer.append("<li>" + doc.toString());
		}
		buffer.append("</ol></body></html>");
		out.append(buffer.toString());
		out.flush();
		out.close();
	}

}
