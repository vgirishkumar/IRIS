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

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.cache.Cache;
import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.expression.Expression;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.MetaDataResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.rim.*;
import com.temenos.interaction.core.web.RequestContext;
import com.temenos.interaction.core.workflow.AbortOnErrorWorkflowStrategyCommand;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A state machine that is responsible for creating the links (hypermedia) to
 * other valid application states.
 * 
 * @author aphethean
 * 
 */
public class ResourceStateMachine {
	private final Logger logger = LoggerFactory.getLogger(ResourceStateMachine.class);

	// members
	ResourceState initial;
	ResourceState exception;
	Transformer transformer;
	CommandController commandController;
	Cache responseCache;
	ResourceStateProvider resourceStateProvider;
	ResourceLocatorProvider resourceLocatorProvider;
	ResourceParameterResolverProvider parameterResolverProvider;

	private List<ResourceState> allStates = new ArrayList<ResourceState>();
	private ResourceStateMachineOptimizationMappings optimizationMappings;

	public ResourceStateMachine(ResourceState initialState) {
		this(initialState, null, null, null);
	}

	public ResourceStateMachine(ResourceState initialState, ResourceLocatorProvider resourceLocatorProvider) {
		this(initialState, null, null, resourceLocatorProvider, null);
	}

	public ResourceStateMachine(ResourceState initialState, ResourceState exceptionState) {
		this(initialState, exceptionState, null, null);
	}

	public ResourceStateMachine(ResourceState initialState, ResourceState exceptionState,
			ResourceLocatorProvider resourceLocatorProvider) {
		this(initialState, exceptionState, null, resourceLocatorProvider, null);
	}

	public CommandController getCommandController() {
		return commandController;
	}

	public void setCommandController(CommandController commandController) {
		this.commandController = commandController;
	}

	public Cache getCache() {
		return responseCache;
	}

	public void setCache(Cache cache) {
		responseCache = cache;
	}

	// TODO support Event
	public InteractionCommand determineAction(Event event, String resourcePath) {
		List<Action> actions = new ArrayList<Action>();
		Set<ResourceState> resourceStates = getResourceStatesByPath().get(resourcePath);
		for (ResourceState s : resourceStates) {
			actions.addAll(determineActions(event, s));
		}
		return buildWorkflow(event, actions);
	}

	public List<Action> determineActions(Event event, ResourceState state) {
		List<Action> actions = new ArrayList<Action>();
		Set<String> interactions = getInteractionByState().get(state.getName());
		// TODO turn interactions into Events
		if (interactions.contains(event.getMethod())) {
			for (Action a : state.getActions()) {
				if (event.isSafe() && a.getType().equals(Action.TYPE.VIEW)) {
                    // Add action to list. Since we now support command chains,
                    // with more than one GET command, it is possible
					// to have more than one VIEW in the action list.
					actions.add(a);
				} else if (event.isUnSafe() && a.getType().equals(Action.TYPE.ENTRY)
						&& (a.getMethod() == null || event.getMethod().equals(a.getMethod()))) {
					actions.add(a);
				}
			}
		}
		return actions;
	}

	public InteractionCommand buildWorkflow(Event event, List<Action> actions) {
		if (actions.size() > 0) {
			AbortOnErrorWorkflowStrategyCommand workflow = new AbortOnErrorWorkflowStrategyCommand();
			for (Action action : actions) {
				assert (action != null && event != null);
                // if (action.getMethod() == null ||
                // action.getMethod().equals(event.getMethod())) {
					workflow.addCommand(getCommandController().fetchCommand(action.getName()));
                // }
			}
			return workflow;
		}
		return null;
	}

