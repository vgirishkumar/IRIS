package com.temenos.interaction.authorization;

/*
 * Interface for authentication beans.
 * 
 * TODO. THIS IS A PLACEHOLDER TO TEST AuthorizationCommand PLUMBING. Feel free to re-design this interface as authorization
 * work progresses.
 */

/*
 * #%L
 * interaction-authorization
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

import java.util.List;
import java.util.Set;

import com.temenos.interaction.authorization.command.data.AccessProfile;
import com.temenos.interaction.authorization.command.data.FieldName;
import com.temenos.interaction.authorization.command.data.RowFilter;
import com.temenos.interaction.core.command.InteractionContext;

public interface IAuthorizationProvider {

	/**
	 * This method will return the authorised AccessProfile for a current logged in user 
	 * 
	 * @param ctx
	 * @return null if nothing
	 */
	public AccessProfile getAccessProfile(InteractionContext ctx);
	
	/*
	 * Get the filter (row filter) for the current principle
	 * 
	 * An empty list means do no filtering ... return all rows.
	 * A null list means return nothing.
	 * 
	 */
	public List<RowFilter> getFilters(InteractionContext ctx);

	/*
	 * Get the select for the current principle.
	 * 
	 * @Return An empty list means select no columns, A null list means select all columns.
	 */
	public Set<FieldName> getSelect(InteractionContext ctx);
	
}