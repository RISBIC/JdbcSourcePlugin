/*
 * Copyright (c) 2014, Newcastle University, Newcastle-upon-Tyne, England. All rights reserved.
 */
package risbic.flow;

import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;
import com.google.common.base.Strings;
import risbic.SimpleProvider;
import risbic.data.DBEntry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static risbic.factory.JdbcSourceNodeFactory.*;

public class JdbcSource implements DataSource {
	private static final Logger logger = Logger.getLogger(JdbcSource.class.getName());

	private String _name;

	private Map<String, String> _properties;

	private DataProvider<DBEntry> _dataProvider;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public JdbcSource(final String name, final Map<String, String> properties) {
		logger.info("JdbcSource: " + name + ", " + properties);

		_name = name;
		_properties = properties;

		_dataProvider = new SimpleProvider<>(this);

		setupDatabaseScan();
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Map<String, String> getProperties() {
		return Collections.unmodifiableMap(_properties);
	}

	@Override
	public Collection<Class<?>> getDataProviderDataClasses() {
		final Set<Class<?>> dataProviderDataClasses = new HashSet<>();

		dataProviderDataClasses.add(DBEntry.class);

		return dataProviderDataClasses;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> DataProvider<T> getDataProvider(final Class<T> dataClass) {
		if (dataClass == DBEntry.class) {
			return (DataProvider<T>) _dataProvider;
		} else {
			return null;
		}
	}

	public void emit(final DBEntry data) {
		logger.info("JdbcSource.emit: " + data);

		_dataProvider.produce(data);
	}

	private void setupDatabaseScan() {
		// Which DB server to connect to
		final String databaseType = getProperty(SOURCE_DB_TYPE, "postgresql");
		final String databaseHost = getProperty(SOURCE_DB_HOST, "localhost");
		final String databasePort = getProperty(SOURCE_DB_PORT, "5432");
		final String databaseDB = getProperty(SOURCE_DB_DATABASE, "smn");

		// Credentials to connect to the DB Server
		final String databaseUser = getProperty(SOURCE_DB_USER, "smn");
		final String databasePass = getProperty(SOURCE_DB_PASS, "smn");

		// Table and column to scan for updates
		final String databaseTable = getProperty(SOURCE_DB_TABLE, "dbdata.inserttime");

		// How often (and how much) to update
		final TimeUnit updateTimeUnit = TimeUnit.valueOf(getProperty(SOURCE_TIME_UNIT, "SECONDS"));
		final Integer updateFrequency = Integer.valueOf(getProperty(SOURCE_INTERVAL, "30"));
		final Integer batchSize = Integer.valueOf(getProperty(SOURCE_BATCH_SIZE, "100"));


		try {
			// Connect to the database
			String connectionURL = String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s&ssl=true", databaseType, databaseHost, databasePort, databaseDB, databaseUser, databasePass);
			final Connection conn = DriverManager.getConnection(connectionURL);

			logger.info(String.format("Connection URL: [%s]", connectionURL));

			// Create the task to be periodically run
			final Runnable updateScanner = new Runnable() {
				public void run() {
					logger.info("updateScanner.run()");
				}
			};

			// Set the task to periodically scan
			scheduler.scheduleAtFixedRate(updateScanner, updateFrequency, updateFrequency, TimeUnit.SECONDS);
		} catch (SQLException e) {
			logger.warning("Could not establish JDBC connection.");
			e.printStackTrace();
		}
	}

	// Helper method to get a property or provide some default value
	private String getProperty(final String key, final String defaultValue) {
		if (Strings.isNullOrEmpty(_properties.get(key))) {
			return defaultValue;
		}

		return _properties.get(key);
	}
}
