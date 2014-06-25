/*
 * Copyright (c) 2014, Newcastle University, Newcastle-upon-Tyne, England. All rights reserved.
 */
package org.risbic;

import org.junit.Test;
import org.risbic.data.DBConfig;
import org.risbic.data.DBEntry;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SourceTest {
	@Test
	public void sourceTest() throws SQLException, URISyntaxException, ClassNotFoundException {
		Connection connection = createConnection();
		populateDB(connection);

		final DBConfig config = new DBConfig();
		config.setDatabase("test");
		config.setType("hsqldb");
		config.setHost("mem");

		ResultSet results = connection.createStatement().executeQuery("SELECT * FROM test");

		final DBEntry dbEntry = DBEntry.fromResultSet(results, "test");
		dbEntry.setDbConfig(config);

		System.out.println(dbEntry);
	}

	private Connection createConnection() throws ClassNotFoundException, SQLException {
		// Load the Driver
		Class.forName("org.hsqldb.jdbcDriver");

		// Create and return the connection
		return DriverManager.getConnection("jdbc:hsqldb:mem:test");
	}

	private void populateDB(Connection connection) throws SQLException {
		// Create the table
		connection.createStatement().execute("CREATE TABLE test(id INTEGER, name VARCHAR(20), num DECIMAL)");

		try (final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?,?,?)")) {
			// Put some data in it
			for (int i = 0; i < 10; i++) {
				preparedStatement.setInt(1, i);
				preparedStatement.setString(2, "Test " + i);
				preparedStatement.setDouble(3, 0.11 * i);

				preparedStatement.executeUpdate();
			}
		}
	}
}
