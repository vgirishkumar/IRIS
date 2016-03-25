package com.temenos.interaction.test.context;

/*
 * #%L
 * useragent-generic-java
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.util.Properties;

/**
 * This class contains the connection configuration at the session level.
 * 
 * @author ssethupathi
 *
 */
public class SessionConnectionConfig implements ConnectionConfig {
	private ConnectionConfig parentConfig;
	private Properties sessionProperties = new Properties();

	public SessionConnectionConfig(ConnectionConfig connectionConfig) {
		this.parentConfig = connectionConfig;
	}

	public void setValue(String propertyName, String value) {
		sessionProperties.put(propertyName, value);
	}

	@Override
	public String getValue(String propertyName) {
		return sessionProperties.getProperty(propertyName,
				parentConfig.getValue(propertyName));
	}
}
