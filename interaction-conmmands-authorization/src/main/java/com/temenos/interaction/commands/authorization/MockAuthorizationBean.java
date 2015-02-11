package com.temenos.interaction.commands.authorization;

/*
 * Mock authorization bean. Used in testing. Instead of extracting real SMS permissions for the current principle returns
 * pre-configured data passed in at construction time.
 */

/*
 * #%L
 * interaction-commands-sms
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionContext;

public class MockAuthorizationBean implements AuthorizationBean {

	private final static Logger logger = LoggerFactory.getLogger(MockAuthorizationBean.class);

	// Somewhere to store dummy test arguments.
	private String testFilter = null;
	private String testSelect = null;

	// A mock bean with no arguments is meaningless.
	// public MockAuthorizationBean() {
	// }

	// Constructor enabling dummy SMS parameters to be passed in for testing.
	// BREAKS SMS. DO NOT USE OTHER THAN FOR TESTING.
	public MockAuthorizationBean(String testFilter, String testSelect) {
		// Store test arguments
		this.testFilter = testFilter;
		this.testSelect = testSelect;
	}

	/*
	 * Get the filter (row filter) for the current principle
	 */
	public String getFilter(InteractionContext ctx) {
		return (testFilter);
	}

	/*
	 * Get the select (column filter) for the current principle.
	 */
	public String getSelect(InteractionContext ctx) {
		return (testSelect);
	}

}
