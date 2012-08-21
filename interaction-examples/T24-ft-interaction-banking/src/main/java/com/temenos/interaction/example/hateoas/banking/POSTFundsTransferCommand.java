package com.temenos.interaction.example.hateoas.banking;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;
import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.resource.EntityResource;

public class POSTFundsTransferCommand implements InteractionCommand {

	/* Implement InteractionCommand interface */

	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getId() == null || "".equals(ctx.getId()));

		String Id = "NEW";
		String TransactionType = "AC";
		String DebitAcctNo = "60127";
		String DebitCurrency = "USD";
		String DebitAmount = "100";
		String CreditAcctNo = "63417";
		
		EntityProperties entityProperties = new EntityProperties();
		entityProperties.setProperty( new EntityProperty( "Id", Id ) );
		entityProperties.setProperty( new EntityProperty( "TransactionType", TransactionType ) );
		entityProperties.setProperty( new EntityProperty( "DebitAcctNo", DebitAcctNo ) );
		entityProperties.setProperty( new EntityProperty( "DebitCurrency", DebitCurrency ) );
		entityProperties.setProperty( new EntityProperty( "DebitAmount", DebitAmount ) );
		entityProperties.setProperty( new EntityProperty( "CreditAcctNo", CreditAcctNo ) );
		
		Entity entity = new Entity("FundsTransfer", entityProperties);
		EntityResource<Entity> er = FundsTransferHelper.createEntityResource( entity );
		ctx.setResource(er);
		
		return Result.SUCCESS;
	}

	public String getMethod() {
		return HttpMethod.POST;
	}

}
