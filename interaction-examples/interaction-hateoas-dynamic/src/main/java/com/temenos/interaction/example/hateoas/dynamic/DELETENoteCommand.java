package com.temenos.interaction.example.hateoas.dynamic;

/*
 * #%L
 * interaction-example-hateoas-simple
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

public class DELETENoteCommand implements InteractionCommand {

	private Persistence persistence;

	public DELETENoteCommand(Persistence p) {
		this.persistence = p;
	}
	
	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		// delete from a database, etc.
		String id = ctx.getId();
		persistence.removeNote(new Long(id));
		return Result.SUCCESS;
	}

}
