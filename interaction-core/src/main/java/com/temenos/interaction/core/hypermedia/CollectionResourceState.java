package com.temenos.interaction.core.hypermedia;

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

	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap) {
		addTransition(httpMethod, targetState, uriLinkageMap, null, targetState.getPath(), true, null);
	}
	
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties) {
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, targetState.getPath(), true, null);
	}

	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties, String label) {
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, targetState.getPath(), true, label);
	}
	
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties, List<Expression> linkConditions, String label) {
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, targetState.getPath(), Transition.FOR_EACH, linkConditions, label);
	}

	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, int transitionFlags) {
		addTransition(httpMethod, targetState, uriLinkageMap, null, targetState.getPath(), transitionFlags | Transition.FOR_EACH, null, null);
	}
	
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties, int transitionFlags) {
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, targetState.getPath(), transitionFlags | Transition.FOR_EACH, null, null);
	}

}
