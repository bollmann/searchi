package edu.upenn.cis455.dao;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class ChannelAccessor {
	public PrimaryIndex<String, Channel> pIndex;
	
	public ChannelAccessor(EntityStore store) {
		pIndex = store.getPrimaryIndex(String.class, Channel.class);
		
	}

}
