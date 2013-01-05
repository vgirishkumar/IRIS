package com.temenos.interaction.core.hypermedia;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.resource.RESTResource;
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
	public final ResourceState exception;
	public final Transformer transformer;
	public NewCommandController commandController;
	
	// optimised access
	private Map<String,Transition> transitionsById = new HashMap<String,Transition>();
	private List<ResourceState> allStates = new ArrayList<ResourceState>();
	private Map<String, Set<String>> interactionsByPath = new HashMap<String, Set<String>>();
	private Map<ResourceState, Set<String>> interactionsByState = new HashMap<ResourceState, Set<String>>();
	private Map<String, Set<ResourceState>> resourceStatesByPath = new HashMap<String, Set<ResourceState>>();
	private Map<String, ResourceState> resourceStatesByName = new HashMap<String, ResourceState>();
	
	public ResourceStateMachine(ResourceState initialState) {
		this(initialState, null);
	}

	public NewCommandController getCommandController() {
		return commandController;
	}

	public void setCommandController(NewCommandController commandController) {
		this.commandController = commandController;
	}

	// TODO support Event
	public InteractionCommand determineAction(Event event, String resourcePath) {
		Action action = null;
		Set<ResourceState> resourceStates = getResourceStatesByPath().get(resourcePath);
		for (ResourceState s : resourceStates) {
			Set<String> interactions = getInteractionByState().get(s);
			// TODO turn interactions into Events
			if (interactions.contains(event.getMethod())) {
				for (Action a : s.getActions()) {
					if (event.isSafe() && a.getType().equals(Action.TYPE.VIEW) && 
							(action == null || s.getActions().size() == 1)) {		//Avoid overriding existing view actions 
						action = a;
					} else if (event.isUnSafe() && a.getType().equals(Action.TYPE.ENTRY)) {
						action = a;
					}
				}
			}
		}
		
		return (action != null ? getCommandController().fetchCommand(action.getName()) : null);
	}
	
	public ResourceState determineState(Event event, String resourcePath) {
		ResourceState state = null;
		Set<ResourceState> resourceStates = getResourceStatesByPath().get(resourcePath);
		for (ResourceState s : resourceStates) {
			Set<String> interactions = getInteractionByState().get(s);
			if (interactions.contains(event.getMethod())) {
				if(state == null || interactions.size() == 1 || !event.getMethod().equals("GET")) {		//Avoid overriding existing view actions
					state = s;
				}
			}
		}
		
		return state;
	}

	/**
	 * 
	 * @invariant initial state not null
	 * @param initialState
	 * @param transformer
	 */
	public ResourceStateMachine(ResourceState initialState, Transformer transformer) {
		this(initialState, null, transformer);
	}
	
	
	public ResourceStateMachine(ResourceState initialState, ResourceState exception, Transformer transformer) {
		assert(initialState != null);
		assert(exception == null || exception.isException());
		this.initial = initialState;
		this.initial.setInitial(true);
		this.exception = exception;
		this.transformer = transformer;
		build();
	}

	private void build() {
		collectStates(allStates, initial);
		collectTransitionsById(transitionsById);
		collectInteractionsByPath(interactionsByPath);
		collectInteractionsByState(interactionsByState);
		collectResourceStatesByPath(resourceStatesByPath);
		collectResourceStatesByName(resourceStatesByName);
	}
	
	public ResourceState getInitial() {
		return initial;
	}

	public ResourceState getException() {
		return exception;
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
	 * Return a map of all the paths, and interactions with those states
	 * mapped to that path
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
		// every state must have a 'GET' interaction
		Set<String> interactions = result.get(currentState.getPath());
		if (interactions == null)
			interactions = new HashSet<String>();
		interactions.add(HttpMethod.GET);
		result.put(currentState.getPath(), interactions);
		// add interactions by iterating through the transitions from this state
		for (ResourceState next : currentState.getAllTargets()) {
			// is the target a state of the same entity
//			if (next.getEntityName().equals(currentState.getEntityName())) {
				// lookup transition to get to here
				Transition t = currentState.getTransition(next);
				TransitionCommandSpec command = t.getCommand();
				String path = command.getPath();
				
				interactions = result.get(path);
				if (interactions == null)
					interactions = new HashSet<String>();
				if (command.getMethod() != null && !command.isAutoTransition())
					interactions.add(command.getMethod());
				
				result.put(path, interactions);
				collectInteractionsByPath(result, states, next);
//			}
		}
		
	}

	/**
	 * Return a map of all the ResourceState's, and interactions with those states.
	 * @return
	 */
	public Map<ResourceState, Set<String>> getInteractionByState() {
		return interactionsByState;
	}
	
	private void collectInteractionsByState(Map<ResourceState, Set<String>> result) {
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectInteractionsByState(result, states, initial);
	}
	
	private void collectInteractionsByState(Map<ResourceState, Set<String>> result, Collection<ResourceState> states, ResourceState currentState) {
		if (currentState == null || states.contains(currentState)) return;
		states.add(currentState);
		// every state must have a 'GET' interaction
		Set<String> interactions = result.get(currentState);
		if (interactions == null)
			interactions = new HashSet<String>();
		if (!currentState.isPseudoState()) {
			interactions.add(HttpMethod.GET);
		}
		result.put(currentState, interactions);
		// add interactions by iterating through the transitions from this state
		for (ResourceState next : currentState.getAllTargets()) {
			// is the target a state of the same entity
//			if (next.getEntityName().equals(currentState.getEntityName())) {
				// lookup transition to get to here
				Transition t = currentState.getTransition(next);
				TransitionCommandSpec command = t.getCommand();
				
				interactions = result.get(next);
				if (interactions == null)
					interactions = new HashSet<String>();
				if (command.getMethod() != null && !command.isAutoTransition())
					interactions.add(command.getMethod());
				
				result.put(next, interactions);
				collectInteractionsByState(result, states, next);
//			}
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
	 * For a given path, return the resource states.
	 * @param state
	 * @return
	 */
	public Set<ResourceState> getResourceStatesForPath(String path) {
		if (path == null) {
			path = initial.getPath();
		}
		return getResourceStatesByPath().get(path);
	}

	/**
	 * For a given path regular expression, return the resource states.
	 * @param state
	 * @return
	 */
	public Set<ResourceState> getResourceStatesForPathRegex(String pathRegex) {
		if (pathRegex == null) {
			pathRegex = initial.getPath();
		}
		return getResourceStatesForPathRegex(Pattern.compile(pathRegex));
	}

	/**
	 * @see {@link ResourceStateMachine#getResourceStatesForPathRegex(String)}
	 */
	public Set<ResourceState> getResourceStatesForPathRegex(Pattern pattern) {
		Set<ResourceState> matchingStates = new HashSet<ResourceState>();
		Set<String> paths = resourceStatesByPath.keySet();
		for (String path : paths) {
			Matcher m = pattern.matcher(path);
			if (m.matches()) {
				matchingStates.addAll(getResourceStatesForPath(path));
			}
		}
		return matchingStates;
	}

	/**
	 * Return a map of all the paths to the various ResourceState's
	 * @invariant initial state not null
	 * @return
	 */
	public Map<String, Set<ResourceState>> getResourceStatesByPath() {
		return resourceStatesByPath;
	}

	/**
	 * Return a map of all the paths to the sub states from the supplied
	 * ResourceState.
	 * @precondition begin state not null
	 * @invariant initial state not null
	 */
	public Map<String, Set<ResourceState>> getResourceStatesByPath(ResourceState begin) {
		assert(begin != null);
		Map<String, Set<ResourceState>> stateMap = new HashMap<String, Set<ResourceState>>();
		collectResourceStatesByPath(stateMap, begin);
		return stateMap;
	}

	private void collectResourceStatesByPath(Map<String, Set<ResourceState>> result) {
		collectResourceStatesByPath(result, initial);
	}

	private void collectResourceStatesByPath(Map<String, Set<ResourceState>> result, ResourceState begin) {
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectResourceStatesByPath(result, states, begin);
	}

	private void collectResourceStatesByPath(Map<String, Set<ResourceState>> result, Collection<ResourceState> states, ResourceState currentState) {
		if (currentState == null || states.contains(currentState)) return;
		states.add(currentState);
		// add current state to results
		Set<ResourceState> thisStateSet = result.get(currentState.getResourcePath());
		if (thisStateSet == null)
			thisStateSet = new HashSet<ResourceState>();
		thisStateSet.add(currentState);
		result.put(currentState.getResourcePath(), thisStateSet);
		for (ResourceState next : currentState.getAllTargets()) {
//			if (!next.equals(currentState) && !next.isPseudoState()) {
			if (!next.equals(currentState)) {
				String path = next.getResourcePath();
				if (result.get(path) != null) {
					if (!result.get(path).contains(next)) {
						logger.debug("Adding to existing ResourceState[" + path + "] set (" + result.get(path) + "): " + next);
						result.get(path).add(next);
					}
				} else {
					logger.debug("Putting a ResourceState[" + path + "]: " + next);
					Set<ResourceState> set = new HashSet<ResourceState>();
					set.add(next);
					result.put(path, set);
				}
			}
			collectResourceStatesByPath(result, states, next);
		}
	}

	/**
	 * For a given state name, return the resource state.
	 * @param name
	 * @return
	 */
	public ResourceState getResourceStateByName(String name) {
		if (name == null)
			throw new IllegalArgumentException("State name not supplied");
		return getResourceStateByName().get(name);
	}

	/**
	 * Return a map of all the state names to ResourceState
	 * @invariant initial state not null
	 * @return
	 */
	public Map<String, ResourceState> getResourceStateByName() {
		return resourceStatesByName;
	}

	/**
	 * Return a map of all the state names to the sub states from the supplied
	 * ResourceState.
	 * @precondition begin state not null
	 * @invariant initial state not null
	 */
	public Map<String, ResourceState> getResourceStateByName(ResourceState begin) {
		assert(begin != null);
		Map<String, ResourceState> stateMap = new HashMap<String, ResourceState>();
		collectResourceStatesByName(stateMap, begin);
		return stateMap;
	}

	private void collectResourceStatesByName(Map<String, ResourceState> result) {
		collectResourceStatesByName(result, initial);
	}

	private void collectResourceStatesByName(Map<String, ResourceState> result, ResourceState begin) {
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectResourceStatesByName(result, states, begin);
	}

	private void collectResourceStatesByName(Map<String, ResourceState> result, Collection<ResourceState> states, ResourceState currentState) {
		if (currentState == null || states.contains(currentState)) return;
		states.add(currentState);
		// add current state to results
		result.put(currentState.getName(), currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			if (!next.equals(currentState)) {
				String name = next.getName();
				logger.debug("Putting a ResourceState[" + name + "]: " + next);
				result.put(name, next);
			}
			collectResourceStatesByName(result, states, next);
		}
	}

	/**
	 * Evaluate and return all the valid links (target states) from this resource state.
	 * @param pathParameters
	 * @param resourceEntity
	 * @param state
	 * @param linkRelations
	 * @return
	 */
	public Collection<Link> injectLinks(MultivaluedMap<String, String> pathParameters, RESTResource resourceEntity, ResourceState state, List<String> linkRelations) {
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
			List<Transition> transitions = state.getTransitions(s);
			for(Transition transition : transitions) {
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
							eLinks.add(createLink(linkTemplate, transition, er.getEntity(), pathParameters));
							er.setLinks(eLinks);
						}
					}
				} else {
					links.add(createLink(linkTemplate, transition, entity, pathParameters));
				}
			}
		}
		resourceEntity.setLinks(links);
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
			String rel = transition.getTarget().getRel();
			if (transition.getSource().equals(transition.getTarget())) {
				rel = "self"; 
			}
			String method = cs.getMethod();
			URI href = null;
			
			//Apply path parameters to URI template
			Map<String, Object> properties = new HashMap<String, Object>();
			if (map != null) {
				for (String key : map.keySet()) {
					properties.put(key, map.getFirst(key));
				}
			}

			//Apply linkage properties defined in the RIM
			Map<String, String> uriLinkageProperties = transition.getSource().getUriLinkageProperties();
			if (uriLinkageProperties != null) {
				properties.putAll(uriLinkageProperties);
			}
			
			//Apply entity properties to URI template
			if (entity != null) {
				if (transformer != null) {
					logger.debug("Using transformer [" + transformer + "] to build properties for link [" + transition + "]");
					Map<String, Object> props = transformer.transform(entity);
					if (props != null) {
						properties.putAll(props);
					}
/*					if (uriLinkageProperties != null && uriLinkageProperties.size() > 0) {
						//URI link properties may have path parameters which should be resolved before creating the final URI
						linkTemplate = UriBuilder.fromPath(linkTemplate.buildFromMap(properties).toASCIIString().replaceAll("%7B", "{").replaceAll("%7D", "}"));
					}*/
					href = linkTemplate.buildFromMap(properties);
				} else {
					logger.debug("Building link with entity (No Transformer) [" + entity + "] [" + transition + "]");
					href = linkTemplate.build(entity);
				}
			} else {
				href = linkTemplate.buildFromMap(properties);
			}
			
			//Create the link
			Link link = new Link(transition, rel, href.toASCIIString(), method);
			logger.debug("Created link for transition [" + transition + "] [title=" + transition.getId()+ ", rel=" + rel + ", method=" + method + ", href=" + href.toString() + "(ASCII=" + href.toASCIIString() + ")]");
			return link;
		} catch (IllegalArgumentException e) {
			logger.error("An error occurred while creating link [" +  transition + "]", e);
			throw e;
		} catch (UriBuilderException e) {
			logger.error("An error occurred while creating link [" + transition + "]", e);
			throw e;
		}
	}

	public InteractionCommand determinAction(String event, String path) {
		return null;
	}
}
