package test.indexer;

import indexer.api.DocumentIDs;

import org.junit.Test;

import junit.framework.TestCase;

public class TestDocumentIDs extends TestCase {
	
	@Test
	public void testAccess() {
		DocumentIDs docIds = DocumentIDs.getInstance();
		String url1 = docIds.getURLFor(9999);
		String url2 = docIds.getURLFor(18000);
		
		assertEquals("https://www.youtube.com/watch?v=NN05jCosAe8", url1);
		assertEquals("http://www.drudge.com/news/193745/blade-runner-re-sentenced-murder", url2);
	}
}
