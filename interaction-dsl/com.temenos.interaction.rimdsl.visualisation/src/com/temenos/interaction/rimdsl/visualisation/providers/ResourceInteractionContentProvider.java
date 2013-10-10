/*******************************************************************************
 * Copyright 2005, 2009 CHISEL Group, University of Victoria, Victoria, BC,
 * Canada. All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: The Chisel Group, University of Victoria IBM CAS, IBM Toronto
 * Lab
 ******************************************************************************/
/*******************************************************************************
 * Modified work Copyright 2013 Temenos Holdings N.V.
 * The example code for XText visualisation has been modified to visualise 
 * the IRIS RIMDSL.
 ******************************************************************************/
package com.temenos.interaction.rimdsl.visualisation.providers;

/*
 * #%L
 * com.temenos.interaction.rimdsl.RimDsl - Visualisation
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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.zest.core.viewers.IGraphEntityContentProvider;

import com.google.common.collect.Iterators;
import com.temenos.interaction.rimdsl.rim.DomainModel;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;
import com.temenos.interaction.rimdsl.rim.State;
import com.temenos.interaction.rimdsl.rim.Transition;


/**
 * Content provider for a ZEST graph viewer
 * @author aphethean 
 */
public class ResourceInteractionContentProvider implements IGraphEntityContentProvider {

	private boolean showIncomingRelations;
	private boolean showOutgoingRelations;
	
	private List<Transition> transitions;
	private Set<State> states;
	
	private EObject input;
	
	
	/**
	 * A utility method called from the label provider 
	 * that is used to get the description of a relation 
	 * @param from The source entity of the relation
	 * @param to The target entity of the relation
	 * @return The description of the relation connecting the source entity to the target entity
	 */
	public List<String> getTransitionDescription(Object from, Object to) {
		List<String> result = new ArrayList<String>();
		
		for (Transition transition : transitions) {
			State fromState = (State) transition.eContainer();
			assert(fromState instanceof State);
			if (transition.getState() == from && fromState == to) {
				if (transition.getTitle() != null) {
					result.add(transition.getTitle().getName());
				}
			}
		}
		return result;
	}
	
