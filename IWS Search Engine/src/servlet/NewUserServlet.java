package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.upenn.cis.cis455.parsers.Parser;
import edu.upenn.cis.cis455.policies.FilePolicy;
import edu.upenn.cis455.dao.UserInfo;
import edu.upenn.cis455.dao.UserInfoAccessor;
import edu.upenn.cis455.storage.DBWrapper;

public class NewUserServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		String content = null;
		try {
			content = FilePolicy.readFile("resources/create_user_form.html");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		String error = request.getParameter("error");
		if(error != null) {
			content = content.replace("<label>Errors: None</label>", "<label>Errors: " + URLDecoder.decode(error, "UTF-8") + "</label>");
		}
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
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String storeLoc = getServletContext().getInitParameter("BDBStore");
		
		Path dbPath = Paths.get(storeLoc);
		logger.info("Accessing dbPath at " + dbPath.toAbsolutePath());
		DBWrapper.initialize(dbPath.toAbsolutePath().toString());
		UserInfoAccessor uiAccessor = new UserInfoAccessor(DBWrapper.getStore());
		UserInfo userInfo = uiAccessor.pIndex.get(username);
		if(userInfo != null) {
			response.sendRedirect("/user/create?error=" + URLEncoder.encode("User already exists!", "UTF-8"));
		} else {
			userInfo = new UserInfo();
			userInfo.setPassword(password);
			userInfo.setUserName(username);
			uiAccessor.pIndex.put(userInfo);
			response.sendRedirect("/login");
		}
		DBWrapper.close();
	}

}
