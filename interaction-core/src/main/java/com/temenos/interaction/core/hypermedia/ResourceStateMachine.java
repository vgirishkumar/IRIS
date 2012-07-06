package com.temenos.interaction.core.hypermedia;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.resource.ServiceDocumentResource;
import com.temenos.interaction.core.web.RequestContext;

/**
 * A state machine that is responsible for creating the links (hypermedia) to other
 * valid application states.
 * @author aphethean
 *
 */
public class ResourceStateMachine {
	private final Logger logger = LoggerFactory.getLogger(ResourceStateMachine.class);

	public final ResourceState initial;
	public final Transformer transformer;
		
	public ResourceStateMachine(ResourceState initialState) {
		this(initialState, null);
	}

	public ResourceStateMachine(ResourceState initialState, Transformer transformer) {
		this.initial = initialState;
		this.initial.setInitial(true);
		this.transformer = transformer;
	}

	public ResourceState getInitial() {
		return initial;
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public Collection<ResourceState> getStates() {
		List<ResourceState> result = new ArrayList<ResourceState>();
		collectStates(result, initial);
		return result;
	}

	private void collectStates(Collection<ResourceState> result, ResourceState currentState) {
		if (result.contains(currentState)) return;
		result.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			if (!next.equals(initial)) {
				collectStates(result, next);
			}
		}
		
	}

	/**
	 * Return a map of all the paths (states), and interactions with other states
	 * @return
	 */
	public Map<String, Set<String>> getInteractionMap() {
		Map<String, Set<String>> interactionMap = new HashMap<String, Set<String>>();
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectInteractions(interactionMap, states, initial);
		return interactionMap;
	}
	
