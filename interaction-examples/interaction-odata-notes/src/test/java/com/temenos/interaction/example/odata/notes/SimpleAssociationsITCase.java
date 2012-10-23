package com.temenos.interaction.example.odata.notes;

import static org.junit.Assert.*;

import java.util.List;

import javax.ws.rs.core.MediaType;

import org.core4j.Enumerable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.odata4j.consumer.ODataConsumer;
import org.odata4j.core.OEntity;
import org.odata4j.core.OLink;
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
public class SimpleAssociationsITCase extends JerseyTest {

	private final static String NOTE_ENTITYSET_NAME = "Note";
	private final static String PERSON_ENTITYSET_NAME = "Person";
	
	public SimpleAssociationsITCase() throws Exception {
		super();
	}
	
	@Before
	public void initTest() {
		// TODO make this configurable
		// test with external server 
    	webResource = Client.create().resource(Configuration.TEST_ENDPOINT_URI); 
	}

	@After
	public void tearDown() {}

	/**
	 * GET item, check link to another entity
	 */
	@Test
	public void getPersonLinksToNotes() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(Configuration.TEST_ENDPOINT_URI).build();

		OEntity person = consumer.getEntity(PERSON_ENTITYSET_NAME, 1).execute();
		Integer id = (Integer) person.getProperty("id").getValue();
		assertEquals(1, (int) id);
		assertEquals("example", person.getProperty("name").getValue());

		// there should be one link to one note for this person
		assertEquals(1, person.getLinks().size());
		assertTrue(containsLink(person.getLinks(), "Person(1)/PersonNotes"));
	}

	/**
	 * GET item, check link to another entity
	 */
	@Test
	public void getNoteLinkToPerson() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(Configuration.TEST_ENDPOINT_URI).build();

		OEntity note = consumer.getEntity(NOTE_ENTITYSET_NAME, 1).execute();
		Integer id = (Integer) note.getProperty("id").getValue();
		assertEquals(1, (int) id);
		assertEquals("example", note.getProperty("body").getValue());

		// there should be one link to one Person for this Note
		assertEquals(3, note.getLinks().size());
		assertTrue(containsLink(note.getLinks(), "Note(1)/NotePerson"));
		assertTrue(containsLink(note.getLinks(), "Note(1)"));   // link to update this note
		assertTrue(containsLink(note.getLinks(), "Note(1)"));   // link to delete this note
	}

	/**
	 * GET item, follow link to another entity
	 */
	@Test
	public void getPersonNotes() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(Configuration.TEST_ENDPOINT_URI).build();

		// GET the notes for person '1'
		Enumerable<OEntity> notes = consumer
				.getEntities(PERSON_ENTITYSET_NAME)
				.nav(1, "PersonNotes")
				.execute();

		// there should be two notes for this person
		assertEquals(2, notes.count());
		assertEquals("example", notes.first().getProperty("body").getValue());
	}

	/**
	 * GET item, follow link to another entity
	 */
	@Test
	public void getNotePerson() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(Configuration.TEST_ENDPOINT_URI).build();

		// GET the Person for Note '1'
		OEntity person = consumer
				.getEntity(NOTE_ENTITYSET_NAME, 1)
				.nav("NotePerson")
				.execute();

		// there should be one Person for this Note
		assertEquals("example", person.getProperty("name").getValue());
	}

	/**
	 * GET item, follow link to another entity
	 */
	@Test
	public void getNotePersons() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(Configuration.TEST_ENDPOINT_URI).build();

		// GET the Person for Note '1'
		Enumerable<OEntity> persons = consumer
				.getEntities(NOTE_ENTITYSET_NAME)
				.nav(1, "NotePerson")
				.execute();

		// there should be one Person for this Note
		assertEquals(1, persons.count());
		assertEquals("example", persons.first().getProperty("name").getValue());
	}

	/**
	 * GET collection, check link to another entity
	 */
	@Test
	public void getPersonsLinksToNotes() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(Configuration.TEST_ENDPOINT_URI).build();

		Enumerable<OEntity> persons = consumer
				.getEntities(PERSON_ENTITYSET_NAME).execute();

		// should be only one result, but test could be run multiple times
		assertTrue(persons.count() > 0);
		
		OEntity person = persons.first();
		Integer id = (Integer) person.getProperty("id").getValue();
		assertEquals(1, (int) id);
		assertEquals("example", person.getProperty("name").getValue());

		// there should be one link to one note for this person
		assertEquals(2, person.getLinks().size());
		assertTrue(containsLink(person.getLinks(), "Person(1)"));
		assertTrue(containsLink(person.getLinks(), "Person(1)/PersonNotes"));
		
	}

	/**
	 * Creation of entity with link to another entity
	 */
	@Test
	public void createPersonSingleNoteWithLink() throws Exception {
		ODataConsumer consumer = ODataJerseyConsumer.newBuilder(Configuration.TEST_ENDPOINT_URI).build();

		OEntity person = consumer
				.createEntity(PERSON_ENTITYSET_NAME)
				.properties(OProperties.string("name", "Noah"))
				.execute();

		Integer id = (Integer) person.getProperty("id").getValue();
		assertTrue(id > 0);
		assertEquals("Noah", person.getProperty("name").getValue());

 		OEntity note = consumer
				.createEntity(NOTE_ENTITYSET_NAME)
				.properties(OProperties.string("body", "test"))
				.link("NotePerson", person)
				.execute();

		Integer noteId = (Integer) note.getProperty("id").getValue();
		assertTrue(noteId > 0);
		assertEquals("test", note.getProperty("body").getValue());
		assertEquals(1, note.getLinks().size());
		assertTrue(containsLink(person.getLinks(), "Person(" + id + ")"));
	}

	/**
	 * Attempt a DELETE to the entity set (collection resource)
	 */
	@Test
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

	/**
	 * Attempt a PUT to the entity set (collection resource)
	 */
	@Test
	public void putPersonMethodNotAllowed() throws Exception {
		// attempt to put to the Persons root, rather than an individual
		ClientResponse response = webResource.path("/Person").type(MediaType.APPLICATION_ATOM_XML).put(ClientResponse.class, "<?xml version='1.0' encoding='utf-8'?><entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\"><title type=\"text\" /><updated>2012-04-02T10:33:39Z</updated><author><name /></author><category term=\"InteractionNoteModel.Person\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\" /><content type=\"application/xml\"><m:properties><d:name>Noah</d:name></m:properties></content></entry>");
        assertEquals(405, response.getStatus());

        assertEquals(4, response.getAllow().size());
        assertTrue(response.getAllow().contains("GET"));
        assertTrue(response.getAllow().contains("POST"));
        assertTrue(response.getAllow().contains("OPTIONS"));
        assertTrue(response.getAllow().contains("HEAD"));
	}

	private boolean containsLink(List<OLink> links, String link) {
		assert(links != null);
		boolean contains = false;
		for (OLink l : links) {
			if (l.getHref().equals(link)) {
				contains = true;
			}
		}
		return contains;
	}
	

}
