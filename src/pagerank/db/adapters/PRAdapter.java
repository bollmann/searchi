package pagerank.db.adapters;

import indexer.db.imports.adapters.FileToDatabaseAdapter;

import org.apache.log4j.Logger;

import pagerank.db.dao.PRDao;
import pagerank.db.ddl.PRCreateTable;

public class PRAdapter implements FileToDatabaseAdapter<PRDao>{

	private static Logger logger = Logger.getLogger(PRAdapter.class);
	
	@Override
	public String getTableName() {
		return PRCreateTable.PR_TABLE_NAME;
	}

	@Override
	public PRDao unserialize(String inputLine) {

		String parts[] = inputLine.split("\t");
		String url = parts[0].trim();
		double score = Double.parseDouble(parts[1].trim());

		return new PRDao(url, score);
	}
}