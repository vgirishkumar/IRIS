package com.temenos.interaction.core.command;

/**
 * A #ResourceStateTransitionCommand can be executed to modify a resources state.
 * @author aphethean
 */
public interface ResourceStateTransitionCommand extends ResourceCommand {
	
	public String getMethod();
	
}
