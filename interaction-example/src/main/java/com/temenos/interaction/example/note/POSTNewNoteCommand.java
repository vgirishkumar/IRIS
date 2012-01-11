package com.temenos.interaction.example.note;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.odata4j.core.OEntities;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.core.OFunctionParameter;
import org.odata4j.core.OFunctionParameters;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;
import org.odata4j.core.OProperty;
import org.odata4j.edm.EdmDataServices;
import org.odata4j.edm.EdmEntitySet;
import org.odata4j.edm.EdmFunctionImport;
import org.odata4j.edm.EdmSimpleType;
import org.odata4j.producer.BaseResponse;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.PropertyResponse;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourcePostCommand;

/**
 * Execute a POST command by calling the 'NEW' function to create then next NOTE ID.
 * @author aphethean
 */
public class POSTNewNoteCommand implements ResourcePostCommand {

	private String method;
	private String path;
	private ODataProducer producer;
	private EdmDataServices edmDataServices;

	public POSTNewNoteCommand(String method, String path, ODataProducer producer) {
		this.method = method;
		this.path = path;
		this.producer = producer;
		this.edmDataServices = producer.getMetadata();
	}

	/*
	 * Implement ResourcePostCommand
	 * @see com.temenos.interaction.core.command.ResourcePostCommand#post(java.lang.String, com.temenos.interaction.core.EntityResource)
	 */
	public RESTResponse post(String id, EntityResource resource) {
		assert(id == null || "".equals(id));

        // find the function that creates us new things
        EdmFunctionImport functionName = edmDataServices.findEdmFunctionImport("NEW");
        
		Map<String, OFunctionParameter> params = new HashMap<String, OFunctionParameter>();
		params.put("PARAM1", OFunctionParameters.create("DOMAIN_OBJECT_NAME", "NOTE"));
		//EdmFunctionImport functionName = new EdmFunctionImport("NEW", null, returnType, "POST", params);
		BaseResponse fr = producer.callFunction(functionName, params, null);
		assert(functionName.returnType == EdmSimpleType.INT64);
		
		// TODO this could either be the type we are creating and ID for, or it could just be a transient type
		EdmEntitySet noteEntitySet = edmDataServices.findEdmEntitySet(OEntityNoteRIM.ENTITY_NAME);
		OEntityKey entityKey = OEntityKey.create("new");
		List<OLink> links = new ArrayList<OLink>();
		String replacement = ((PropertyResponse)fr).getProperty().getValue().toString();
		links.add(OLinks.link("_new", "NewNote", OEntityNoteRIM.RESOURCE_PATH.replaceFirst("\\{id\\}", replacement)));
		final OEntity entity = OEntities.create(noteEntitySet, entityKey, new ArrayList<OProperty<?>>(), links);
		EntityResource er = new EntityResource(entity);
		return new RESTResponse(Response.Status.OK, er, null);
	}

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

}
