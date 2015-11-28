package indexer.offline;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.hadoop.io.Writable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InputDocument implements Writable {
	public static final String TEXT_SPLIT = "[^a-zA-Z0-9'-]+";
	public static final String AHREF_SPLIT = "\\s+|[.,;_/-]+";
	
	private List<String> headers;
	private List<String> links;
	private List<String> meta;
	private List<String> content;
	
	public InputDocument(String html, String baseURI) {
		Document doc = Jsoup.parse(html, baseURI);
		this.headers = extractText(doc, "title,h1,h2,h3,h4,h5,h6");
		this.links = extractLinks(doc);
		this.meta = extractMetaTags(doc);
		this.content = extractText(doc, "body");
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		headers = readList(in);
		links = readList(in);
		meta = readList(in);
		content = readList(in);
	}
	
	@Override
	public void write(DataOutput out) throws IOException {
		writeList(out, headers);
		writeList(out, links);
		writeList(out, meta);
		writeList(out, content);
	}
	
	public List<String> getHeaders() {
		return headers;
	}

	public List<String> getLinks() {
		return links;
	}

	public List<String> getMeta() {
		return meta;
	}

	public List<String> getContent() {
		return content;
	}

	private List<String> readList(DataInput in) throws IOException {
		List<String> result = new LinkedList<>();
		String line = null;
		while ((line = in.readUTF()) != null) {
			if(line.equals(""))
				break;
			result.add(line);
		}
		return result;
	}
	
	private void writeList(DataOutput out, List<String> list) throws IOException {
		for(String item: list) {
			out.writeUTF(item + '\n');
		}
		out.writeUTF("\n");
	}
	
	private static List<String> extractLinks(Document dom) {
		Elements links = dom.select("a[href]");
		List<String> tokens = new LinkedList<String>();
		for(Element link: links) {
			tokens.addAll(Arrays.asList(link.attr("href").split(AHREF_SPLIT)));
			tokens.addAll(Arrays.asList(link.text().split(TEXT_SPLIT)));
		}
		return tokens;
	}
	
	private static List<String> extractMetaTags(Document dom) {
		Elements links = dom.select("meta[name~=(description|keywords)][content]");
		List<String> tokens = new LinkedList<String>();
		for(Element link: links)
			tokens.addAll(Arrays.asList(link.attr("content").split(TEXT_SPLIT)));
		return tokens;
	}
	
	private static List<String> extractText(Document dom, String selector) {
		Elements headers = dom.select(selector);
		List<String> tokens = new LinkedList<String>();
		for(Element header: headers)
			tokens.addAll(Arrays.asList(header.text().split(TEXT_SPLIT)));
		return tokens;
	}
}