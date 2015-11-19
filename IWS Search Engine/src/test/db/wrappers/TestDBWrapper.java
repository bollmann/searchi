package test.db.wrappers;

import junit.framework.TestCase;

import org.junit.Test;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMappingException;

import db.dbo.URLMetaInfo;
import db.wrappers.DBWrapper;

public class TestDBWrapper extends TestCase {

	@Test
	public void testCreateTable() {
		DBWrapper wp = DBWrapper.getInstance("http://localhost:8000");
//		wp.deleteTable("URLMetaInfo");
		wp.createTable("URLMetaInfo", 100, 100, "url", "S");
		URLMetaInfo info = new URLMetaInfo("abc");
		wp.putItem(info);

		try {
			// DynamoDBMapper map = wp.getMapper();
			// URLMetaInfo rInfo = map.load(URLMetaInfo.class, info.getUrl());
			URLMetaInfo rInfo = (URLMetaInfo) wp.getItem("abc",
					URLMetaInfo.class);
			assertEquals("abc", rInfo.getUrl());
		} catch (DynamoDBMappingException e) {
			e.printStackTrace();
		}
		wp.deleteTable("URLMetaInfo");
	}
	
	@Test
	public void testGetForNonExistentItem() {
		DBWrapper wp = DBWrapper.getInstance("http://localhost:8000");
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

}
