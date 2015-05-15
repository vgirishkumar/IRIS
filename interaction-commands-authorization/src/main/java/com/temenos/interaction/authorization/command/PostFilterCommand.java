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
		Boolean filterDone = (Boolean) ctx.getAttribute(AuthorizationAttributes.FILTER_DONE_ATTRIBUTE);

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
			do {
				// Do the filtering
				res = postFilter(ctx);

				if (Result.SUCCESS != res) {
					// Asked to filter but could not. Security failure.
					throw (new AuthorizationException(Status.UNAUTHORIZED, "Post filtering failed"));
				}

			} while (getMoreDataIfReqd(ctx));

			// Note that filtering has been done.
			ctx.setAttribute(AuthorizationAttributes.FILTER_DONE_ATTRIBUTE, Boolean.TRUE);

		}
		return (res);
	}

	private Result postFilter(InteractionContext ctx) {

		String filter = ctx.getQueryParameters().getFirst(ODataParser.FILTER_KEY);
		logger.info("Post filtering with \"" + filter + "\"");

		// TODO implement filtering.
		logger.info("Post filtering not yet implemented");

		return (Result.SUCCESS);
	}

	/*
	 * If there is not enough data in the result set to it up.
	 * 
	 * @return True if data toped up. False if data not toped up.
	 * 
	 * Throws if cannot top up.
	 */
	private boolean getMoreDataIfReqd(InteractionContext ctx) throws InteractionException {

		// Get producer
		ODataProducer producer = (ODataProducer) ctx.getAttribute(ODataAttributes.O_DATA_PRODUCER_ATTRIBUTE);
		if (null == producer) {
			throw (new AuthorizationException(Status.UNAUTHORIZED,
					"More data required but OData producer not available"));
		}

		// If we already have enough data no more needed.
		// TODO implement check.
		boolean enoughData = true;
		if (enoughData) {
			return (false);
		}

		// TODO implement top up
		logger.info("Data top up not yet implemented");

		// We added some data so must re-filter.
		return (true);
	}
}
