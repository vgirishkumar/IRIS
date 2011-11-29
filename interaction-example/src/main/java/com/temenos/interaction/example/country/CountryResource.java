package com.temenos.interaction.example.country;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;

import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
import org.odata4j.core.OLinks;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.state.SHARDIResourceInteractionModel;

@Path("/countries/{id}")
public class CountryResource extends SHARDIResourceInteractionModel implements EntityResource {

	private final static String RESOURCE_PATH = "/countries/{id}";
	private final GetCountryCommand getCommand = new GetCountryCommand();
	
	private OEntity entity;
		
	public CountryResource(OEntity entity) {
		super(null);
		this.entity = entity;
	}
	
	public CountryResource() {
		super(RESOURCE_PATH);
//		getCommand.setProducer(new DummyODataProducer());
		registerGetCommand(RESOURCE_PATH, getCommand);
	}
	
	public OEntity getEntity() {
		return entity;
	}

	public Set<OLink> getLinks() {
		Set<OLink> links = new HashSet<OLink>();
		// add the links that should be represented in the resource (embedded)
		links.add(OLinks.relatedEntityInline("/metadata/currency", "currency", "/currency/" + entity.getProperty("currency"), loadCurrency()));
		// add the links to related resources ('self' might be related, or it might be special)
		links.add(OLinks.relatedEntity("/metadata/country", "_self", "/country/" + entity.getEntityKey()));
		// add the links that can change this resources state
		links.add(OLinks.link("/metadata/authorise", "AUTHORISE", "/country/" + entity.getEntityKey() + "/authorise"));

		return links;
	}

	private OEntity loadCurrency() {
		return null;
	}
}
