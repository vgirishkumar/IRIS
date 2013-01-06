package com.temenos.interaction.core.hypermedia.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.NewCommandController;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.Transition;

public class HypermediaValidator {
	private final static Logger logger = LoggerFactory.getLogger(HypermediaValidator.class);

	private final static String FINAL_STATE = "final";
	
	private ResourceStateMachine hypermediaEngine;
	private LogicalConfigurationListener logicalConfigurationListener;
	
	protected HypermediaValidator(ResourceStateMachine rsm) {
		this.hypermediaEngine = rsm;
	}
	
	public static HypermediaValidator createValidator(ResourceStateMachine rsm) {
		return new HypermediaValidator(rsm);
	}
	
	public void setLogicalConfigurationListener(LogicalConfigurationListener lcl) {
		this.logicalConfigurationListener = lcl;
	}
	
	/*
	 * @precondition ResourceStateMachine must have had a CommandController set.
	 */
	public void validate() {
		/*
		 * Validate the resource by attempting to fetch a command for all the required
		 * actions for the resource state.
		 */
		for (ResourceState currentState : hypermediaEngine.getStates()) {
			logger.debug("Checking configuration for [" + currentState + "] " + currentState.getPath());
			List<Action> actions = currentState.getActions();
			if (actions == null) {
				fireNoActionsConfigured(hypermediaEngine, currentState);
				continue;
			}
			boolean viewActionSeen = false;
			for (Action action : actions) {
				NewCommandController commandController = hypermediaEngine.getCommandController();
				assert(commandController != null);
				InteractionCommand command = commandController.fetchCommand(action.getName());
				if (command == null)
					fireActionNotAvailable(hypermediaEngine, currentState, action);
				if (action.getType().equals(Action.TYPE.VIEW)) {
					viewActionSeen = true;
				}
			}

			// every resource MUST have a GET command
			if (!viewActionSeen)
				fireViewActionNotSeen(hypermediaEngine, currentState);
		}
		
	}
	
	private void fireNoActionsConfigured(ResourceStateMachine rsm, ResourceState state) {
		if (logicalConfigurationListener != null)
			logicalConfigurationListener.noActionsConfigured(hypermediaEngine, state);
	}

	private void fireActionNotAvailable(ResourceStateMachine rsm, ResourceState state, Action action) {
		if (logicalConfigurationListener != null)
			logicalConfigurationListener.actionNotAvailable(hypermediaEngine, state, action);
	}
	
	private void fireViewActionNotSeen(ResourceStateMachine rsm, ResourceState state) {
		if (logicalConfigurationListener != null)
			logicalConfigurationListener.viewActionNotSeen(hypermediaEngine, state);
	}
	
	public Set<ResourceState> unreachableStates(final Set<ResourceState> states, final ResourceStateMachine sm) {
		Set<ResourceState> copyStates = new HashSet<ResourceState>(states);
		copyStates.removeAll(sm.getStates());
		return copyStates;
	}
	
	public boolean validate(Set<ResourceState> states, ResourceStateMachine sm) {
		// validate the StateMachine can reach all the defined supplied states
		return unreachableStates(states, sm).size() == 0;
	}

