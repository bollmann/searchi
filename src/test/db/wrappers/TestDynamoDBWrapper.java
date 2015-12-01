package test.db.wrappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;
import com.amazonaws.services.dynamodbv2.model.DescribeTableResult;

import db.dbo.URLMetaInfo;
import db.wrappers.DynamoDBWrapper;

public class TestDynamoDBWrapper extends TestCase {

	@Test
	public void testBatchLoad() {
		DynamoDBWrapper wp = DynamoDBWrapper.getInstance("http://localhost:8000");
		wp.deleteTable("URLMetaInfo");
		DescribeTableResult descr = wp.describeTable("URLMetaInfo");
		System.out.println(descr);
		wp.createTable("URLMetaInfo", 100, 100, "url", "S");
		URLMetaInfo info1 = new URLMetaInfo("abc1");
		wp.putItem(info1);
		URLMetaInfo info2 = new URLMetaInfo("abc2");
		wp.putItem(info2);
		URLMetaInfo info3 = new URLMetaInfo("abc3");
		wp.putItem(info3);
		
		List<String> query = new ArrayList<String>() {{
			add("abc1");
			add("abc2");
			add("abc3");
		}};
		
		List<Object> ids = URLMetaInfo.convertToQuery(query);
		Map<String, List<Object>> result = wp.getBatchItem(ids);
		System.out.println("Result is" + result);
		List<String> resultIds = URLMetaInfo.convertLinksToIds(query, wp);
		assertEquals(info1.getId(), resultIds.get(0));
		assertEquals(info2.getId(), resultIds.get(1));
		assertEquals(info3.getId(), resultIds.get(2));
		wp.deleteTable("URLMetaInfo");
	}
	
	/*
	 * @Test
	public void testCreateRemoteTable() {
		DynamoDBWrapper wp = DynamoDBWrapper.getInstance(DynamoDBWrapper.URL_CONTENT_ENDPOINT);
//		wp.deleteTable("URLMetaInfo");
		DescribeTableResult result = wp.describeTable("URLMetaInfo");
		System.out.println(result);
		wp.createTable("URLMetaInfo", 100, 100, "url", "S");
		URLMetaInfo info = new URLMetaInfo("abc");
		wp.putItem(info);

		try {
			// DynamoDBMapper map = wp.getMapper();
			// URLMetaInfo rInfo = map.load(URLMetaInfo.class, info.getUrl());
			URLMetaInfo rInfo = (URLMetaInfo) wp.getItem("abc",
					URLMetaInfo.class);
			assertEquals("abc", rInfo.getUrl());
			System.out.println(rInfo.getId());
		} catch (DynamoDBMappingException e) {
			e.printStackTrace();
		}
		wp.deleteTable("URLMetaInfo");
	}
	*/
	
	@Test
	public void testCreateTable() {
		DynamoDBWrapper wp = DynamoDBWrapper.getInstance("http://localhost:8000");
		wp.deleteTable("URLMetaInfo");
		DescribeTableResult result = wp.describeTable("URLMetaInfo");
		System.out.println(result);
		wp.createTable("URLMetaInfo", 100, 100, "url", "S");
		URLMetaInfo info = new URLMetaInfo("abc");
		wp.putItem(info);

		try {
			// DynamoDBMapper map = wp.getMapper();
			// URLMetaInfo rInfo = map.load(URLMetaInfo.class, info.getUrl());
			URLMetaInfo rInfo = (URLMetaInfo) wp.getItem("abc",
					URLMetaInfo.class);
			assertEquals("abc", rInfo.getUrl());
			System.out.println(rInfo.getId());
		} catch (DynamoDBMappingException e) {
			e.printStackTrace();
		}
		wp.deleteTable("URLMetaInfo");
	}
	
	@Test
	public void testGetForNonExistentItem() {
		DynamoDBWrapper wp = DynamoDBWrapper.getInstance("http://localhost:8000");
//		wp.deleteTable("URLMetaInfo");
		wp.createTable("URLMetaInfo", 100, 100, "url", "S");
		URLMetaInfo info = new URLMetaInfo("abc");
		wp.putItem(info);

		try {
			// DynamoDBMapper map = wp.getMapper();
			// URLMetaInfo rInfo = map.load(URLMetaInfo.class, info.getUrl());
			URLMetaInfo rInfo = (URLMetaInfo) wp.getItem("pqr",
					URLMetaInfo.class);
			assertNull(rInfo);
		} catch (DynamoDBMappingException e) {
			e.printStackTrace();
		}
		wp.deleteTable("URLMetaInfo");
	}
	
	@Test
	public void testGetNumberOfItemsInTable() {
		DynamoDBWrapper ddb = DynamoDBWrapper.getInstance(DynamoDBWrapper.US_EAST);
		Integer result = ddb.getNumberOfItemsInTable("URLMetaInfoCopy");
		System.out.println(result);
	}

}
