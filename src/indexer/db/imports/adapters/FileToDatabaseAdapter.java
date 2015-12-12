package indexer.db.imports.adapters;

/**
 * Adapter to read in a line from S3 and to create a suitable DynamoDB row from
 * it
 */
public interface FileToDatabaseAdapter<T> {
	public String getTableName();

	public T unserialize(String input);
}
