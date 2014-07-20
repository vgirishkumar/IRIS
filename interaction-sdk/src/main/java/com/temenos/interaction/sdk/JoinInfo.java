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

public class JoinInfo {

	private String name;
	private String targetType;
	private List<String> annotations;
	
	public JoinInfo(String name, String targetType, List<String> annotations) {
		this.name = name;
		this.targetType = targetType;
		this.annotations = annotations;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTargetType() {
		return targetType;
	}
	
	public List<String> getAnnotations() {
		return annotations;
	}
	
	public boolean equals(Object other) {
		if (other instanceof JoinInfo) {
			JoinInfo theOther = (JoinInfo) other;
			if ((theOther.getName() != null && this.name != null) && theOther.getName().equals(this.name)) {
				if ((theOther.getTargetType() != null && this.targetType != null) && theOther.getTargetType().equals(this.targetType)) {
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
		if ( targetType != null ) hash += 4097 * targetType.hashCode();
		return hash;
	}
}
