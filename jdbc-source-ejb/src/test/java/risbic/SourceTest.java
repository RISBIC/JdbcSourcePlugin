/*
 * Copyright (c) 2014, Newcastle University, Newcastle-upon-Tyne, England. All rights reserved.
 */
package risbic;

import org.junit.Test;
import risbic.data.DBConfig;
import risbic.data.DBEntry;

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

		ResultSet results = connection.createStatement().executeQuery("SELECT * FROM test");

		final DBConfig config = new DBConfig();
		config.setDatabase("test");
		config.setType("hsqldb");
		config.setHost("mem");

		while (results.next()) {
			final DBEntry dbEntry = DBEntry.of(results, "test");
			dbEntry.setDbConfig(config);

			System.out.println(dbEntry);
		}
	}

	private Connection createConnection() throws ClassNotFoundException, SQLException {
		// Load the Driver
		Class.forName("org.hsqldb.jdbcDriver");

		// Create and return the connection
		return DriverManager.getConnection("jdbc:hsqldb:mem:test");
	}

	private void populateDB(Connection con) throws SQLException {
		// Create the table
		con.createStatement().execute("CREATE TABLE test(id INTEGER, name VARCHAR(20), num DECIMAL)");

		try (final PreparedStatement ps = con.prepareStatement("INSERT INTO test VALUES(?,?,?)")) {
			// Put some data in it
			for (int i = 0; i < 10; i++) {
				ps.setInt(1, i);
				ps.setString(2, "Test " + i);
				ps.setDouble(3, 0.11 * i);

				ps.executeUpdate();
			}
		}
	}
}
