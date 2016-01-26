package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import java.util.*;

import static com.temenos.interaction.core.hypermedia.ResourceStateMachineOptimizationMappingsImpl.InformationType;

/**
 * @author kwieconkowski
 */

public class ResourceStateMachineOptimizationMappingsImpl implements ResourceStateMachineOptimizationMappings<InformationType> {
    private final Logger logger = LoggerFactory.getLogger(ResourceStateMachineOptimizationMappingsImpl.class);


    public enum InformationType {
        TRANSITIONS_BY_ID,
        TRANSITIONS_BY_REL,
        INTERACTIONS_BY_PATH,
        INTERACTIONS_BY_STATE_NAME,
        RESOURCE_STATES_BY_PATH,
        RESOURCE_STATES_BY_NAME;
    }

    private Map<String, Transition> transitionsById = new HashMap<String, Transition>();
    private Map<String, Transition> transitionsByRel = new HashMap<String, Transition>();
    private Map<String, Set<String>> interactionsByPath = new HashMap<String, Set<String>>();
    private Map<String, Set<String>> interactionsByStateName = new HashMap<String, Set<String>>();
    private Map<String, Set<ResourceState>> resourceStatesByPath = new HashMap<String, Set<ResourceState>>();
    private Map<String, ResourceState> resourceStatesByName = new HashMap<String, ResourceState>();

    private ResourceState initial;
    private ResourceStateProvider resourceStateProvider;

    @Override
    public List<ResourceState> buildAllMappingsAndInitializeLazyResourceAndReturnAllStates(ResourceStateProvider resourceStateProvider, ResourceState initial) {
        this.initial = initial;
        this.resourceStateProvider = resourceStateProvider;

        List<ResourceState> allStates = new ArrayList<ResourceState>();
        checkAndResolve(initial);
        collectAllStatesAndTransitionsByIdAndRelAndResourceStatesByName(allStates, initial);
        collectInteractionsByPathAndState(new ArrayList<ResourceState>(), initial, HttpMethod.GET);
        collectResourceStatesByPath(resourceStatesByPath, new HashSet<ResourceState>(), initial);

        clear();
        return allStates;
    }

    @Override
    public boolean updateMapsWithNewState(ResourceStateProvider resourceStateProvider, ResourceState state, String method) {
        this.resourceStateProvider = resourceStateProvider;
        this.initial = state;

        checkAndResolve(state);

        if (state == null) {
            clear();
            return false;
        }

        populateTransitionsByIdAndRel(state);
        collectInteractionsByPathForState(state, method);
        collectInteractionsByStateForState(state, method);
        collectResourceStatesByPathForState(state);
        populateResourceStatesByName(state);

        clear();
        return true;
    }

    @Override
    public Map<String, Set<ResourceState>> getResourceStatesByPath(ResourceState begin) {
        Map<String, Set<ResourceState>> stateMap = new HashMap<String, Set<ResourceState>>();
        collectResourceStatesByPath(stateMap, new ArrayList<ResourceState>(), begin);
        return stateMap;
    }

    @Override
    public Object getInformationFrom(InformationType target, String key) {
        return getInformationFrom(target).get(key);
    }

    @Override
    public Map getInformationFrom(InformationType target) {
        if (target == null) {
            return null;
        }

        switch (target) {
            case INTERACTIONS_BY_PATH:
                return interactionsByPath;
            case INTERACTIONS_BY_STATE_NAME:
                return interactionsByStateName;
            case RESOURCE_STATES_BY_NAME:
                return resourceStatesByName;
            case RESOURCE_STATES_BY_PATH:
                return resourceStatesByPath;
            case TRANSITIONS_BY_ID:
                return transitionsById;
            case TRANSITIONS_BY_REL:
                return transitionsByRel;
            default:
                return null;
        }
    }

    @Override
    public void removeResourceStateByName(String stateName) {
        resourceStatesByName.remove(stateName);
    }

    private void clear() {
        initial = null;
        resourceStateProvider = null;
    }

