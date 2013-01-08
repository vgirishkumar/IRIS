package com.temenos.interaction.core.media.edmx;

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