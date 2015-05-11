package com.temenos.interaction.authorization.command;

/*
 * PostSelect bean. This is called after the database query. If the database was unable to carry out the $select operation then
 * it will be done here.
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

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.authorization.exceptions.AuthorizationException;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

public class PostSelectCommand implements InteractionCommand {

	public static final String SELECT_DONE_ATTRIBUTE = "selectDone";

	private final static Logger logger = LoggerFactory.getLogger(PostSelectCommand.class);

	/*
	 * Execute the command.
	 */
	public Result execute(InteractionContext ctx) throws InteractionException {

		// Check if selecting has already been done.
		Boolean selectDone = (Boolean) ctx.getAttribute(SELECT_DONE_ATTRIBUTE);

		if (null == selectDone) {
			// If attribute not present then something has gone wrong with
			// selecting. Security failure.
			throw (new AuthorizationException(Status.UNAUTHORIZED, "selectDone not found."));
		}

		Result res;
		if (Boolean.TRUE == selectDone) {
			logger.info("Post selecting not required");
			res = Result.SUCCESS;
		} else {
			// Do the selecting
			res = postSelect(ctx);

			if (Result.SUCCESS == res) {
				// Note that selecting has been done.
				ctx.setAttribute(SELECT_DONE_ATTRIBUTE, Boolean.TRUE);
			} else {
				// Asked to select but could not. Security failure.
				throw (new AuthorizationException(Status.UNAUTHORIZED, "Post selecting failed"));
			}
		}
		return (res);
	}

	private Result postSelect(InteractionContext ctx) {

		// TODO implement it.
		logger.info("Post selecting not yet implemented");

		return (Result.SUCCESS);
	}
}
