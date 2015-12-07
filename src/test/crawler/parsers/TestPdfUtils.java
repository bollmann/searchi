package test.crawler.parsers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.tika.exception.TikaException;
import org.junit.Test;
import org.xml.sax.SAXException;

import crawler.parsers.PdfUtils;

public class TestPdfUtils extends TestCase {
	
	@Test
	public void testExtractContent() {
		String testFile = "testcontent/google-hacking.pdf";
		String readContent = null;
		try {
			readContent = PdfUtils.extractContent(new FileInputStream(new File(testFile)));
		} catch (IOException | SAXException | TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(readContent);
	}
}
