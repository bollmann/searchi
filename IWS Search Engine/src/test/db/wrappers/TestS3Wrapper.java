package test.db.wrappers;

import junit.framework.TestCase;

import org.junit.Test;

import db.wrappers.S3Wrapper;

public class TestS3Wrapper extends TestCase {
	
	@Test
	public void testCreateBucket() {
		S3Wrapper wp = S3Wrapper.getInstance("http://s3.amazonaws.com:8001");
		wp.deleteBucket("test");
		wp.createBucket("test");
		wp.putItem("test", "someKey", "some long content");
		
		String rContent = wp.getItem("test", "someKey");
		assertEquals("some long content", rContent);
		
		wp.deleteBucket("test");
	}

}
