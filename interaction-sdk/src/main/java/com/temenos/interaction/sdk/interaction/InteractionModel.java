package com.temenos.interaction.sdk.interaction;

import java.util.ArrayList;
import java.util.List;

/**
 * This class holds information about the interaction model
 */
public class InteractionModel {

	private List<IMResourceStateMachine> resourceStateMachines = new ArrayList<IMResourceStateMachine>();
	
	public InteractionModel() {
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
