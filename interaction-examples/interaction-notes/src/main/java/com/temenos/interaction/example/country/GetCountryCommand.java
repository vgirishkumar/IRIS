package com.temenos.interaction.example.country;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.RESTResponse;
import com.temenos.interaction.core.command.ResourceGetCommand;

public class GetCountryCommand implements ResourceGetCommand {

	private ODataProducer producer;

	public GetCountryCommand(ODataProducer producer) {
		this.producer = producer;
	}
	
	/*
	 * Execute a command to get the country resource.
	 * 
	 * @precondition entityManager has been set to an entity manager for the 'country' domain
	 * @precondition String id is not null
	 * @postcondition resource will be set to a country resource
	 * @postcondition Response.Status will indicate whether the resource could be retrieved
	 * @invariant none
	 * @see com.temenos.interaction.core.command.ResourceGetCommand#execute(java.lang.String)
	 */
	public RESTResponse get(String id, MultivaluedMap<String, String> queryParams) {
		assert(producer != null);
		assert(id != null);
		EntityResource<Country> resource = null;
		EntityResponse entityResponse = producer.getEntity(CountryRIM.ENTITY_NAME, OEntityKey.create(id), null);
		if (entityResponse != null && entityResponse.getEntity() != null) {
			OEntity entity = entityResponse.getEntity();
			resource = new EntityResource<Country>(new Country(entity));
		}
		return new RESTResponse(resource == null ? Response.Status.NOT_FOUND : Response.Status.OK, resource);
	}

	public void setProducer(ODataProducer producer) {
		this.producer = producer;
	}
	
	public Set<String> getValidNextStates() {
		Set<String> nextStates = new HashSet<String>();
		nextStates.add("AUTHORISE");
		nextStates.add("DELETE");
		nextStates.add("GET");
		return nextStates;
	}

}
