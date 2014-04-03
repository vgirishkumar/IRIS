package com.temenos.interaction.core.command;

/*
 * #%L
 * interaction-core
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.entity.GenericError;

/**
 * A GET command that returns the last exception as an entity resource
 */
public final class GETExceptionCommand implements InteractionCommand {
	private final Logger logger = LoggerFactory.getLogger(GETExceptionCommand.class);

	/* Implement InteractionCommand interface */
	
	@Override
	public Result execute(InteractionContext ctx) throws InteractionException {
		assert(ctx != null);
		
		InteractionException exception = ctx.getException();
		if(exception == null) {
			logger.error("[" + ctx.getCurrentState().getId() + "] No exceptions available.");
			return Result.FAILURE;
		}
		String code = String.valueOf(exception.getHttpStatus().getStatusCode());
		String message = exception.getMessage();
		ctx.setResource(CommandHelper.createEntityResource(ctx.getCurrentState().getEntityName(), new GenericError(code, message), GenericError.class));
		return Result.SUCCESS;
	}

}
