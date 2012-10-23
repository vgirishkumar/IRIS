package com.temenos.interaction.core.hypermedia;

import java.util.Map;
import java.util.Set;

public class CollectionResourceState extends ResourceState {

	public CollectionResourceState(String entityName, String name, Set<Action> actions, String path) {
		super(entityName, name, actions, path, "collection".split(" "));
	}
	public CollectionResourceState(String entityName, String name, Set<Action> actions, String path, UriSpecification uriSpec) {
		super(entityName, name, actions, path, "collection".split(" "), uriSpec);
	}

	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap) {
		addTransition(httpMethod, targetState, uriLinkageMap, targetState.getPath(), true);
	}
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, int transitionFlags) {
		addTransition(httpMethod, targetState, uriLinkageMap, targetState.getPath(), transitionFlags | Transition.FOR_EACH);
	}

}
