package com.temenos.interaction.authorization.command;

/*
 * PostFilter bean. This is called after the database query. If the database was unable to carry out the $filter operation then
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

import org.odata4j.producer.ODataProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.authorization.command.util.ODataParser;
import com.temenos.interaction.authorization.exceptions.AuthorizationException;
import com.temenos.interaction.commands.odata.ODataAttributes;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

public class PostFilterCommand implements InteractionCommand {

	private final static Logger logger = LoggerFactory.getLogger(PostFilterCommand.class);

	/*
	 * Execute the command.
	 */
	public Result execute(InteractionContext ctx) throws InteractionException {

		// Check if filtering has already been done.
		Boolean filterDone = (Boolean)ctx.getAttribute(AuthorizationAttributes.FILTER_DONE_ATTRIBUTE);

		if (null == filterDone) {
			// If attribute not present then something has gone wrong with
			// filtering. Security failure.
			throw (new AuthorizationException(Status.UNAUTHORIZED, "FilterDone not found."));
		}

		Result res;
		if (Boolean.TRUE == filterDone) {
			logger.info("Post filtering not required");
			res = Result.SUCCESS;
		} else {
			// Do the filtering
			res = postFilter(ctx);

			if (Result.SUCCESS == res) {
				// Note that filtering has been done.
				ctx.setAttribute(AuthorizationAttributes.FILTER_DONE_ATTRIBUTE, Boolean.TRUE);
			} else {
				// Asked to filter but could not. Security failure.
				throw (new AuthorizationException(Status.UNAUTHORIZED, "Post filtering failed"));
			}
		}
		return (res);
	}

	private Result postFilter(InteractionContext ctx) throws InteractionException {
		
		String filter = ctx.getQueryParameters().getFirst(ODataParser.FILTER_KEY);
		logger.info("Post filtering with \"" + filter + "\"");
		
		// If there is not enough data and a producer is available get more data.
		ODataProducer producer = (ODataProducer)ctx.getAttribute(ODataAttributes.O_DATA_PRODUCER_ATTRIBUTE);		
		if (null == producer) {
			throw (new AuthorizationException(Status.UNAUTHORIZED, "More data required but OData producer not available"));
		}
		
		// TODO implement it.
		logger.info("Post filtering not yet implemented");

		return (Result.SUCCESS);
	}
}
