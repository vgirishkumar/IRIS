package com.temenos.interaction.sdk.interaction;

import java.util.ArrayList;
import java.util.List;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntityType;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;

/**
 * This class holds information about the interaction model
 */
public class InteractionModel {

	private List<IMResourceStateMachine> resourceStateMachines = new ArrayList<IMResourceStateMachine>();

	/**
	 * Construct an initial model from odata4j metadata
	 * @param edmDataServices odata4j metadata 
	 */
	public InteractionModel(EdmDataServices edmDataServices) {
		for (EdmEntityType entityType : edmDataServices.getEntityTypes()) {
			//ResourceStateMachine with one collection and one resource entity state
			String entityName = entityType.getName();
			String collectionStateName = entityName.toLowerCase() + "s";
			String entityStateName = entityName.toLowerCase();
			String mappedEntityProperty = entityType.getKeys().size() > 0 ? entityType.getKeys().get(0) : "id";
			IMResourceStateMachine rsm = new IMResourceStateMachine(entityName, collectionStateName, entityStateName, mappedEntityProperty);
			addResourceStateMachine(rsm);
		}
	}
	
	/**
	 * Construct an initial model from entity metadata
	 * @param metadata metadata 
	 */
	public InteractionModel(Metadata metadata) {
		for (EntityMetadata entityMetadata : metadata.getEntitiesMetadata().values()) {
			//ResourceStateMachine with one collection and one resource entity state
			String entityName = entityMetadata.getEntityName();
			String collectionStateName = entityName.toLowerCase() + "s";
			String entityStateName = entityName.toLowerCase();
			List<String> idFields = entityMetadata.getIdFields();
			String mappedEntityProperty = idFields.size() > 0 ? idFields.get(0) : "id";
			IMResourceStateMachine rsm = new IMResourceStateMachine(entityName, collectionStateName, entityStateName, mappedEntityProperty);
			addResourceStateMachine(rsm);
		}
	}
	
	public void addResourceStateMachine(IMResourceStateMachine resourceStateMachine) {
		resourceStateMachines.add(resourceStateMachine);
	}
	
	public List<IMResourceStateMachine> getResourceStateMachines() {
		return resourceStateMachines;
	}
	
	public IMResourceStateMachine findResourceStateMachine(String entityName) {
		for(IMResourceStateMachine rsm : resourceStateMachines) {
			if(rsm.getEntityName().equals(entityName)) {
				return rsm;
			}
		}
		return null;
	}
}
