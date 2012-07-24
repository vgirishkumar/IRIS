package com.temenos.interaction.example.hateoas.banking;

import java.util.Date;
import java.util.Random;

import javax.ws.rs.HttpMethod;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

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
		String json = "";
		json += "{";
		json += "  \"FundTransfer\" : {";
		json += "    \"id\" : \"" + key + "\",";
		json += "    \"body\" : \"" + now + "\"";
		json += "  }";
		json += "}";
		EntityResource<FundTransfer> er = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			er = new EntityResource<FundTransfer>(new FundTransfer(key, jsonObject.toString()));
		}
		catch(JSONException je) {
			je.printStackTrace();
		}
		ctx.setResource(er);
		return Result.SUCCESS;
	}

	public String getMethod() {
		return HttpMethod.POST;
	}

}
