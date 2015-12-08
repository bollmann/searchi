package test.utils.string;

import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;

import utils.string.StringUtils;

@RunWith(JUnit4.class)
public class TestStringUtils {
	
	@Test
	public void testNormalizeUrlToString() {
		Assert.assertEquals(StringUtils.normalizeUrlToString("www.google.com/abc"), "google.com/abc");
		Assert.assertEquals(StringUtils.normalizeUrlToString("https://www.youtube.com/abc/"), "youtube.com/abc");
		Assert.assertEquals(StringUtils.normalizeUrlToString("http://yahoo.com"), "yahoo.com");
		Assert.assertEquals(StringUtils.normalizeUrlToString("http://trippy.com/avbsd/#"), "trippy.com/avbsd");
		Assert.assertEquals(StringUtils.normalizeUrlToString("http://en.wikipedia.org"), "en.wikipedia.org");
		
	}
	
	@Test
	public void testGetDomainFromUrl() throws MalformedURLException {
		
		Assert.assertNull(StringUtils.getDomainFromUrl(null));
		Assert.assertEquals("www.google.com",
			StringUtils.getDomainFromUrl("http://www.google.com/abc/def"));
		Assert.assertEquals("yahoo.com",
				StringUtils.getDomainFromUrl("http://yahoo.com/abc/def/#"));
		Assert.assertEquals("www.facebook.com",
				StringUtils.getDomainFromUrl("https://www.facebook.com/homepage?a=d"));
		
	}

}