	/**
	 * Constructor
	 * @param showIncomingRelations whether incoming relations are shown
	 * @param showOutgoingRelations whether outgoing relations are shown
	 */
	public ResourceInteractionContentProvider(boolean showIncomingRelations, boolean showOutgoingRelations) {
		super();
		this.showIncomingRelations = showIncomingRelations;
		this.showOutgoingRelations = showOutgoingRelations;
		
		transitions = new ArrayList<Transition>();
		states = new HashSet<State>();
		
		input = null;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphEntityContentProvider#getConnectedTo(java.lang.Object)
	 */
	public Object[] getConnectedTo(Object o) {
		if (o instanceof State) {
			State state = (State)o;
			List<State> result = new ArrayList<State>();
			for (Transition transition : transitions) {
				if (transition.eContainer() == state) {
					result.add(transition.getState());
				}
			}
			return result.toArray();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.zest.core.viewers.IGraphEntityContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		List<EObject> result = new ArrayList<EObject>();
		result.addAll(states);
		return result.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
		input = null;
		states.clear();
		transitions.clear();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput == null) {
			input = null;
			
			rebuildTransitions();
		} else {
			if (newInput instanceof State) {
				input = (EObject) newInput;
				rebuildTransitions();
			} else {
				throw new RuntimeException("Input element not supported! Need to be " + State.class.getCanonicalName());
			}
		}
	}

	/**
	 * Setter, shows or hides incoming relations
	 * @param enable
	 */
	public void setShowIncomingRelations(boolean enable) {
		this.showIncomingRelations = enable;
		rebuildTransitions();
	}

	/**
	 * Setter, shows or hides outgoing relations
	 * @param enable
	 */
	public void setShowOutgoingRelations(boolean enable) {
		this.showOutgoingRelations = enable;
		rebuildTransitions();
	}

	
	/**
	 * Used internally to build up all transitions and caches. 
	 * Must be called after the visualisation options have been changed
	 */
	private void rebuildTransitions() {
		states.clear();
		transitions.clear();
		
		if (input != null) {
			// Some temporary buffers
			Set<State> allStates = new HashSet<State>();
			Map<State, Set<Transition>> incomingTransitions = 
				new HashMap<State, Set<Transition>>();
			Map<State, Set<Transition>> outgoingTransitions = 
				new HashMap<State, Set<Transition>>();
			
			if (input instanceof State) {
				State state = (State)input;
				
				// Iterate over all elements in model
				Iterator<EObject> iter = state.eResource().getAllContents();
				while (iter.hasNext()) {
					EObject rootContent = iter.next();
					if (rootContent instanceof DomainModel) {
						DomainModel model = (DomainModel) rootContent;
						Iterator<ResourceInteractionModel> rims = Iterators.filter(model.eAllContents(), ResourceInteractionModel.class);
						while (rims.hasNext()) {
							ResourceInteractionModel rim = rims.next();
							processRIM(rim, allStates, incomingTransitions, outgoingTransitions);
						}
					}
				}

				// Then build the visual representation of the part of 
				// the network that is to be shown
				if (showIncomingRelations) {
					retrieveStatesTransitions(state, incomingTransitions, true);
				}
				
				if (showOutgoingRelations) {
					retrieveStatesTransitions(state, outgoingTransitions, false);
				}
			}
		}
	}

	private void processRIM(ResourceInteractionModel model, Set<State> allStates, Map<State, Set<Transition>> incomingTransitions, Map<State, Set<Transition>> outgoingTransitions) {
		Iterator<State> states = Iterators.filter(model.eAllContents(), State.class);
		while (states.hasNext()) {
			State state = states.next();
			System.out.println("Adding state " + state.getName());
			allStates.add((State) state);
			Iterator<Transition> transitions = Iterators.filter(state.eAllContents(), Transition.class);
			while (transitions.hasNext()) {
				Transition transition = transitions.next();
				System.out.println("Adding transition " + transition.getTitle());
				
				State toState = transition.getState();
				Set<Transition> incomingTransitionsForState = incomingTransitions.get(toState);
				if (incomingTransitionsForState == null) {
					incomingTransitionsForState = new HashSet<Transition>();
					incomingTransitions.put(toState, incomingTransitionsForState);
				}
				incomingTransitionsForState.add(transition);
				
				State fromState = (State) transition.eContainer();
				assert(fromState instanceof State);
				Set<Transition> outgoingTransitionsForState = outgoingTransitions.get(fromState);
				if (outgoingTransitionsForState == null) {
					outgoingTransitionsForState = new HashSet<Transition>();
					outgoingTransitions.put(fromState, outgoingTransitionsForState);
				}
				outgoingTransitionsForState.add(transition);
			}
		}
		
	}
	
	private void retrieveStatesTransitions(State root, Map<State, Set<Transition>> allTransistions, boolean backwards) {
		Set<State> visitedStates = new HashSet<State>();
		visitedStates.clear();
		internalRetrieveStatesTransitions(root, visitedStates, allTransistions, backwards);
	}

	private void internalRetrieveStatesTransitions(State root, Set<State> visitedStates, Map<State, Set<Transition>> allTransitions, boolean backwards) {
		states.add(root);
		Set<Transition> transitionsForState = allTransitions.get(root);
		if (transitionsForState != null) {
			for (Transition transitionForState : transitionsForState) {
				transitions.add(transitionForState);
			
				State next = null;
				if (backwards) {
					next = (State) transitionForState.eContainer();
				} else {
					next = transitionForState.getState();
				}
				
				// Recursive call if this element has not been in focus before
				if (!visitedStates.contains(next)) {
					visitedStates.add(next);
					internalRetrieveStatesTransitions(next, visitedStates, allTransitions, backwards);
				} 				
			}
		}
	}
}