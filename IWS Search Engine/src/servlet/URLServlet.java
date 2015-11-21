package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sleepycat.persist.EntityCursor;

import edu.upenn.cis.cis455.parsers.Parser;
import edu.upenn.cis.cis455.policies.FilePolicy;
import edu.upenn.cis455.dao.URLContent;
import edu.upenn.cis455.dao.URLContentAccessor;
import edu.upenn.cis455.storage.DBWrapper;

public class URLServlet extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String content = null;
		try {
			content = FilePolicy.readFile("resources/list_urls.html");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String storeLoc = getServletContext().getInitParameter("BDBStore");
		Path dbPath = Paths.get(storeLoc);
		DBWrapper.initialize(dbPath.toAbsolutePath().toString());
		URLContentAccessor ucAccessor = new URLContentAccessor(DBWrapper.getStore());
		
		StringBuilder sb = new StringBuilder();
		sb.append("<h3>List of urls crawled:</h3><br/>");
		sb.append("<ol>");
		
		EntityCursor<URLContent> uCursor = ucAccessor.pIndex.entities();
		
		for(URLContent urlContent : uCursor) {
			sb.append("<li>" + urlContent.getAbsolutePath() 
					+ " - Crawled on:" + Parser.formatTDate(urlContent.getCrawledOn()) + "</li>");
		}
		uCursor.close();
		DBWrapper.close();
		
		sb.append("</ol>");
		
		
		content = content.replace("<$urls$>", sb.toString());
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		out.write(content);
		out.flush();
	}

}
