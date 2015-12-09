package test.utils.nlp;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import crawler.policies.FilePolicy;
import utils.nlp.LanguageDetector;

public class TestLanguageDetector extends TestCase {

	@Test
	public void testIsEnglish() {
		String content = null;
		try {
			content = FilePolicy.readFile("testcontent/cn.nytimes.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertFalse(LanguageDetector.isEnglish(content));

	}
	
	@Test
	public void testIsEnglishChecksDifferentWords() {
		String contentPass = "the the to be of and";
		String contentFail = " I I of of";
		
		assertFalse(LanguageDetector.isEnglish(contentFail));
		assertTrue(LanguageDetector.isEnglish(contentPass));
	}
	
	@Test
	public void testIsEnglishWithMeere() {
		String content = null;
		try {
			content = FilePolicy.readFile("testcontent/meere.html");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertFalse(LanguageDetector.isEnglish(content));
	}
	
}
