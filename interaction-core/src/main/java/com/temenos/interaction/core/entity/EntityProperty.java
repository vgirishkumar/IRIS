package com.temenos.interaction.core.entity;

/**
 * An Entity property class can hold either simple or complex types.    
 */
public class EntityProperty {

	private final String name;
	private final Object value;
	
	public EntityProperty(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the name of this property.
	 * @return property name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the value of this property.
	 * @return property value
	 */
	public Object getValue() {
		return value;
	}
}
