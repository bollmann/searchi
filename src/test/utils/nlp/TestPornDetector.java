package test.utils.nlp;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import utils.file.FileUtils;
import utils.nlp.PornDetector;

public class TestPornDetector {
	
	@Test
	public void testIsPorn() throws IOException {
		 String text = FileUtils.readFile("testcontent/SamplePornHtmlSource");
		 Assert.assertTrue(PornDetector.isPorn(text));
	}
}