	private void collectInteractions(Map<String, Set<String>> result, Collection<ResourceState> states, ResourceState currentState) {
		if (currentState == null || states.contains(currentState)) return;
		states.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			// is the target a state of the same entity
			if (next.getEntityName().equals(currentState.getEntityName())) {
				// lookup transition to get to here
				Transition t = currentState.getTransition(next);
				TransitionCommandSpec command = t.getCommand();
				String path = command.getPath();
				
				Set<String> interactions = result.get(path);
				if (interactions == null)
					interactions = new HashSet<String>();
				interactions.add(command.getMethod());
				
				result.put(path, interactions);
				collectInteractions(result, states, next);
			}
		}
		
	}

	/**
	 * For a given resource state, get the valid interactions.
	 * @param state
	 * @return
	 */
	public Set<String> getInteractions(ResourceState state) {
		Set<String> interactions = null;
		if(state != null) {
			assert(getStates().contains(state));
			Map<String, Set<String>> interactionMap = getInteractionMap();
			interactions = interactionMap.get(state.getPath());
		}
		return interactions;
	}
	
	/**
	 * Return a map of all the paths to the various ResourceState's
	 * @return
	 */
	public Map<String, ResourceState> getStateMap() {
		return getStateMap(initial);
	}

	public Map<String, ResourceState> getStateMap(ResourceState begin) {
		if (begin == null)
			begin = initial;
		Map<String, ResourceState> stateMap = new HashMap<String, ResourceState>();
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectStates(stateMap, states, begin);
		return stateMap;
	}

	private void collectStates(Map<String, ResourceState> result, Collection<ResourceState> states, ResourceState currentState) {
		if (currentState == null || states.contains(currentState)) return;
		states.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			if (!next.isSelfState()) {
				String path = next.getPath();
				
				if (result.get(path) != null)
					logger.debug("Replacing ResourceState[" + path + "] " + result.get(path));
				
				result.put(path, next);
			}
			collectStates(result, states, next);
		}
		
	}

	/**
	 * For a given path, return the resource state.
	 * @param state
	 * @return
	 */
	public ResourceState getState(String path) {
		if (path == null)
			return initial;
		return getStateMap().get(path);
	}

	/**
	 * Evaluate and return all the valid links (target states) from this resource state.
	 * @param pathParameters
	 * @param resourceEntity
	 * @param state
	 * @param linkRelations
	 * @return
	 */
	public Collection<Link> getLinks(MultivaluedMap<String, String> pathParameters, RESTResource resourceEntity, ResourceState state, List<String> linkRelations) {
		List<Link> links = new ArrayList<Link>();
		if (resourceEntity == null)
			return links;
		
		Object entity = null;
		CollectionResource<?> collectionResource = null;
		if (resourceEntity instanceof EntityResource) {
			entity = ((EntityResource<?>) resourceEntity).getEntity();
		} else if (resourceEntity instanceof CollectionResource) {
			collectionResource = (CollectionResource<?>) resourceEntity;
			// TODO add support for properties on collections
			logger.warn("I hope you don't need to build a link from a template for links from this collection, no properties on the collection at the moment");
		} else if (resourceEntity instanceof MetaDataResource) {
			// TODO deprecate all resource types apart from item (EntityResource) and collection (CollectionResource)
			logger.debug("Returning from the call to getLinks for a MetaDataResource without doing anything");
			return links;
		} else if (resourceEntity instanceof ServiceDocumentResource) {
			// TODO deprecate all resource types apart from item (EntityResource) and collection (CollectionResource)
			logger.debug("Returning from the call to getLinks for a ServiceDocumentResource without doing anything");
			return links;
		} else {
			throw new RuntimeException("Unable to get links, an error occurred");
		}
		
		// add link to GET 'self'
		links.add(createSelfLink(state, entity, pathParameters));

		/*
		 * Add links to other application states (resources)
		 */
		Collection<ResourceState> targetStates = state.getAllTargets();
		for (ResourceState s : targetStates) {
			Transition transition = state.getTransition(s);
			TransitionCommandSpec cs = transition.getCommand();
			/* 
			 * build link and add to list of links
			 */
			UriBuilder linkTemplate = RequestContext.getRequestContext().getBasePath().path(cs.getPath());
			if (cs.isForEach()) {
				if (collectionResource != null) {
					for (EntityResource<?> er : collectionResource.getEntities()) {
						Collection<Link> eLinks = er.getLinks();
						if (eLinks == null) {
							eLinks = new ArrayList<Link>();
						}
						eLinks.add(createLink(linkTemplate, transition, er.getEntity(), null));
						er.setLinks(eLinks);
					}
				}
			} else {
				links.add(createLink(linkTemplate, transition, entity, null));
			}
		}
		return links;
	}

	/**
	 * Create a Link to a target state if the supplied custom 
	 * link relation resolves to a valid transition.
	 * @param customLinkRelations
	 * @param resourceEntity
	 * @param currentState
	 * @return
	 */
	public Link getLinkFromRelations(MultivaluedMap<String, String> pathParameters, RESTResource resourceEntity, ResourceState currentState, List<String> customLinkRelations) {
		Link target = null;
		// Was a custom link relation supplied, informing us which link was used?
		if (customLinkRelations != null) {
			for (String link : customLinkRelations) {
				for (ResourceState nextState : currentState.getAllTargets()) {
					Transition transition = currentState.getTransition(nextState);
					if (link.contains(transition.getId())) {
						target = createSelfLink(transition.getTarget(), resourceEntity, pathParameters);
					}
				}
			}
		}
		return target;
	}
	
	/*
	 * @invariant {@link RequestContext} must have been initialised
	 */
	private Link createSelfLink(ResourceState state, Object entity, MultivaluedMap<String, String> pathParameters) {
		assert(RequestContext.getRequestContext() != null);
		UriBuilder selfUriTemplate = RequestContext.getRequestContext().getBasePath().path(state.getPath());
		return createLink(selfUriTemplate, state.getSelfTransition(), entity, pathParameters);
	}
	
	private Link createLink(UriBuilder linkTemplate, Transition transition, Object entity, MultivaluedMap<String, String> map) {
		TransitionCommandSpec cs = transition.getCommand();
		try {
			String linkId = transition.getId();
			// TODO get rels properly
			String rel = transition.getTarget().getRel();
			if (transition.getSource().equals(transition.getTarget())) {
				rel = "self";
			}
			
			String method = cs.getMethod();
			URI href = null;
			Map<String, Object> properties = new HashMap<String, Object>();
			if (map != null) {
				for (String key : map.keySet()) {
					properties.put(key, map.getFirst(key));
				}
			}
			if (entity != null) {
				if (transformer != null) {
					properties.putAll(transformer.transform(entity));
					href = linkTemplate.buildFromMap(properties);
				} else {
					href = linkTemplate.build(entity);
				}
			} else {
				href = linkTemplate.buildFromMap(properties);
			}
			Link link = new Link(linkId, rel, href.toASCIIString(), null, null, method, "label", "description", null);
			logger.debug("Created link for transition [" + transition + "] [id=" + linkId+ ", rel=" + rel + ", method=" + method + ", href=" + href.toString() + "(" + href.toASCIIString() + ")]");
			return link;
		} catch (IllegalArgumentException e) {
			logger.error("An error occurred while creating link [" +  cs.getPath() + "]", e);
			throw e;
		} catch (UriBuilderException e) {
			logger.error("An error occurred while creating link [" + cs.getPath() + "]", e);
			throw e;
		}
	}

}
