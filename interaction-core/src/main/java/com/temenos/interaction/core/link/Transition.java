package com.temenos.interaction.core.link;

public class Transition {

	private final ResourceState source, target;
	private final TransitionCommandSpec command;
	
	public Transition(ResourceState source, TransitionCommandSpec command, ResourceState target) {
		this.source = source;
		this.target = target;
		this.command = command;
	}
	
	public ResourceState getSource() {
		return source;
	}

	public ResourceState getTarget() {
		return target;
	}

	public TransitionCommandSpec getCommand() {
		return command;
	}
	
	public String getId() {
		if(source == null) {
			return target.getId() + ">" + target.getId();		//transition to itself
		}
		else {
			return source.getId() + ">" + target.getId();
		}
	}
	
	public boolean equals(Object other) {
		//check for self-comparison
	    if ( this == other ) return true;
	    if ( !(other instanceof Transition) ) return false;
	    Transition otherTrans = (Transition) other;
	    // only compare the ResourceState name to avoid recursion
	    return ((source == null && otherTrans.source == null)
	    		|| source != null && otherTrans.source != null && source.getName().equals(otherTrans.source.getName()) ) &&
	    	target.getName().equals(otherTrans.target.getName()) &&
	    	command.equals(otherTrans.command);
	}
	
	public int hashCode() {
		return (source != null ? source.getName().hashCode() : 0) +
			target.getName().hashCode() +
			command.hashCode();
	}

}
