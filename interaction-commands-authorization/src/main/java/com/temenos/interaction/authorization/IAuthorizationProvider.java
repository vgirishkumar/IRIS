package com.temenos.interaction.authorization;

/*
 * Interface for authentication beans.
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
import com.temenos.interaction.core.command.InteractionException;

public interface IAuthorizationProvider {

	/**
	 * This method will return the authorised AccessProfile for a current logged in user.
	 * 
	 * Note : For some providers this may return cached data. 
	 * 
	 * @param ctx
	 */
	public AccessProfile getAccessProfile(InteractionContext ctx) throws InteractionException;
	
	/*
	 * Get the row filter for the current principle
	 * 
	 * An empty list means do no filtering i.e. return all rows. This is represented by a missing $filter term.
	 * 
	 * Note : This should return current, non cached, data,
	 */
	public List<RowFilter> getFilters(InteractionContext ctx) throws InteractionException;

	/*
	 * Get the select for the current principle.
	 * 
	 * An empty list means do no selecting i.e. return all columns. This is represented by a missing $select term.
	 * 
	 * * Note : This should return current, non cached, data,
	 */
	public Set<FieldName> getSelect(InteractionContext ctx)  throws InteractionException;
	
}