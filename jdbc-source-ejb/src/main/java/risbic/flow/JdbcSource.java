/*
 * Copyright (c) 2014, Newcastle University, Newcastle-upon-Tyne, England. All rights reserved.
 */
package risbic.flow;

import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;
import com.google.common.base.Splitter;
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

	private final String _name;

	private final Map<String, String> _properties;

	private final DataProvider<DBEntry> _dataProvider;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	// Which DB server to connect to
	private final String dbType;
	private final String dbHost;
	private final String dbPort;
	private final String dbName;

	// Credentials to connect to the DB Server
	private final String dbUser;
	private final String dbPass;

	// DB URL for JDBC
	private final String dbURL;

	// Table(s) and column(s) to scan for updates
	private final String dbTables;

	// How often (and how much) to update
	private final TimeUnit updateTimeUnit;
	private final Integer updateFrequency;
	private final Integer updateBatchSize;

	public JdbcSource(final String name, final Map<String, String> properties) {
		logger.info("JdbcSource: " + name + ", " + properties);

		_name = name;
		_properties = properties;
		_dataProvider = new SimpleProvider<>(this);

		// Which DB server to connect to
		dbType = getProperty(SOURCE_DB_TYPE, "postgresql");
		dbHost = getProperty(SOURCE_DB_HOST, "localhost");
		dbPort = getProperty(SOURCE_DB_PORT, "5432");
		dbName = getProperty(SOURCE_DB_DATABASE, "smn");

		// Credentials to connect to the DB Server
		dbUser = getProperty(SOURCE_DB_USER, "smn");
		dbPass = getProperty(SOURCE_DB_PASS, "smn");

		// DB URL for JDBC
		dbURL = String.format("jdbc:%s://%s:%s/%s?user=%s&password=%s&ssl=true", dbType, dbHost, dbPort, dbName, dbUser, dbPass);

		// Table(s) and column(s) to scan for updates
		dbTables = getProperty(SOURCE_DB_TABLE, "dbdata.inserttime");

		// How often (and how much) to update
		updateTimeUnit = TimeUnit.valueOf(getProperty(SOURCE_TIME_UNIT, "SECONDS"));
		updateFrequency = Integer.valueOf(getProperty(SOURCE_INTERVAL, "30"));
		updateBatchSize = Integer.valueOf(getProperty(SOURCE_BATCH_SIZE, "100"));

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

	private Map<String, String> parseTableMapping() {
		return Splitter.on(",").withKeyValueSeparator(".").split(dbTables);
	}

	private void setupDatabaseScan() {
		try {
			final Connection conn = DriverManager.getConnection(dbURL);

			logger.info(String.format("DB URL: [%s]", dbURL));

			// Create the task to be periodically run
			final Runnable updateScanner = new Runnable() {
				public void run() {
					logger.info("updateScanner.run()");
				}
			};

			// Set the task to periodically scan
			scheduler.scheduleAtFixedRate(updateScanner, updateFrequency, updateFrequency, updateTimeUnit);
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
