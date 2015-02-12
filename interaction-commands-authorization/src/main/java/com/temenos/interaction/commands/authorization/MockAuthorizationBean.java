package com.temenos.interaction.commands.authorization;

/*
 * Mock authorization bean. Used in testing. Instead of extracting real credentials for the current principle returns
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

import javax.ws.rs.core.MultivaluedMap;

import com.temenos.interaction.core.command.InteractionContext;

public class MockAuthorizationBean implements AuthorizationBean {

	// Keys for test arguments that can be passed in as parameters.
	public static String TEST_FILTER_KEY = "$testAuthorizationKey";
	public static String TEST_SELECT_KEY = "$testSelectKey";

	// Somewhere to store dummy test arguments.
	private String testFilter = null;
	private String testSelect = null;

	// A mock bean with no arguments is meaningless.
	// public MockAuthorizationBean() {
	// }

	// Constructor enabling dummy credentials to be passed in for testing.
	// BREAKS AUTHORIZATION. DO NOT USE OTHER THAN FOR TESTING.
	public MockAuthorizationBean(String testFilter, String testSelect) {
		// Store test arguments
		this.testFilter = testFilter;
		this.testSelect = testSelect;
	}

	/*
	 * Get the filter (row filter) for the current principle
	 */
	public String getFilter(InteractionContext ctx) {
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// If a filter was passed on the command line use that
		if (null != queryParams) {
			String passedFilter = queryParams.getFirst(TEST_FILTER_KEY);
			if (null != passedFilter) {
				return (passedFilter);
			}
		}

		return (testFilter);
	}

	/*
	 * Get the select (column filter) for the current principle.
	 */
	public String getSelect(InteractionContext ctx) {
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// If a filter was passed on the command line use that.
		if (null != queryParams) {
			String passedSelect = queryParams.getFirst(TEST_SELECT_KEY);
			if (null != passedSelect) {
				return (passedSelect);
			}
		}
		return (testSelect);
	}
}
