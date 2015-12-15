package pagerank.db.adapters;

import org.apache.log4j.Logger;

import pagerank.db.dao.DRDao;
import pagerank.db.ddl.PRCreateTable;
import indexer.db.imports.adapters.FileToDatabaseAdapter;

public class DRAdapter implements FileToDatabaseAdapter<DRDao>{

	private static Logger logger = Logger.getLogger(DRAdapter.class);
	
	@Override
	public String getTableName() {
		return PRCreateTable.DR_TABLE_NAME;
	}

	@Override
	public DRDao unserialize(String inputLine) {

		String parts[] = inputLine.split("\t");
		String url = parts[0].trim();
		double score = Double.parseDouble(parts[1].trim());

		return new DRDao(url, score);
	}
}
