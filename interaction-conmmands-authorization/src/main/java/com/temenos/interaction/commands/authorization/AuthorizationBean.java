package com.temenos.interaction.commands.authorization;

/*
 * Interface for authentication beans.
 * 
 * TODO. THIS IS A PLACEHOLDER TO TEST SMSCommand PLUMBING. Feel free to re-design this interface as SMS work 
 * progresses.
 */

/*
 * #%L
 * interaction-sms
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

import static org.junit.Assert.assertTrue;

import com.temenos.interaction.core.command.InteractionContext;

public interface AuthorizationBean {

	/*
	 * Get the filter (row filter) for the current principle
	 *
	 * Return a 'and' separated list of rows to return e.g. "name eq Tim and id eq 1234"
	 * For now only 'eq' is supported.
	 */
	public String getFilter(InteractionContext ctx);

	/*
	 * Get the select for the current principle.
	 * 
	 * Return a comma separated list of columns to display e.g. "name, id".
	 */
	public String getSelect(InteractionContext ctx);
}