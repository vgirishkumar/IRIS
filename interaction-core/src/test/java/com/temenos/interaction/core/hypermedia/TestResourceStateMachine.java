package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.junit.Before;
import org.junit.Test;

import com.temenos.interaction.core.MultivaluedMapImpl;
import com.temenos.interaction.core.hypermedia.ASTValidation;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.web.RequestContext;

public class TestResourceStateMachine {

	@Before
	public void setup() {
		// initialise the thread local request context with requestUri and baseUri
		UriBuilder baseUri = UriBuilder.fromUri("/baseuri");
		String requestUri = "/baseuri/";
        RequestContext ctx = new RequestContext(baseUri, requestUri, null);
        RequestContext.setRequestContext(ctx);
	}

	/*
	 * Evaluate custom link relation, via the Link header.  See (see rfc5988)
	 * We return a Link if the header is set and the @{link Transition} can be found.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetLinkForCustomLinkRelation() {
		ResourceState existsState = new ResourceState("toaster", "exists", "/machines/toaster");
		ResourceState cookingState = new ResourceState("toaster", "cooking", "/machines/toaster/cooking");

		// view the resource if the toaster is cooking (could be time remaining)
		existsState.addTransition("GET", cookingState);
		// stop the toast cooking
		cookingState.addTransition("DELETE", existsState);
		// the entity for linkage mapping
		EntityResource<Object> testResponseEntity = new EntityResource<Object>(null);
		// initialise application state
		ResourceStateMachine stateMachine = new ResourceStateMachine(existsState);

		// mock the Link header
		LinkHeader linkHeader = LinkHeader.valueOf("</path>; rel=\"toaster.cooking>toaster.exists\"");

		Link targetLink = stateMachine.getLinkFromRelations(mock(MultivaluedMap.class), testResponseEntity, cookingState, linkHeader);

		assertNotNull(targetLink);
		assertEquals("/baseuri/machines/toaster", targetLink.getHref());
		assertEquals("toaster.cooking>toaster.exists", targetLink.getTitle());
	}


	/*
	 * We return a Link if a @{link Transition} for supplied method can be found.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetLinkForTargetState() {
		ResourceState existsState = new ResourceState("toaster", "exists", "/machines/toaster");
		ResourceState cookingState = new ResourceState("toaster", "cooking", "/machines/toaster/cooking");

		// view the resource if the toaster is cooking (could be time remaining)
		existsState.addTransition("GET", cookingState);
		// stop the toast cooking
		cookingState.addTransition("DELETE", existsState);
		// the entity for linkage mapping
		EntityResource<Object> testResponseEntity = new EntityResource<Object>(null);
		// initialise application state
		ResourceStateMachine stateMachine = new ResourceStateMachine(existsState);

		Link targetLink = stateMachine.getLink(mock(MultivaluedMap.class), testResponseEntity, cookingState, "DELETE");

		assertNotNull(targetLink);
		assertEquals("/baseuri/machines/toaster", targetLink.getHref());
		assertEquals("toaster.cooking>toaster.exists", targetLink.getTitle());
	}

	/*
	 * We return a Link if a @{link Transition} for supplied method can be found.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetLinkForSelfState() {
		CollectionResourceState collectionState = new CollectionResourceState("machines", "MachineView", "/machines");

		// create machines
		collectionState.addTransition("POST", collectionState);
		// the entity for linkage mapping
		EntityResource<Object> testResponseEntity = new EntityResource<Object>(null);
		// initialise application state
		ResourceStateMachine stateMachine = new ResourceStateMachine(collectionState);

		Link targetLink = stateMachine.getLink(mock(MultivaluedMap.class), testResponseEntity, collectionState, "POST");

		assertNotNull(targetLink);
		// a target link the same as our current state equates to 205 Reset Content
		assertEquals("/baseuri/machines", targetLink.getHref());
		// we use createSelfLink under the covers so link id looks like transition to self
		assertEquals("machines.MachineView>machines.MachineView", targetLink.getTitle());
	}

	/*
	 * We return a Link if a @{link Transition} for supplied method can be found.
	 * When the target state is a pseudo final state, no link will be returned.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetLinkForFinalPseudoState() {
		ResourceState existsState = new ResourceState("toaster", "exists", "/machines/toaster/{id}");
		ResourceState deletedState = new ResourceState("toaster", "cooking", null);

		// delete the toaster
		existsState.addTransition("DELETE", deletedState);
		// the entity for linkage mapping
		EntityResource<Object> testResponseEntity = new EntityResource<Object>(null);
		// initialise application state
		ResourceStateMachine stateMachine = new ResourceStateMachine(existsState);

		Link targetLink = stateMachine.getLink(mock(MultivaluedMap.class), testResponseEntity, existsState, "DELETE");

		// no target link equates to 204 No Content
		assertNull(targetLink);
	}

	@Test
	public void testStates() {
		String ENTITY_NAME = "T24CONTRACT";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState unauthorised = new ResourceState(ENTITY_NAME, "INAU", "unauthorised/{id}");
		ResourceState authorised = new ResourceState(ENTITY_NAME, "LIVE", "authorised/{id}");
		ResourceState reversed = new ResourceState(ENTITY_NAME, "RNAU", "reversed/{id}");
		ResourceState history = new ResourceState(ENTITY_NAME, "REVE", "history/{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "{id}");
	
		begin.addTransition("PUT", unauthorised);
		
		unauthorised.addTransition("PUT", unauthorised);
		unauthorised.addTransition("PUT", authorised);
		unauthorised.addTransition("DELETE", end);
		
		authorised.addTransition("PUT", history);
		
		history.addTransition("PUT", reversed);
		
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		assertEquals(6, sm.getStates().size());
		
		Set<ResourceState> testStates = new HashSet<ResourceState>();
		testStates.add(begin);
		testStates.add(unauthorised);
		testStates.add(authorised);
		testStates.add(reversed);
		testStates.add(history);
		testStates.add(end);
		for (ResourceState s : testStates) {
			assertTrue(sm.getStates().contains(s));
		}
		
		System.out.println(new ASTValidation().graph(sm));
	}
	
	/**
	 * Test {@link ResourceStateMachine#getStates() should return all states.}
	 */
	@Test
	public void testGetStates() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", null);
	
