/*
 * Copyright (c) 2014, Newcastle University, Newcastle-upon-Tyne, England. All rights reserved.
 */
package org.risbic.factory;

import com.arjuna.databroker.data.DataFlowNodeFactory;
import com.arjuna.databroker.data.DataFlowNodeFactoryInventory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Collections;

@Startup
@Singleton
public class FactorySetup {
	public static final String SOURCE_FACTORY_NAME = "JDBC Source Factory";

	@EJB(lookup = "java:global/server-ear-1.0.0p1m1/control-core-1.0.0p1m1/DataFlowNodeFactoryInventory")
	private DataFlowNodeFactoryInventory _dataFlowNodeFactoryInventory;

	@PostConstruct
	public void setup() {
		System.out.println("setup");
		final DataFlowNodeFactory dataFlowNodeFactory = new JdbcSourceNodeFactory(SOURCE_FACTORY_NAME, Collections.<String, String>emptyMap());

		_dataFlowNodeFactoryInventory.addDataFlowNodeFactory(dataFlowNodeFactory);
	}

	@PreDestroy
	public void cleanup() {
		_dataFlowNodeFactoryInventory.removeDataFlowNodeFactory(SOURCE_FACTORY_NAME);
	}
}
