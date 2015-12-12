package indexer.db.imports.adapters;

import com.google.gson.Gson;

import indexer.db.dao.InvertedIndexRow;

public class InvertedIndexAdapter implements
		FileToDatabaseAdapter<InvertedIndexRow> {
	//public static final String TABLE_NAME = "InvertedIndexNew";

	@Override
	public String getTableName() {
		return "InvertedIndexNew";
	}
	
	@Override
	public InvertedIndexRow unserialize(String input) {
		return new Gson().fromJson(input, InvertedIndexRow.class);
	}

}
