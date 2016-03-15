package com.temenos.interaction.core.hypermedia;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ws.rs.core.MultivaluedMap;

import com.temenos.interaction.core.MultivaluedMapImpl;

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


public class ParameterAndValue {
	private String parameter;
	private String value;
		
	public ParameterAndValue(String parameter, String value) {
		super();
		this.parameter = parameter;
		this.value = value;
	}
	public String getParameter() {
		return parameter;
	}
	public String getValue() {
		return value;
	}
	
	/**
     * This method would convert ParameterAndvalue[] to MultiValueMap<String, String>
     * @param paramAndValues
     * @return
     */
    public static MultivaluedMap<String, String> getParamAndValueAsMultiValueMap(ParameterAndValue[] paramAndValues) {
    	MultivaluedMap<String, String> parameters = new MultivaluedMapImpl<String>();
    	if (paramAndValues != null) {
	    	for (ParameterAndValue paramAndValue : paramAndValues) {
	    		parameters.put(paramAndValue.getParameter(), 
	    				new ArrayList<String> (Arrays.asList(paramAndValue.getValue())));
	    	}
    	}
    	return parameters;
    }
}
