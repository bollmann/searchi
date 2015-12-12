package indexer.db.imports.adapters;

import com.google.gson.Gson;

import indexer.db.dao.InvertedIndex;

public class InvertedIndexAdapter implements
		FileToDatabaseAdapter<InvertedIndex> {

	@Override
	public String getTableName() {
		return InvertedIndex.TABLE_NAME;
	}
	
	@Override
	public InvertedIndex unserialize(String input) {
		return new Gson().fromJson(input, InvertedIndex.class);
	}

}
