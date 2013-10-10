package com.temenos.interaction.sdk.adapter;

/*
 * #%L
 * interaction-sdk
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
