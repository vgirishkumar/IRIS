package com.temenos.interaction.example.integtest.utils;

import org.junit.Test;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.resources.ODataProducerProvider;

import com.temenos.interaction.example.note.NoteProducerFactory;

public class TestJPAProducerExample {

	protected static final String endpointUri = "http://localhost:8810/interaction/Note.svc/";

	@Test
	public void testJPAProducer() {
		NoteProducerFactory factory = new NoteProducerFactory();
		TestDBUtils.fillNoteDatabase();

		ODataProducer producer = factory.getJPAProducer();
		ODataProducerProvider.setInstance(producer);
		JerseyServerUtil.hostODataServer(endpointUri);
	}
}
