package test.db.wrappers;

import junit.framework.TestCase;

import org.junit.Test;

import db.wrappers.S3Wrapper;

public class TestS3Wrapper extends TestCase {
	
	@Test
	public void testCreateBucket() {
		S3Wrapper wp = S3Wrapper.getInstance();
//		wp.deleteBucket("test");
		wp.createBucket("shreejittest");
		wp.putItem("shreejittest", "someKey", "some long content");
		
		String rContent = wp.getItem("shreejittest", "someKey");
		assertEquals("some long content", rContent);
		
//		wp.deleteBucket("shreejittest");
	}

}
