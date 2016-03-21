package com.temenos.interaction.test;

/**
 * Defines an entity which represents an item in the payload.
 * 
 * @author ssethupathi
 *
 */
public interface Entity {

	/**
	 * Returns the id of the entity.
	 * 
	 * @return id
	 */
	String id();

	/**
	 * Returns the value from this {@link Entity entity} for the fully qualified
	 * property name.
	 * 
	 * @param fqName
	 *            property name
	 * @return property value
	 */
	String get(String fqName);

	/**
	 * Returns the number of existence of a property for the specified fully
	 * qualified name in this {@link Entity entity}.
	 * 
	 * @param fqName
	 *            property name
	 * @return count
	 */
	int count(String fqName);

	/**
	 * Returns the {@link Link links} which are part of this {@link Entity
	 * entity}.
	 * 
	 * @return links
	 */
	Links links();

}