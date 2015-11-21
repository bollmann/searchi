package edu.upenn.cis455.dao;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class UserInfoAccessor {
	public PrimaryIndex<String, UserInfo> pIndex;

	public UserInfoAccessor(EntityStore store) {
		pIndex = store.getPrimaryIndex(String.class, UserInfo.class);

	}
}
