package com.temenos.interaction.test.context;

import java.util.Properties;

/**
 * This class contains base connection configuration parameters.
 * 
 * @author ssethupathi
 *
 */
public class BaseConnectionConfig implements ConnectionConfig {

	private Properties connectionProperties = new Properties();

	public BaseConnectionConfig() {
		this.connectionProperties = getBaseConnectionProperties();
	}

	@Override
	public String getValue(String name) {
		return connectionProperties.getProperty(name, "");
	}

	private Properties getBaseConnectionProperties() {
		Properties baseConnprops = new Properties();
		baseConnprops
				.setProperty(ConnectionConfig.ENDPOINT_URI,
						"http://localhost:9089/t24interactiontests-iris/t24interactiontests.svc");
		baseConnprops.setProperty(ConnectionConfig.SERVICE_ROOT, "GB0010001");
		baseConnprops.setProperty(ConnectionConfig.USER_NAME, "INPUTT");
		baseConnprops.setProperty(ConnectionConfig.PASSWORD, "123456");
		return baseConnprops;
	}
}
