package test.utils.nlp;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import utils.nlp.Dictionary;


public class TestDictionary {
	
	@Test
	public void testContains() throws IOException {
		Dictionary dict = Dictionary.getInstance();
		 if (dict == null) {
			 System.out.println("Sdf");
		 }
		Assert.assertTrue(dict.contains("amazing"));
		Assert.assertTrue(dict.contains("german"));
		Assert.assertTrue(dict.contains("crawl"));
		Assert.assertTrue(dict.contains("rank"));
		
	}

}
