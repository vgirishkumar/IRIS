package com.temenos.interaction.core.link;

public class Transition {

	private final ResourceState source, target;
	
	/* 
	 * Evaluate some condition to determine whether this transition is enabled
	 */
	private final Eval eval;
	
	public Transition(ResourceState source, Eval eval, ResourceState target) {
		this.source = source;
		this.target = target;
		this.eval = eval;
	}
}
