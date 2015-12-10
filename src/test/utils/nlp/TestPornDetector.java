package test.utils.nlp;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import utils.file.FilePolicy;
import utils.nlp.PornDetector;

public class TestPornDetector {
	
	@Test
	public void testIsPorn() throws IOException {
		 String text = FilePolicy.readFile("testcontent/SamplePornHtmlSource");
		 Assert.assertTrue(PornDetector.isPorn(text));
	}
}
