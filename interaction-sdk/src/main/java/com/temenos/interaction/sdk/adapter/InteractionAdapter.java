package com.temenos.interaction.sdk.adapter;

import java.util.List;

import com.temenos.interaction.sdk.EntityInfo;
import com.temenos.interaction.sdk.command.Commands;
import com.temenos.interaction.sdk.entity.EntityModel;
import com.temenos.interaction.sdk.interaction.InteractionModel;

/**
 * An implementation of this adapter is supplied to the SDK to generate an
 * interaction project with optional database responder.
 * @author aphethean
 */
public interface InteractionAdapter {

	public InteractionModel getInteractionModel();
	public EntityModel getEntityModel();
	public Commands getCommands();
	public List<EntityInfo> getEntitiesInfo();
}
