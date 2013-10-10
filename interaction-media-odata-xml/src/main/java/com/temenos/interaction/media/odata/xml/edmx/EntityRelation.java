package com.temenos.interaction.media.odata.xml.edmx;

/*
 * #%L
 * interaction-media-odata-xml
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


/**
 * This class defines a relation between two entities. 
 */
public class EntityRelation {
	public final static int MULTIPLICITY_TO_ONE = 0;
	public final static int MULTIPLICITY_TO_MANY = 1;
	public final static int MULTIPLICITY_MANY_TO_MANY = 2;
	
	private String name;
	private String namespace;
	private String sourceEntityName;
	private String sourceEntitySetName;
	private String targetEntityName;
	private String targetEntitySetName;
	private int multiplicity;
	
	public EntityRelation(String name, String namespace, String sourceEntityName, String targetEntityName, int multiplicity, String sourceEntitySetName, String targetEneitySetName) {
		this.name = name;
		this.namespace = namespace;
		this.sourceEntityName = sourceEntityName;
		this.targetEntityName = targetEntityName;
		this.multiplicity = multiplicity;
		this.sourceEntitySetName = sourceEntitySetName;
		this.targetEntitySetName = targetEneitySetName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
	public String getSourceEntityName() {
		return sourceEntityName;
	}

	public void setSourceEntityName(String sourceEntityName) {
		this.sourceEntityName = sourceEntityName;
	}

	public String getSourceEntitySetName() {
		return sourceEntitySetName;
	}

	public String getTargetEntityName() {
		return targetEntityName;
	}

	public void setTargetEntityName(String targetEntityName) {
		this.targetEntityName = targetEntityName;
	}

	public String getTargetEntitySetName() {
		return targetEntitySetName;
	}

	public int getMultiplicity() {
		return multiplicity;
	}

	public void setMultiplicity(int multiplicity) {
		this.multiplicity = multiplicity;
	}
}