	/**
	 * Produce a pretty DOT graph
	 * @param sm
	 * @return
	 */
	public String graph() {
		ResourceStateMachine sm = hypermediaEngine;
		List<ResourceState> states = new ArrayList<ResourceState>(sm.getStates());
		// sort the states for a predictable output
		Collections.sort(states);
		
		StringBuffer sb = new StringBuffer();
		sb.append("digraph ").append(sm.getInitial().getEntityName()).append(" {\n");
		// declare the initial state circle
		sb.append("    ").append(sm.getInitial().getEntityName() + sm.getInitial().getName()).append("[shape=circle, width=.25, label=\"\", color=black, style=filled]").append("\n");
		// declare the exception state
		ResourceState exceptionState = sm.getException();
		if (exceptionState != null) {
			sb.append("    ").append(exceptionState.getEntityName() + exceptionState.getName()).append("[label=\"" + exceptionState.getId() + "\"]").append("\n");
		}
		// declare all the states and set a label
		for (ResourceState s : states) {
			if (!s.equals(sm.getInitial()) && !s.isException()) {
				sb.append("    ").append(s.getEntityName() + s.getName()).append("[label=\"" + s.getId() + (s.getPath().length() > 0 ? (" " + s.getPath()) : "") + "\"]").append("\n");
			}
		}
		int countFinal = 0;
		for (ResourceState s : states) {
			for (ResourceState targetState : s.getAllTargets()) {
				List<Transition> transitions = s.getTransitions(targetState);
				for(Transition transition : transitions) {
					sb.append("    ").append(s.getEntityName() + s.getName()).append("->").append(targetState.getEntityName() + targetState.getName())
						.append("[");
					if (transition.getCommand().isAutoTransition()) {
						// this is an auto transition
						sb.append("style=\"dotted\"");
					} else {
						sb.append("label=\"")
						.append(transition.getCommand())
						.append("\"");
						
					}
					sb.append("]").append("\n");
				}
			}
			if (s.isFinalState()) {
				sb.append("    ").append(FINAL_STATE);
				if (countFinal > 0) {
					sb.append(countFinal);
				}
				sb.append("[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]").append("\n");
				sb.append("    ").append(s.getEntityName() + s.getName()).append("->").append(FINAL_STATE);
				if (countFinal > 0) {
					sb.append(countFinal);
				}
				sb.append("[label=\"\"]").append("\n");
				countFinal++;
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
	/**
	 * Produce a pretty DOT graph, only for the states of this current entity.
	 * @param sm
	 * @return
	 */
	public String graphEntityNextStates() {
		ResourceStateMachine sm = hypermediaEngine;
		Collection<ResourceState> states = sm.getStates();
		StringBuffer sb = new StringBuffer();
		sb.append("digraph ").append(sm.getInitial().getEntityName()).append(" {\n");
		// declare the initial state circle
		sb.append("    ").append(sm.getInitial().getEntityName() + sm.getInitial().getName()).append("[shape=circle, width=.25, label=\"\", color=black, style=filled]").append("\n");
		// declare all the state machines as a square, and set the label for states of this entity
		for (ResourceState s : states) {
			if (!s.equals(sm.getInitial())) {
				if (s.getEntityName().equals(sm.getInitial().getEntityName())) {
					sb.append("    ").append(s.getEntityName() + s.getName()).append("[label=\"" + s.getId() + "\"]").append("\n");
				} else if (s.isInitial()) {
					sb.append("    ").append(s.getEntityName() + s.getName()).append("[shape=square, width=.25, label=\"" + s.getId() + "\"]").append("\n");
				}
			}
		}
		int countFinal = 0;
		for (ResourceState s : states) {
			// only show transition for states of this state machine
			if (s.getEntityName().equals(sm.getInitial().getEntityName())) {
				for (ResourceState targetState : s.getAllTargets()) {
					List<Transition> transitions = s.getTransitions(targetState);
					for(Transition transition : transitions) {
						sb.append("    ").append(s.getEntityName() + s.getName()).append("->").append(targetState.getEntityName() + targetState.getName())
							.append("[label=\"")
							.append(transition.getCommand())
							.append("\"]").append("\n");
					}
				}
				if (s.isFinalState()) {
					sb.append("    ").append(FINAL_STATE);
					if (countFinal > 0) {
						sb.append(countFinal);
					}
					sb.append("[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]").append("\n");
					sb.append("    ").append(s.getName()).append("->").append(FINAL_STATE);
					if (countFinal > 0) {
						sb.append(countFinal);
					}
					sb.append("[label=\"\"]").append("\n");
					countFinal++;
				}
			}
		}
		sb.append("}");
		return sb.toString();
	}

}
