package crawler.extractors;

import java.util.List;

public interface ContentExtractor {

	public List<String> extractLinks(String content);
}
