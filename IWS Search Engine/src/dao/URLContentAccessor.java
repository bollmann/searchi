package edu.upenn.cis455.dao;

import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;

public class URLContentAccessor {
	public PrimaryIndex<String, URLContent> pIndex;

	public URLContentAccessor(EntityStore store) {
		pIndex = store.getPrimaryIndex(String.class, URLContent.class);

	}
}
