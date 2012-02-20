package com.temenos.interaction.core.link;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ASTValidation {

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
		sb.append("digraph G {").append("\n");
		for (ResourceState s : states) {
			for (ResourceState targetState : s.getAllTargets()) {
				Transition transition = s.getTransition(targetState);
				sb.append("    ").append(s.getName()).append("->").append(targetState.getName())
					.append("[style=bold,label=\"")
					.append(transition.getCommand().getMethod()).append(" ").append(transition.getCommand().getPath())
					.append("\"]").append("\n");
			}
		}
		sb.append("}");
		return sb.toString();
	}
}
