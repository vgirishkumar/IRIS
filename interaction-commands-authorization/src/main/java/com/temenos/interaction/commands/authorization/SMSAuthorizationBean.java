package com.temenos.interaction.commands.authorization;

/*
 * SMS authorization bean. Extracts row filters and column select lists from the T24 SMS system.
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

import com.temenos.interaction.core.command.InteractionContext;

public class SMSAuthorizationBean implements AuthorizationBean {

	public SMSAuthorizationBean() {
	}

	/*
	 * Get the filter (row filter) for the current principle
	 * 
	 * An empty list means do no filtering ... return all rows.
	 */
	public List<RowFilter> getFilters(InteractionContext ctx) {

		List<RowFilter> RowFilters = new ArrayList<RowFilter>();

		// TODO Replace following with plumbing into SMS
		RowFilter perm = new RowFilter("aname", RowFilter.Relation.EQ, "value");
		RowFilters.add(perm);

		return (RowFilters);
	}

	/*
	 * Get the select (column filter) for the current principle.
	 * 
	 * An empty list means select all columns
	 */
	public Set<FieldName> getSelect(InteractionContext ctx) {

		Set<FieldName> fields = new HashSet<FieldName>();

		// TODO Replace following with plumbing into SMS
		FieldName name = new FieldName("aname");
		fields.add(name);

		return (fields);
	}
}
