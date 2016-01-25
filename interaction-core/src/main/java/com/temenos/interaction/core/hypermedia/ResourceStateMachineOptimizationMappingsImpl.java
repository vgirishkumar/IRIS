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
import static com.temenos.interaction.core.hypermedia.ResourceStateMachineOptimizationMappingsImpl.InformationType.*;

/**
 * @author kwieconkowski
 */

public class ResourceStateMachineOptimizationMappingsImpl implements ResourceStateMachineOptimizationMappings<InformationType> {
    private final Logger logger = LoggerFactory.getLogger(ResourceStateMachineOptimizationMappingsImpl.class);


    public enum InformationType {
        TRANSITIONS_BY_ID(new HashMap<String, Transition>()),
        TRANSITIONS_BY_REL(new HashMap<String, Transition>()),
        INTERACTIONS_BY_PATH(new HashMap<String, Set<String>>()),
        INTERACTIONS_BY_STATE_NAME(new HashMap<String, Set<String>>()),
        RESOURCE_STATES_BY_PATH(new HashMap<String, Set<ResourceState>>()),
        RESOURCE_STATES_BY_NAME(new HashMap<String, ResourceState>());

        private final Map informationMap;

        private Map get() {
            return informationMap;
        }

        private Object get(String key) {
            return informationMap.get(key);
        }

        private void put(String key, Object value) {
            informationMap.put(key, value);
        }

        private void clear() {
            informationMap.clear();
        }

        InformationType(final Map informationMap) {
            this.informationMap = informationMap;
        }
    }


    private ResourceState initial;
    private ResourceStateProvider resourceStateProvider;

    @Override
    public List<ResourceState> buildAllMappingsAndInitializeLazyResourceAndReturnAllStates(ResourceStateProvider resourceStateProvider, ResourceState initial) {
        this.initial = initial;
        this.resourceStateProvider = resourceStateProvider;

        for (InformationType informationType : InformationType.values()) {
            informationType.clear();
        }

        List<ResourceState> allStates = new ArrayList<ResourceState>();
        checkAndResolve(initial);
        collectAllStatesAndTransitionsByIdAndRelAndResourceStatesByName(allStates, initial);
        collectInteractionsByPathAndState(new ArrayList<ResourceState>(), initial, HttpMethod.GET);
        collectResourceStatesByPath((Map<String, Set<ResourceState>>) RESOURCE_STATES_BY_PATH.get(), new HashSet<ResourceState>(), initial);

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
        return target.get(key);
    }

    @Override
    public Map getInformationFrom(InformationType target) {
        return target.get();
    }

    @Override
    public void removeResourceStateByName(String stateName) {
        RESOURCE_STATES_BY_NAME.get().remove(stateName);
    }

    private void clear() {
        initial = null;
        resourceStateProvider = null;
    }

    private void collectInteractionsByPathForState(ResourceState state, String method) {
        Set<String> pathInteractions = (Set<String>) INTERACTIONS_BY_PATH.get(state.getPath());
        if (pathInteractions == null) {
            pathInteractions = new HashSet<String>();
            INTERACTIONS_BY_PATH.put(state.getPath(), pathInteractions);
        }
        putMethodOrGetMethodToCollection(method, pathInteractions);
    }

    private void collectInteractionsByStateForState(ResourceState state, String method) {
        Set<String> stateInteractions = (Set<String>) INTERACTIONS_BY_STATE_NAME.get(state.getName());
        if (stateInteractions == null) {
            stateInteractions = new HashSet<String>();
            INTERACTIONS_BY_STATE_NAME.put(state.getName(), stateInteractions);
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

                Set<String> tmpStateInteractions = (Set<String>) INTERACTIONS_BY_STATE_NAME.get(next.getName());

                if (tmpStateInteractions == null) {
                    tmpStateInteractions = new HashSet<String>();
                    INTERACTIONS_BY_STATE_NAME.put(next.getName(), tmpStateInteractions);
                }

                if (command.getMethod() != null && !command.isAutoTransition())
                    tmpStateInteractions.add(command.getMethod());
            }
        }
    }

    private void collectResourceStatesByPathForState(ResourceState state) {
        Set<ResourceState> pathStates = (Set<ResourceState>) RESOURCE_STATES_BY_PATH.get(state.getResourcePath());
        if (pathStates == null) {
            pathStates = new HashSet<ResourceState>();
            RESOURCE_STATES_BY_PATH.put(state.getResourcePath(), pathStates);
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
        Set<String> interactions = (Set<String>) INTERACTIONS_BY_PATH.get(currentState.getPath());
        if (interactions == null) {
            interactions = new HashSet<String>();
        }
        // every state must have a 'GET' interaction
        putMethodOrGetMethodToCollection(method, interactions);
        INTERACTIONS_BY_PATH.put(currentState.getPath(), interactions);
    }

    private void populateInteractionsByPath(String path, TransitionCommandSpec command) {
        Set<String> interactions = (Set<String>) INTERACTIONS_BY_PATH.get(path);
        interactions = addMethodsFromCommandNotAutoTransition(command, interactions);
        INTERACTIONS_BY_PATH.put(path, interactions);
    }

    private void populateInteractionsByState(String name, TransitionCommandSpec command) {
        Set<String> interactions = (Set<String>) INTERACTIONS_BY_STATE_NAME.get(name);
        interactions = addMethodsFromCommandNotAutoTransition(command, interactions);
        INTERACTIONS_BY_STATE_NAME.put(name, interactions);
    }

    private void populateInteractionsByState(ResourceState currentState, String method) {
        Set<String> interactions = (Set<String>) INTERACTIONS_BY_STATE_NAME.get(currentState.getName());
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
        INTERACTIONS_BY_STATE_NAME.put(currentState.getName(), interactions);
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
                TRANSITIONS_BY_ID.put(transition.getId(), transition);
                TRANSITIONS_BY_REL.put(transition.getTarget().getRel(), transition);
            }
        }
    }

    private void populateResourceStatesByName(ResourceState currentState) {
        RESOURCE_STATES_BY_NAME.put(currentState.getName(), currentState);
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

