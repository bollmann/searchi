package edu.upenn.cis455.dao;

import java.util.HashSet;
import java.util.Set;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

@Entity
public class UserInfo {
	
	@PrimaryKey
	private String userName;
	
	private String password;
	
	private Set<String> channelNames;
	
	public UserInfo() {
		channelNames = new HashSet<String>();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getChannels() {
		return channelNames;
	}

	public void addChannel(Channel channel) {
		channelNames.add(channel.getName());
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Username:" + userName + " Password:" + password + " Channels:" + channelNames);
		return sb.toString();
	}
}
