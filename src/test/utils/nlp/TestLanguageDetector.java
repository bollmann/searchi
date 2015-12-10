package test.utils.nlp;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import crawler.policies.FilePolicy;
import utils.nlp.LanguageDetector;

public class TestLanguageDetector extends TestCase {

	@Test
	public void testIsEnglish() {
		List<String> nonEnglish = Arrays.asList(
			"testcontent/cn.nytimes.html",
			"testcontent/sample-german-page",
			"testcontent/some-french-page.html",
			"testcontent/sample-spanish-page",
			"testcontent/sample-spanish-page"
//			"testcontent/sample-fake-english"
		);
		
		String content = null;
		for (String fileName : nonEnglish) {
			content = null;
			try {				
				content = FilePolicy.readFile(fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(content);		
			assertFalse(LanguageDetector.isEnglish(content));
		}
		
		List<String> english = Arrays.asList(
				"testcontent/sample-english-page");
		for (String fileName : english) {
			content = null;
			try {				
				content = FilePolicy.readFile(fileName);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println(content);		
			assertTrue(LanguageDetector.isEnglish(content));
		}
		
		

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
