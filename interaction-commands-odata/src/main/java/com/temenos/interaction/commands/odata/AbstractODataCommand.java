package com.temenos.interaction.commands.odata;

import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.hypermedia.Action;

public abstract class AbstractODataCommand {

	/**
	 * Use this property to configure an action to use this entity 
	 * instead of the entity specified for the Resource.
	 */
	public final static String ENTITY_PROPERTY = "entity";
	
	public String getEntityName(InteractionContext ctx) {
		String entityName = ctx.getCurrentState().getEntityName();
		// TODO improve this naive implmentation, only using properties from first action
		Action action = null;
		if (ctx.getCurrentState().getActions().size() > 0)
			action = ctx.getCurrentState().getActions().get(0);
		
		if (action != null && action.getProperties() != null && action.getProperties().getProperty(ENTITY_PROPERTY) != null) {
			entityName = action.getProperties().getProperty(ENTITY_PROPERTY);
		}
		return entityName;
	}
	
}
