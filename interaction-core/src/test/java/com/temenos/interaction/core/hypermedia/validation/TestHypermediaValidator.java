package com.temenos.interaction.core.hypermedia.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression;

public class TestHypermediaValidator {

	@Test
	public void testValidateStatesValid() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", new ArrayList<Action>(), "{id}");
	
		begin.addTransition("PUT", exists);		
		exists.addTransition("DELETE", end);
		
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		HypermediaValidator v = HypermediaValidator.createValidator(sm);
		assertTrue(v.validate(states, sm));	
	}

	@Test
	public void testValidateStatesUnreachable() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", new ArrayList<Action>(), "{id}");
	
		begin.addTransition("PUT", exists);		
		exists.addTransition("DELETE", end);
		
		ResourceState unreachableState = new ResourceState(ENTITY_NAME, "unreachable", new ArrayList<Action>(), "/unreachable");
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(unreachableState);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		HypermediaValidator v = HypermediaValidator.createValidator(sm);
		assertFalse(v.validate(states, sm));	
	}

	@Test
	public void testDOTExceptionResource() {
		String expected = "digraph G {\n" +
				"    Ginitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n" +
				"    EXCEPTIONexception[label=\"EXCEPTION.exception\"]\n" +
				"    Gexists[label=\"G.exists /entities/{id}\"]\n" +
				"    Ginitial->Gexists[label=\"PUT /entities/{id}\"]\n" +
				"    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n" +
				"    Gexists->final[label=\"\"]\n}";
		
		String ENTITY_NAME = "G";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "/entities/{id}");
		ResourceState exception = new ResourceState("EXCEPTION", "exception", new ArrayList<Action>(), "/");
		exception.setException(true);
	
		initial.addTransition("PUT", exists);
				
		ResourceStateMachine sm = new ResourceStateMachine(initial, exception, null);
		HypermediaValidator v = HypermediaValidator.createValidator(sm);
		String result = v.graph();
		System.out.println("DOTException: \n" + result);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOTTransitionEval() {
		String expected = "digraph G {\n" +
				"    Ginitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n" +
				"    Gother[label=\"G.other /entities/{id}\"]\n" +
				"    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n" +
				"    Gother->final[label=\"\"]\n" +
				"    Ginitial->Gother[label=\"PUT /entities/{id} (OK(other))\"]\n" +
				"}";
		
		String ENTITY_NAME = "G";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/");
		ResourceState other = new ResourceState(ENTITY_NAME, "other", new ArrayList<Action>(), "/entities/{id}");
	
		initial.addTransition("PUT", other, new ResourceGETExpression("other", ResourceGETExpression.Function.OK));
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		HypermediaValidator v = HypermediaValidator.createValidator(sm);
		String result = v.graph();
		System.out.println("DOTTransitionEval: \n" + result);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOT204NoContent() {
		String expected = "digraph G {\n" +
				"    Ginitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n" +
				"    Gexists[label=\"G.exists /entities/{id}\"]\n" +
				"    Gdeleted[label=\"G.deleted /entities/{id}\"]\n" +
				"    Ginitial->Gexists[label=\"PUT /entities/{id}\"]\n" +
				"    Gexists->Gdeleted[label=\"DELETE /entities/{id}\"]\n" +
				"    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n    Gdeleted->final[label=\"\"]\n}";
		
		String ENTITY_NAME = "G";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "/entities/{id}");
		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted", new ArrayList<Action>(), "/entities/{id}");
	
		initial.addTransition("PUT", exists);
		// a transition to a final state will result in 204 (No Content) at runtime
		exists.addTransition("DELETE", deleted);
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		HypermediaValidator v = HypermediaValidator.createValidator(sm);
		String result = v.graph();
		System.out.println("DOTNoContent: \n" + result);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOT205ResetContent() {
		String expected = "digraph G {\n" +
				"    Ginitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n" +
				"    Gexists[label=\"G.exists /entities/{id}\"]\n" +
				"    Gdeleted[label=\"G.deleted /entities/{id}\"]\n" +
				"    Ginitial->Gdeleted[label=\"*DELETE /entities/{id}\"]\n" +
				"    Ginitial->Gexists[label=\"*GET /entities/{id}\"]\n" +
				"    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n    Gexists->final[label=\"\"]\n    Gdeleted->Ginitial[style=\"dotted\"]\n}";
		
		String ENTITY_NAME = "G";
		CollectionResourceState initial = new CollectionResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/entities");
		ResourceState exists = new ResourceState(initial, "exists", new ArrayList<Action>(), "/{id}");
		ResourceState deleted = new ResourceState(initial, "deleted", new ArrayList<Action>(), "/{id}");
	
		initial.addTransitionForEachItem("GET", exists, null);		
		// add an auto transition from deleted state to a different state
		deleted.addTransition(initial);
		// 205, as the auto transition is to the same state we expect to see a 205 (Reset Content) at runtime
		initial.addTransitionForEachItem("DELETE", deleted, null, Transition.FOR_EACH);
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		HypermediaValidator v = HypermediaValidator.createValidator(sm);
		String result = v.graph();
		System.out.println("DOTResetContent: \n" + result);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOT303SeeOther() {
		String expected = "digraph G {\n" +
				"    Ginitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n" +
				"    Gexists[label=\"G.exists /entities/{id}\"]\n" +
				"    Gdeleted[label=\"G.deleted /entities/{id}\"]\n" +
				"    Ginitial->Gexists[label=\"*GET /entities/{id}\"]\n" +
				"    Gexists->Gdeleted[label=\"DELETE /entities/{id}\"]\n" +
				"    Gdeleted->Ginitial[style=\"dotted\"]\n}";
		
		String ENTITY_NAME = "G";
		CollectionResourceState initial = new CollectionResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/entities");
		ResourceState exists = new ResourceState(initial, "exists", new ArrayList<Action>(), "/{id}");
		ResourceState deleted = new ResourceState(initial, "deleted", new ArrayList<Action>(), "/{id}");
	
		initial.addTransitionForEachItem("GET", exists, null);
		// add an auto transition from deleted state to a different state
		deleted.addTransition(initial);
		// 303, as the auto transition is to a different state we expect to see a 303 (Redirect) at runtime
		exists.addTransition("DELETE", deleted);
		
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		HypermediaValidator v = HypermediaValidator.createValidator(sm);
		String result = v.graph();
		System.out.println("DOTSeeOther: \n" + result);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOTMultipleFinalStates() {
		String expected = "digraph CRUD_ENTITY {\n" +
				"    CRUD_ENTITYinitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n" +
				"    CRUD_ENTITYexists[label=\"CRUD_ENTITY.exists /\"]\n" +
				"    CRUD_ENTITYdeleted[label=\"CRUD_ENTITY.deleted /\"]\n" +
				"    CRUD_ENTITYarchived[label=\"CRUD_ENTITY.archived /archived\"]\n"
			+ "    CRUD_ENTITYinitial->CRUD_ENTITYexists[label=\"PUT /\"]\n"
			+ "    CRUD_ENTITYexists->CRUD_ENTITYdeleted[label=\"DELETE /\"]\n"
			+ "    CRUD_ENTITYexists->CRUD_ENTITYarchived[label=\"PUT /archived\"]\n"
			+ "    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    CRUD_ENTITYdeleted->final[label=\"\"]\n"
			+ "    final1[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    CRUD_ENTITYarchived->final1[label=\"\"]\n}";
		
		String ENTITY_NAME = "CRUD_ENTITY";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/");
		ResourceState exists = new ResourceState(initial, "exists", new ArrayList<Action>(), null);
		ResourceState archived = new ResourceState(ENTITY_NAME, "archived", new ArrayList<Action>(), "/archived");
		ResourceState deleted = new ResourceState(initial, "deleted", new ArrayList<Action>(), null);
	
		initial.addTransition("PUT", exists);		
		exists.addTransition("PUT", archived);
		exists.addTransition("DELETE", deleted);
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		HypermediaValidator v = HypermediaValidator.createValidator(sm);
		String result = v.graph();
		assertEquals(expected, result);	
	}

	@Test
	public void testDOTTransitionToStateMachine() {
		ResourceState home = new ResourceState("SERVICE_ROOT", "home", new ArrayList<Action>(), "/");
		ResourceStateMachine processSM = getProcessSM();
		home.addTransition("GET", processSM);
		ResourceStateMachine serviceDocumentSM = new ResourceStateMachine(home);
		
		String result = HypermediaValidator.createValidator(serviceDocumentSM).graph();
		System.out.println("DOTTransitionToStateMachine: \n" + result);

		String expected = "digraph SERVICE_ROOT {\n"
				+ "    SERVICE_ROOThome[shape=circle, width=.25, label=\"\", color=black, style=filled]\n"
				+ "    taskcomplete[label=\"task.complete /completed\"]\n"
				+ "    taskacquired[label=\"task.acquired /acquired\"]\n"
				+ "    taskabandoned[label=\"task.abandoned /acquired\"]\n"
				+ "    processtaskAvailable[label=\"process.taskAvailable /processes/nextTask\"]\n"
				+ "    processprocesses[label=\"process.processes /processes\"]\n"
				+ "    processnew[label=\"process.new /processes/new\"]\n"
				+ "    processinitialProcess[label=\"process.initialProcess /processes/{id}\"]\n"
				+ "    processcompletedProcess[label=\"process.completedProcess /processes/{id}\"]\n"
				+ "    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
				+ "    taskcomplete->final[label=\"\"]\n"
				+ "    taskacquired->taskcomplete[label=\"PUT /completed\"]\n"
				+ "    taskacquired->taskabandoned[label=\"DELETE /acquired\"]\n"
				+ "    final1[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
				+ "    taskabandoned->final1[label=\"\"]\n"
				+ "    processtaskAvailable->taskacquired[label=\"PUT /acquired\"]\n"
				+ "    processprocesses->processnew[label=\"POST /processes/new\"]\n"
				+ "    processnew->processinitialProcess[label=\"PUT /processes/{id}\"]\n"
				+ "    processinitialProcess->processtaskAvailable[label=\"GET /processes/nextTask\"]\n"
				+ "    processinitialProcess->processcompletedProcess[label=\"DELETE /processes/{id}\"]\n"
				+ "    final2[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			    + "    processcompletedProcess->final2[label=\"\"]\n"
			    + "    SERVICE_ROOThome->processprocesses[label=\"GET /processes\"]\n"
				+ "}";
		assertEquals(expected, result);
	}

	@Test
	public void testDOTGraphOneLevel() {

		ResourceState home = new ResourceState("SERVICE_ROOT", "home", new ArrayList<Action>(), "/");
		// processes
		ResourceStateMachine processSM = getProcessSM();
		home.addTransition("GET", processSM);
		// notes
		ResourceStateMachine notesSM = new ResourceStateMachine(new ResourceState("notes", "initial", new ArrayList<Action>(), "/notes"));
		home.addTransition("GET", notesSM);
		
		ResourceStateMachine serviceDocumentSM = new ResourceStateMachine(home);
		String expected = "digraph SERVICE_ROOT {\n"
				+ "    SERVICE_ROOThome[shape=circle, width=.25, label=\"\", color=black, style=filled]\n"
			    + "    notesinitial[shape=square, width=.25, label=\"notes.initial\"]\n"
			    + "    processprocesses[shape=square, width=.25, label=\"process.processes\"]\n"
			    + "    taskacquired[shape=square, width=.25, label=\"task.acquired\"]\n"
				+ "    SERVICE_ROOThome->notesinitial[label=\"GET /notes\"]\n"
				+ "    SERVICE_ROOThome->processprocesses[label=\"GET /processes\"]\n"
				+ "}";
		assertEquals(expected, HypermediaValidator.createValidator(serviceDocumentSM).graphEntityNextStates());
	}

	private ResourceStateMachine getProcessSM() {
		String PROCESS_ENTITY_NAME = "process";
		// process behaviour
		ResourceState processes = new ResourceState(PROCESS_ENTITY_NAME, "processes", new ArrayList<Action>(), "/processes");
		ResourceState newProcess = new ResourceState(PROCESS_ENTITY_NAME, "new", new ArrayList<Action>(), "/processes/new");
		// create new process
		processes.addTransition("POST", newProcess);

		// Process states
		ResourceState processInitial = new ResourceState(PROCESS_ENTITY_NAME, "initialProcess", new ArrayList<Action>(), "/processes/{id}");
		ResourceState nextTask = new ResourceState(PROCESS_ENTITY_NAME,	"taskAvailable", new ArrayList<Action>(), "/processes/nextTask");
		ResourceState processCompleted = new ResourceState(PROCESS_ENTITY_NAME, "completedProcess", new ArrayList<Action>(), "/processes/{id}");
		// start new process
		newProcess.addTransition("PUT", processInitial);
		// do a task
		processInitial.addTransition("GET", nextTask);
		// finish the process
		processInitial.addTransition("DELETE", processCompleted);

		/*
		 * acquire task by a PUT to the initial state of the task state machine (acquired)
		 */
		ResourceStateMachine taskSM = getTaskSM();
		nextTask.addTransition("PUT", taskSM);

		return new ResourceStateMachine(processes);
	}
	
	private ResourceStateMachine getTaskSM() {
		String TASK_ENTITY_NAME = "task";
		// Task states
		ResourceState taskAcquired = new ResourceState(TASK_ENTITY_NAME, "acquired", new ArrayList<Action>(), "/acquired");
		ResourceState taskComplete = new ResourceState(TASK_ENTITY_NAME, "complete", new ArrayList<Action>(), "/completed");
		ResourceState taskAbandoned = new ResourceState(TASK_ENTITY_NAME, "abandoned", new ArrayList<Action>(), "/acquired");
		// abandon task
		taskAcquired.addTransition("DELETE", taskAbandoned);
		// complete task
		taskAcquired.addTransition("PUT", taskComplete);

		return new ResourceStateMachine(taskAcquired);
	}
}
