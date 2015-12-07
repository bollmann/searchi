package crawler.parsers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

public class PdfUtils {

	public static String extractContent(InputStream inputStream) throws IOException, SAXException, TikaException {
		BodyContentHandler handler = new BodyContentHandler();
		Metadata metadata = new Metadata();
//		InputStream inputstream = new FileInputStream(new File(
//				"testcontent/google-hacking.pdf"));
		ParseContext pcontext = new ParseContext();

		// parsing the document using PDF parser
		PDFParser pdfparser = new PDFParser();
		pdfparser.parse(inputStream, handler, metadata, pcontext);

		// getting the content of the document
		System.out.println("Contents of the PDF :" + handler.toString());
		
		StringBuilder sb = new StringBuilder();
		sb.append(handler.toString());

		// getting metadata of the document
		System.out.println("Metadata of the PDF:");
		String[] metadataNames = metadata.names();

		for (String name : metadataNames) {
//			System.out.println(name + " : " + metadata.get(name));
			sb.append("\n" + name + ":" + metadata.get(name));
		}
		return sb.toString();
	}
}
