package com.temenos.interaction.core.command;

/*
 * #%L
 * interaction-core
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


/**
 * Implementors of this interface are providing commands that will be
 * executed when a given interaction with a resource occurs.
 * @author aphethean
 */
public interface InteractionCommand {

	enum Result {
		SUCCESS, 
		FAILURE, 
		INVALID_REQUEST, 
	}
	
	/**
	 * Main execution interface for resource interactions.
	 * @precondition a valid, non null {@link InteractionContext}
	 * @postcondition a non null InteractionCommand.Result indicating command outcome
	 * @param ctx
	 * @throws interaction command exception
	 * @return result
	 */
	public Result execute(InteractionContext ctx) throws InteractionException;
	
}
