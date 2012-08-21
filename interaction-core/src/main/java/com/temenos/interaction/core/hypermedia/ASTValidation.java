package com.temenos.interaction.core.hypermedia;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ASTValidation {
	
	private final static String FINAL_STATE = "final";
	
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
	public String graph(ResourceStateMachine sm) {
		List<ResourceState> states = new ArrayList<ResourceState>(sm.getStates());
		// sort the states for a predictable output
		Collections.sort(states);
		
		StringBuffer sb = new StringBuffer();
		sb.append("digraph ").append(sm.getInitial().getEntityName()).append(" {\n");
		// declare the initial state circle
		sb.append("    ").append(sm.getInitial().getEntityName() + sm.getInitial().getName()).append("[shape=circle, width=.25, label=\"\", color=black, style=filled]").append("\n");
		// declare all the states and set a label
		for (ResourceState s : states) {
			if (!s.equals(sm.getInitial())) {
				sb.append("    ").append(s.getEntityName() + s.getName()).append("[label=\"" + s.getId() + (s.getPath().length() > 0 ? (" " + s.getPath()) : "") + "\"]").append("\n");
			}
		}
		int countFinal = 0;
		for (ResourceState s : states) {
			for (ResourceState targetState : s.getAllTargets()) {
				Transition transition = s.getTransition(targetState);
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
	public String graphEntityNextStates(ResourceStateMachine sm) {
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
					Transition transition = s.getTransition(targetState);
					sb.append("    ").append(s.getEntityName() + s.getName()).append("->").append(targetState.getEntityName() + targetState.getName())
						.append("[label=\"")
						.append(transition.getCommand())
						.append("\"]").append("\n");
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
