package com.temenos.interaction.core.hypermedia;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.temenos.interaction.core.hypermedia.ASTValidation;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;

public class TestASTValidation {

	@Test
	public void testValidateStatesValid() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "{id}");
	
		begin.addTransition("PUT", exists);		
		exists.addTransition("DELETE", end);
		
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		ASTValidation v = new ASTValidation();
		assertTrue(v.validate(states, sm));	
	}

	@Test
	public void testValidateStatesUnreachable() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", "{id}");
	
		begin.addTransition("PUT", exists);		
		exists.addTransition("DELETE", end);
		
		ResourceState unreachableState = new ResourceState(ENTITY_NAME, "unreachable", "");
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(unreachableState);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		ASTValidation v = new ASTValidation();
		assertFalse(v.validate(states, sm));	
	}

	@Test
	public void testDOT204NoContent() {
		String expected = "digraph G {\n    Ginitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    Gexists[label=\"G.exists /entities/{id}\"]\n    Gdeleted[label=\"G.deleted /entities/{id}\"]\n    Ginitial->Gexists[label=\"PUT /entities/{id} (0)\"]\n    Gexists->Gdeleted[label=\"DELETE /entities/{id} (0)\"]\n    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n    Gdeleted->final[label=\"\"]\n}";
		
		String ENTITY_NAME = "G";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "/");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "/entities/{id}");
		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted", "/entities/{id}");
	
		initial.addTransition("PUT", exists);
		// a transition to a final state will result in 204 (No Content) at runtime
		exists.addTransition("DELETE", deleted);
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		ASTValidation v = new ASTValidation();
		String result = v.graph(sm);
		System.out.println("DOTNoContent: \n" + result);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOT205ResetContent() {
		String expected = "digraph G {\n    Ginitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    Gexists[label=\"G.exists /entities/{id}\"]\n    Gdeleted[label=\"G.deleted /entities/{id}\"]\n    Ginitial->Gdeleted[label=\"DELETE /entities/{id} (1)\"]\n    Ginitial->Gexists[label=\"GET /entities/{id} (1)\"]\n    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n    Gexists->final[label=\"\"]\n    Gdeleted->Ginitial[style=\"dotted\"]\n}";
		
		String ENTITY_NAME = "G";
		CollectionResourceState initial = new CollectionResourceState(ENTITY_NAME, "initial", "/entities");
		ResourceState exists = new ResourceState(initial, "exists", "/{id}");
		ResourceState deleted = new ResourceState(initial, "deleted", "/{id}");
	
		initial.addTransitionForEachItem("GET", exists, null);		
		// add an auto transition from deleted state to a different state
		deleted.addTransition(initial);
		// 205, as the auto transition is to the same state we expect to see a 205 (Reset Content) at runtime
		initial.addTransitionForEachItem("DELETE", deleted, null, Transition.FOR_EACH);
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		ASTValidation v = new ASTValidation();
		String result = v.graph(sm);
		System.out.println("DOTResetContent: \n" + result);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOT303SeeOther() {
		String expected = "digraph G {\n    Ginitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    Gexists[label=\"G.exists /entities/{id}\"]\n    Gdeleted[label=\"G.deleted /entities/{id}\"]\n    Ginitial->Gexists[label=\"GET /entities/{id} (1)\"]\n    Gexists->Gdeleted[label=\"DELETE /entities/{id} (0)\"]\n    Gdeleted->Ginitial[style=\"dotted\"]\n}";
		
		String ENTITY_NAME = "G";
		CollectionResourceState initial = new CollectionResourceState(ENTITY_NAME, "initial", "/entities");
		ResourceState exists = new ResourceState(initial, "exists", "/{id}");
		ResourceState deleted = new ResourceState(initial, "deleted", "/{id}");
	
		initial.addTransitionForEachItem("GET", exists, null);
		// add an auto transition from deleted state to a different state
		deleted.addTransition(initial);
		// 303, as the auto transition is to a different state we expect to see a 303 (Redirect) at runtime
		exists.addTransition("DELETE", deleted);
		
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		ASTValidation v = new ASTValidation();
		String result = v.graph(sm);
		System.out.println("DOTSeeOther: \n" + result);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOTMultipleFinalStates() {
		String expected = "digraph CRUD_ENTITY {\n    CRUD_ENTITYinitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    CRUD_ENTITYexists[label=\"CRUD_ENTITY.exists\"]\n    CRUD_ENTITYdeleted[label=\"CRUD_ENTITY.deleted\"]\n    CRUD_ENTITYarchived[label=\"CRUD_ENTITY.archived /archived\"]\n"
			+ "    CRUD_ENTITYinitial->CRUD_ENTITYexists[label=\"PUT (0)\"]\n"
			+ "    CRUD_ENTITYexists->CRUD_ENTITYdeleted[label=\"DELETE (0)\"]\n"
			+ "    CRUD_ENTITYexists->CRUD_ENTITYarchived[label=\"PUT /archived (0)\"]\n"
			+ "    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    CRUD_ENTITYdeleted->final[label=\"\"]\n"
			+ "    final1[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    CRUD_ENTITYarchived->final1[label=\"\"]\n}";
		
		String ENTITY_NAME = "CRUD_ENTITY";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "");
		ResourceState exists = new ResourceState(initial, "exists", null);
		ResourceState archived = new ResourceState(ENTITY_NAME, "archived", "/archived");
		ResourceState deleted = new ResourceState(initial, "deleted", null);
	
		initial.addTransition("PUT", exists);		
		exists.addTransition("PUT", archived);
		exists.addTransition("DELETE", deleted);
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		ASTValidation v = new ASTValidation();
		String result = v.graph(sm);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOTTransitionToStateMachine() {
		ResourceState home = new ResourceState("SERVICE_ROOT", "home", "");
		ResourceStateMachine processSM = getProcessSM();
		home.addTransition("GET", processSM);
		ResourceStateMachine serviceDocumentSM = new ResourceStateMachine(home);
		
		String result = new ASTValidation().graph(serviceDocumentSM);
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
				+ "    taskacquired->taskcomplete[label=\"PUT /completed (0)\"]\n"
				+ "    taskacquired->taskabandoned[label=\"DELETE /acquired (0)\"]\n"
				+ "    final1[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
				+ "    taskabandoned->final1[label=\"\"]\n"
				+ "    processtaskAvailable->taskacquired[label=\"PUT /acquired (0)\"]\n"
				+ "    processprocesses->processnew[label=\"POST /processes/new (0)\"]\n"
				+ "    processnew->processinitialProcess[label=\"PUT /processes/{id} (0)\"]\n"
				+ "    processinitialProcess->processtaskAvailable[label=\"GET /processes/nextTask (0)\"]\n"
				+ "    processinitialProcess->processcompletedProcess[label=\"DELETE /processes/{id} (0)\"]\n"
				+ "    final2[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			    + "    processcompletedProcess->final2[label=\"\"]\n"
			    + "    SERVICE_ROOThome->processprocesses[label=\"GET /processes (0)\"]\n"
				+ "}";
		assertEquals(expected, result);
	}

	@Test
	public void testDOTGraphOneLevel() {

		ResourceState home = new ResourceState("SERVICE_ROOT", "home", "");
		// processes
		ResourceStateMachine processSM = getProcessSM();
		home.addTransition("GET", processSM);
		// notes
		ResourceStateMachine notesSM = new ResourceStateMachine(new ResourceState("notes", "initial", "/notes"));
		home.addTransition("GET", notesSM);
		
		ResourceStateMachine serviceDocumentSM = new ResourceStateMachine(home);
		String expected = "digraph SERVICE_ROOT {\n"
				+ "    SERVICE_ROOThome[shape=circle, width=.25, label=\"\", color=black, style=filled]\n"
			    + "    notesinitial[shape=square, width=.25, label=\"notes.initial\"]\n"
			    + "    processprocesses[shape=square, width=.25, label=\"process.processes\"]\n"
			    + "    taskacquired[shape=square, width=.25, label=\"task.acquired\"]\n"
				+ "    SERVICE_ROOThome->notesinitial[label=\"GET /notes (0)\"]\n"
				+ "    SERVICE_ROOThome->processprocesses[label=\"GET /processes (0)\"]\n"
				+ "}";
		assertEquals(expected, new ASTValidation().graphEntityNextStates(serviceDocumentSM));
	}

	private ResourceStateMachine getProcessSM() {
		String PROCESS_ENTITY_NAME = "process";
		// process behaviour
		ResourceState processes = new ResourceState(PROCESS_ENTITY_NAME, "processes", "/processes");
		ResourceState newProcess = new ResourceState(PROCESS_ENTITY_NAME, "new", "/processes/new");
		// create new process
		processes.addTransition("POST", newProcess);

		// Process states
		ResourceState processInitial = new ResourceState(PROCESS_ENTITY_NAME, "initialProcess", "/processes/{id}");
		ResourceState nextTask = new ResourceState(PROCESS_ENTITY_NAME,	"taskAvailable", "/processes/nextTask");
		ResourceState processCompleted = new ResourceState(PROCESS_ENTITY_NAME, "completedProcess", "/processes/{id}");
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
		ResourceState taskAcquired = new ResourceState(TASK_ENTITY_NAME, "acquired", "/acquired");
		ResourceState taskComplete = new ResourceState(TASK_ENTITY_NAME, "complete", "/completed");
		ResourceState taskAbandoned = new ResourceState(TASK_ENTITY_NAME, "abandoned", "/acquired");
		// abandon task
		taskAcquired.addTransition("DELETE", taskAbandoned);
		// complete task
		taskAcquired.addTransition("PUT", taskComplete);

		return new ResourceStateMachine(taskAcquired);
	}
}
