package test.db.wrappers;

import junit.framework.TestCase;

import org.junit.Test;

import db.wrappers.S3Wrapper;

public class TestS3Wrapper extends TestCase {
	
//	@Test
//	public void testCreateBucket() {
//		S3Wrapper wp = S3Wrapper.getInstance();
////		wp.deleteBucket("test");
//		wp.createBucket("shreejittest");
//		wp.putItem("shreejittest", "someKey", "some long content");
//		
//		String rContent = wp.getItem("shreejittest", "someKey");
//		assertEquals("some long content", rContent);
//		
////		wp.deleteBucket("shreejittest");
//	}
	
	@Test
	public void testGetNumberOfItemsInBucket() {
		S3Wrapper s3 = S3Wrapper.getInstance();
		Integer result = s3.getNumberOfItemsInBucket("cis455-url-content");
		System.out.println(result);
	}
	
	@Test
	public void testFindDuplicateUrlsInBucket() {
		S3Wrapper s3 = S3Wrapper.getInstance();
		Integer count = s3.getDuplicateUrlsInBucket(s3.URL_BUCKET);
		System.out.println(count);
	}

}
