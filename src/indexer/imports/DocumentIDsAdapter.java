package indexer.imports;

import indexer.dao.DocumentIDRow;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import crawler.dao.URLContent;

public class DocumentIDsAdapter implements FileToDatabaseAdapter<DocumentIDRow> {
	private static Logger logger = Logger.getLogger(DocumentIDsAdapter.class);
	
	@Override
	public String getTableName() {
		return "DocumentIDs";
	}
	
	@Override
	public DocumentIDRow unserialize(String inputLine) {
		try {
			String parts[] = inputLine.split("\t");
			int docId = Integer.parseInt(parts[0]);
			URLContent page = new Gson().fromJson(parts[1], URLContent.class);

			return new DocumentIDRow(docId, page.getUrl());
		} catch(JsonSyntaxException e) {
			logger.error("failed to parse JSON for input:");
			logger.error(inputLine);
			throw e;
		}
	}
}
