package com.temenos.interaction.core.hypermedia;

import java.util.List;
import java.util.Map;

public class CollectionResourceState extends ResourceState {

	public CollectionResourceState(String entityName, String name, List<Action> actions, String path) {
		super(entityName, name, actions, path, "collection".split(" "));
	}
	public CollectionResourceState(String entityName, String name, List<Action> actions, String path, UriSpecification uriSpec) {
		super(entityName, name, actions, path, "collection".split(" "), uriSpec);
	}
	public CollectionResourceState(String entityName, String name, List<Action> actions, String path, String[] rels, UriSpecification uriSpec) {
		super(entityName, name, actions, path, rels, uriSpec);
	}

	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap) {
		addTransition(httpMethod, targetState, uriLinkageMap, null, targetState.getPath(), true);
	}
	
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties) {
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, targetState.getPath(), true);
	}
	
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, int transitionFlags) {
		addTransition(httpMethod, targetState, uriLinkageMap, null, targetState.getPath(), transitionFlags | Transition.FOR_EACH, null);
	}
	
	public void addTransitionForEachItem(String httpMethod, ResourceState targetState, Map<String, String> uriLinkageMap, Map<String, String> uriLinkageProperties, int transitionFlags) {
		addTransition(httpMethod, targetState, uriLinkageMap, uriLinkageProperties, targetState.getPath(), transitionFlags | Transition.FOR_EACH, null);
	}

}
