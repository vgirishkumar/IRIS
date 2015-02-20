package com.temenos.interaction.commands.authorization;

/*
 * Authorization inteceptor bean. This is passed parameters corresponding to the options of an OData request and a child
 * InteractionCommand. It checks T24 Authorization and adds OData parameters, implementing the required additional filtering, as 
 * defined in the OData protocol specification:
 * 
 *   http://www.odata.org/documentation/odata-version-3-0/odata-version-3-0-core-protocol
 *   
 * It then passes the modified parameters to the child command.
 * 
 * The child command will make a 'best effort' to implement the requested filtering. This should limit the amount of data
 * returned but cannot be guaranteed to be 100% complete for all databases. On exit this module must check and make any
 * final adjustments to the returned data set.
 */

/*
 * #%L
 * interaction-commands-Authorization
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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.producer.EntityQueryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.commands.authorization.oDataParser.UnsupportedQueryOperationException;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

public class AuthorizationCommand extends AbstractAuthorizationCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(AuthorizationCommand.class);

	// Normal constructor
	public AuthorizationCommand(InteractionCommand command, AuthorizationBean authorizationBean) {
		this.command = command;
		this.authorizationBean = authorizationBean;
	}

	public Result execute(InteractionContext ctx) throws InteractionException {

		// TODO Remove before production
		// Dump query parameters
		Iterator<String> it = ctx.getQueryParameters().keySet().iterator();
		while (it.hasNext()) {
			String theKey = (String) it.next();
			logger.info("    Key " + theKey + " = Value " + ctx.getQueryParameters().getFirst(theKey));
		}

		try {
			// Parse the incoming oData. Do once extracting filter and select.
			EntityQueryInfo queryInfo = oDataParser.getEntityQueryInfo(ctx);

			if (!addRowFilter(ctx, queryInfo.filter)) {
				logger.info("After authorization there are no rows to return. Command not called.");
				return (Result.SUCCESS);
			}

			addColFilter(ctx, queryInfo.select);

		} catch (Exception e) {
			// Any sort of exception is an Authorization failure
			logger.info("Authorization failed: " + e.getMessage());
			return (Result.FAILURE);
		}

		Result res = command.execute(ctx);

		// The database may not have fully completed the filtering.
		// So re-do here.
		// TODO Implement full Authorization filtering.
		logger.info("Full authorization filtering not yet implemented");

		return (res);
	}

	// Add a row.
	//
	// @Returns true = rows added, false = rows not added. Return no entries
	private boolean addRowFilter(InteractionContext ctx, BoolCommonExpression oldFilter)
			throws UnsupportedQueryOperationException {
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// Get filter from the authorization bean.
		List<RowFilter> newList = authorizationBean.getFilters(ctx);
		if (null == newList) {
			// Null means return no entries
			return (false);
		}

		// Get any existing filter
		List<RowFilter> oldList = oDataParser.parseFilter(oldFilter);

		// Final list contains both sets of filters
		if (null != oldList) {
			// TODO Some additional work may be required to combine filters on
			// the same column. What if "a > b" and
			// "a = c"? For now include both and let the database decide how it
			// handles tests conditions.
			newList.addAll(oldList);
		}

		// By the time we get here the target 'and' terms will be in newList.
		if (newList.isEmpty()) {
			// No filtering, i.e. return everything. Delete any existing filter.
			queryParams.remove(oDataParser.FILTER_KEY);
		} else {
			queryParams.putSingle(oDataParser.FILTER_KEY, oDataParser.toFilter(newList));
		}

		// Return the entries specified by the filter.
		return (true);
	}

	private void addColFilter(InteractionContext ctx, List<EntitySimpleProperty> oldSelect) {

		// getEntityQueryInfo returns an empty list for missing 'selects'. In
		// the authentication framework these
		// are represented with nulls.
		if (oldSelect.isEmpty()) {
			oldSelect = null;
		}

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// Get select from the authorization bean.
		Set<FieldName> authSet = authorizationBean.getSelect(ctx);

		// Get any existing select
		Set<FieldName> oldSet = oDataParser.parseSelect(oldSelect);

		if (null == authSet) {
			// null from authorization means 'return all requested' i.e.
			// don't modify existing $select parameter.
			return;
		} else {
			if (null == oldSet) {
				// null in oldlist means just return authorization list
				queryParams.putSingle(oDataParser.SELECT_KEY, oDataParser.toSelect(authSet));
			} else {

				// If we get here both sets contain entries. Final list is
				// a union of the other two.
				Iterator<FieldName> it = oldSet.iterator();
				while (it.hasNext()) {
					FieldName oldName = it.next();
					if (!authSet.contains(oldName)) {
						it.remove();
					}
				}

				// By the time we get here the target select list will be
				// in oldSet. Write the target list ... which may be empty
				queryParams.putSingle(oDataParser.SELECT_KEY, oDataParser.toSelect(oldSet));
			}
		}
	}
}
