package com.temenos.interaction.sdk;

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


import java.util.List;

public class FieldInfo {

	private String name;
	private String type;
	private List<String> annotations;
	
	public FieldInfo(String name, String type, List<String> annotations) {
		this.name = name;
		this.type = type;
		this.annotations = annotations;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public List<String> getAnnotations() {
		return annotations;
	}
	
	public boolean equals(Object other) {
		if (other instanceof FieldInfo) {
			FieldInfo theOther = (FieldInfo) other;
			if ((theOther.getName() != null && this.name != null) && theOther.getName().equals(this.name)) {
				if ((theOther.getType() != null && this.type != null) && theOther.getType().equals(this.type)) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return false;
	}
	
	public int hashCode() {
		int hash = 0;
		if ( name != null ) hash = name.hashCode();
		if ( type != null ) hash += 4097 * type.hashCode();
		return hash;
	}
}
