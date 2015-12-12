package indexer.db.imports.adapters;

import indexer.db.dao.ImageIndex;

import com.google.gson.Gson;

public class ImageIndexAdapter implements FileToDatabaseAdapter<ImageIndex> {

	@Override
	public String getTableName() {
		return ImageIndex.TABLE_NAME;
	}

	@Override
	public ImageIndex unserialize(String input) {
		return new Gson().fromJson(input, ImageIndex.class);
	}
}
