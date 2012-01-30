package com.temenos.interaction.core.link;

public class Transition {

	private final ResourceState source, target;
	private final CommandSpec command;
	
	public Transition(ResourceState source, CommandSpec command, ResourceState target) {
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

	public CommandSpec getCommand() {
		return command;
	}
	
	public boolean equals(Object other) {
		//check for self-comparison
	    if ( this == other ) return true;
	    if ( !(other instanceof Transition) ) return false;
	    Transition otherTrans = (Transition) other;
	    // only compare the ResourceState name to avoid recursion
	    return source.getName().equals(otherTrans.source.getName()) &&
	    	target.getName().equals(otherTrans.target.getName()) &&
	    	command.equals(otherTrans.command);
	}
	
	public int hashCode() {
		return source.getName().hashCode() +
			target.getName().hashCode() +
			command.hashCode();
	}

}
