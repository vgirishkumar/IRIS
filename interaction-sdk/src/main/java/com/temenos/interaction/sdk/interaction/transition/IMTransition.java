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


import com.temenos.interaction.sdk.interaction.state.IMState;

/**
 * This class holds information about a state transition
 */
public class IMTransition {
	private String title;					//Transition label
	private IMState targetState;	//Name of target state
	private String method;					//Method for transition to pseudo state
	
	public IMTransition(String title, IMState targetState,	String method) {
		this.title = title;
		this.targetState = targetState;
		this.method = method;
	}
	
	public String getTitle() {
		return title;
	}

	public IMState getTargetState() {
		return targetState;
	}
	
	public String getTitleAssignment() {
		String title = "";
		if(getTitle() != null) {
			title = "title: \"" + getTitle() + "\"";
		}
		return title;
	}

	public String getMethod() {
		return method;
	}
}
