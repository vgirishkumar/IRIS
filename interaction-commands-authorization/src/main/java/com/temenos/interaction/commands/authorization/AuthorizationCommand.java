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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		addRowFilter(ctx);
		addColFilter(ctx);

		Result res = command.execute(ctx);

		// The database may not have fully completed the filtering.
		// So re-do here.
		// TODO Implement full Authorization filtering.
		logger.info("Full authorization filtering not yet implemented");

		return (res);
	}

	private void addRowFilter(InteractionContext ctx) {
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// Get filter from the authorization bean.
		String filterString = authorizationBean.getFilter(ctx);
		
		ArrayList<String> targetList = null;
		if (null != filterString) {
			// Break filter into list of 'anded' terms. Don't care about other
			// boolean logic between 'ands'.
			// Note: May have to revisit this if bracketing becomes important.
			targetList = new ArrayList<String>(Arrays.asList(filterString.split("\\s* and \\s*")));
		} else {
			targetList = new ArrayList<String>();
		}

		// Add any existing row filtering.
		String oldFilter = queryParams.getFirst(FILTER_KEY);
		if (null != oldFilter) {
			targetList.addAll(Arrays.asList(oldFilter.split("\\s* and \\s*")));
		}

		// By the time we get here the target 'and' terms will be in targetList.
		if (targetList.isEmpty()) {
			// No filtering, i.e. return everything. Delete any existing filter
			// term
			queryParams.remove(FILTER_KEY);
		} else {
			// Write in target list
			Iterator<String> it = targetList.iterator();
			String targetStr = new String(it.next());
			while (it.hasNext()) {
				targetStr = targetStr.concat(" and " + it.next());
			}
			queryParams.putSingle(FILTER_KEY, targetStr);
		}
	}

	private void addColFilter(InteractionContext ctx) {
		MultivaluedMap<String, String> queryParams = ctx.getQueryParameters();

		// Get select from the authorization bean.
		String selectString = authorizationBean.getSelect(ctx);

		// Break filter into list of comma separated terms.
		ArrayList<String> targetList;
		if (null != selectString) {
			targetList = new ArrayList<String>(Arrays.asList(selectString.split("\\s*,\\s*")));
		} else {
			targetList = new ArrayList<String>();
		}

		// Parse existing select list. If no old select list then we want all
		// fields in target list.
		String oldSelect = queryParams.getFirst(SELECT_KEY);
		if (null != oldSelect) {
			// Parse old select list into a list
			List<String> oldList = Arrays.asList(oldSelect.split("\\s*,\\s*"));

			// Field is returned only if it is in BOTH lists.
			Iterator<String> it = targetList.iterator();
			while (it.hasNext()) {
				String str = it.next();
				if (!oldList.contains(str)) {
					it.remove();
				}
			}
		}

		// By the time we get here the target select list will be in targetList.
		if (targetList.isEmpty()) {
			// Delete any existing select term
			queryParams.remove(SELECT_KEY);
		} else {
			// Write in target list
			Iterator<String> it = targetList.iterator();
			String targetStr = new String(it.next());
			while (it.hasNext()) {
				targetStr = targetStr.concat("," + it.next());
			}
			queryParams.putSingle(SELECT_KEY, targetStr);
		}
	}
}