		begin.addTransition("PUT", exists);
		exists.addTransition("PUT", exists);
		exists.addTransition("DELETE", end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		Collection<ResourceState> states = sm.getStates();
		assertEquals("Number of states", 3, states.size());
		assertTrue(states.contains(begin));
		assertTrue(states.contains(exists));
		assertTrue(states.contains(end));
	}

	@Test
	public void testGetTransitions() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", null);
	
		begin.addTransition("PUT", exists);
		exists.addTransition("PUT", exists);
		exists.addTransition("DELETE", end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		Map<String,Transition> transitions = sm.getTransitionsById();
		assertEquals("Number of transistions", 3, transitions.size());
		assertNotNull(transitions.get(".begin>.exists"));
		assertNotNull(transitions.get(".exists>.exists"));
		assertNotNull(transitions.get(".exists>.end"));
	}
	
	@Test
	public void testInteractionMap() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "{id}");
	
		begin.addTransition("PUT", exists);
		exists.addTransition("PUT", exists);
		exists.addTransition("DELETE", end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);

		Map<String, Set<String>> interactionMap = sm.getInteractionMap();
		assertEquals("Number of resources", 1, interactionMap.size());
		Set<String> entrySet = interactionMap.keySet();
		assertTrue(entrySet.contains("{id}"));
		Collection<String> interactions = interactionMap.get("{id}");
		assertEquals("Number of interactions", 2, interactions.size());
		assertTrue(interactions.contains("PUT"));
		assertTrue(interactions.contains("DELETE"));
	}

	@Test
	public void testInteractions() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "{id}");
	
		begin.addTransition("PUT", exists);
		exists.addTransition("PUT", exists);
		exists.addTransition("DELETE", end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);

		Set<String> interactions = sm.getInteractions(begin);
		assertEquals("Number of interactions", 2, interactions.size());
		assertTrue(interactions.contains("PUT"));
		assertTrue(interactions.contains("DELETE"));
	}

	@Test
	public void testSubstateInteractions() {
		String ENTITY_NAME = "";
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "/entity");
		ResourceState published = new ResourceState(ENTITY_NAME, "published", "/published");
		ResourceState draft = new ResourceState(ENTITY_NAME, "draft", "/draft");
		ResourceState deleted = new ResourceState(initial, "deleted");
	
		// create draft
		initial.addTransition("PUT", draft);
		// updated draft
		draft.addTransition("PUT", draft);
		// publish
		draft.addTransition("PUT", published);
		// delete draft
		draft.addTransition("DELETE", deleted);
		// delete published
		published.addTransition("DELETE", deleted);
		
		ResourceStateMachine sm = new ResourceStateMachine(initial);

		Set<String> initialInteractions = sm.getInteractions(initial);
		assertNull("Number of interactions", initialInteractions);

		Set<String> draftInteractions = sm.getInteractions(draft);
		assertEquals("Number of interactions", 2, draftInteractions.size());
		assertTrue(draftInteractions.contains("PUT"));
		assertTrue(draftInteractions.contains("DELETE"));

		Set<String> publishInteractions = sm.getInteractions(published);
		assertEquals("Number of interactions", 2, publishInteractions.size());
		assertTrue(publishInteractions.contains("PUT"));
		assertTrue(publishInteractions.contains("DELETE"));

		Set<String> deletedInteractions = sm.getInteractions(deleted);
		assertNull("Number of interactions", deletedInteractions);

	}

	@Test
	public void testStateMap() {
		String ENTITY_NAME = "";
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "/entity");
		ResourceState published = new ResourceState(initial, "published", "/published");
		ResourceState draft = new ResourceState(initial, "draft", "/draft");
	
		// create draft
		initial.addTransition("PUT", draft);
		// updated draft
		draft.addTransition("PUT", draft);
		// publish
		draft.addTransition("PUT", published);
		// delete draft
		draft.addTransition("DELETE", ResourceState.FINAL);
		// delete published
		published.addTransition("DELETE", ResourceState.FINAL);
		
		ResourceStateMachine sm = new ResourceStateMachine(initial);

		Map<String, ResourceState> stateMap = sm.getStateMap();
		assertEquals("Number of states", 2, stateMap.size());
		Set<String> entrySet = stateMap.keySet();
		assertTrue(entrySet.contains("/entity/published"));
		assertTrue(entrySet.contains("/entity/draft"));
		assertEquals(published, stateMap.get("/entity/published"));
		assertEquals(draft, stateMap.get("/entity/draft"));
	}

	@Test
	public void testGetState() {
		String ENTITY_NAME = "";
  		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "/entity");
		ResourceState published = new ResourceState(initial, "published", "/published");
		ResourceState draft = new ResourceState(initial, "draft", "/draft");
		ResourceState deleted = new ResourceState(initial, "deleted");
	
		// create draft
		initial.addTransition("PUT", draft);
		// updated draft
		draft.addTransition("PUT", draft);
		// publish
		draft.addTransition("PUT", published);
		// delete draft
		draft.addTransition("DELETE", deleted);
		// delete published
		published.addTransition("DELETE", deleted);
		
		ResourceStateMachine sm = new ResourceStateMachine(initial);

		assertEquals(initial, sm.getState(null));
		assertEquals(published, sm.getState("/entity/published"));
		assertEquals(draft, sm.getState("/entity/draft"));
	}

	@Test(expected=AssertionError.class)
	public void testInteractionsInvalidState() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "{id}");
	
		begin.addTransition("PUT", exists);
		exists.addTransition("PUT", exists);
		exists.addTransition("DELETE", end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);

		ResourceState other = new ResourceState("other", "initial", "/other");
		sm.getInteractions(other);
	}

	@Test
	public void testTransitionToStateMachine() {
		String PROCESS_ENTITY_NAME = "process";
		String TASK_ENTITY_NAME = "task";

		// process behaviour
		ResourceState processes = new ResourceState(PROCESS_ENTITY_NAME, "processes", "/processes");
		ResourceState newProcess = new ResourceState(PROCESS_ENTITY_NAME, "new", "/new");
		// create new process
		processes.addTransition("POST", newProcess);

		// Process states
		ResourceState processInitial = new ResourceState(PROCESS_ENTITY_NAME, "initialProcess", "/processes/{id}");
		ResourceState processStarted = new ResourceState(processInitial, "started");
		ResourceState nextTask = new ResourceState(PROCESS_ENTITY_NAME,	"taskAvailable", "/nextTask");
		ResourceState processCompleted = new ResourceState(processInitial,	"completedProcess");
		// start new process
		newProcess.addTransition("PUT", processInitial);
		processInitial.addTransition("PUT", processStarted);
		// do a task
		processStarted.addTransition("GET", nextTask);
		// finish the process
		processStarted.addTransition("DELETE", processCompleted);

		ResourceStateMachine processSM = new ResourceStateMachine(processes);

		// Task states
		ResourceState taskAcquired = new ResourceState(TASK_ENTITY_NAME, "acquired", "/acquired");
		ResourceState taskComplete = new ResourceState(TASK_ENTITY_NAME, "complete", "/completed");
		ResourceState taskAbandoned = new ResourceState(taskAcquired, "abandoned");
		// abandon task
		taskAcquired.addTransition("DELETE", taskAbandoned);
		// complete task
		taskAcquired.addTransition("PUT", taskComplete);

		ResourceStateMachine taskSM = new ResourceStateMachine(taskAcquired);
		/*
		 * acquire task by a PUT to the initial state of the task state machine (acquired)
		 */
		nextTask.addTransition("PUT", taskSM);

		ResourceState home = new ResourceState("", "home", "/");
		home.addTransition("GET", processSM);
		ResourceStateMachine serviceDocumentSM = new ResourceStateMachine(home);
		
		Map<String, Set<String>> interactionMap = serviceDocumentSM.getInteractionMap();
		// no interactions for this entity
		assertEquals(0, interactionMap.size());

		// all target states, including states not for this entity (application states)
		Collection<ResourceState> targetStates = serviceDocumentSM.getInitial().getAllTargets();
		assertEquals(1, targetStates.size());
		assertEquals(10, serviceDocumentSM.getStates().size());
	}

	/*
	 * Test we do not return any links if our entity is null (not found).
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetLinksEntityNotFound() {
		ResourceState initial = new ResourceState("NOTE", "initial", "/note/{id}");
		
		// the null entity for our test
		EntityResource<Object> testResponseEntity = null;
		
		// initialise and get the application state (links)
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial);
		Collection<Link> links = stateMachine.getLinks(mock(MultivaluedMap.class), testResponseEntity, initial, null);
		assertNotNull(links);
		assertTrue(links.isEmpty());
	}

	/*
	 * We use links (hypermedia) for controlling / describing application 
	 * state.  Test we return the link to 'self' correctly for our test resource.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testGetLinksSelf() {
		String ENTITY_NAME = "NOTE";
		String resourcePath = "/notes/new";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		EntityResource<Object> testResponseEntity = new EntityResource<Object>(null);
		
		// initialise and get the application state (links)
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial);
		Collection<Link> links = stateMachine.getLinks(mock(MultivaluedMap.class), testResponseEntity, initial, null);

		assertNotNull(links);
		assertFalse(links.isEmpty());
		assertEquals(1, links.size());
		Link link = (Link) links.toArray()[0];
		assertEquals("self", link.getRel());
		assertEquals("/baseuri/notes/new", link.getHref());
		assertEquals("NOTE.initial>NOTE.initial", link.getTitle());
	}

	/*
	 * We use links (hypermedia) for controlling / describing application 
	 * state.  Test we return the link to 'self' correctly for our test 
	 * resource; in this self link we have used a path parameter.
	 */
	@Test
	public void testGetLinksSelfPathParameters() {
		String ENTITY_NAME = "NOTE";
		String resourcePath = "/notes/{id}/reviewers";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		EntityResource<Object> testResponseEntity = new EntityResource<Object>(null);
		
		/*
		 * Mock the path parameters with the default 'id' element
		 */
		MultivaluedMap<String, String> mockPathparameters = new MultivaluedMapImpl<String>();
		mockPathparameters.add("id", "123");
		
		// initialise and get the application state (links)
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial);
		Collection<Link> links = stateMachine.getLinks(mockPathparameters, testResponseEntity, initial, null);

		assertNotNull(links);
		assertFalse(links.isEmpty());
		assertEquals(1, links.size());
		Link link = (Link) links.toArray()[0];
		assertEquals("self", link.getRel());
		assertEquals("/baseuri/notes/123/reviewers", link.getHref());
		assertEquals("NOTE.initial>NOTE.initial", link.getTitle());
	}

	/*
	 * We use links (hypermedia) for controlling / describing application 
	 * state.  Test we return the link to 'self' correctly for our test resource.
	 */
	@Test
	public void testGetLinksSelfTemplate() {
		String ENTITY_NAME = "NOTE";
		String resourcePath = "/notes/{id}";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", resourcePath);
		EntityResource<Object> testResponseEntity = new EntityResource<Object>(null);
		
		/*
		 * Mock the path parameters with default 'id' path element
		 */
		MultivaluedMap<String, String> mockPathparameters = new MultivaluedMapImpl<String>();
		mockPathparameters.add("id", "123");

		// initialise and get the application state (links)
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial);
		Collection<Link> links = stateMachine.getLinks(mockPathparameters, testResponseEntity, initial, null);

		assertNotNull(links);
		assertFalse(links.isEmpty());
		assertEquals(1, links.size());
		Link link = (Link) links.toArray()[0];
		assertEquals("self", link.getRel());
		assertEquals("/baseuri/notes/123", link.getHref());
		assertEquals("NOTE.initial>NOTE.initial", link.getTitle());
	}

	/*
	 * We use links (hypermedia) for controlling / describing application 
	 * state.  Test we return the links to other resource in our state
	 * machine.
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testGetLinksOtherResources() {
		String rootResourcePath = "/";
		ResourceState initial = new ResourceState("root", "initial", rootResourcePath);
		String NOTE_ENTITY = "NOTE";
		String notesResourcePath = "/notes";
		CollectionResourceState notesResource = new CollectionResourceState(NOTE_ENTITY, "collection", notesResourcePath);
		String PERSON_ENTITY = "PERSON";
		String personResourcePath = "/persons";
		CollectionResourceState personsResource = new CollectionResourceState(PERSON_ENTITY, "collection", personResourcePath);
		
		// create the transitions (links)
		initial.addTransition("GET", notesResource);
		initial.addTransition("GET", personsResource);

		// initialise and get the application state (links)
		ResourceStateMachine stateMachine = new ResourceStateMachine(initial);
		Collection<Link> unsortedLinks = stateMachine.getLinks(mock(MultivaluedMap.class), new EntityResource<Object>(null), initial, null);

		assertNotNull(unsortedLinks);
		assertFalse(unsortedLinks.isEmpty());
		assertEquals(3, unsortedLinks.size());
		/*
		 * expect 3 links
		 * 'self'
		 * 'collection notes'
		 * 'colleciton persons'
		 */
		List<Link> links = new ArrayList<Link>(unsortedLinks);
		// sort the links so we have a predictable order for this test
		Collections.sort(links, new Comparator<Link>() {
			@Override
			public int compare(Link o1, Link o2) {
				return o1.getTitle().compareTo(o2.getTitle());
			}
			
		});
		// notes
		assertEquals("collection", links.get(0).getRel());
		assertEquals("/baseuri/notes", links.get(0).getHref());
		assertEquals("root.initial>NOTE.collection", links.get(0).getTitle());
		// persons
		assertEquals("collection", links.get(1).getRel());
		assertEquals("/baseuri/persons", links.get(1).getHref());
		assertEquals("root.initial>PERSON.collection", links.get(1).getTitle());
		// service root
		assertEquals("self", links.get(2).getRel());
		assertEquals("/baseuri/", links.get(2).getHref());
		assertEquals("root.initial>root.initial", links.get(2).getTitle());
	}

	/*
	 * We use links (hypermedia) for controlling / describing application 
	 * state.  Test we return the links for the collection itself.
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testGetLinksCollection() {
		String NOTE_ENTITY = "NOTE";
		String notesResourcePath = "/notes";
		CollectionResourceState notesResource = new CollectionResourceState(NOTE_ENTITY, "collection", notesResourcePath);
		String noteItemResourcePath = "/notes/{noteId}";
		ResourceState noteResource = new ResourceState(NOTE_ENTITY, "item", noteItemResourcePath);
		/* create the transitions (links) */
		// link to form to create new note
		notesResource.addTransition("POST", new ResourceState("stack", "new", "/notes/new", "new".split(" ")));
		/*
		 * define transition to view each item of the note collection
		 * no linkage map as target URI element (self) must exist in source entity element (also self)
		 */
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		notesResource.addTransitionForEachItem("GET", noteResource, uriLinkageMap);
		// the items of the collection
		List<EntityResource<Object>> entities = new ArrayList<EntityResource<Object>>();
		entities.add(new EntityResource<Object>(createTestNote("1")));
		entities.add(new EntityResource<Object>(createTestNote("2")));
		entities.add(new EntityResource<Object>(createTestNote("6")));
		CollectionResource<Object> testResponseEntity = new CollectionResource<Object>("notes", entities);

		// initialise and get the application state (links)
		ResourceStateMachine stateMachine = new ResourceStateMachine(notesResource, new BeanTransformer());
		Collection<Link> unsortedLinks = stateMachine.getLinks(mock(MultivaluedMap.class), testResponseEntity, notesResource, null);

		assertNotNull(unsortedLinks);
		assertFalse(unsortedLinks.isEmpty());
		assertEquals(2, unsortedLinks.size());
		/*
		 * expect 2 links - self and one to form to create new note
		 */
		List<Link> links = new ArrayList<Link>(unsortedLinks);
		// sort the links so we have a predictable order for this test
		Collections.sort(links, new Comparator<Link>() {
			@Override
			public int compare(Link o1, Link o2) {
				return o1.getTitle().compareTo(o2.getTitle());
			}
		});
		// notes resource (self)
		assertEquals("GET", links.get(0).getMethod());
		assertEquals("self", links.get(0).getRel());
		assertEquals("/baseuri/notes", links.get(0).getHref());
		assertEquals("NOTE.collection>NOTE.collection", links.get(0).getTitle());
		// new notes
		assertEquals("POST", links.get(1).getMethod());
		assertEquals("new", links.get(1).getRel());
		assertEquals("/baseuri/notes/new", links.get(1).getHref());
		assertEquals("NOTE.collection>stack.new", links.get(1).getTitle());
		
		/* collect the links defined in each entity */
		List<Link> itemLinks = new ArrayList<Link>();
		for (EntityResource<Object> entity : entities) {
			assertNotNull(entity.getLinks());
			itemLinks.addAll(entity.getLinks());
		}
		
		/*
		 * expect 3 links - one to each note for 'collection notes'
		 */
		assertFalse(itemLinks.isEmpty());
		assertEquals(3, itemLinks.size());
		// sort the links so we have a predictable order for this test
		Collections.sort(itemLinks, new Comparator<Link>() {
			@Override
			public int compare(Link o1, Link o2) {
				return o1.getTitle().compareTo(o2.getTitle());
			}
			
		});
		// link to note '1'
