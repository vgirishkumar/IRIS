package com.temenos.interaction.example.country;

import javax.ws.rs.Path;

import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.state.SHARDIResourceInteractionModel;
import com.temenos.interaction.example.note.NoteProducerFactory;

@Path("/countries/{id}")
public class CountryRIM extends SHARDIResourceInteractionModel {

	private final static String RESOURCE_PATH = "/countries/{id}";
	
	public CountryRIM() {
		super(RESOURCE_PATH);
		/*
		 * Not required when wired with Spring and not a Country producer at the moment
		 */
  		NoteProducerFactory npf = new NoteProducerFactory();
  		ODataProducer producer = npf.getJPAProducer();
		initialise(producer);
	}
	
	public CountryRIM(ODataProducer producer) {
		super(RESOURCE_PATH);
		initialise(producer);
	}
  		
  	public void initialise(ODataProducer producer) {
  		registerGetCommand(RESOURCE_PATH, new GetCountryCommand(producer));
	}

}
