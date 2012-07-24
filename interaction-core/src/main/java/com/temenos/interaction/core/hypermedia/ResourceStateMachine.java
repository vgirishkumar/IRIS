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
	
	// optimised access
	private Map<String,Transition> transitionsById = new HashMap<String,Transition>();
	private List<ResourceState> allStates = new ArrayList<ResourceState>();
	private Map<String, Set<String>> interactionsByPath = new HashMap<String, Set<String>>();
	private Map<String, ResourceState> resourceStatesByPath = new HashMap<String, ResourceState>();
	
	public ResourceStateMachine(ResourceState initialState) {
		this(initialState, null);
	}

	/**
	 * 
	 * @invariant initial state not null
	 * @param initialState
	 * @param transformer
	 */
	public ResourceStateMachine(ResourceState initialState, Transformer transformer) {
		assert(initialState != null);
		this.initial = initialState;
		this.initial.setInitial(true);
		this.transformer = transformer;
		build();
	}

	private void build() {
		collectStates(allStates, initial);
		collectTransitionsById(transitionsById);
		collectInteractionsByPath(interactionsByPath);
		collectResourceStatesByPath(resourceStatesByPath);
	}
	
	public ResourceState getInitial() {
		return initial;
	}

	public Transformer getTransformer() {
		return transformer;
	}

	public Collection<ResourceState> getStates() {
		return allStates;
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

	private void collectTransitionsById(Map<String,Transition> transitions) {
		for (ResourceState s : getStates()) {
			for (ResourceState target : s.getAllTargets()) {
				Transition transition = s.getTransition(target);
				transitions.put(transition.getId(), transition);
			}
		}
	}
	
	/**
	 * Return a map of all the paths (states), and interactions with other states
	 * @return
	 */
	public Map<String, Set<String>> getInteractionByPath() {
		return interactionsByPath;
	}
	
	private void collectInteractionsByPath(Map<String, Set<String>> result) {
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectInteractionsByPath(result, states, initial);
	}
	
	private void collectInteractionsByPath(Map<String, Set<String>> result, Collection<ResourceState> states, ResourceState currentState) {
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
				if (command.getMethod() != null && ! command.isAutoTransition())
					interactions.add(command.getMethod());
				
				result.put(path, interactions);
				collectInteractionsByPath(result, states, next);
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
			Map<String, Set<String>> interactionMap = getInteractionByPath();
			interactions = interactionMap.get(state.getPath());
		}
		return interactions;
	}
	
	/**
	 * Return a map of all the paths to the various ResourceState's
	 * @invariant initial state not null
	 * @return
	 */
	public Map<String, ResourceState> getResourceStatesByPath() {
		return resourceStatesByPath;
	}

	/*
	 * @invariant begin state not null
	 * @invariant initial state not null
	 */
	public Map<String, ResourceState> getResourceStatesByPath(ResourceState begin) {
		assert(begin != null);
		Map<String, ResourceState> stateMap = new HashMap<String, ResourceState>();
		collectResourceStatesByPath(stateMap, begin);
		return stateMap;
	}

	private void collectResourceStatesByPath(Map<String, ResourceState> result) {
		collectResourceStatesByPath(result, initial);
	}

	private void collectResourceStatesByPath(Map<String, ResourceState> result, ResourceState begin) {
		List<ResourceState> states = new ArrayList<ResourceState>();
		result.put(begin.getPath(), begin);
		collectResourceStatesByPath(result, states, begin);
	}

	private void collectResourceStatesByPath(Map<String, ResourceState> result, Collection<ResourceState> states, ResourceState currentState) {
		if (currentState == null || states.contains(currentState)) return;
		states.add(currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			if (!next.equals(currentState) && !next.isPseudoState()) {
				String path = next.getPath();
				if (result.get(path) != null)
					logger.warn("Replacing ResourceState[" + path + "] " + result.get(path) + " with " + next + ", this could result in unexpected transitions.");
				result.put(path, next);
			}
			collectResourceStatesByPath(result, states, next);
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
		return getResourceStatesByPath().get(path);
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
		links.add(createSelfLink(state.getSelfTransition(), entity, pathParameters));

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
			UriBuilder linkTemplate = UriBuilder.fromUri(RequestContext.getRequestContext().getBasePath()).path(cs.getPath());
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
	 * Find the transition that was used by evaluating the LinkHeader and 
	 * create a a Link for that transition.
	 * @param pathParameters
	 * @param resourceEntity
	 * @param customLinkRelations
	 * @return
	 */
	public Link getLinkFromRelations(MultivaluedMap<String, String> pathParameters, RESTResource resourceEntity, LinkHeader linkHeader) {
		Link target = null;
		// Was a custom link relation supplied, informing us which link was used?
		if (linkHeader != null) {
			Set<String> relationships = linkHeader.getLinksByRelationship().keySet();
			for (String related : relationships) {
				Transition transition = getTransitionsById().get(related);
				if (transition != null) {
					target = createLinkToTarget(transition, resourceEntity, pathParameters);
				}
			}
		}
		return target;
	}

	public Map<String,Transition> getTransitionsById() {
		return transitionsById;
	}
	
	/**
	 * Find the transition that was used by assuming the HTTP method was
	 * applied to this state; create a a Link for that transition.
	 * @param pathParameters
	 * @param resourceEntity
	 * @param currentState
	 * @param method
	 * @return
	 * @invariant method != null
	 */
	public Link getLinkFromMethod(MultivaluedMap<String, String> pathParameters, RESTResource resourceEntity, ResourceState currentState, String method) {
		assert(method != null);
		Link target = null;
		for (ResourceState nextState : currentState.getAllTargets()) {
			Transition transition = currentState.getTransition(nextState);
			if (method.contains(transition.getCommand().getMethod())) {
				// do not create link if this a pseudo state, effectively no state
				if (!transition.getTarget().isPseudoState())
					target = createLinkToTarget(transition, resourceEntity, pathParameters);
			}
		}
		return target;
	}

	/*
	 * @invariant {@link RequestContext} must have been initialised
	 */
	public Link createLinkToTarget(Transition transition, Object entity, MultivaluedMap<String, String> pathParameters) {
		assert(RequestContext.getRequestContext() != null);
		UriBuilder selfUriTemplate = UriBuilder.fromUri(RequestContext.getRequestContext().getBasePath()).path(transition.getTarget().getPath());
		return createLink(selfUriTemplate, transition, entity, pathParameters);
	}

	/*
	 * @invariant {@link RequestContext} must have been initialised
	 */
	private Link createSelfLink(Transition transition, Object entity, MultivaluedMap<String, String> pathParameters) {
		assert(RequestContext.getRequestContext() != null);
		UriBuilder selfUriTemplate = UriBuilder.fromUri(RequestContext.getRequestContext().getBasePath()).path(transition.getCommand().getPath());
		return createLink(selfUriTemplate, transition, entity, pathParameters);
	}

	private Link createLink(UriBuilder linkTemplate, Transition transition, Object entity, MultivaluedMap<String, String> map) {
		TransitionCommandSpec cs = transition.getCommand();
		try {
			String rel = "self";
			if (!transition.getTarget().getRel().equals("self") && !transition.getSource().equals(transition.getTarget())) {
				rel = transition.getTarget().getName();		//Not a self-link so use name of target state as relation name
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
			Link link = new Link(transition, rel, href.toASCIIString(), method);
			logger.debug("Created link for transition [" + transition + "] [title=" + transition.getId()+ ", rel=" + rel + ", method=" + method + ", href=" + href.toString() + "(" + href.toASCIIString() + ")]");
			return link;
		} catch (IllegalArgumentException e) {
			logger.error("An error occurred while creating link [" +  transition + "]", e);
			throw e;
		} catch (UriBuilderException e) {
			logger.error("An error occurred while creating link [" + transition + "]", e);
			throw e;
		}
	}

}
