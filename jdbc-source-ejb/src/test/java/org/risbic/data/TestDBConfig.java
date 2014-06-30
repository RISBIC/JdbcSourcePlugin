package org.risbic.data;

public class TestDBConfig extends DBConfig {
	@Override
	public String getURL() {
		return "jdbc:hsqldb:mem:test";
	}
}
