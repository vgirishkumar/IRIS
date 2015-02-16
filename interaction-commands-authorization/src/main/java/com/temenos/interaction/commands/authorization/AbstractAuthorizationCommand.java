package com.temenos.interaction.commands.authorization;

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

import com.temenos.interaction.core.command.InteractionCommand;

public abstract class AbstractAuthorizationCommand {
	// Somewhere to store the child command that will be called.
	InteractionCommand command = null;

	// Somewhere to store the bean that interfaces to a given authentication
	// mechanism.
	AuthorizationBean authorizationBean = null;

	// Odata option keys. Must comply with the OData standard.
	public static final String FILTER_KEY = "$filter";
	public static final String SELECT_KEY = "$select";
}