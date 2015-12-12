package indexer.db.imports.adapters;

import indexer.db.dao.DocumentIndex;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import crawler.dao.URLContent;

public class DocumentIndexAdapter implements FileToDatabaseAdapter<DocumentIndex> {
	private static Logger logger = Logger.getLogger(DocumentIndexAdapter.class);
	
	@Override
	public String getTableName() {
		return DocumentIndex.TABLE_NAME;
	}
	
	@Override
	public DocumentIndex unserialize(String inputLine) {
		try {
			String parts[] = inputLine.split("\t");
			int docId = Integer.parseInt(parts[0]);
			URLContent page = new Gson().fromJson(parts[1], URLContent.class);

			return new DocumentIndex(docId, page.getUrl());
		} catch(JsonSyntaxException e) {
			logger.error("failed to parse JSON for input:");
			logger.error(inputLine);
			throw e;
		}
	}
}
