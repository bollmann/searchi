package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import edu.upenn.cis.cis455.policies.FilePolicy;
import edu.upenn.cis455.dao.UserInfo;
import edu.upenn.cis455.dao.UserInfoAccessor;
import edu.upenn.cis455.storage.DBWrapper;

public class UserLoginServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String content = null;
		try {
			content = FilePolicy.readFile("resources/login_form.html");
		} catch (IOException e) {
			logger.error("Couldn't read login form!");
			e.printStackTrace();
		}
//		logger.info("Writing content:" + content);
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

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		logger.info("Got parameters: " + request.getParameterNames());
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		String storeLoc = getServletContext().getInitParameter("BDBStore");
		Path dbPath = Paths.get(storeLoc);
		DBWrapper.initialize(dbPath.toAbsolutePath().toString());
		UserInfoAccessor uiAccessor = new UserInfoAccessor(DBWrapper.getStore());
		UserInfo info = uiAccessor.pIndex.get(userName);
		DBWrapper.close();
		try {
			if (info == null) {
				String message = "No such user present!";
				logger.error(message);
				response.sendRedirect("/login");
			} else {
				if (!info.getPassword().equals(password)) {
					String message = "User name password combination incorrect";
					logger.error(message);
					response.sendRedirect("/login");
				} else {
					HttpSession session = request.getSession(true);
					
					session.setAttribute("username", userName);
					response.sendRedirect("/user");
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
