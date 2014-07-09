/*
 * Copyright (c) 2014, Newcastle University, Newcastle-upon-Tyne, England. All rights reserved.
 */

package org.risbic.flow;

import com.arjuna.databroker.data.DataProvider;
import com.arjuna.databroker.data.DataSource;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.risbic.SimpleProvider;
import org.risbic.data.DBConfig;
import org.risbic.data.DBEntry;
import org.risbic.data.UpdateConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.risbic.factory.JdbcSourceNodeFactory.*;

public class JdbcSource implements DataSource {
	private static final Logger logger = Logger.getLogger(JdbcSource.class.getName());
	private final String _name;
	private final Map<String, String> _properties;
	private final DataProvider<DBEntry> _dataProvider;
	private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	private final DBConfig dbConfig;
	private final UpdateConfig updateConfig;

	public JdbcSource(final String name, final Map<String, String> properties) {
		_name = name;
		_properties = properties;
		_dataProvider = new SimpleProvider<>(this);

		dbConfig = parseDBConfig();
		updateConfig = parseUploadConfig();

		logger.info("JdbcSource: " + name + ", " + dbConfig + ", " + updateConfig);

		scheduleRunnable();
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

	private void scheduleRunnable() {
		try {
			final Connection connection = DriverManager.getConnection(dbConfig.getURL());
			logger.info(String.format("DB URL: [%s]", dbConfig.getURL()));

			// Set the task to periodically scan
			scheduler.scheduleAtFixedRate(createRunnable(connection), updateConfig.getFrequency(), updateConfig.getFrequency(), updateConfig.getTimeUnit());
		} catch (SQLException e) {
			logger.warning("Could not establish JDBC connection.");
			e.printStackTrace();
		}
	}

	// Create the Runnable using the dbConfig and uploadConfig objects
	private Runnable createRunnable(final Connection connection) {
		return new Runnable() {
			private final Map<String, Object> discriminatorValues = new HashMap<>();

			public void run() {
				for (final Map.Entry<String, String> entry : dbConfig.getTableMappings().entrySet()) {
					final String tableName = entry.getKey();
					final String columnName = entry.getValue();

					logger.info(String.format("T: '%s' DC: '%s'", tableName, columnName));

					try {
						ResultSet resultSet;

						// TODO: refactor duplicate code in ifs
						if (discriminatorValues.containsKey(tableName)) {
							// TODO: parameterise using PreparedStatement
							final String sql = String.format("SELECT * FROM %s WHERE %s > %s ORDER BY %s DESC", tableName, columnName, discriminatorValues.get(tableName), columnName);
							resultSet = connection.createStatement().executeQuery(sql);
						} else {
							// TODO: parameterise using PreparedStatement
							final String sql = String.format("SELECT * FROM %s ORDER BY %s DESC", tableName, columnName);
							resultSet = connection.createStatement().executeQuery(sql);
						}

						final DBEntry dbEntry = DBEntry.fromResultSet(resultSet, tableName);

						if (dbEntry.getRows().size() > 0) {
							final Map<String, Object> firstRow = dbEntry.getRow(0);

							discriminatorValues.put(tableName, firstRow.get(columnName));
						}

						emit(dbEntry);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		};
	}

	// Parse DBConfig properties (or default values)
	private DBConfig parseDBConfig() {
		// Which DB server to connect to
		final String type = getProperty(SOURCE_DB_TYPE, "postgresql");
		final String host = getProperty(SOURCE_DB_HOST, "localhost");
		final String port = getProperty(SOURCE_DB_PORT, "5432");
		final String database = getProperty(SOURCE_DB_DATABASE, "db");

		// Credentials to connect to the DB Server
		final String user = getProperty(SOURCE_DB_USER, "username");
		final String pass = getProperty(SOURCE_DB_PASS, "password");

		// Table(s) and column(s) to scan for updates
		final String tables = getProperty(SOURCE_DB_TABLES, "table.column");
		final Map<String, String> tableMap = parseTableMapping(tables);

		// Create the DB Config object
		return new DBConfig(type, host, port, user, pass, database, tableMap);
	}

	// Parse UploadConfig properties (or default values)
	private UpdateConfig parseUploadConfig() {
		final Integer updateFrequency = Integer.valueOf(getProperty(SOURCE_INTERVAL, "30"));
		final TimeUnit updateTimeUnit = TimeUnit.valueOf(getProperty(SOURCE_TIME_UNIT, "SECONDS"));
		final Integer updateBatchSize = Integer.valueOf(getProperty(SOURCE_BATCH_SIZE, "100"));

		return new UpdateConfig(updateFrequency, updateTimeUnit, updateBatchSize);
	}

	// Parse table1.col_name,table2.col_name,...,tableN.col_name
	private Map<String, String> parseTableMapping(final String rawMapping) {
		return Splitter.on(",").withKeyValueSeparator(".").split(rawMapping);
	}

	// Helper method to get a property or provide some default value
	private String getProperty(final String key, final String defaultValue) {
		if (Strings.isNullOrEmpty(_properties.get(key))) {
			return defaultValue;
		}

		return _properties.get(key);
	}
}
