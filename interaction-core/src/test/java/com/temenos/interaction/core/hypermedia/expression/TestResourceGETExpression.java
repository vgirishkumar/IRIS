package com.temenos.interaction.core.hypermedia.expression;

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


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Assert;
import org.junit.Test;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionCommand.Result;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.Action.TYPE;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression.Function;

public class TestResourceGETExpression {

	private NewCommandController mockCommandController() {
		NewCommandController cc = new NewCommandController();
		try {
			InteractionCommand notfound = mock(InteractionCommand.class);
			when(notfound.execute(any(InteractionContext.class))).thenReturn(Result.FAILURE);
			InteractionCommand found = mock(InteractionCommand.class);
			when(found.execute(any(InteractionContext.class))).thenReturn(Result.SUCCESS);
			
			cc.addCommand("notfound", notfound);
			cc.addCommand("found", found);
		}
		catch(InteractionException ie) {
			Assert.fail(ie.getMessage());
		}
		return cc;
	}
	
	private ResourceStateMachine mockRIM() {
		String rootResourcePath = "/bookings/{bookingId}";
		ResourceState initial = new ResourceState("BOOKING", "initial", new ArrayList<Action>(), rootResourcePath);
		// room reserved for the booking
		ResourceState room = new ResourceState(initial, "room", new ArrayList<Action>(), "/room");
		// booking cancelled
		ResourceState cancelled = new ResourceState(initial, "cancelled", new ArrayList<Action>(), "/cancelled", "cancel".split(" "));
		// payment information
		ResourceState paid = new ResourceState(initial, "paid", new ArrayList<Action>(), "/payment", "pay".split(" "));
		// waiting for merchant confirmation
		List<Action> mockNotFound = new ArrayList<Action>();
		mockNotFound.add(new Action("notfound", TYPE.VIEW));
		ResourceState pwaiting = new ResourceState(paid, "pwaiting", mockNotFound, "/pwaiting", "wait".split(" "));
		// merchant confirmed
		List<Action> mockFound = new ArrayList<Action>();
		mockFound.add(new Action("found", TYPE.VIEW));
		ResourceState pconfirmed = new ResourceState(paid, "pconfirmed", mockFound, "/pconfirmed", "confirmed".split(" "));

		// create transitions that indicate state
		initial.addTransition(room);
		initial.addTransition(cancelled);
		initial.addTransition(paid);
		// TODO, expressions should also be followed in determining resource state graph
		initial.addTransition(pwaiting);
		initial.addTransition(pconfirmed);
		
		// pseudo states that do the processing
		ResourceState cancel = new ResourceState(cancelled, "psuedo_cancel", new ArrayList<Action>());
		ResourceState assignRoom = new ResourceState(room, "psuedo_assignroom", new ArrayList<Action>());
		ResourceState paymentDetails = new ResourceState(paid, "psuedo_setcarddetails", new ArrayList<Action>());
		
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		int transitionFlags = 0;  // regular transition
		// create the transitions (links)
		initial.addTransition("POST", cancel);
		initial.addTransition("PUT", assignRoom);
		
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(new ResourceGETExpression(pconfirmed.getName(), Function.NOT_FOUND));
		expressions.add(new ResourceGETExpression(pwaiting.getName(), Function.NOT_FOUND));
		initial.addTransition("PUT", paymentDetails, uriLinkageMap, transitionFlags, expressions, "Make a payment");

		return new ResourceStateMachine(initial);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testLookupResourceThenGET_404() {
		ResourceStateMachine rsm = mockRIM();
		rsm.setCommandController(mockCommandController());
		InteractionContext ctx = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), rsm.getInitial(), mock(Metadata.class));
		
		ResourceGETExpression rgeOK = new ResourceGETExpression("pwaiting", Function.OK);
		boolean result1 = rgeOK.evaluate(rsm, ctx);
		assertFalse("We did a GET on 'pwaiting' and it was NOT_FOUND(404), therefore OK link condition evaluates to 'false'", result1);
		ResourceGETExpression rgeNOT_FOUND = new ResourceGETExpression("pwaiting", Function.NOT_FOUND);
		boolean result2 = rgeNOT_FOUND.evaluate(rsm, ctx);
		assertTrue("We did a GET on 'pwaiting' and it was NOT_FOUND(404), therefore NOT_FOUND link condition evaluates to 'true'", result2);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testLookupResourceThenGET_200() {
		ResourceStateMachine rsm = mockRIM();
		rsm.setCommandController(mockCommandController());
		InteractionContext ctx = new InteractionContext(mock(MultivaluedMap.class), mock(MultivaluedMap.class), rsm.getInitial(), mock(Metadata.class));
		
		ResourceGETExpression rgeOK = new ResourceGETExpression("pconfirmed", Function.OK);
		boolean result1 = rgeOK.evaluate(rsm, ctx);
		assertTrue("We did a GET on 'pconfirmed' and it was OK(200), therefore OK link condition evaluates to 'true'", result1);
		ResourceGETExpression rgeNOT_FOUND = new ResourceGETExpression("pconfirmed", Function.NOT_FOUND);
		boolean result2 = rgeNOT_FOUND.evaluate(rsm, ctx);
		assertFalse("We did a GET on 'pconfirmed' and it was OK(200), therefore NOT_FOUND link condition evaluates to 'false'", result2);
	}
	
}
