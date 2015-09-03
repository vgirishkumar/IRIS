package com.temenos.interaction.jdbc.producer;

/*
 * Utility class for building SQL commands.
 * 
 * If given a key constructs a command for a single row. 
 * 
 * If given a null key constructs a command to get all rows.
 * 
 * TODO maybe need variants for different databases.
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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.temenos.interaction.authorization.command.data.AccessProfile;
import com.temenos.interaction.authorization.command.data.FieldName;
import com.temenos.interaction.authorization.command.data.RowFilter;

public class SqlCommandBuilder {

	// Somewhere to store arguments
	private String tableName;
	private String keyValue;
	private AccessProfile accessProfile;
	private ColumnTypesMap colTypesMap;

	// private final static Logger logger = LoggerFactory.getLogger(SqlCommandBuilder.class);

	/*
	 * Constructor when there is not a key.
	 */
	public SqlCommandBuilder(String tableName, AccessProfile accessProfile, ColumnTypesMap colTypesMap) {
		this.tableName = tableName;
		this.keyValue = null;
		this.accessProfile = accessProfile;
		this.colTypesMap = colTypesMap;
	}
	
	/*
	 * Constructor when there is a key.
	 */
	public SqlCommandBuilder(String tableName, String keyValue, AccessProfile accessProfile, ColumnTypesMap colTypesMap) {
		this.tableName = tableName;
		this.keyValue = keyValue;
		this.accessProfile = accessProfile;
		this.colTypesMap = colTypesMap;
	}

	/*
	 * Method to build Sql command
	 */
	public String getCommand() {

		// Build an SQL command
		String sqlCommand = "SELECT ";
		sqlCommand = sqlCommand.concat(getSelects());
		sqlCommand = sqlCommand.concat(getFrom());
		sqlCommand = sqlCommand.concat(getWhere());

		return sqlCommand;
	}

	private String getSelects() {
		String cmd = "";

		// Add columns to select
		Set<FieldName> names = accessProfile.getFieldNames();
		if (null == names) {
			throw (new SecurityException("Cannot generate Sql command for null field set."));
		}
		if (names.isEmpty()) {
			// Empty select list means "return all columns".
			cmd = cmd.concat("*");
		} else {
			// Add comma separated list of select terms. Need to detect the last
			// operation so use old style iterator.
			Iterator<FieldName> iterator = names.iterator();
			while (iterator.hasNext()) {
				FieldName name = iterator.next();
				cmd = cmd.concat(getSelect(name));
				;

				// If not the last entry
				if (iterator.hasNext()) {
					cmd = cmd.concat(", ");
				}
			}
		}
		return cmd;
	}

	private String getSelect(FieldName name) {
		return "\"" + name.getName() + "\"";
	}

	private String getFrom() {
		return " FROM \"" + tableName + "\"";
	}

	/*
	 * Get the "WHERE x AND y" etc clause. Adds filters and/or key.
	 */
	private String getWhere() {
		
		// If there are no filters or key return;
		if (accessProfile.getRowFilters().isEmpty() && (null == keyValue)) {
			return "";
		}

		String cmd = " WHERE ";
		
		if (null != keyValue) {
			if (null == colTypesMap.getPrimaryKeyName()) {
				throw (new SecurityException("No primary key column defined for \"" + tableName + "\". Cannot look up key."));
			}
			
			// Add key as a filter
			RowFilter keyFilter = new RowFilter(colTypesMap.getPrimaryKeyName(), RowFilter.Relation.EQ, keyValue);
			cmd = cmd.concat(getFilter(keyFilter));
		}

		if (!accessProfile.getRowFilters().isEmpty()) {
			// If we already had a key need to link with an 'AND'.
			if (null != keyValue) {
				cmd = cmd.concat(getAnd());
			}
			cmd = cmd.concat(getFilters());
		}		
		return cmd;
	}
	
	private String getAnd() {
		return " AND ";
	}

	private String getFilters() {
		String cmd = "";

		// Add row filters
		List<RowFilter> filters = accessProfile.getRowFilters();
		if (null == filters) {
			throw (new SecurityException("Cannot generate Sql command for null row filters list."));
		}

		// Add AND separated list of filter terms. Need to detect the last
		// operation so use old style iterator.
		Iterator<RowFilter> iterator = filters.iterator();
		while (iterator.hasNext()) {
			RowFilter filter = iterator.next();

			cmd = cmd.concat(getFilter(filter));

			// If not the last entry
			if (iterator.hasNext()) {
				cmd = cmd.concat(getAnd());
			}
		}

		return cmd;
	}

	private String getFilter(RowFilter filter) {
		String cmd = "\"" + filter.getFieldName().getName() + "\"";
		cmd = cmd.concat(filter.getRelation().getSqlSymbol());

		// Extract the column type from the metadata. Text
		// must be quoted but not numerics.
		boolean numeric = colTypesMap.isNumeric(filter.getFieldName().getName());
		if (numeric) {
			cmd = cmd.concat(filter.getValue());
		} else {

			cmd = cmd.concat("'" + filter.getValue() + "'");
		}
		return cmd;
	}
}
