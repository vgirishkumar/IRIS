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
	public void testDOT() {
		String expected = "digraph G {\n    Ginitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    Gexists[label=\"G.exists\"]\n    Gdeleted[label=\"G.deleted\"]\n    Ginitial->Gexists[label=\"PUT {id}\"]\n    Gexists->Gdeleted[label=\"DELETE {id}\"]\n    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n    Gdeleted->final[label=\"\"]\n}";
		
		String ENTITY_NAME = "G";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", "{id}");
		ResourceState deleted = new ResourceState(ENTITY_NAME, "deleted", "{id}");
	
		initial.addTransition("PUT", exists);		
		exists.addTransition("DELETE", deleted);
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		ASTValidation v = new ASTValidation();
		String result = v.graph(sm);
		assertEquals(expected, result);	
	}

	@Test
	public void testDOTMultipleFinalStates() {
		String expected = "digraph CRUD_ENTITY {\n    CRUD_ENTITYinitial[shape=circle, width=.25, label=\"\", color=black, style=filled]\n    CRUD_ENTITYexists[label=\"CRUD_ENTITY.exists\"]\n    CRUD_ENTITYdeleted[label=\"CRUD_ENTITY.deleted\"]\n    CRUD_ENTITYarchived[label=\"CRUD_ENTITY.archived\"]\n"
			+ "    CRUD_ENTITYinitial->CRUD_ENTITYexists[label=\"PUT\"]\n"
			+ "    CRUD_ENTITYexists->CRUD_ENTITYdeleted[label=\"DELETE\"]\n"
			+ "    CRUD_ENTITYexists->CRUD_ENTITYarchived[label=\"PUT /archived\"]\n"
			+ "    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    CRUD_ENTITYdeleted->final[label=\"\"]\n"
			+ "    final1[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    CRUD_ENTITYarchived->final1[label=\"\"]\n}";
		
		String ENTITY_NAME = "CRUD_ENTITY";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", "");
		ResourceState exists = new ResourceState(initial, "exists");
		ResourceState archived = new ResourceState(ENTITY_NAME, "archived", "/archived");
		ResourceState deleted = new ResourceState(initial, "deleted");
	
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
		
		System.out.println(new ASTValidation().graph(serviceDocumentSM));

		String expected = "digraph SERVICE_ROOT {\n"
				+ "    SERVICE_ROOThome[shape=circle, width=.25, label=\"\", color=black, style=filled]\n"
				+ "    taskcomplete[label=\"task.complete\"]\n"
				+ "    taskacquired[label=\"task.acquired\"]\n"
				+ "    taskabandoned[label=\"task.abandoned\"]\n"
				+ "    processtaskAvailable[label=\"process.taskAvailable\"]\n"
				+ "    processprocesses[label=\"process.processes\"]\n"
				+ "    processnew[label=\"process.new\"]\n"
				+ "    processinitialProcess[label=\"process.initialProcess\"]\n"
				+ "    processcompletedProcess[label=\"process.completedProcess\"]\n"
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
		assertEquals(expected, new ASTValidation().graph(serviceDocumentSM));
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
				+ "    SERVICE_ROOThome->notesinitial[label=\"GET /notes\"]\n"
				+ "    SERVICE_ROOThome->processprocesses[label=\"GET /processes\"]\n"
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
		ResourceState processCompleted = new ResourceState(processInitial, "completedProcess");
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
		ResourceState taskAbandoned = new ResourceState(taskAcquired, "abandoned");
		// abandon task
		taskAcquired.addTransition("DELETE", taskAbandoned);
		// complete task
		taskAcquired.addTransition("PUT", taskComplete);

		return new ResourceStateMachine(taskAcquired);
	}
}
