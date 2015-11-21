package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import edu.upenn.cis.cis455.parsers.Parser;
import edu.upenn.cis.cis455.policies.FilePolicy;
import edu.upenn.cis455.dao.Channel;
import edu.upenn.cis455.dao.ChannelAccessor;
import edu.upenn.cis455.dao.UserInfo;
import edu.upenn.cis455.dao.UserInfoAccessor;
import edu.upenn.cis455.storage.DBWrapper;

public class UserManagementServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		HttpSession session = request.getSession(false);
		UserInfo info = null;
		if (session == null) {
			try {
				response.sendRedirect("/login");
				return;
			} catch (IOException e) {
				logger.error("IOException");
				e.printStackTrace();
				return;
			}
		}

		String content = null;
		try {
			content = FilePolicy.readFile("resources/list_channels.html");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		String message = request.getParameter("message");
		if(message != null) {
			content = content.replace("<label>Message: None</label>", "<label>Message: " 
		+ URLDecoder.decode(message, "UTF-8") + "</label>");
		}

		String storeLoc = getServletContext().getInitParameter("BDBStore");
		Path dbPath = Paths.get(storeLoc);
		DBWrapper.initialize(dbPath.toAbsolutePath().toString());
		String userName = (String) session.getAttribute("username");
		UserInfoAccessor uiAccessor = new UserInfoAccessor(DBWrapper.getStore());
		info = uiAccessor.pIndex.get(userName);
		StringBuilder sb = new StringBuilder();
		sb.append("<ul>");
		List<Channel> cList = new ArrayList<Channel>();
		ChannelAccessor cAccessor = new ChannelAccessor(DBWrapper.getStore());
		
		for (String cName : info.getChannels()) {
			Channel c = cAccessor.pIndex.get(cName);
			cList.add(c);
		}
		DBWrapper.close();
		for (Channel channel : cList) {
			sb.append("<li><a href=\"/channels?name=" + channel.getName() + "\">"
					+ channel.getName() + "</a> - " + URLDecoder.decode(Arrays.toString(channel.getxPaths()), "UTF-8") + "</li>");
		}
		sb.append("</ul>");
		content = content.replace("<$channels$>", sb.toString());
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
	public void doPost(HttpServletRequest request, HttpServletResponse response) {
		
	}
}
