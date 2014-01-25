package com.temenos.interaction.core.hypermedia;

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


import java.util.List;
import java.util.Map;

import com.temenos.interaction.core.hypermedia.expression.Expression;

public class CollectionResourceState extends ResourceState {

	public CollectionResourceState(String entityName, String name, List<Action> actions, String path) {
		super(entityName, name, actions, path, "collection".split(" "));
	}
	public CollectionResourceState(String entityName, String name, List<Action> actions, String path, UriSpecification uriSpec) {
		super(entityName, name, actions, path, "collection".split(" "), uriSpec);
	}
	public CollectionResourceState(String entityName, String name, List<Action> actions, String path, String[] rels, UriSpecification uriSpec) {
		super(entityName, name, actions, path, rels != null ? rels : "collection".split(" "), uriSpec);
	}
	public CollectionResourceState(String entityName, String name, List<Action> actions, String path, String[] rels, UriSpecification uriSpec, ResourceState errorState) {
		super(entityName, name, actions, path, rels != null ? rels : "collection".split(" "), uriSpec, errorState);
	}

	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap) {
		addTransition(httpMethod, targetState, uriLinkageMap, true, null);
	}
	
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, String label) {
		addTransition(httpMethod, targetState, uriLinkageMap, true, label);
	}
	
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, List<Expression> linkConditions, String label) {
		addTransition(httpMethod, targetState, uriLinkageMap, targetState.getPath(), Transition.FOR_EACH, linkConditions, label);
	}

	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, int transitionFlags) {
		addTransition(httpMethod, targetState, uriLinkageMap, targetState.getPath(), transitionFlags | Transition.FOR_EACH, null, null);
	}
	
}
