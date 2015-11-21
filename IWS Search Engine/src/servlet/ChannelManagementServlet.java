package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.sleepycat.persist.EntityCursor;

import edu.upenn.cis.cis455.parsers.Parser;
import edu.upenn.cis.cis455.policies.FilePolicy;
import edu.upenn.cis455.dao.Channel;
import edu.upenn.cis455.dao.ChannelAccessor;
import edu.upenn.cis455.dao.URLContent;
import edu.upenn.cis455.dao.URLContentAccessor;
import edu.upenn.cis455.dao.UserInfo;
import edu.upenn.cis455.dao.UserInfoAccessor;
import edu.upenn.cis455.storage.DBWrapper;

public class ChannelManagementServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {

		String content = null;
		try {
			content = FilePolicy.readFile("resources/display_channel.html");
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		String storeLoc = getServletContext().getInitParameter("BDBStore");
		Path dbPath = Paths.get(storeLoc);
		DBWrapper.initialize(dbPath.toAbsolutePath().toString());
		ChannelAccessor cAccessor = new ChannelAccessor(DBWrapper.getStore());
		String channelName = request.getParameter("name");
		StringBuilder sb = new StringBuilder();

		URLContentAccessor ucAccessor = new URLContentAccessor(
				DBWrapper.getStore());
		if (channelName == null) {
			// show all channels
			EntityCursor<Channel> cCursor = cAccessor.pIndex.entities();
			for (Channel channel : cCursor) {
				sb.append("<h2>Channel:" + channel.getName()
						+ "</h2> - xPaths: " + URLDecoder.decode(Arrays.toString(channel.getxPaths()), "UTF-8") + " <br/>");
				for (String url : channel.getMatchedPages()) {
					URLContent urlContent = ucAccessor.pIndex.get(url);
					sb.append(Parser.formatURLContent(urlContent));
					sb.append("<br/><br/>");
				}
				sb.append("<br/>");
			}
			cCursor.close();
		} else {
			// show specific channel
			Channel channel = cAccessor.pIndex.get(channelName);
			sb.append("<h2>Channel:" + channel.getName() 
					+ "</h2> - xPaths: " + URLDecoder.decode(Arrays.toString(channel.getxPaths()), "UTF-8") + " <br/>");
			for (String url : channel.getMatchedPages()) {
				URLContent urlContent = ucAccessor.pIndex.get(url);
				sb.append(Parser.formatURLContent(urlContent));
				sb.append("<br/><br/>");
			}
			sb.append("<br/>");
		}
		DBWrapper.close();
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
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		List<String> paramNames = Collections.list(request.getParameterNames());
		logger.info("Got parameters: " + paramNames);
		String name = request.getParameter("name");
		String xs = request.getParameter("xPaths");
		HttpSession session = request.getSession(false);
		if (session == null) {
			try {
				response.sendRedirect("/login");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		}
		String user = (String) session.getAttribute("username");
		String[] xPaths = xs.split(";");

		String storeLoc = getServletContext().getInitParameter("BDBStore");
		Path dbPath = Paths.get(storeLoc);
		DBWrapper.initialize(dbPath.toAbsolutePath().toString());

		UserInfoAccessor uiAccessor = new UserInfoAccessor(DBWrapper.getStore());
		UserInfo userInfo = uiAccessor.pIndex.get(user);

		ChannelAccessor cAccessor = new ChannelAccessor(DBWrapper.getStore());
		Channel channel = cAccessor.pIndex.get(name);
		if (channel == null) {
			channel = new Channel();
			channel.setName(name);
			channel.setxPaths(xPaths);
			channel.setUserName(user);
			logger.info("Saved channel " + channel.getName() + " by user "
					+ user);
			cAccessor.pIndex.put(channel);
			userInfo.addChannel(channel);
			uiAccessor.pIndex.put(userInfo);
			DBWrapper.close();
			response.sendRedirect("/user");
		} else {
			DBWrapper.close();
			try {
				response.sendError(400, "Channel already exists! Created by:"
						+ channel.getUserName());
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
}
