package com.temenos.interaction.jdbc.producer;

/*
 * Map storing mappings between column names and column types.
 * 
 * Types used are from java.sql.Types. Would be nice if there were enumerated but they are integers.
 */

/*
 * #%L
 * interaction-jdbc-producer
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;

import com.temenos.interaction.jdbc.exceptions.JdbcException;

public class ColumnTypesMap {
	// Default primary key
	private static String DEFAULT_PRIMARY_KEY = "RECID";

	// Somewhere to cache mapping information.
	Map<String, Integer> typesMap = null;

	// Somewhere to store primary key.
	String primaryKeyName = null;

	// A producer that can be used to query the table.
	JdbcProducer producer;

	private final static Logger logger = LoggerFactory.getLogger(ColumnTypesMap.class);

	public ColumnTypesMap(JdbcProducer producer, String tableName, boolean primaryKeyNameRequired) throws Exception {
		this.producer = producer;

		// This will open a new connection. Remember to close it latter.
		DataSource ds = producer.getDataSource();
		Connection conn = ds.getConnection();

		// Read the initial map.
		this.typesMap = readColumnTypes(conn, tableName);

		// If required obtain the primary key.
		if (primaryKeyNameRequired) {
			try {
				this.primaryKeyName = readPrimaryKey(conn, tableName);
			} catch (Exception e) {
				// Remember to close the connection
				conn.close();

				// Re-throw
				throw (e);
			}
		}

		// Close the connection
		conn.close();
	}

	/*
	 * USE THIS CONSTRUCTOR ONLY FOR UNIT TESTING.
	 */
	public ColumnTypesMap(Map<String, Integer> typesMap, String primaryKeyName) {
		this.producer = null;
		this.typesMap = typesMap;
		this.primaryKeyName = primaryKeyName;
	}

	/*
	 * Get type of a given column
	 */
	public Integer getType(String columnName) {
		return typesMap.get(columnName);
	}

	/*
	 * Determines if a given column is numeric.
	 */
	public boolean isNumeric(String columnName) {
		return JdbcUtils.isNumeric(typesMap.get(columnName));
	}

	/*
	 * Get the primary key name.
	 */
	public String getPrimaryKeyName() {
		return primaryKeyName;
	}

	/*
	 * Utility to read primary key for a given table.
	 */
	String readPrimaryKey(Connection conn, String tableName) throws Exception {
		String key = null;

		DatabaseMetaData dsMetaData = conn.getMetaData();

		ResultSet result = dsMetaData.getPrimaryKeys(null, null, tableName);

		// Move cursor to first line.
		if (result.next()) {
			key = result.getString("COLUMN_NAME");

			if (result.next()) {
				throw (new JdbcException(Status.INTERNAL_SERVER_ERROR, "Table \"" + tableName
						+ "\" has multiple primary keys. Not currently supported."));
			}
		}

		// If the primary key could not be found, and default column is present,
		// use that.
		if (null == key) {
			if (typesMap.containsKey(DEFAULT_PRIMARY_KEY)) {
				key = DEFAULT_PRIMARY_KEY;
			} else {
				throw (new JdbcException(
						Status.INTERNAL_SERVER_ERROR,
						"Table \""
								+ tableName
								+ "\" does not have a primary key or "
								+ DEFAULT_PRIMARY_KEY
								+ " column. For this table try multiple version of the command with \"?$filter=<Key name> eq <key_value>\"."));
			}

			// As an alternative it should also be possible to get the
			// primary key from the entity metadata. Something like:
			// EntityMetadata emd =
			// ctx.getMetadata().getEntityMetadata(entityType);
			// Vocabulary voc = emd.getPropertyVocabulary("ACCOUNT_NUMBER");
			// Term term = voc.getTerm("TERM_ID_FIELD");
			// key = term.getValue();
		}
		return key;
	}

	/*
	 * Utility to read column types for a given table.
	 */
	private Map<String, Integer> readColumnTypes(Connection conn, String tableName) throws Exception {

		// Create type map
		typesMap = new HashMap<String, Integer>();

		DatabaseMetaData dsMetaData = conn.getMetaData();

		ResultSet resultSet = dsMetaData.getColumns(null, null, tableName, null);
		int columnCount = 0;
		while (resultSet.next()) {
			typesMap.put(resultSet.getString("COLUMN_NAME"), resultSet.getInt("DATA_TYPE"));
			columnCount++;
		}

		if (0 == columnCount) {
			// Maybe there aren't any columns. So not necessarily an error.
			// However not normally expected so warn.
			logger.warn("No column types found for. " + tableName + " Maybe not supported on this database.");
		}
		return typesMap;
	}
}
