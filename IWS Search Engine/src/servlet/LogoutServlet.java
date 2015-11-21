package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import edu.upenn.cis.cis455.policies.FilePolicy;

public class LogoutServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String content = null;
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
		
		try {
			content = FilePolicy.readFile("resources/logout.html");
		} catch (IOException e) {
			logger.error("Couldn't read login form!");
			e.printStackTrace();
		}
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			logger.error("Error in writing to response writer");
			e.printStackTrace();
		}
		out.write(content);
		out.flush();
	}

}
