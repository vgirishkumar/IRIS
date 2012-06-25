package com.temenos.interaction.example.hateoas.banking;

import java.util.Date;
import java.util.Random;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourcePostCommand;

public class POSTFundTransferCommand implements ResourcePostCommand {

	public RESTResponse post(String id, EntityResource<?> resource) {
		assert(id == null || "".equals(id));

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
		return new RESTResponse(Response.Status.OK, er);
	}

	public String getMethod() {
		return HttpMethod.POST;
	}

}
