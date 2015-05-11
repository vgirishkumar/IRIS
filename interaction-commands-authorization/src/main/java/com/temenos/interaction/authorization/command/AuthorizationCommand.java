package com.temenos.interaction.authorization.command;

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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;

import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.expression.EntitySimpleProperty;
import org.odata4j.producer.EntityQueryInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.authorization.IAuthorizationProvider;
import com.temenos.interaction.authorization.command.data.AccessProfile;
import com.temenos.interaction.authorization.command.data.FieldName;
import com.temenos.interaction.authorization.command.data.RowFilter;
import com.temenos.interaction.authorization.command.util.ODataParser;
import com.temenos.interaction.authorization.command.util.ODataParser.UnsupportedQueryOperationException;
import com.temenos.interaction.authorization.exceptions.AuthorizationException;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;

public class AuthorizationCommand extends AbstractAuthorizationCommand implements InteractionCommand {
	private final static Logger logger = LoggerFactory.getLogger(AuthorizationCommand.class);

	// Normal constructor
	public AuthorizationCommand(IAuthorizationProvider authorizationBean) {
		this.authorizationBean = authorizationBean;
	}

	/*
	 * Execute the command.
	 * 
	 * If there is any form of internal error during authorization this will throw and nothing should be returned to the use.
	 */
	public Result execute(InteractionContext ctx) throws InteractionException {

		// TODO Remove before production
		// Dump query parameters
		Iterator<String> it = ctx.getQueryParameters().keySet().iterator();
		while (it.hasNext()) {
			String theKey = (String) it.next();
			logger.info("    Key " + theKey + " = Value " + ctx.getQueryParameters().getFirst(theKey));
		}

		EntityQueryInfo queryInfo = ODataParser.getEntityQueryInfo(ctx);

		// Add authorization to context
		applyAuthorization(ctx, queryInfo.filter, queryInfo.select);
		
		// Set attributes indicating that authorization has not yet been done.
		ctx.setAttribute(PostFilterCommand.FILTER_DONE_ATTRIBUTE, Boolean.FALSE);
		ctx.setAttribute(PostSelectCommand.SELECT_DONE_ATTRIBUTE, Boolean.FALSE);

		return (Result.SUCCESS);
	}

	/**
	 * This method will apply Authorization on InteractionContext for filtering
	 * data
	 * 
	 * @param ctx
	 * @param oldFilter
	 * @param oldSelect
	 * @throws UnsupportedQueryOperationException
	 */
	private void applyAuthorization(InteractionContext ctx, BoolCommonExpression oldFilter,
			List<EntitySimpleProperty> oldSelect) throws InteractionException {

		// TODO When IRIS supports it the following line will become a call to an Authorization resource.
		AccessProfile accessProfile = authorizationBean.getAccessProfile(ctx);
		
		List<RowFilter> newList = accessProfile.getRowFilters();
		try {
			addRowFilter(ctx, newList, oldFilter);
		} catch (UnsupportedQueryOperationException e) {
			throw (new AuthorizationException(Status.UNAUTHORIZED, e.toString()));
		}

		Set<FieldName> authSet = accessProfile.getFieldNames();
		addColFilter(ctx, authSet, oldSelect);
	}

	private boolean addRowFilter(InteractionContext ctx, List<RowFilter> newList, BoolCommonExpression oldFilter)
			throws UnsupportedQueryOperationException {
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// Get any existing filter
		List<RowFilter> oldList = ODataParser.parseFilter(oldFilter);

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
			queryParams.remove(ODataParser.FILTER_KEY);
		} else {
			queryParams.putSingle(ODataParser.FILTER_KEY, ODataParser.toFilter(newList));
		}

		// Return the entries specified by the filter.
		return (true);
	}

	private void addColFilter(InteractionContext ctx, Set<FieldName> authSet, List<EntitySimpleProperty> oldSelect) {

		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// Get any existing select
		Set<FieldName> oldSet = ODataParser.parseSelect(oldSelect);

		if (authSet.isEmpty()) {
			// Empty authorization list means 'return all requested' i.e.
			// don't modify existing $select parameter.
			return;
		} else {
			if (oldSet.isEmpty()) {
				// Empty oldlist means just return authorization list
				queryParams.putSingle(ODataParser.SELECT_KEY, ODataParser.toSelect(authSet));
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
				queryParams.putSingle(ODataParser.SELECT_KEY, ODataParser.toSelect(oldSet));
			}
		}
	}
}
