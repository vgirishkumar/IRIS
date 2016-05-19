package com.temenos.interaction.core.hypermedia.validation;

/*
 * #%L
 * interaction-core
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.temenos.interaction.core.entity.EntityMetadata;
import com.temenos.interaction.core.entity.Metadata;
import com.temenos.interaction.core.hypermedia.Action;
import com.temenos.interaction.core.hypermedia.CollectionResourceState;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.hypermedia.ResourceStateMachine;
import com.temenos.interaction.core.hypermedia.Transition;
import com.temenos.interaction.core.hypermedia.expression.Expression;
import com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression;
import com.temenos.interaction.core.hypermedia.expression.SimpleLogicalExpressionEvaluator;

public class TestHypermediaValidator {

	@Test
	public void testValidateStatesValid() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", new ArrayList<Action>(), "{id}");
	
		begin.addTransition(new Transition.Builder().method("PUT").target(exists).build());		
		exists.addTransition(new Transition.Builder().method("DELETE").target(end).build());
		
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata(""));
		HypermediaValidator v = HypermediaValidator.createValidator(sm, metadata);
		assertTrue(v.validate(states, sm));	
	}

	@Test
	public void testValidateStatesUnreachable() {
		String ENTITY_NAME = "";
		ResourceState begin = new ResourceState(ENTITY_NAME, "begin", new ArrayList<Action>(), "{id}");
		ResourceState exists = new ResourceState(ENTITY_NAME, "exists", new ArrayList<Action>(), "{id}");
		ResourceState end = new ResourceState(ENTITY_NAME, "end", new ArrayList<Action>(), "{id}");
	
		begin.addTransition(new Transition.Builder().method("PUT").target(exists).build());		
		exists.addTransition(new Transition.Builder().method("DELETE").target(end).build());
		
		ResourceState unreachableState = new ResourceState(ENTITY_NAME, "unreachable", new ArrayList<Action>(), "/unreachable");
		Set<ResourceState> states = new HashSet<ResourceState>();
		states.add(begin);
		states.add(exists);
		states.add(unreachableState);
		states.add(end);
		
		ResourceStateMachine sm = new ResourceStateMachine(begin);
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata(""));
		HypermediaValidator v = HypermediaValidator.createValidator(sm, metadata);
		assertFalse(v.validate(states, sm));	
	}

	@Test
	public void testValidateMetadataNotFound() {
		String ENTITY_NAME = "root_entity";
		ResourceState root = new ResourceState(ENTITY_NAME, "root", new ArrayList<Action>(), "/root");
		
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata("root_entity"));
		ResourceStateMachine sm = new ResourceStateMachine(root);
		HypermediaValidator v = HypermediaValidator.createValidator(sm, metadata);
		assertFalse(v.validate());	
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
	
		initial.addTransition(new Transition.Builder().method("PUT").target(exists).build());
				
		ResourceStateMachine sm = new ResourceStateMachine(initial, exception, null, null);
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata(""));
		HypermediaValidator v = HypermediaValidator.createValidator(sm, metadata);
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
				"    Ginitial->Gother[label=\"PUT (OK(other)) /entities/{id}\"]\n" +
				"}";
		
		String ENTITY_NAME = "G";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/");
		ResourceState other = new ResourceState(ENTITY_NAME, "other", new ArrayList<Action>(), "/entities/{id}");
	
		List<Expression> expressions = new ArrayList<Expression>();
		expressions.add(new ResourceGETExpression(other, ResourceGETExpression.Function.OK));
		initial.addTransition(new Transition.Builder().method("PUT").target(other).evaluation(new SimpleLogicalExpressionEvaluator(expressions)).build());
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata("G"));
		HypermediaValidator v = HypermediaValidator.createValidator(sm, metadata);
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
	
		initial.addTransition(new Transition.Builder().method("PUT").target(exists).build());
		// a transition to a final state will result in 204 (No Content) at runtime
		exists.addTransition(new Transition.Builder().method("DELETE").target(deleted).build());
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata("G"));
		HypermediaValidator v = HypermediaValidator.createValidator(sm, metadata);
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
				"    Ginitial->Gexists[label=\"*GET /entities/{id}\"]\n" +
				"    Ginitial->Gdeleted[label=\"*DELETE /entities/{id}\"]\n" +
				"    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n    Gexists->final[label=\"\"]\n    Gdeleted->Ginitial[style=\"dotted\"]\n}";
		
		String ENTITY_NAME = "G";
		CollectionResourceState initial = new CollectionResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/entities");
		ResourceState exists = new ResourceState(initial, "exists", new ArrayList<Action>(), "/{id}");
		ResourceState deleted = new ResourceState(initial, "deleted", new ArrayList<Action>(), "/{id}");
	
		initial.addTransition(new Transition.Builder().flags(Transition.FOR_EACH).method("GET").target(exists).build());		
		// add an auto transition from deleted state to a different state
		deleted.addTransition(new Transition.Builder().flags(Transition.AUTO).target(initial).build());
		// 205, as the auto transition is to the same state we expect to see a 205 (Reset Content) at runtime
		initial.addTransition(new Transition.Builder().flags(Transition.FOR_EACH).method("DELETE").target(deleted).build());
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata("G"));
		HypermediaValidator v = HypermediaValidator.createValidator(sm, metadata);
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
	
		initial.addTransition(new Transition.Builder().flags(Transition.FOR_EACH).method("GET").target(exists).build());
		// add an auto transition from deleted state to a different state
		deleted.addTransition(new Transition.Builder().flags(Transition.AUTO).target(initial).build());
		// 303, as the auto transition is to a different state we expect to see a 303 (Redirect) at runtime
		exists.addTransition(new Transition.Builder().method("DELETE").target(deleted).build());
		
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata("G"));
		HypermediaValidator v = HypermediaValidator.createValidator(sm, metadata);
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
			+ "    CRUD_ENTITYexists->CRUD_ENTITYarchived[label=\"PUT /archived\"]\n"
			+ "    CRUD_ENTITYexists->CRUD_ENTITYdeleted[label=\"DELETE /\"]\n"
			+ "    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    CRUD_ENTITYdeleted->final[label=\"\"]\n"
			+ "    final1[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n"
			+ "    CRUD_ENTITYarchived->final1[label=\"\"]\n}";
		
		String ENTITY_NAME = "CRUD_ENTITY";
		ResourceState initial = new ResourceState(ENTITY_NAME, "initial", new ArrayList<Action>(), "/");
		ResourceState exists = new ResourceState(initial, "exists", new ArrayList<Action>(), null);
		ResourceState archived = new ResourceState(ENTITY_NAME, "archived", new ArrayList<Action>(), "/archived");
		ResourceState deleted = new ResourceState(initial, "deleted", new ArrayList<Action>(), null);
	
		initial.addTransition(new Transition.Builder().method("PUT").target(exists).build());		
		exists.addTransition(new Transition.Builder().method("PUT").target(archived).build());
		exists.addTransition(new Transition.Builder().method("DELETE").target(deleted).build());
				
		ResourceStateMachine sm = new ResourceStateMachine(initial);
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata("CRUD_ENTITY"));
		HypermediaValidator v = HypermediaValidator.createValidator(sm, metadata);
		String result = v.graph();
		assertEquals(expected, result);	
	}

	@Test
	public void testDOTTransitionToStateMachine() {
		ResourceState home = new ResourceState("SERVICE_ROOT", "home", new ArrayList<Action>(), "/");
		ResourceStateMachine processSM = getProcessSM();
		home.addTransition("GET", processSM);
		ResourceStateMachine serviceDocumentSM = new ResourceStateMachine(home);
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata("SERVICE_ROOT"));
		
		String dotGraph = HypermediaValidator.createValidator(serviceDocumentSM, metadata).graph();
		System.out.println("DOTTransitionToStateMachine: \n" + dotGraph);

        // we cannot directly compared the produced string with the expected one because the generation of the DOT graph
        // relies on getting a Collection from the ResourceStateMachine, so no order can be assumed
        String expectedSubstr1 = "digraph SERVICE_ROOT {\n";
        assertTrue(dotGraph.contains(expectedSubstr1));
        String expectedSubstr2 = "    SERVICE_ROOThome[shape=circle, width=.25, label=\"\", color=black, style=filled]\n";
        assertTrue(dotGraph.contains(expectedSubstr2));
        String expectedSubstr3 = "    taskcomplete[label=\"task.complete /completed\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr3));
        String expectedSubstr4 = "    taskacquired[label=\"task.acquired /acquired\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr4));
        String expectedSubstr5 = "    taskabandoned[label=\"task.abandoned /acquired\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr5));
        String expectedSubstr6 = "    processtaskAvailable[label=\"process.taskAvailable /processes/nextTask\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr6));
        String expectedSubstr7 = "    processprocesses[label=\"process.processes /processes\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr7));
        String expectedSubstr8 = "    processnew[label=\"process.new /processes/new\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr8));
        String expectedSubstr9 = "    processinitialProcess[label=\"process.initialProcess /processes/{id}\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr9));
        String expectedSubstr10 = "    processcompletedProcess[label=\"process.completedProcess /processes/{id}\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr10));
        String expectedSubstr11 = "    final[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n";
        assertTrue(dotGraph.contains(expectedSubstr11));
        String expectedSubstr12 = "    taskcomplete->final[label=\"\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr12));
        String expectedSubstr13 = "    taskacquired->taskabandoned[label=\"DELETE /acquired\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr13));
        String expectedSubstr14 = "    taskacquired->taskcomplete[label=\"PUT /completed\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr14));
        String expectedSubstr15 = "    final1[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n";
        assertTrue(dotGraph.contains(expectedSubstr15));
        String expectedSubstr16 = "    taskabandoned->final1[label=\"\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr16));
        String expectedSubstr17 = "    processtaskAvailable->taskacquired[label=\"PUT /acquired\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr17));
        String expectedSubstr18 = "    processprocesses->processnew[label=\"POST /processes/new\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr18));
        String expectedSubstr19 = "    processnew->processinitialProcess[label=\"PUT /processes/{id}\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr19));
        String expectedSubstr20 = "    processinitialProcess->processtaskAvailable[label=\"GET /processes/nextTask\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr20));
        String expectedSubstr21 = "    processinitialProcess->processcompletedProcess[label=\"DELETE /processes/{id}\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr21));
        String expectedSubstr22 = "    final2[shape=circle, width=.25, label=\"\", color=black, style=filled, peripheries=2]\n";
        assertTrue(dotGraph.contains(expectedSubstr22));
        String expectedSubstr23 = "    processcompletedProcess->final2[label=\"\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr23));
        String expectedSubstr24 = "    SERVICE_ROOThome->processprocesses[label=\"GET /processes\"]\n";
        assertTrue(dotGraph.contains(expectedSubstr24));
        String expectedSubstr25 =  "}";
        assertTrue(dotGraph.contains(expectedSubstr25));
        // checked that all expected lines are in the return DOT graph, now I'm checking that
        // no other lines than the expected ones are returned
        assertEquals(25, dotGraph.split("\n").length);
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
		Metadata metadata = new Metadata("");
		metadata.setEntityMetadata(new EntityMetadata("SERVICE_ROOT"));
        String dotGraph = HypermediaValidator.createValidator(serviceDocumentSM, metadata).graphEntityNextStates();
        // we cannot directly compared the produced string with the expected one because the generation of the DOT graph
        // relies on getting a Collection from the ResourceStateMachine, so no order can be assumed
		String expectedSubstr1 = "digraph SERVICE_ROOT {\n";
		assertTrue(dotGraph.contains(expectedSubstr1));
		String expectedSubstr2 = "    SERVICE_ROOThome[shape=circle, width=.25, label=\"\", color=black, style=filled]\n";
	    assertTrue(dotGraph.contains(expectedSubstr2));
		String expectedSubstr3 = "    processprocesses[shape=square, width=.25, label=\"process.processes\"]\n";
	    assertTrue(dotGraph.contains(expectedSubstr3));
		String expectedSubstr4 = "    taskacquired[shape=square, width=.25, label=\"task.acquired\"]\n";
	    assertTrue(dotGraph.contains(expectedSubstr4));
		String expectedSubstr5 = "    notesinitial[shape=square, width=.25, label=\"notes.initial\"]\n";
	    assertTrue(dotGraph.contains(expectedSubstr5));
		String expectedSubstr6 = "    SERVICE_ROOThome->processprocesses[label=\"GET /processes\"]\n";
	    assertTrue(dotGraph.contains(expectedSubstr6));
		String expectedSubstr7 = "    SERVICE_ROOThome->notesinitial[label=\"GET /notes\"]\n";
	    assertTrue(dotGraph.contains(expectedSubstr7));
		String expectedSubstr8 =  "}";
	    assertTrue(dotGraph.contains(expectedSubstr8));
	    // checked that all expected lines are in the return DOT graph, now I'm checking that
	    // no other lines than the expected ones are returned
	    assertEquals(8, dotGraph.split("\n").length);
	}

	private ResourceStateMachine getProcessSM() {
		String PROCESS_ENTITY_NAME = "process";
		// process behaviour
		ResourceState processes = new ResourceState(PROCESS_ENTITY_NAME, "processes", new ArrayList<Action>(), "/processes");
		ResourceState newProcess = new ResourceState(PROCESS_ENTITY_NAME, "new", new ArrayList<Action>(), "/processes/new");
		// create new process
		processes.addTransition(new Transition.Builder().method("POST").target(newProcess).build());

		// Process states
		ResourceState processInitial = new ResourceState(PROCESS_ENTITY_NAME, "initialProcess", new ArrayList<Action>(), "/processes/{id}");
		ResourceState nextTask = new ResourceState(PROCESS_ENTITY_NAME,	"taskAvailable", new ArrayList<Action>(), "/processes/nextTask");
		ResourceState processCompleted = new ResourceState(PROCESS_ENTITY_NAME, "completedProcess", new ArrayList<Action>(), "/processes/{id}");
		// start new process
		newProcess.addTransition(new Transition.Builder().method("PUT").target(processInitial).build());
		// do a task
		processInitial.addTransition(new Transition.Builder().method("GET").target(nextTask).build());
		// finish the process
		processInitial.addTransition(new Transition.Builder().method("DELETE").target(processCompleted).build());

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
		taskAcquired.addTransition(new Transition.Builder().method("DELETE").target(taskAbandoned).build());
		// complete task
		taskAcquired.addTransition(new Transition.Builder().method("PUT").target(taskComplete).build());

		return new ResourceStateMachine(taskAcquired);
	}
}