// TODO with better rel support we should have self and NOTE.item
//		assertEquals("self NOTE.item", links.get(0).getRel());
		assertEquals("item", itemLinks.get(0).getRel());
		assertEquals("/baseuri/notes/1", itemLinks.get(0).getHref());
		assertEquals("NOTE.collection>NOTE.item", itemLinks.get(0).getTitle());
		// link to note '2'
		assertEquals("item", itemLinks.get(1).getRel());
		assertEquals("/baseuri/notes/2", itemLinks.get(1).getHref());
		assertEquals("NOTE.collection>NOTE.item", itemLinks.get(1).getTitle());
		// link to note '6'
		assertEquals("item", itemLinks.get(2).getRel());
		assertEquals("/baseuri/notes/6", itemLinks.get(2).getHref());
		assertEquals("NOTE.collection>NOTE.item", itemLinks.get(2).getTitle());
		
	}

	/*
	 * We use links (hypermedia) for controlling / describing application 
	 * state.  Test we return the links for items in the collection.
	 */
	@SuppressWarnings({ "unchecked" })
	@Test
	public void testGetLinksCollectionItems() {
		String NOTE_ENTITY = "NOTE";
		String notesResourcePath = "/notes";
		CollectionResourceState notesResource = new CollectionResourceState(NOTE_ENTITY, "collection", notesResourcePath);
		String noteItemResourcePath = "/notes/{noteId}";
		ResourceState noteResource = new ResourceState(NOTE_ENTITY, "item", noteItemResourcePath);
		ResourceState noteFinalState = new ResourceState(NOTE_ENTITY, "final", noteItemResourcePath);
		/* create the transitions (links) */
		/*
		 * define transition to view each item of the note collection
		 * no linkage map as target URI element (self) must exist in source entity element (also self)
		 */
		Map<String, String> uriLinkageMap = new HashMap<String, String>();
		notesResource.addTransitionForEachItem("GET", noteResource, uriLinkageMap);
		notesResource.addTransitionForEachItem("DELETE", noteFinalState, uriLinkageMap);
		// the items of the collection
		List<EntityResource<Object>> entities = new ArrayList<EntityResource<Object>>();
		entities.add(new EntityResource<Object>(createTestNote("1")));
		entities.add(new EntityResource<Object>(createTestNote("2")));
		entities.add(new EntityResource<Object>(createTestNote("6")));
		CollectionResource<Object> testResponseEntity = new CollectionResource<Object>("notes", entities);
				
		// initialise and get the application state (links)
		ResourceStateMachine stateMachine = new ResourceStateMachine(notesResource, new BeanTransformer());
		Collection<Link> baseLinks = stateMachine.getLinks(mock(MultivaluedMap.class), testResponseEntity, notesResource, null);
		// just one link to self, not really testing that here
		assertEquals(1, baseLinks.size());
		
		/* collect the links defined in each entity */
		List<Link> links = new ArrayList<Link>();
		for (EntityResource<Object> entity : entities) {
			assertNotNull(entity.getLinks());
			links.addAll(entity.getLinks());
		}
		
		/*
		 * expect 6 links - one to self for each note for 'collection notes', one to DELETE each note
		 */
		assertFalse(links.isEmpty());
		assertEquals(6, links.size());
		// sort the links so we have a predictable order for this test
		Collections.sort(links, new Comparator<Link>() {
			@Override
			public int compare(Link o1, Link o2) {
				return o1.getTitle().compareTo(o2.getTitle());
			}
			
		});
		// link to DELETE note '1'
		assertEquals("item", links.get(0).getRel());
		assertEquals("/baseuri/notes/1", links.get(0).getHref());
		assertEquals("NOTE.collection>NOTE.final", links.get(0).getTitle());
		assertEquals("DELETE", links.get(0).getMethod());
		// link to DELETE note '2'
		assertEquals("item", links.get(1).getRel());
		assertEquals("/baseuri/notes/2", links.get(1).getHref());
		assertEquals("NOTE.collection>NOTE.final", links.get(1).getTitle());
		assertEquals("DELETE", links.get(1).getMethod());
		// link to DELETE note '6'
		assertEquals("item", links.get(2).getRel());
		assertEquals("/baseuri/notes/6", links.get(2).getHref());
		assertEquals("NOTE.collection>NOTE.final", links.get(2).getTitle());
		assertEquals("DELETE", links.get(0).getMethod());
		// link to GET note '1'
		assertEquals("item", links.get(3).getRel());
		assertEquals("/baseuri/notes/1", links.get(3).getHref());
		assertEquals("NOTE.collection>NOTE.item", links.get(3).getTitle());
		assertEquals("GET", links.get(3).getMethod());
		// link to GET note '2'
		assertEquals("item", links.get(4).getRel());
		assertEquals("/baseuri/notes/2", links.get(4).getHref());
		assertEquals("NOTE.collection>NOTE.item", links.get(4).getTitle());
		assertEquals("GET", links.get(4).getMethod());
		// link to GET note '6'
		assertEquals("item", links.get(5).getRel());
		assertEquals("/baseuri/notes/6", links.get(5).getHref());
		assertEquals("NOTE.collection>NOTE.item", links.get(5).getTitle());
		assertEquals("GET", links.get(5).getMethod());
	}

	@SuppressWarnings({ "unused" })
	private Object createTestNote(final String id) {
		return new Object() {
			final String noteId = id;
			public String getNoteId() {
				return noteId;
			}
		};
	}

}
