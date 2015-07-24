package com.temenos.interaction.authorization.mock;

/*
 * Mock authorization bean. Used in testing. Instead of extracting real RowFilters for the current principle returns
 * pre-configured data passed in at construction and/or call time.
 */

/*
 * #%L
 * interaction-commands-authorization
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.authorization.IAuthorizationProvider;
import com.temenos.interaction.authorization.command.data.AccessProfile;
import com.temenos.interaction.authorization.command.data.FieldName;
import com.temenos.interaction.authorization.command.data.RowFilter;
import com.temenos.interaction.authorization.command.util.ODataParser;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

/**
 * Once we have actual functionality available. PLEASE DELETE THIS CLASS
 * 
 * @author sjunejo
 * 
 */

public class MockAuthorizationBean implements IAuthorizationProvider {
	private final static Logger logger = LoggerFactory.getLogger(MockAuthorizationBean.class);

	// Keys for test arguments that can be passed in as parameters.
	public static String TEST_FILTER_KEY = "$testAuthorizationKey";
	public static String TEST_SELECT_KEY = "$testSelectKey";

	// Somewhere to store dummy test arguments.
	private List<RowFilter> testFilter = null;
	private Set<FieldName> testSelect = null;

	// Place to store an exception to be thrown on execute
	private InteractionException exception = null;

	// A mock bean with no arguments is meaningless.
	// public MockAuthorizationBean() {
	// }

	// Constructor that will throw on execute
	public MockAuthorizationBean(InteractionException exception) {
		this.exception = exception;
	}

	// Constructors enabling dummy RowFilters to be passed in for testing.
	// BREAKS AUTHORIZATION. DO NOT USE OTHER THAN FOR TESTING.
	public MockAuthorizationBean(String filter, String select) {
		CommonTestConstructor(stringToFilter(filter), stringToSelect(select));
	}

	private void CommonTestConstructor(List<RowFilter> testFilter, Set<FieldName> testSelect) {
		// Store test arguments
		this.testFilter = testFilter;
		this.testSelect = testSelect;
	}

	// Helper to convert a filter string into a filter. Handles parsing and also
	// the edge 'all' and 'none' cases.
	private List<RowFilter> stringToFilter(String filter) {
		if (null == filter) {
			// A null string means return nothing. Represented by a null filter.
			return (null);
		}

		if (filter.isEmpty()) {
			// An empty string means do no filtering. Represented by an empty
			// filter list.
			return (new ArrayList<RowFilter>());
		}

		// If we get here parse the filter.
		try {
			List<RowFilter> filterList = ODataParser.parseFilter(filter);
			return (filterList);
		} catch (Exception e) {
			logger.info("Could not parse test filter \"" + filter + "\" : ", e);

			// Return the 'no output' case
			return (null);
		}
	}

	// Helper to convert a select string into a select. Handles parsing and also
	// the edge 'all' and 'none' cases.
	private Set<FieldName> stringToSelect(String select) {
		if (null == select) {
			// An null string means do no filtering. Represented by a null empty
			// select list.
			return (null);
		}

		if (select.isEmpty()) {
			// Ae empty string means return nothing. Represented by an empty
			// select list.
			return (new HashSet<FieldName>());
		}

		// If we get here parse the select.
		try {
			Set<FieldName> selectList = ODataParser.parseSelect(select);
			return (selectList);
		} catch (Exception e) {
			logger.info("Could not parse test seelct \"" + select + "\" : ", e);

			// Return the 'no output' case
			return (new HashSet<FieldName>());
		}
	}

	/*
	 * Get the filter (row filter) for the current principle
	 */
	@Override
	public List<RowFilter> getFilters(InteractionContext ctx) throws InteractionException {
		if (null != exception) {
			throw (exception);
		}

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// If a filter was passed on the command line use that
		if (null != queryParams) {
			String passedFilter = queryParams.getFirst(TEST_FILTER_KEY);

			if (null != passedFilter) {
				return (stringToFilter(passedFilter));
			}
		}

		return (testFilter);
	}

	/*
	 * Get the select (column filter) for the current principle.
	 */
	@Override
	public Set<FieldName> getSelect(InteractionContext ctx) throws InteractionException {
		if (null != exception) {
			throw (exception);
		}

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// If a select was passed on the command line use that.
		if (null != queryParams) {
			String passedSelect = queryParams.getFirst(TEST_SELECT_KEY);
			if (null != passedSelect) {
				return (stringToSelect(passedSelect));
			}
		}
		return (testSelect);
	}

	@Override
	public AccessProfile getAccessProfile(InteractionContext ctx) throws InteractionException {
		if (null != exception) {
			throw (exception);
		}

		AccessProfile profile = new AccessProfile(getFilters(ctx), getSelect(ctx));
		return (profile);
	}
}
