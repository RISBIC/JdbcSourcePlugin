/*
 * Copyright (c) 2014, Newcastle University, Newcastle-upon-Tyne, England. All rights reserved.
 */
package org.risbic;

import com.arjuna.databroker.data.DataConsumer;
import com.arjuna.databroker.data.DataFlowNode;
import com.arjuna.databroker.data.DataProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class SimpleProvider<T> implements DataProvider<T> {
	private static final Logger logger = Logger.getLogger(SimpleProvider.class.getName());

	private DataFlowNode _dataFlowNode;

	private List<DataConsumer<T>> _dataConsumers;

	public SimpleProvider(final DataFlowNode dataFlowNode) {
		logger.fine("DirectProvider: " + dataFlowNode);

		_dataFlowNode = dataFlowNode;
		_dataConsumers = new LinkedList<>();
	}

	@Override
	public DataFlowNode getDataFlowNode() {
		return _dataFlowNode;
	}

	@Override
	public Collection<DataConsumer<T>> getDataConsumers() {
		return Collections.unmodifiableList(_dataConsumers);
	}

	@Override
	public void addDataConsumer(final DataConsumer<T> dataConsumer) {
		_dataConsumers.add(dataConsumer);
	}

	@Override
	public void removeDataConsumer(final DataConsumer<T> dataConsumer) {
		_dataConsumers.remove(dataConsumer);
	}

	@Override
	public void produce(final T data) {
		for (final DataConsumer<T> dataConsumer : _dataConsumers) {
			dataConsumer.consume(this, data);
		}
	}
}
