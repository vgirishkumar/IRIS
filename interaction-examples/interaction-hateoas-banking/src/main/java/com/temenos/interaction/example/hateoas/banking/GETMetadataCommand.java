package com.temenos.interaction.example.hateoas.banking;

/*
 * #%L
 * interaction-example-hateoas-banking
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


import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.resource.MetaDataResource;

/**
 * GET command for obtaining meta data. 
 */
public class GETMetadataCommand implements InteractionCommand {

	private Metadata metadata;

	/**
	 * Construct an instance of this command
	 * @param metadata metadata
	 */
	public GETMetadataCommand(Metadata metadata) {
		this.metadata = metadata;
	}
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		MetaDataResource<Metadata> mdr = new MetaDataResource<Metadata>(metadata) {};	
		ctx.setResource(mdr);
		return Result.SUCCESS;
	}

}
