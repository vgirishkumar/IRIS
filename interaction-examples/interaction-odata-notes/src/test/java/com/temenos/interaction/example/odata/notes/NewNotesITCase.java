package com.temenos.interaction.example.odata.notes;

import static org.junit.Assert.*;

import javax.ws.rs.core.MediaType;

import org.core4j.Enumerable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OProperties;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.test.framework.JerseyTest;

/**
 * This test ensures that we can create OData entities that have 
 * a simply association (link) to another entity.
 * 
 * @author aphethean
 */
public class NewNotesITCase extends JerseyTest {

	protected static final String endpointUri = "http://localhost:8080/example/interaction-odata-notes.svc/";

	public NewNotesITCase() throws Exception {
		super();
	}
	
	@Before
	public void initTest() {
		// TODO make this configurable
		// test with external server 
    	webResource = Client.create().resource(endpointUri); 
	}

	@After
	public void tearDown() {}

	@Test
	/**
	 * GET collection, check link to another entity
	 */
	public void getPersonsLinksToNotes() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();

		Enumerable<OEntity> persons = consumer
				.getEntities("Person").execute();

		// should be only one result, but test could be run multiple times
		assertTrue(persons.count() > 0);
		
		OEntity person = persons.first();
		Integer id = (Integer) person.getProperty("Id").getValue();
		assertEquals(1, (int) id);
		assertEquals("example", person.getProperty("name").getValue());

		// there should be one link to one note for this person
		assertEquals(1, person.getLinks().size());
		assertEquals("Person(1)/Note", person.getLinks().get(0).getHref());
		
	}

//	@Test
	/**
	 * Creation of entity with link to another entity
	 */
	public void createPersonSingleNote() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(endpointUri).build();

		OEntity person = consumer
				.createEntity("Person")
				.properties(OProperties.string("name", "Noah"))
				.execute();

		Integer id = (Integer) person.getProperty("Id").getValue();
		assertTrue(id > 0);
		assertEquals("Noah", person.getProperty("name").getValue());

		OEntity note = consumer
				.createEntity("Note")
				.properties(OProperties.string("body", "test"))
				.link("person", person)
				.execute();

		Integer noteId = (Integer) note.getProperty("Id").getValue();
		assertTrue(noteId > 0);
		assertEquals("test", note.getProperty("body").getValue());
		assertEquals(1, note.getLinks().size());

	}

	/*
	 *  TODO enable test when we start to use the resource state machine in the mockresponders
	 *  At the moment we use the pseudo state machine which simple adds the requirement for all 
	 *  http method interaction, but then we bind noop command as the implementation.
	 */
//	@Test
	/**
	 * Attempt a DELETE to the entity set (collection resource)
	 */
	public void deletePersonMethodNotAllowed() throws Exception {
		// attempt to delete the Person root, rather than an individual
		ClientResponse response = webResource.path("/Person").delete(ClientResponse.class);
        assertEquals(405, response.getStatus());

        assertEquals(4, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("POST"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

	/*
	 *  TODO enable test when we start to use the resource state machine in the mockresponders
	 *  At the moment we use the pseudo state machine which simple adds the requirement for all 
	 *  http method interaction, but then we bind noop command as the implementation.
	 */
//	@Test
	/**
	 * Attempt a PUT to the entity set (collection resource)
	 */
	public void putPersonMethodNotAllowed() throws Exception {
		// attempt to delete the Person root, rather than an individual
		ClientResponse response = webResource.path("/Person").type(MediaType.APPLICATION_ATOM_XML).put(ClientResponse.class, "<?xml version='1.0' encoding='utf-8'?><entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><title type=\"text\" /><updated>2012-04-02T10:33:39Z</updated><author><name /></author><category term=\"InteractionNoteModel.Person\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\" /><content type=\"application/xml\"><m:properties><d:name>Noah</d:name></m:properties></content></entry>");
        assertEquals(405, response.getStatus());

        assertEquals(4, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("POST"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

}
