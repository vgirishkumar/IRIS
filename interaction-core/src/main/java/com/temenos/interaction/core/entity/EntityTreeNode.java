package com.temenos.interaction.core.entity;

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
 * Defines a node in a tree of properties of an entity.
 */
public interface EntityTreeNode {

	/**
	 * Returns the fully qualified name of this node. The fully qualified name
	 * of this node is in the form
	 * <i>fullyQualifiedNameOfParentNode.thisNodeName</i>
	 * 
	 * @return fully qualified name of this node
	 */
	public String getFullyQualifiedName();

	/**
	 * Sets the parent node.
	 * 
	 * @param parent
	 *            node
	 */
	public void setParent(EntityTreeNode parent);
}
