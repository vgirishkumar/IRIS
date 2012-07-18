package com.temenos.interaction.core.hypermedia;

import java.util.Map;

public class CollectionResourceState extends ResourceState {

	public CollectionResourceState(String entityName, String name, String path) {
		super(entityName, name, path, "collection".split(" "));
	}

	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap) {
		addTransition(httpMethod, targetState, uriLinkageMap, targetState.getPath(), true);
	}
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, int transitionFlags) {
		addTransition(httpMethod, targetState, uriLinkageMap, targetState.getPath(), transitionFlags | Transition.FOR_EACH);
	}

}
