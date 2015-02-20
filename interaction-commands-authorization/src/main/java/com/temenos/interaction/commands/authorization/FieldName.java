package com.temenos.interaction.commands.authorization;

/*
 * Classes containing information about a field name (column in relational DBS).
 * 
 * For now field names are strings. One day they may be more complex.
 */

/*
 * #%L
 * interaction-commands-Authorization
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

public class FieldName {
	String name;

	public FieldName(String name) {
		this.name = name;
	}

	public String getName() {
		return (name);
	}

	/**
	 * Define equality of state. To enable comparison.
	 */
	@Override
	public boolean equals(Object aThat) {
		if (this == aThat)
			return true;
		if (!(aThat instanceof FieldName))
			return false;

		FieldName that = (FieldName) aThat;
		return (this.getName().equals(that.getName()));
	}

	/**
	 * Return same hash code for identical objects. So contains() works.
	 */
	@Override
	public int hashCode() {
		return name.hashCode();
	}

}