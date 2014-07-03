package org.risbic.data;

import java.util.Map;

public class DBConfig {
	private String type;

	private String host;

	private String port;

	private String user;

	private String pass;

	private String database;

	private Map<String, String> tableMappings;

	public DBConfig() {
	}

	public DBConfig(String type, String host, String port, String user, String pass, String database, Map<String, String> tableMappings) {
		this.type = type;
		this.host = host;
		this.port = port;
		this.user = user;
		this.pass = pass;
		this.database = database;
		this.tableMappings = tableMappings;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public Map<String, String> getTableMappings() {
		return tableMappings;
	}

	public void setTableMappings(Map<String, String> tableMappings) {
		this.tableMappings = tableMappings;
	}

	public String getURL() {
		return String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s&ssl=true", type, host, port, database, user, pass);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		DBConfig dbConfig = (DBConfig) o;

		if (database != null ? !database.equals(dbConfig.database) : dbConfig.database != null) {
			return false;
		}
		if (host != null ? !host.equals(dbConfig.host) : dbConfig.host != null) {
			return false;
		}
		if (pass != null ? !pass.equals(dbConfig.pass) : dbConfig.pass != null) {
			return false;
		}
		if (port != null ? !port.equals(dbConfig.port) : dbConfig.port != null) {
			return false;
		}
		if (type != null ? !type.equals(dbConfig.type) : dbConfig.type != null) {
			return false;
		}
		if (user != null ? !user.equals(dbConfig.user) : dbConfig.user != null) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = type != null ? type.hashCode() : 0;
		result = 31 * result + (host != null ? host.hashCode() : 0);
		result = 31 * result + (port != null ? port.hashCode() : 0);
		result = 31 * result + (user != null ? user.hashCode() : 0);
		result = 31 * result + (pass != null ? pass.hashCode() : 0);
		result = 31 * result + (database != null ? database.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "DBConfig{" + "type='" + type + '\'' + ", host='" + host + '\'' + ", port='" + port + '\'' + ", user='" + user + '\'' + ", pass='" + pass + '\'' + ", database='" + database + '\'' + '}';
	}
}
