/*
 * Copyright (c) 2014, Newcastle University, Newcastle-upon-Tyne, England. All rights reserved.
 */
package org.risbic;

import org.junit.BeforeClass;
import org.junit.Test;
import org.risbic.data.DBConfig;
import org.risbic.data.TestDBConfig;
import org.risbic.data.UpdateConfig;
import org.risbic.flow.JdbcSource;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class SourceTest {
	@BeforeClass
	public static void setupDriver() throws ClassNotFoundException {
		// Load the Driver
		Class.forName("org.hsqldb.jdbcDriver");
	}

	@Test
	public void sourceTest() throws SQLException, URISyntaxException, ClassNotFoundException {
		final DBConfig dbConfig = new TestDBConfig();
		final UpdateConfig updateConfig = new UpdateConfig(5, TimeUnit.SECONDS, 1);

		populateDB(dbConfig);

		final JdbcSource jdbcSource = new JdbcSource("Test JDBC Source", dbConfig, updateConfig);

		// Test the output
	}

	private void populateDB(DBConfig dbConfig) throws SQLException {
		// Create a JDBC connection
		final Connection connection = DriverManager.getConnection(dbConfig.getURL());

		// Create the table
		connection.createStatement().execute("CREATE TABLE test(id INTEGER, name VARCHAR(20), num DOUBLE)");

		try (final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO test VALUES(?,?,?)")) {
			// Put some data in it
			for (int i = 1; i <= 50; i++) {
				preparedStatement.setInt(1, i);
				preparedStatement.setString(2, "Test " + i);
				preparedStatement.setDouble(3, 0.11 * i);

				preparedStatement.executeUpdate();
			}
		}
	}
}
