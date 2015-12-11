package indexer.db.imports;

public interface FileToDatabaseAdapter<T> {
	public String getTableName();
	public T unserialize(String input);
}
