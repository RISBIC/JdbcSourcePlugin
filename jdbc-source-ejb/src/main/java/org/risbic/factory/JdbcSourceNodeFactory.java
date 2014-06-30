/*
 * Copyright (c) 2014, Newcastle University, Newcastle-upon-Tyne, England. All rights reserved.
 */
package org.risbic.factory;

import com.arjuna.databroker.data.*;
import org.risbic.flow.JdbcSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JdbcSourceNodeFactory implements DataFlowNodeFactory {
	// SMN SOURCE PROPERTY NAMES
	public static final String SOURCE_INTERVAL = "Update Interval";

	public static final String SOURCE_TIME_UNIT = "Update Interval Time Unit (e.g., SECONDS)";

	public static final String SOURCE_BATCH_SIZE = "Batch Size (rows)";

	public static final String SOURCE_DB_TYPE = "Database Type (Postgres, MySQL)";

	public static final String SOURCE_DB_HOST = "Database Host";

	public static final String SOURCE_DB_PORT = "Database Port";

	public static final String SOURCE_DB_USER = "Database User";

	public static final String SOURCE_DB_PASS = "Database Password";

	public static final String SOURCE_DB_DATABASE = "Database Name";

	public static final String SOURCE_DB_TABLES = "Table Name(s)";

	private final String _name;

	private final Map<String, String> _properties;

	public JdbcSourceNodeFactory(final String name, final Map<String, String> properties) {
		_name = name;
		_properties = properties;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public Map<String, String> getProperties() {
		return _properties;
	}

	@Override
	public List<Class<? extends DataFlowNode>> getClasses() {
		final List<Class<? extends DataFlowNode>> classes = new LinkedList<>();

		classes.add(DataSource.class);

		return classes;
	}

	@Override
	public <T extends DataFlowNode> List<String> getMetaPropertyNames(final Class<T> dataFlowNodeClass) {
		return Collections.emptyList();
	}

	@Override
	public <T extends DataFlowNode> List<String> getPropertyNames(final Class<T> dataFlowNodeClass, final Map<String, String> metaProperties) throws InvalidClassException, InvalidMetaPropertyException, MissingMetaPropertyException {
		final List<String> propertyNames = new ArrayList<>();

		if (dataFlowNodeClass.isAssignableFrom(JdbcSource.class)) {
			propertyNames.addAll(Arrays.asList(SOURCE_INTERVAL, SOURCE_TIME_UNIT, SOURCE_BATCH_SIZE, SOURCE_DB_TYPE, SOURCE_DB_HOST, SOURCE_DB_PORT, SOURCE_DB_USER, SOURCE_DB_PASS, SOURCE_DB_DATABASE, SOURCE_DB_TABLES));
		}

		return propertyNames;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DataFlowNode> T createDataFlowNode(final String name, final Class<T> dataFlowNodeClass, final Map<String, String> metaProperties, final Map<String, String> properties) throws InvalidNameException, InvalidPropertyException, MissingPropertyException {
		if (dataFlowNodeClass.isAssignableFrom(JdbcSource.class)) {
			return (T) new JdbcSource(name, properties);
		} else {
			return null;
		}
	}
}
