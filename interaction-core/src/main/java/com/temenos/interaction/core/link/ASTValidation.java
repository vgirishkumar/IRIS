package com.temenos.interaction.core.link;

import java.util.Collection;
import java.util.HashSet;
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
		Collection<ResourceState> states = sm.getStates();
		StringBuffer sb = new StringBuffer();
		sb.append("digraph ").append(sm.getEntityName()).append(" {\n");
		sb.append("    ").append(sm.getInitial().getName()).append("[shape=circle, width=.25, label=\"\", color=black, style=filled]").append("\n");
		int countFinal = 0;
		for (ResourceState s : states) {
			for (ResourceState targetState : s.getAllTargets()) {
				Transition transition = s.getTransition(targetState);
				sb.append("    ").append(s.getName()).append("->").append(targetState.getName())
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
		sb.append("}");
		return sb.toString();
	}
}
