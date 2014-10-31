package com.temenos.interaction.core.hypermedia;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2014 Temenos Holdings N.V.
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



/**
 * TODO: Document me!
 *
 * @author mlambert
 *
 */
public class ResourceStateAndParameters {
	private ResourceState state;
	private ParameterAndValue[] paramsAndValues;
	
	/**
	 * @return the state
	 */
	public ResourceState getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(ResourceState state) {
		this.state = state;
	}
	/**
	 * @return the params
	 */
	public ParameterAndValue[] getParams() {
		return paramsAndValues;
	}
	/**
	 * @param paramsAndValues the params to set
	 */
	public void setParams(ParameterAndValue[] params) {
		this.paramsAndValues = params;
	}
	
	
}
