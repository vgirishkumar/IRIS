package com.temenos.interaction.sdk.interaction.transition;

/*
 * #%L
 * interaction-sdk
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


import com.temenos.interaction.sdk.interaction.IMResourceStateMachine;
import com.temenos.interaction.sdk.interaction.state.IMState;

/**
 * This class holds information about a transition to a collection state
 */
public class IMEntityStateTransition extends IMTransition {

	private String linkProperty;			//The depend element of a referential constraint (i.e. the property that specifies the target resource)
	private IMResourceStateMachine targetResourceStateMachine;	//Resource state machine of target state
	
	public IMEntityStateTransition(
			IMResourceStateMachine targetResourceStateMachine, 
			IMState targetState, 
			String linkProperty,
			String title, 
			String method) {
		super(title, targetState, method);
		this.linkProperty = linkProperty;
		this.targetResourceStateMachine = targetResourceStateMachine;
	}
	
	public String getLinkProperty() {
		return linkProperty;
	}

	public IMResourceStateMachine getTargetResourceStateMachine() {
		return targetResourceStateMachine;
	}
}
