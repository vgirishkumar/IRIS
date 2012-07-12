package com.temenos.interaction.example.hateoas.banking;

import java.util.Date;
import java.util.Random;

import javax.ws.rs.HttpMethod;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.command.InteractionCommand;
import com.temenos.interaction.core.command.InteractionContext;

public class POSTFundTransferCommand implements InteractionCommand {

	/* Implement InteractionCommand interface */

	@Override
	public Result execute(InteractionContext ctx) {
		assert(ctx != null);
		assert(ctx.getId() == null || "".equals(ctx.getId()));

		Long key = Math.abs(new Random().nextLong() % Long.MAX_VALUE);
		Date now = new Date();
		
		EntityResource<FundTransfer> er = new EntityResource<FundTransfer>(new FundTransfer(key, "<resource><FundTransfer><id>" + key + "</id><body>Funds tranfer issued at " + now + "</body></FundTransfer><links></links></resource>"));
		ctx.setResource(er);
		return Result.SUCCESS;
	}

	public String getMethod() {
		return HttpMethod.POST;
	}

}
