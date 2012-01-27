package com.temenos.interaction.core.link;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.temenos.interaction.core.state.ResourceInteractionModel;

public class ResourceState {

	public ResourceInteractionModel resource;
	
	private String name;
//	  private List<Command> actions = new ArrayList<Command>();
	private Map<String, Transition> transitions = new HashMap<String, Transition>();
	  public void addTransition(Eval eval, ResourceState targetState) {
	    assert null != targetState;
	    transitions.put(eval.getName(), new Transition(this, eval, targetState));
	  }

	public ResourceState(ResourceInteractionModel resource) {
		this.resource = resource;
	}
	
	public void addTransition(Transition t) {
		transitions.add(t);
	}
	
	
}
