package com.temenos.interaction.core.command;

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
		ctx.setResource(CommandHelper.createEntityResource(ctx.getCurrentState().getEntityName(), new GenericError(code, message)));
		return Result.SUCCESS;
	}

}