    private void collectInteractionsByPathForState(ResourceState state, String method) {
        Set<String> pathInteractions = (Set<String>) interactionsByPath.get(state.getPath());
        if (pathInteractions == null) {
            pathInteractions = new HashSet<String>();
            interactionsByPath.put(state.getPath(), pathInteractions);
        }
        putMethodOrGetMethodToCollection(method, pathInteractions);
    }

    private void collectInteractionsByStateForState(ResourceState state, String method) {
        Set<String> stateInteractions = (Set<String>) interactionsByStateName.get(state.getName());
        if (stateInteractions == null) {
            stateInteractions = new HashSet<String>();
            interactionsByStateName.put(state.getName(), stateInteractions);
        }

        if (!state.isPseudoState()) {
            putMethodOrGetMethodToCollection(method, stateInteractions);
        }
        if (state.getActions() != null) {
            for (Action action : state.getActions()) {
                if (action.getMethod() != null) {
                    stateInteractions.add(action.getMethod());
                }
            }
        }

        for (ResourceState next : state.getAllTargets()) {
            List<Transition> transitions = state.getTransitions(next);
            for (Transition t : transitions) {
                TransitionCommandSpec command = t.getCommand();

                Set<String> tmpStateInteractions = (Set<String>) interactionsByStateName.get(next.getName());

                if (tmpStateInteractions == null) {
                    tmpStateInteractions = new HashSet<String>();
                    interactionsByStateName.put(next.getName(), tmpStateInteractions);
                }

                if (command.getMethod() != null && !command.isAutoTransition())
                    tmpStateInteractions.add(command.getMethod());
            }
        }
    }

    private void collectResourceStatesByPathForState(ResourceState state) {
        Set<ResourceState> pathStates = (Set<ResourceState>) resourceStatesByPath.get(state.getResourcePath());
        if (pathStates == null) {
            pathStates = new HashSet<ResourceState>();
            resourceStatesByPath.put(state.getResourcePath(), pathStates);
        }
        pathStates.add(state);
    }

    private ResourceState checkAndResolve(ResourceState targetState) {
        if (isResourceStateLazy(targetState)) {
            targetState = resourceStateProvider.getResourceState(targetState.getName());
        }
        if (targetState != null) {
            setResourceStatesToTransitionsTargets(targetState);
            setErrorState(targetState);
        }
        return targetState;
    }

    private void collectAllStatesAndTransitionsByIdAndRelAndResourceStatesByName(List<ResourceState> allStates, ResourceState currentState) {
        if (currentState == null) {
            return;
        }
        //currentState = checkAndResolve(currentState);
        if (isEntryInsideCollectionCheckingByReference(allStates, currentState)) return;

        allStates.add(currentState);
        populateTransitionsByIdAndRel(currentState);
        populateResourceStatesByName(currentState);

        for (ResourceState next : currentState.getAllTargets()) {
            next = checkAndResolve(next);
            if (next != null && next != initial) {
                collectAllStatesAndTransitionsByIdAndRelAndResourceStatesByName(allStates, next);
            }
        }
    }

    private void collectInteractionsByPathAndState(Collection<ResourceState> allStates, ResourceState currentState, String method) {

        if (currentState == null) {
            return;
        }

        if (isEntryInsideCollectionCheckingByReference(allStates, currentState)) return;
        allStates.add(currentState);

        populateInteractionsByPath(currentState, method);
        populateInteractionsByState(currentState, method);

        // add interactions by iterating through the transitions from this state
        for (ResourceState next : currentState.getAllTargets()) {
            List<Transition> transitions = currentState.getTransitions(next);
            for (Transition t : transitions) {
                TransitionCommandSpec command = t.getCommand();
                populateInteractionsByPath(t.getTarget().getPath(), command);
                populateInteractionsByState(next.getName(), command);

                collectInteractionsByPathAndState(allStates, next, command.getMethod());
            }
        }
    }

