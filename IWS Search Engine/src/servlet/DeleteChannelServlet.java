package edu.upenn.cis455.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import edu.upenn.cis.cis455.policies.FilePolicy;
import edu.upenn.cis455.dao.Channel;
import edu.upenn.cis455.dao.ChannelAccessor;
import edu.upenn.cis455.dao.UserInfo;
import edu.upenn.cis455.dao.UserInfoAccessor;
import edu.upenn.cis455.storage.DBWrapper;

public class DeleteChannelServlet extends HttpServlet {
	private final Logger logger = Logger.getLogger(getClass());

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		String content = null;
		try {
			content = FilePolicy.readFile("resources/delete_channel_form.html");
		} catch (IOException e) {
			logger.error("Couldn't read delete channel form!");
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
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
		String name = request.getParameter("name");
		String storeLoc = getServletContext().getInitParameter("BDBStore");
		Path dbPath = Paths.get(storeLoc);
		DBWrapper.initialize(dbPath.toAbsolutePath().toString());

		ChannelAccessor cAccessor = new ChannelAccessor(DBWrapper.getStore());
		Channel channel = cAccessor.pIndex.get(name);
		
		
		
		
		if(channel.getUserName().equals(user)) {
			UserInfoAccessor uiAccessor = new UserInfoAccessor(DBWrapper.getStore());
			UserInfo userInfo = uiAccessor.pIndex.get(user);
			userInfo.getChannels().remove(channel.getName());
			uiAccessor.pIndex.put(userInfo);
			cAccessor.pIndex.delete(channel.getName());
			response.sendRedirect("/user?message=" + URLEncoder.encode("Channel Deleted Sucessfully!", "UTF-8"));
		} else {
			response.sendRedirect("/user");
		}
		DBWrapper.close();
	}

}
