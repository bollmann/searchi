package test.db.dbo;

import junit.framework.TestCase;

import org.junit.Test;

import db.dbo.URLMetaInfo;

public class TestURLMetaInfo extends TestCase {

	@Test
	public void testSameURLSameHash() {
		String url1 = "http://abc";
		String url2 = "http://abc";
		
		URLMetaInfo u1 = new URLMetaInfo(url1);
		URLMetaInfo u2 = new URLMetaInfo(url2);
		
		assertEquals(u1.getId(), u2.getId());
		
	}
}
