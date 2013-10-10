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


import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import com.temenos.interaction.core.command.CommandFailureException;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;

public class ResourceGETExpression implements Expression {

	public enum Function {
		OK,
		NOT_FOUND
	}
	
	public final Function function;
	public final String state;
	
	public ResourceGETExpression(String state, Function function) {
		this.function = function;
		this.state = state;
	}
	
	public Function getFunction() {
		return function;
	}

	public String getState() {
		return state;
	}
	
	@Override
	public boolean evaluate(ResourceStateMachine hypermediaEngine, InteractionContext ctx) {
		ResourceState target = hypermediaEngine.getResourceStateByName(state);
		if (target == null)
			throw new IllegalArgumentException("Indicates a problem with the RIM, it allowed an invalid state to be supplied");
		assert(target.getActions() != null);
		assert(target.getActions().size() == 1);
		
		//Create a new interaction context for this state
    	Transition transition = ctx.getCurrentState().getTransition(target);
    	MultivaluedMap<String, String> pathParameters = getPathParametersForTargetState(hypermediaEngine, ctx, transition);
    	InteractionContext newCtx = new InteractionContext(ctx, pathParameters, null, target);

    	//Get the target resource
		try {
			hypermediaEngine.getResource(target, newCtx, false);	//Ignore the resource and its links, just interested in the result status
			if(getFunction().equals(Function.OK)) {
				return true;
			}
		}
		catch(CommandFailureException cfe) {
			if(getFunction().equals(Function.NOT_FOUND)) {
				return true;
			}
		}
		catch(InteractionException ie) {
			if(getFunction().equals(Function.NOT_FOUND)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (getFunction().equals(ResourceGETExpression.Function.OK))
			sb.append("OK(").append(getState()).append(")");
		if (getFunction().equals(ResourceGETExpression.Function.NOT_FOUND))
			sb.append("NOT_FOUND").append(getState()).append(")");
		return sb.toString();
	}
	
	/*
	 * Obtain path parameters to use when accessing
	 * a resource state on an expression.  
	 */
	@SuppressWarnings("unchecked")
	private MultivaluedMap<String, String> getPathParametersForTargetState(ResourceStateMachine hypermediaEngine, InteractionContext ctx, Transition transition) {
		//If available (for performance), read transition properties from the context
    	Map<String, Object> transitionProperties = new HashMap<String, Object>();
    	Object transPropsAttr = ctx.getAttribute(ResourceStateMachine.TRANSITION_PROPERTIES_CTX_ATTRIBUTE);
    	if(transPropsAttr != null) {
    		transitionProperties = (Map<String, Object>) transPropsAttr;
    	}
    	else {
    		//Otherwise, re-evaluate transition properties 
    		RESTResource resource = ctx.getResource();
    		if(resource != null && resource instanceof EntityResource) {
    			Object entity = ((EntityResource<?>) resource).getEntity();
    	    	transitionProperties = hypermediaEngine.getTransitionProperties(transition, entity, null); 
    		}    		
    	}
    	
    	//apply transition properties to path parameters 
    	return hypermediaEngine.getPathParametersForTargetState(transition, transitionProperties);
	}
}
