package org.risbic.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBEntry {
	private DBConfig dbConfig;

	private String table;

	private List<Map<String, Object>> rows = new ArrayList<>();

	public DBConfig getDbConfig() {
		return dbConfig;
	}

	public void setDbConfig(DBConfig db) {
		this.dbConfig = db;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public Map<String, Object> getRow(int i) {
		return rows.get(i);
	}

	public List<Map<String, Object>> getRows() {
		return rows;
	}

	public void addRow(Map<String, Object> row) {
		this.rows.add(row);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DBEntry dbEntry = (DBEntry) o;

		if (dbConfig != null ? !dbConfig.equals(dbEntry.dbConfig) : dbEntry.dbConfig != null) {
			return false;
		}
		if (table != null ? !table.equals(dbEntry.table) : dbEntry.table != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = dbConfig != null ? dbConfig.hashCode() : 0;
		result = 31 * result + (table != null ? table.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "DBEntry{" + "dbConfig=" + dbConfig + ", table='" + table + '\'' + ", rows=" + rows + '}';
	}

	public static DBEntry fromResultSet(final ResultSet rows, final String tableName) throws SQLException {
		final DBEntry result = new DBEntry();

		result.setTable(tableName);

		while (rows.next()) {
			Map<String, Object> data = new HashMap<>();

			for (int i = 1; i <= rows.getMetaData().getColumnCount(); i++) {
				data.put(rows.getMetaData().getColumnName(i), rows.getObject(i));
			}

			result.addRow(data);
		}

		return result;
	}
}
