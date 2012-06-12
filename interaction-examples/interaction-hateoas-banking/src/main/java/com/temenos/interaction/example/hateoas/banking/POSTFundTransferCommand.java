package com.temenos.interaction.example.hateoas.banking;

import java.util.Date;
import java.util.Random;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourcePostCommand;

public class POSTFundTransferCommand implements ResourcePostCommand {

	public RESTResponse post(String id, EntityResource<?> resource) {
		assert(id == null || "".equals(id));

		Long key = Math.abs(new Random().nextLong() % Long.MAX_VALUE);
		Date now = new Date();
		
		EntityResource<FundTransfer> er = new EntityResource<FundTransfer>(new FundTransfer(key, "<resource><FundTransfer><id>" + key + "</id><body>Funds tranfer issued at " + now + "</body></FundTransfer><links></links></resource>"));
		
		return new RESTResponse(Response.Status.OK, er);
	}

	public String getMethod() {
		return HttpMethod.POST;
	}

}
