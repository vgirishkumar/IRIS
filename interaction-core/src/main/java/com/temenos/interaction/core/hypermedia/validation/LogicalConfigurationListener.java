package com.temenos.interaction.core.hypermedia.validation;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
