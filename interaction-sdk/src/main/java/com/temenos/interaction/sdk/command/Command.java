package com.temenos.interaction.sdk.command;

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


import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information about an IRIS command
 */
public class Command {
	private String id;
	private String className;
	private List<Parameter> parameters = new ArrayList<Parameter>();

	public Command(String id, String className) {
		this.id = id;
		this.className = className;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getParameterName(int i) {
		return parameters.get(i).getValue();
	}

	public boolean isParameterByReference(int i) {
		return parameters.get(i).isByReference();
	}

	public List<Parameter> getParameters() {
		return parameters;
	}
	
	public void addParameter(String value, boolean isByReference, String refId) {
		parameters.add(new Parameter(value, isByReference, refId));
	}

	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}
}
