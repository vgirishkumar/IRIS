package com.temenos.interaction.core.hypermedia.validation;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public interface LogicalConfigurationListener {

	/**
	 * Called when a ResourceState does not have any Metadata for entity in metadata.xml.
	 * @param rsm
	 * @param state
	 */
	public void noMetadataFound(ResourceStateMachine rsm, ResourceState state);

	/**
	 * Called when a ResourceState does not have any Action bound to the resource.
	 * @param rsm
	 * @param state
	 */
	public void noActionsConfigured(ResourceStateMachine rsm, ResourceState state);

	/**
	 * Called when a ResourceState does not have an Action bound to view the resource.
	 * @param rsm
	 * @param state
	 */
	public void viewActionNotSeen(ResourceStateMachine rsm, ResourceState state);
	
	/**
	 * Called when the {@link ResourceStateMachine#getCommandController()} does not
	 * have an {@link InteractionCommand} available for the configured {@link Action}
	 * @param rsm
	 * @param state
	 * @param action
	 */
	public void actionNotAvailable(ResourceStateMachine rsm, ResourceState state, Action action);
}