    private void collectResourceStatesByPath(Map<String, Set<ResourceState>> result, Collection<ResourceState> states,
                                             ResourceState currentState) {

        if (currentState == null) {
            return;
        }

        if (isEntryInsideCollectionCheckingByReference(states, currentState)) return;

        states.add(currentState);
        // add current state to results
        Set<ResourceState> thisStateSet = result.get(currentState.getResourcePath());
        if (thisStateSet == null)
            thisStateSet = new HashSet<ResourceState>();
        thisStateSet.add(currentState);
        result.put(currentState.getResourcePath(), thisStateSet);
        for (ResourceState next : currentState.getAllTargets()) {
            // if (!next.equals(currentState) && !next.isPseudoState()) {
            if (next != null && next != currentState) {
                String path = next.getResourcePath();
                if (result.get(path) != null) {
                    if (!result.get(path).contains(next)) {
                        logger.debug(String.format("Adding to existing ResourceState[%s] set (%s): %s", path, result.get(path), next));
                        result.get(path).add(next);
                    }
                } else {
                    logger.debug(String.format("Putting a ResourceState[%s]: %s", path, next));
                    Set<ResourceState> set = new HashSet<ResourceState>();
                    set.add(next);
                    result.put(path, set);
                }
            }
            collectResourceStatesByPath(result, states, next);
        }
    }

    private void populateInteractionsByPath(ResourceState currentState, String method) {
        Set<String> interactions = (Set<String>) interactionsByPath.get(currentState.getPath());
        if (interactions == null) {
            interactions = new HashSet<String>();
        }
        // every state must have a 'GET' interaction
        putMethodOrGetMethodToCollection(method, interactions);
        interactionsByPath.put(currentState.getPath(), interactions);
    }

    private void populateInteractionsByPath(String path, TransitionCommandSpec command) {
        Set<String> interactions = (Set<String>) interactionsByPath.get(path);
        interactions = addMethodsFromCommandNotAutoTransition(command, interactions);
        interactionsByPath.put(path, interactions);
    }

    private void populateInteractionsByState(String name, TransitionCommandSpec command) {
        Set<String> interactions = (Set<String>) interactionsByStateName.get(name);
        interactions = addMethodsFromCommandNotAutoTransition(command, interactions);
        interactionsByStateName.put(name, interactions);
    }

    private void populateInteractionsByState(ResourceState currentState, String method) {
        Set<String> interactions = (Set<String>) interactionsByStateName.get(currentState.getName());
        if (interactions == null) {
            interactions = new HashSet<String>();
        }
        // every state must have a 'GET' interaction
        if (!currentState.isPseudoState()) {
            putMethodOrGetMethodToCollection(method, interactions);
        }
        if (currentState.getActions() != null) {
            for (Action action : currentState.getActions()) {
                if (action.getMethod() != null) {
                    interactions.add(action.getMethod());
                }
            }
        }
        interactionsByStateName.put(currentState.getName(), interactions);
    }

    private void putMethodOrGetMethodToCollection(String method, Set<String> interactions) {
        if (method != null) {
            interactions.add(method);
        } else {
            interactions.add(HttpMethod.GET);
        }
    }

    private Set<String> addMethodsFromCommandNotAutoTransition(TransitionCommandSpec command, Set<String> interactions) {
        if (interactions == null)
            interactions = new HashSet<String>();
        if (command.getMethod() != null && !command.isAutoTransition())
            interactions.add(command.getMethod());
        return interactions;
    }

    private void populateTransitionsByIdAndRel(ResourceState currentState) {
        for (Transition transition : currentState.getTransitions()) {
            if (transition == null) {
                logger.warn("collectTransitionsByRel : null transition detected");
            } else if (transition.getTarget() == null) {
                logger.warn("collectTransitionsByRel : null target detected");
            } else if (transition.getTarget().getRel() == null) {
                logger.warn("collectTransitionsByRel : null relation detected");
            } else {
                transitionsById.put(transition.getId(), transition);
                transitionsByRel.put(transition.getTarget().getRel(), transition);
            }
        }
    }

    private void populateResourceStatesByName(ResourceState currentState) {
        resourceStatesByName.put(currentState.getName(), currentState);
    }


    private boolean isEntryInsideCollectionCheckingByReference(Collection<ResourceState> result, ResourceState currentState) {
        for (ResourceState tmpState : result) {
            if (tmpState == currentState) {
                return true;
            }
        }
        return false;
    }

    private boolean isResourceStateLazy(ResourceState targetState) {
        return targetState instanceof LazyResourceState || targetState instanceof LazyCollectionResourceState;
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
}
