package com.temenos.interaction.translate.test.stub.controller;

/*
 * #%L
 * interaction-translate
 * %%
 * Copyright (C) 2012 - 2016 Temenos Holdings N.V.
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.commands.odata.CommandHelper;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.command.InteractionException;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.resource.EntityResource;

/**
 * 
 * @author dgroves
 *
 */
public class GETTestController implements InteractionCommand {

	private final static Logger logger = LoggerFactory.getLogger(GETTestController.class);

	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		String entityName = "HelloWorld";
		EntityProperties properties = new EntityProperties();
		properties.setParent(new EntityProperty("Hello", "World"));
		EntityResource<Entity> entityResource = CommandHelper.createEntityResource(new Entity(entityName, properties));
		ctx.setResource(entityResource);
		return Result.SUCCESS;
	}
}
