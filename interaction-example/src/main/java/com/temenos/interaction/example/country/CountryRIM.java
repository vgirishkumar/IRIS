package com.temenos.interaction.example.country;

import javax.ws.rs.Path;

import org.odata4j.producer.ODataProducer;

import com.temenos.interaction.core.state.SHARDIResourceInteractionModel;
import com.temenos.interaction.example.note.NoteProducerFactory;

@Path("/countries/{id}")
public class CountryRIM extends SHARDIResourceInteractionModel {

	private final static String RESOURCE_PATH = "/countries/{id}";
	private final GetCountryCommand getCommand = new GetCountryCommand();
	
	public CountryRIM() {
		super(RESOURCE_PATH);
		/*
		 * Not required when wired with Spring and not a Country producer at the moment
		 */
		  		NoteProducerFactory npf = new NoteProducerFactory();
		  		ODataProducer producer = npf.getFunctionsProducer();
		getCommand.setProducer(producer);
		registerGetCommand(RESOURCE_PATH, getCommand);
	}

}