	public ResourceState determineState(Event event, String resourcePath) {
		ResourceState state = null;
		Set<ResourceState> resourceStates = getResourceStatesByPath().get(resourcePath);
		if (resourceStates != null) {
			for (ResourceState s : resourceStates) {
				Set<String> interactions = getInteractionByState().get(s.getName());
				if (interactions.contains(event.getMethod())) {
					if (state == null || interactions.size() == 1 || !event.getMethod().equals("GET")) { // Avoid
																											// overriding
																											// existing
																											// view
																											// actions
						if (state != null && state.getViewAction() != null) {
							logger.error(String.format("Multiple matching resource states for [%s] event on [%s], [%s] and [%s]",
									event, resourcePath, state, s));
						}
						state = s;
					}
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
		this(initialState, null, transformer, null);
	}

	public ResourceStateMachine(ResourceState initialState, Transformer transformer,
			ResourceStateProvider resourceStateProvider) {
		this(initialState, null, transformer, null, resourceStateProvider);
	}

	/**
	 * 
	 * @invariant initial state not null
	 * @param initialState
	 * @param transformer
	 */
	public ResourceStateMachine(ResourceState initialState, Transformer transformer,
			ResourceLocatorProvider resourceLocatorProvider) {
		this(initialState, null, transformer, resourceLocatorProvider, null);
	}

	public ResourceStateMachine(ResourceState initialState, ResourceState exceptionState, Transformer transformer) {
		this(initialState, exceptionState, transformer, null, null);
	}

	public ResourceStateMachine(ResourceState initialState, ResourceState exceptionState, Transformer transformer,
			ResourceStateProvider resourceStateProvider) {
		this(initialState, exceptionState, transformer, null, resourceStateProvider);
	}

	public ResourceStateMachine(ResourceState initialState, ResourceState exceptionState, Transformer transformer,
			ResourceLocatorProvider resourceLocatorProvider, ResourceStateProvider resourceStateProvider) {
		if (initialState == null)
			throw new RuntimeException("Initial state must be supplied");
		logger.info(String.format("Constructing ResourceStateMachine with initial state [%s]", initialState));
		assert (exceptionState == null || exceptionState.isException());
		this.initial = initialState;
		this.initial.setInitial(true);
		this.exception = exceptionState;
		this.transformer = transformer;
		this.resourceLocatorProvider = resourceLocatorProvider;
		this.resourceStateProvider = resourceStateProvider;
		optimizationMappings = new ResourceStateMachineOptimizationMappingsImpl();
		build();
	}

	/**
     * This method is called during resource state machine construction and
     * builds the resource state machine's internal state graph starting from
     * the initial state.
	 */
	private synchronized void build() {
		allStates = optimizationMappings.buildAllMappingsAndInitializeLazyResourceAndReturnAllStates(resourceStateProvider, initial);
	}

	/**
     * Registers the given state / method pair, and any states required to
     * process the given state, with the resource state machine's internal state
     * graph
	 *  
     * @param state
     *            The resource state to register
     * @param method
     *            The HTTP method associated with the state, this is important
     *            as the state to handle a request is determined by the duo of
     *            the state's path and HTTP method; path alone is not sufficient
     *            as multiple states can share the same path
	 */
	public synchronized void register(ResourceState state, String method) {
		if (optimizationMappings.updateMapsWithNewState(resourceStateProvider, state, method)) {
			// To prevent circular transitions lists looping forever add state to allStates.
			populateListAllStates(state);
			registerEmbeddedAndForEachAndForEachEmbeddedResources(state);
		}
	}

	private void populateListAllStates(ResourceState currentState) {
		allStates.add(currentState);
	}

	private void registerEmbeddedAndForEachAndForEachEmbeddedResources(ResourceState state) {
		// Process any embedded / foreach resources linked to this resource
		for (Transition tmpTransition : state.getTransitions()) {
			if (!allStates.contains(tmpTransition.getTarget())) {
				if (tmpTransition.isType(Transition.EMBEDDED)) {
					register(tmpTransition.getTarget(), tmpTransition.getCommand().getMethod());
				}

				if (tmpTransition.isType(Transition.FOR_EACH)) {
					register(tmpTransition.getTarget(), tmpTransition.getCommand().getMethod());
				}

				if (tmpTransition.isType(Transition.FOR_EACH_EMBEDDED)) {
					register(tmpTransition.getTarget(), tmpTransition.getCommand().getMethod());
				}
			} else {
				logger.warn(String.format("Multiple registration of transition \"%s\" to \"%s\". Possible circular transition path.",
						tmpTransition.getSource().getEntityName(), tmpTransition.getTarget().getEntityName()));
			}
		}
	}

	/**
     * Unregisters the given state / method pair from the resource state
     * machine's internal state graph
	 *  
     * @param state
     *            The resource state to unregister
     * @param method
     *            The HTTP method associated with the state, this is important
     *            as the state to handle a request is determined by the duo of
     *            the state's path and HTTP method; path alone is not sufficient
     *            as multiple states can share the same path
     */
	public synchronized void unregister(ResourceState state, String method) {
		checkAndResolve(state);
		allStates.remove(state);
		// collectTransitionsById(transitionsById, state);
		// collectTransitionsByRel(transitionsByRel, state);

        // Process interactions by path
		final Set<String> pathInteractions = optimizationMappings.getInteractionsByPath(state.getPath());

        if (pathInteractions != null)
			pathInteractions.remove(method);

        // Process interactions by state
		final Set<String> stateInteractions = optimizationMappings.getInteractionsByStateName(state.getName());

        if (stateInteractions != null)
			stateInteractions.remove(method);

        // Process resource states by path
		final Set<ResourceState> pathStates = optimizationMappings.getResourceStatesByPath(state.getResourcePath());

        if (pathStates != null)
			pathStates.remove(state);

		// Process resource states by name
		optimizationMappings.removeResourceStateByName(state.getName());
	}

	public ResourceState checkAndResolve(ResourceState targetState) {
		if (isResourceStateLazy(targetState)) {
			targetState = resourceStateProvider.getResourceState(targetState.getName());
		}
		if (targetState != null) {
			setResourceStatesToTransitionsTargets(targetState);
			setErrorState(targetState);
		}
		return targetState;
	}

	private void setResourceStatesToTransitionsTargets(ResourceState targetState) {
		for (Transition transition : targetState.getTransitions()) {
			if (isResourceStateLazy(transition.getTarget())) {
				if (transition.getTarget() != null) {
					ResourceState tt = resourceStateProvider.getResourceState(transition.getTarget().getName());
					if (tt == null) {
						logger.error(String.format("Invalid transition [%s]", transition.getId()));
					}
					transition.setTarget(tt);
				}
			}
		}
	}

	private void setErrorState(ResourceState targetState) {
		// Target can have errorState which is not a normal transition, so
		// resolve and add it here
		if (targetState.getErrorState() != null) {
			ResourceState errorState = targetState.getErrorState();
			if (isResourceStateLazy(errorState) && errorState.getId().startsWith(".")) {
				// We should resolve and overwrite the one already there
				errorState = resourceStateProvider.getResourceState(errorState.getName());
				targetState.setErrorState(errorState);
			}
		}
	}

	private boolean isResourceStateLazy(ResourceState targetState) {
		return targetState instanceof LazyResourceState || targetState instanceof LazyCollectionResourceState;
	}

	public void setParameterResolverProvider(ResourceParameterResolverProvider parameterResolverProvider) {
		this.parameterResolverProvider = parameterResolverProvider;
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

    public synchronized Collection<ResourceState> getStates() {
		return Collections.unmodifiableCollection(allStates);
	}

	private boolean isEntryInsideCollectionCheckingByReference(Collection<ResourceState> result, ResourceState currentState) {
		for (ResourceState tmpState : result) {
			if (tmpState == currentState) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a map of all the paths, and interactions with those states mapped
	 * to that path
	 * 
	 * @return
	 */
	public Map<String, Set<String>> getInteractionByPath() {
		return optimizationMappings.getInteractionByPath();
	}

	/**
	 * Return a map of all the ResourceState's, and interactions with those
	 * states.
	 * 
	 * @return
	 */
	public Map<String, Set<String>> getInteractionByState() {
		return optimizationMappings.getInteractionsByStateName();
	}

	private boolean isStringInsideCollectionCheckingByReference(Collection<String> states, ResourceState currentState) {
		for (String tmpState : states) {
			if (tmpState == currentState.getName())
				return true;
		}
		return false;
	}

	/**
	 * For a given resource state, get the valid interactions.
	 * 
	 * @param state
	 * @return
	 */
	public Set<String> getInteractions(ResourceState state) {
		Set<String> interactions = null;
		if (state != null) {
			assert (getStates().contains(state));
			Map<String, Set<String>> interactionMap = getInteractionByPath();
			interactions = interactionMap.get(state.getPath());
		}
		return interactions;
	}

	/**
	 * For a given path, return the resource states.
	 * 
	 * @param path
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
	 * 
	 * @param pathRegex
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
		Set<String> paths = getResourceStatesByPath().keySet();
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
	 * 
	 * @invariant initial state not null
	 * @return
	 */
	public Map<String, Set<ResourceState>> getResourceStatesByPath() {
		return optimizationMappings.getResourceStatesByPath();
	}

	/**
	 * Return a map of all the paths to the sub states from the supplied
	 * ResourceState.
	 * 
	 * @precondition begin state not null
	 * @invariant initial state not null
	 */
	public Map<String, Set<ResourceState>> getResourceStatesByPath(ResourceState begin) {
		assert (begin != null);
		return optimizationMappings.getResourceStatesByPath(begin);
	}

	/**
	 * For a given state name, return the resource state.
	 * 
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
	 * 
	 * @invariant initial state not null
	 * @return
	 */
	public Map<String, ResourceState> getResourceStateByName() {
		return optimizationMappings.getResourceStateByName();
	}

	/**
	 * Return a map of all the state names to the sub states from the supplied
	 * ResourceState.
	 * 
	 * @precondition begin state not null
	 * @invariant initial state not null
	 */
	public Map<String, ResourceState> getResourceStateByName(ResourceState begin) {
		assert (begin != null);
		Map<String, ResourceState> stateMap = new HashMap<String, ResourceState>();
		collectResourceStatesByName(stateMap, begin);
		return stateMap;
	}

	private void collectResourceStatesByName(Map<String, ResourceState> result, ResourceState begin) {
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectResourceStatesByName(result, states, begin);
	}

	private void collectResourceStatesByName(Map<String, ResourceState> result, Collection<ResourceState> states,
			ResourceState currentState) {
		if (currentState == null)
			return;

		if (isEntryInsideCollectionCheckingByReference(states, currentState)) return;

		states.add(currentState);
		// add current state to results
		result.put(currentState.getName(), currentState);
		for (ResourceState next : currentState.getAllTargets()) {
			if (next != null && next != currentState) {
				String name = next.getName();
				logger.debug(String.format("Putting a ResourceState[%s]: %s", name, next));
				result.put(name, next);
			}
			collectResourceStatesByName(result, states, next);
		}
	}

	/**
	 * Evaluate and return all the valid links (target states) from this
	 * resource state.
	 * 
	 * @param rimHandler
	 * @param ctx
	 * @param resourceEntity
	 * @return
	 */
	public Collection<Link> injectLinks(HTTPHypermediaRIM rimHandler, InteractionContext ctx,
			RESTResource resourceEntity, HttpHeaders headers, Metadata metadata) {
		return injectLinks(rimHandler, ctx, resourceEntity, null, headers, metadata);
	}

	/**
	 * Evaluate and return all the valid links (target states) from the current
	 * resource state (@see {@link InteractionContext#getCurrentState()}).
	 * 
	 * @param rimHander
	 * @param ctx
	 * @param resourceEntity
	 * @param selfTransition
	 *            if we are injecting links into a resource that has resulted
	 *            from a transition from another resource (e.g an auto
	 *            transition or an embedded transition) then we need to use the
	 *            transition parameters as there are no path parameters
	 *            available. i.e. because we've not made a request for this
	 *            resource through the whole jax-rs stack
	 * @return
	 */
	public Collection<Link> injectLinks(HTTPHypermediaRIM rimHander, InteractionContext ctx,
			RESTResource resourceEntity, Transition selfTransition, HttpHeaders headers, Metadata metadata) {
		// Add path and query parameters to the list of resource properties
		MultivaluedMap<String, String> resourceProperties = new MultivaluedMapImpl<String>();
		resourceProperties.putAll(ctx.getPathParameters());
		resourceProperties.putAll(ctx.getQueryParameters());

		ResourceState state = ctx.getCurrentState();
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
			logger.warn("Injecting links into a collection, only support simple, non template, links as there are no properties on the collection at the moment");
		} else if (resourceEntity instanceof MetaDataResource) {
			// TODO deprecate all resource types apart from item
			// (EntityResource) and collection (CollectionResource)
			logger.debug("Returning from the call to getLinks for a MetaDataResource without doing anything");
			return links;
		} else {
			throw new RuntimeException("Unable to get links, an error occurred");
		}

		// add link to GET 'self'
		if (selfTransition == null)
			selfTransition = state.getSelfTransition();
		links.add(createSelfLink(selfTransition, entity, resourceProperties));

		/*
		 * Add links to other application states (resources)
		 */
		List<Transition> transitions = state.getTransitions();
		for (Transition transition : transitions) {
            if (transition.getTarget() == null) {
				logger.warn("Skipping invalid transition: " + transition);
				continue;
			}

			TransitionCommandSpec cs = transition.getCommand();

            if (cs.isAutoTransition()) {
                // Au revoir - Auto transitions should not be seen by user
                // agents
				continue;
			}

			/*
			 * build link and add to list of links
			 */
			if (cs.isForEach() || cs.isEmbeddedForEach()) {
				if (collectionResource != null) {
					for (EntityResource<?> er : collectionResource.getEntities()) {
						Collection<Link> eLinks = er.getLinks();
						if (eLinks == null) {
							eLinks = new ArrayList<Link>();
						}
						Link link = createLink(transition, er.getEntity(), resourceProperties);
						if (link != null) {
							boolean addLink = true;
							// evaluate the conditional expression
							Expression conditionalExp = transition.getCommand().getEvaluation();
							if (conditionalExp != null) {
								try{
									addLink = conditionalExp.evaluate(rimHander, ctx, er.clone());
								}catch(CloneNotSupportedException cnse){ //not thrown, but added to support clone design contract
									throw new RuntimeException("Failed to clone EntityResource", cnse);
								}
							}
							if (addLink) {
								eLinks.add(link);
							}
						}
						er.setLinks(eLinks);

                        if (cs.isEmbeddedForEach()) {
                            // Embedded resource
				            MultivaluedMap<String, String> newPathParameters = new MultivaluedMapImpl<String>();
				            newPathParameters.putAll(ctx.getPathParameters());

                            EntityMetadata entityMetadata = metadata.getEntityMetadata(collectionResource
                                    .getEntityName());
				            List<String> ids = new ArrayList<String>();

				            Object tmpObj = er.getEntity();

                            if (tmpObj instanceof Entity) {
                                EntityProperty prop = ((Entity) tmpObj).getProperties().getProperty(ids.get(0));
				                ids.add(prop.getValue().toString());
                            } else if (tmpObj instanceof OEntity) {
                                OEntityKey entityKey = ((OEntity) tmpObj).getEntityKey();
				                ids.add(entityKey.toKeyStringWithoutParentheses().replaceAll("'", ""));
                            } else {
                                try {
    				                String fieldName = entityMetadata.getIdFields().get(0);
                                    String methodName = "get" + fieldName.substring(0, 1).toUpperCase()
                                            + fieldName.substring(1);
                                    Method method = tmpObj.getClass().getMethod(methodName);
                                    ids.add(method.invoke(tmpObj).toString());
                                } catch (Exception e) {
                                    logger.warn(
                                            "Failed to add record id while trying to embed current collection resource",
                                            e);
                                }
				            }

                            newPathParameters.put("id", ids);

                            InteractionContext tmpCtx = new InteractionContext(ctx, headers, newPathParameters,
                                    ctx.getQueryParameters(), transition.getTarget());
                            embedResources(rimHander, headers, tmpCtx, er);
						}
					}
				}
			} else {
				boolean addLink = true;
				// evaluate the conditional expression
				Expression conditionalExp = cs.getEvaluation();
				if (conditionalExp != null) {
					EntityResource<?> entityResource = null;

                    if (ctx.getResource() instanceof EntityResource<?>) {
						try{
							entityResource = ((EntityResource<?>) ctx.getResource()).clone();
						}catch(CloneNotSupportedException cnse){ //not thrown, but added to support clone design contract
							throw new RuntimeException("Failed to clone EntityResource", cnse);
						}
					}
					
					addLink = conditionalExp.evaluate(rimHander, ctx, entityResource);
				}

				if (addLink) {
                    Map<String, Object> transitionProperties = getTransitionProperties(transition, entity,
                            resourceProperties, null);

					links.add(createLink(transition, transitionProperties, entity, null, false, ctx));
				}
			}
		}
		resourceEntity.setLinks(links);
		return links;
	}

	/**
	 * Execute and return all the valid embedded links (target states) from the
	 * supplied resource. Should be identical to
	 * {@link InteractionContext#getResource()}.
	 * 
	 * @param rimHandler
	 * @param headers
	 * @param ctx
	 * @param resource
	 * @return
	 */
	public Map<Transition, RESTResource> embedResources(HTTPHypermediaRIM rimHandler, HttpHeaders headers,
			InteractionContext ctx, RESTResource resource) {
		ResourceRequestHandler resourceRequestHandler = rimHandler.getResourceRequestHandler();
		assert (resourceRequestHandler != null);
		try {
			ResourceRequestConfig.Builder configBuilder = new ResourceRequestConfig.Builder();
			Collection<Link> links = resource.getLinks();
			if (links != null) {
				for (Link link : links) {
					if (link == null) {
						logger.warn("embedResources : null Link detected.");
					} else {
						Transition t = link.getTransition();
						/*
						 * when embedding resources we don't want to embed
						 * ourselves we only want to embed the 'EMBEDDED'
						 * transitions
						 */
						if (t.getSource() != t.getTarget()) {
                            if ((t.getCommand().getFlags() & Transition.EMBEDDED) == Transition.EMBEDDED) {
							    configBuilder.transition(t);
							}

                            if ((t.getCommand().getFlags() & Transition.FOR_EACH_EMBEDDED) == Transition.FOR_EACH_EMBEDDED) {
                                configBuilder.transition(t);
						}
					}
				}
			}
            }

			ResourceRequestConfig config = configBuilder.build();

			Map<Transition, ResourceRequestResult> results = null;

            if (resource instanceof EntityResource<?>
                    && resourceRequestHandler instanceof SequentialResourceRequestHandler) {
                /*
                 * Handle cases where we may be embedding a resource that has
                 * filter criteria whose values are contained in the current
                 * resource's entity properties.
				 */
                Object tmpEntity = ((EntityResource) resource).getEntity();

                results = ((SequentialResourceRequestHandler) resourceRequestHandler).getResources(rimHandler, headers,
                        ctx, null, tmpEntity, config);

			} else {
				results = resourceRequestHandler.getResources(rimHandler, headers, ctx, null, config);
			}

			if (config.getTransitions() != null && config.getTransitions().size() > 0
					&& config.getTransitions().size() != results.keySet().size()) {
				throw new InteractionException(Status.INTERNAL_SERVER_ERROR, String.format("Resource state [%s] did not return correct number of embedded resources.",
						ctx.getCurrentState().getId()));
			}
			// don't replace any embedded resources added within the commands
			Map<Transition, RESTResource> resourceResults = null;
			if (ctx.getResource() != null && ctx.getResource().getEmbedded() != null) {
				resourceResults = ctx.getResource().getEmbedded();
			} else {
				resourceResults = new HashMap<Transition, RESTResource>();
			}
			for (Transition transition : results.keySet()) {
				ResourceRequestResult result = results.get(transition);
				if (Family.SUCCESSFUL.equals(Status.fromStatusCode(result.getStatus()).getFamily())) {
					resourceResults.put(transition, result.getResource());
				} else {
					logger.error(String.format("Failed to embed resource for transition [%s]", transition.getId()));
				}
			}
			resource.setEmbedded(resourceResults);
			return resourceResults;
		} catch (InteractionException ie) {
			logger.error(String.format("Failed to embed resources [%s] with error [%s - %s]: %s",
					ctx.getCurrentState().getId(), ie.getHttpStatus(), ie.getHttpStatus().getReasonPhrase(), ie));
			throw new RuntimeException(ie);
		}
	}

	/**
	 * Find the transition that was used by evaluating the LinkHeader and create
	 * a a Link for that transition.
	 * 
	 * @param pathParameters
	 * @param resourceEntity
	 * @param linkHeader
	 * @return
	 */
	public Link getLinkFromRelations(MultivaluedMap<String, String> pathParameters, RESTResource resourceEntity,
			LinkHeader linkHeader) {
		Link target = null;
		// Was a custom link relation supplied, informing us which link was
		// used?
		if (linkHeader != null) {
			Set<String> relationships = linkHeader.getLinksByRelationship().keySet();
			for (String related : relationships) {
				Transition transition = getTransitionsById().get(related);
				if (transition != null) {
					target = createLink(transition, resourceEntity, pathParameters);
				}
			}
		}
		return target;
	}

	public Map<String, Transition> getTransitionsById() {
		return optimizationMappings.getTransitionsById();
	}

	public Map<String, Transition> getTransitionsByRel() {
		return optimizationMappings.getTransitionsByRel();
	}

	/**
	 * Find the transition that was used by assuming the HTTP method was applied
	 * to this state; create a a Link for that transition.
	 * 
	 * @param pathParameters
	 * @param resourceEntity
	 * @param currentState
	 * @param method
	 * @return
	 * @invariant method != null
	 */
	public Link getLinkFromMethod(MultivaluedMap<String, String> pathParameters, RESTResource resourceEntity,
			ResourceState currentState, String method) {
		assert (method != null);
		Link target = null;
		for (ResourceState nextState : currentState.getAllTargets()) {
			Transition transition = currentState.getTransition(nextState);
			if (method.contains(transition.getCommand().getMethod())) {
				// do not create link if this a pseudo state, effectively no
				// state
				if (!transition.getTarget().isPseudoState())
					target = createLink(transition, resourceEntity, pathParameters);
			}
		}
		return target;
	}

	/*
	 * @precondition {@link RequestContext} must have been initialised
	 */
	private Link createSelfLink(Transition transition, Object entity, MultivaluedMap<String, String> pathParameters) {
		return createLink(transition, entity, pathParameters, null, false);
	}

	/*
	 * Create a Link using the supplied transition, entity and path parameters
	 * 
	 * @param resourcePath uri template resource path
	 * 
	 * @param transition transition
	 * 
	 * @param entity entity
	 * 
	 * @param map path parameters
	 * 
	 * @return link
	 */
	public Link createLink(Transition transition, Object entity, MultivaluedMap<String, String> transitionParameters) {
		return createLink(transition, entity, transitionParameters, null);
	}

	public Link createLink(Transition transition, Object entity, MultivaluedMap<String, String> transitionParameters,
			MultivaluedMap<String, String> queryParameters) {
		return createLink(transition, entity, transitionParameters, queryParameters, false);
	}

	public Link createLink(Transition transition, Object entity, MultivaluedMap<String, String> transitionParameters,
			MultivaluedMap<String, String> queryParameters, boolean allQueryParameters) {
		Map<String, Object> transitionProperties = getTransitionProperties(transition, entity, transitionParameters,
				queryParameters);
		return createLink(transition, transitionProperties, entity, queryParameters, allQueryParameters, null);
	}

    public ResourceStateAndParameters resolveDynamicState(DynamicResourceState dynamicResourceState,
            Map<String, Object> transitionProperties, InteractionContext ctx) {
		Object[] aliases = getResourceAliases(transitionProperties, dynamicResourceState, ctx).toArray();

		// Use resource locator to resolve dynamic target
		String locatorName = dynamicResourceState.getResourceLocatorName();

		ResourceLocator locator = resourceLocatorProvider.get(locatorName);

		ResourceStateAndParameters result = new ResourceStateAndParameters();

		ResourceState tmpState = locator.resolve(aliases);

        if (tmpState == null) {
			// A dead link, target could not be found
			logger.error(String.format("Dead link - Failed to resolve resource using %s resource locator", dynamicResourceState.getResourceLocatorName()));
		} else {
			boolean registrationRequired = false;

            for (Transition transition : tmpState.getTransitions()) {
				ResourceState target = transition.getTarget();

				if (isResourceStateLazy(target)) {
					registrationRequired = true;
				}
			}

            if (registrationRequired) {
				register(tmpState, HttpMethod.GET);
			}

            result.setState(tmpState);

            if (parameterResolverProvider != null) {
				try {
					// Add query parameters
					ResourceParameterResolver parameterResolver = parameterResolverProvider.get(locatorName);
					ParameterAndValue[] paramsAndValues = parameterResolver.resolve(aliases);
					result.setParams(paramsAndValues);
				} catch (IllegalArgumentException e) {
					// noop - No parameter resolver configured for this resource
				}
			}
		}

		return result;

	}

	/*
	 * Create a link using the supplied transition, entity and transition
	 * properties. This method is intended for re-using transition properties
	 * (path params, link params and entity properties).
	 * 
	 * @param linkTemplate uri template
	 * 
	 * @param transition transition
	 * 
	 * @param transitionProperties transition properties
	 * 
	 * @param entity entity
	 * 
	 * @return link
	 * 
	 * @precondition {@link RequestContext} must have been initialised
	 */
	private Link createLink(Transition transition, Map<String, Object> transitionProperties, Object entity,
			MultivaluedMap<String, String> queryParameters, boolean allQueryParameters, InteractionContext ctx) {
		assert (RequestContext.getRequestContext() != null);

		try {
			ResourceState targetState = transition.getTarget();

			if (isResourceStateLazy(targetState)) {
				targetState = resourceStateProvider.getResourceState(targetState.getName());
			}

			if (targetState != null) {
				for (Transition tmpTransition : targetState.getTransitions()) {
                    if (tmpTransition.isType(Transition.EMBEDDED)) {
						if (isResourceStateLazy(tmpTransition.getTarget())) {
							if (tmpTransition.getTarget() != null) {
                                ResourceState tt = resourceStateProvider.getResourceState(tmpTransition.getTarget()
                                        .getName());
								if (tt == null) {
									logger.error(String.format("Invalid transition [%s]", tmpTransition.getId()));
								}
								tmpTransition.setTarget(tt);
							}
				}
                    }
                }

                // Target can have errorState which is not a normal transition,
                // so resolve and add it here
				if (targetState.getErrorState() != null) {
					ResourceState errorState = targetState.getErrorState();
					if ((isResourceStateLazy(errorState))
							&& errorState.getId().startsWith(".")) {
                        // We should resolve and overwrite the one already there
						errorState = resourceStateProvider.getResourceState(errorState.getName());
						targetState.setErrorState(errorState);
					}
				}
			}

			if (targetState == null) {
				// a dead link, target could not be found
				logger.error(String.format("Dead link to [%s]", transition.getId()));

				return null;
			}

			UriBuilder linkTemplate = UriBuilder.fromUri(RequestContext.getRequestContext().getBasePath());

			// Add any query parameters set by the command to the response
            if (ctx != null) {
                Map<String, String> outQueryParams = ctx.getOutQueryParameters();

                for (Map.Entry<String, String> param : outQueryParams.entrySet()) {
                    linkTemplate.queryParam(param.getKey(), param.getValue());
				}
			}

            TransitionCommandSpec cs = transition.getCommand();
			String method = cs.getMethod();

			URI href;
			String rel = "";

			if (targetState instanceof DynamicResourceState) {
				// We are dealing with a dynamic target

				// Identify real target state
                ResourceStateAndParameters stateAndParams = resolveDynamicState((DynamicResourceState) targetState,
                        transitionProperties, ctx);

                if (stateAndParams.getState() == null) {
					// Bail out as we failed to resolve resource
					return null;
				} else {
					targetState = stateAndParams.getState();
				}

				if (targetState.getRel().contains("http://temenostech.temenos.com/rels/new")) {
					method = "POST";
                }

				rel = configureLink(linkTemplate, transition, transitionProperties, targetState);

                if (stateAndParams.getParams() != null) {
                    // Add query parameters
					for (ParameterAndValue paramAndValue : stateAndParams.getParams()) {
                        linkTemplate.queryParam(paramAndValue.getParameter(), paramAndValue.getValue());
					}
				}

                href = linkTemplate.buildFromMap(transitionProperties);
			} else {
				// We are NOT dealing with a dynamic target

				rel = configureLink(linkTemplate, transition, transitionProperties, targetState);

				// Pass any query parameters
                addQueryParams(queryParameters, allQueryParameters, linkTemplate, targetState.getPath(), transition
                        .getCommand().getUriParameters());

				// Build href from template
				if (entity != null && transformer == null) {
					logger.debug(String.format("Building link with entity (No Transformer) [%s] [%s]", entity, transition));
					href = linkTemplate.build(entity);
                } else {
					href = linkTemplate.buildFromMap(transitionProperties);
			}
            }

			// Create the link
			Link link = new Link(transition, rel, href.toASCIIString(), method);
			logger.debug(String.format("Created link for transition [%s] [title=%s, rel=%s, method=%s, href=%s(ASCII=%s)]",
					transition, transition.getId(), rel, method, href.toString(), href.toASCIIString()));
			return link;
		} catch (IllegalArgumentException e) {
			logger.warn(String.format("Dead link [%s]", transition), e);

			return null;

		} catch (UriBuilderException e) {
			logger.warn(String.format("Dead link [%s]", transition), e);
			throw e;
		}
	}

    private String configureLink(UriBuilder linkTemplate, Transition transition,
            Map<String, Object> transitionProperties, ResourceState targetState) {
		String targetResourcePath = targetState.getPath();
		linkTemplate.path(targetResourcePath);

		String rel = targetState.getRel();

		if (transition.getSource() == targetState) {
			rel = "self";
        }

		// Pass uri parameters as query parameters if they are not
		// replaceable in the path, and replace any token.

		Map<String, String> uriParameters = transition.getCommand().getUriParameters();
		if (uriParameters != null) {
			for (String key : uriParameters.keySet()) {
				String value = uriParameters.get(key);
				if (!targetResourcePath.contains("{" + key + "}")) {
					linkTemplate.queryParam(key, HypermediaTemplateHelper.templateReplace(value, transitionProperties));
				}
			}
		}

		return rel;
	}

    private void addQueryParams(MultivaluedMap<String, String> queryParameters, boolean allQueryParameters,
				UriBuilder linkTemplate, String targetResourcePath, Map<String, String> uriParameters) {
		if (queryParameters != null && allQueryParameters) {
			for (String param : queryParameters.keySet()) {
                if (!targetResourcePath.contains("{" + param + "}")
                        && (uriParameters == null || !uriParameters.containsKey(param))) {
					linkTemplate.queryParam(param, queryParameters.getFirst(param));
				}
			}
		}
	}

	/**
	 * @param transitionProperties
	 * @param dynamicResourceState
     * @param ctx
	 * @return
	 */
	private List<Object> getResourceAliases(Map<String, Object> transitionProperties,
			DynamicResourceState dynamicResourceState, InteractionContext ctx) {
		List<Object> aliases = new ArrayList<Object>();

		final Pattern pattern = Pattern.compile("\\{*([a-zA-Z0-9]+)\\}*");

		for (String resourceLocatorArg : dynamicResourceState.getResourceLocatorArgs()) {
			Matcher matcher = pattern.matcher(resourceLocatorArg);
			matcher.find();
			String key = matcher.group(1);

			if (transitionProperties.containsKey(key)) {
				aliases.add(transitionProperties.get(key));
            } else if (ctx != null) {
				Object value = ctx.getAttribute(key);

                if (value != null) {
					aliases.add(value);
				}
			}
		}
		return aliases;
	}

	/**
	 * Obtain transition properties. Transition properties are a list of entity
	 * properties, path parameters, and query parameters.
	 * 
	 * @param transition
	 *            transition
	 * @param entity
	 *            usually an entity of the source state
	 * @param pathParameters
	 *            path parameters
	 * @param pathParameters
	 *            path parameters
	 * @return map of transition properties
	 */
	public Map<String, Object> getTransitionProperties(Transition transition, Object entity,
			MultivaluedMap<String, String> pathParameters, MultivaluedMap<String, String> queryParameters) {
		Map<String, Object> transitionProps = new HashMap<String, Object>();

		// Obtain query parameters
		if (queryParameters != null) {
			for (String key : queryParameters.keySet()) {
				transitionProps.put(key, queryParameters.getFirst(key));
			}
		}

		// Obtain path parameters
		if (pathParameters != null) {
			for (String key : pathParameters.keySet()) {
				transitionProps.put(key, pathParameters.getFirst(key));
			}
		}

		// Obtain entity properties
		Map<String, Object> entityProperties = null;
		if (entity != null && transformer != null) {
			logger.debug(String.format("Using transformer [%s] to build properties for link [%s]", transformer, transition));
			entityProperties = transformer.transform(entity);
			if (entityProperties != null) {
				transitionProps.putAll(entityProperties);
			}
		}

		// Obtain linkage properties
		Map<String, String> linkParameters = transition.getCommand().getUriParameters();
		if (linkParameters != null) {
			for (String key : linkParameters.keySet()) {
				String value = linkParameters.get(key);
				value = HypermediaTemplateHelper.templateReplace(value, transitionProps);
				transitionProps.put(key, value);
			}
		}

		return transitionProps;
	}

	public InteractionCommand determinAction(String event, String path) {
		return null;
	}

	public ResourceStateProvider getResourceStateProvider() {
		return resourceStateProvider;
	}

	public void setResourceStateProvider(ResourceStateProvider resourceStateProvider) {
		this.resourceStateProvider = resourceStateProvider;
	}

	// Generated builder pattern from here
	public static class Builder {
		private ResourceState initial;
		private ResourceState exception;
		private Transformer transformer;
		private CommandController commandController;
		private ResourceStateProvider resourceStateProvider;
		private ResourceLocatorProvider resourceLocatorProvider;
		private ResourceParameterResolverProvider parameterResolverProvider;
		private Cache responseCache;

		public Builder initial(ResourceState initial) {
			this.initial = initial;
			return this;
		}

		public Builder exception(ResourceState exception) {
			this.exception = exception;
			return this;
		}

		public Builder transformer(Transformer transformer) {
			this.transformer = transformer;
			return this;
		}

		public Builder commandController(CommandController commandController) {
			this.commandController = commandController;
			return this;
		}

		public Builder resourceStateProvider(ResourceStateProvider resourceStateProvider) {
			this.resourceStateProvider = resourceStateProvider;
			return this;
		}

		public Builder resourceLocatorProvider(ResourceLocatorProvider resourceLocatorProvider) {
			this.resourceLocatorProvider = resourceLocatorProvider;
			return this;
		}

		public Builder parameterResolverProvider(ResourceParameterResolverProvider parameterResolverProvider) {
			this.parameterResolverProvider = parameterResolverProvider;
			return this;
		}

		public Builder responseCache(Cache cache) {
			this.responseCache = cache;
			return this;
		}

		public ResourceStateMachine build() {
			return new ResourceStateMachine(this);
		}
	}

	private ResourceStateMachine(Builder builder) {
		this.initial = builder.initial;
		this.exception = builder.exception;
		this.transformer = builder.transformer;
		this.commandController = builder.commandController;
		this.resourceStateProvider = builder.resourceStateProvider;
		this.resourceLocatorProvider = builder.resourceLocatorProvider;
		this.parameterResolverProvider = builder.parameterResolverProvider;
		this.responseCache = builder.responseCache;
		this.optimizationMappings = new ResourceStateMachineOptimizationMappingsImpl();
		build();
	}
}
