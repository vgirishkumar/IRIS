package com.temenos.interaction.rimdsl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.temenos.interaction.rimdsl.rim.Command;
import com.temenos.interaction.rimdsl.rim.NotFoundFunction;
import com.temenos.interaction.rimdsl.rim.OKFunction;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;
import com.temenos.interaction.rimdsl.rim.State;
import com.temenos.interaction.rimdsl.rim.Transition;

@InjectWith(RIMDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class ParserTest {

	@Inject
	ParseHelper<ResourceInteractionModel> parser;

	@Test 
	public void parseModel() throws Exception {
		ResourceInteractionModel model = parser.parse(loadTestRIM());
		assertEquals(4, model.getStates().size());
	    assertEquals("demo_initial", model.getStates().get(0).getName());
	    assertEquals("demo_tickets", model.getStates().get(1).getName());
	    assertEquals("demo_aticket", model.getStates().get(2).getName());
	    assertEquals("demo_deleted", model.getStates().get(3).getName());
	}
	
	private String loadTestRIM() throws IOException {
		URL url = Resources.getResource("ParserTest.rim");
		String rim = Resources.toString(url, Charsets.UTF_8);
		return rim;
	}



	private final static String LINE_SEP = System.getProperty("line.separator");
	
	private final static String SIMPLE_STATES_RIM = "" +
	"commands" + LINE_SEP +
	"	GetEntity properties" + LINE_SEP +
	"	UpdateEntity properties" + LINE_SEP +
	"end" + LINE_SEP +
			
	"initial resource A" + LINE_SEP +
	"	collection ENTITY" + LINE_SEP +
	"	actions { GetEntity }" + LINE_SEP +
	"end" + LINE_SEP +

	"resource B" + LINE_SEP +
	"	item ENTITY" + LINE_SEP +
// TODO - new specification for View commands
//	"	view { GetEntity }" + LINE_SEP +
	"	actions { GetEntity, UpdateEntity }" + LINE_SEP +
	"end" + LINE_SEP +
	"";

	@Test
	public void testParseSimpleStates() throws Exception {
		ResourceInteractionModel model = parser.parse(SIMPLE_STATES_RIM);
		assertEquals(0, model.eResource().getErrors().size());
		
		// there should be exactly two states
		assertEquals(2, model.getStates().size());
	    assertEquals("A", model.getStates().get(0).getName());
	    assertEquals("B", model.getStates().get(1).getName());

	    // there should be no transitions between these states
	    State Astate = model.getStates().get(0);
	    assertEquals(0, Astate.getTransitions().size());
	    assertEquals(0, Astate.getTransitionsAuto().size());
	    assertEquals(0, Astate.getTransitionsForEach().size());
	    State Bstate = model.getStates().get(1);
	    assertEquals(0, Bstate.getTransitions().size());
	    assertEquals(0, Bstate.getTransitionsAuto().size());
	    assertEquals(0, Bstate.getTransitionsForEach().size());   
	}

	private final static String SINGLE_STATE_VIEW_COMMAND_ONLY_RIM = "" +
	"commands" + LINE_SEP +
	"	GetEntity properties" + LINE_SEP +
	"end" + LINE_SEP +
			
	"initial resource A" + LINE_SEP +
	"	collection ENTITY" + LINE_SEP +
	"	actions { GetEntity }" + LINE_SEP +
	"end\r\n" + LINE_SEP +
	"";

	@Test
	public void testParseSingleStateViewCommandOnly() throws Exception {
		ResourceInteractionModel model = parser.parse(SINGLE_STATE_VIEW_COMMAND_ONLY_RIM);
		assertEquals(0, model.eResource().getErrors().size());
		
		// there should be exactly one states
		assertEquals(1, model.getStates().size());
	    assertEquals("A", model.getStates().get(0).getName());

	    // there should be one action on the state
	    EList<Command> actions = model.getStates().get(0).getActions();
	    assertEquals(1, actions.size());
	}
	
	private final static String SINGLE_STATE_NO_COMMANDS_RIM = "" +
	"initial resource A" + LINE_SEP +
	"	collection ENTITY" + LINE_SEP +
	"end\r\n" + LINE_SEP +
	"";

	@Test
	public void testParseSingleStateNoCommands() throws Exception {
		ResourceInteractionModel model = parser.parse(SINGLE_STATE_NO_COMMANDS_RIM);
		
		// there should be exactly one states
		assertEquals(1, model.getStates().size());
	    assertEquals("A", model.getStates().get(0).getName());

	    // there should be an error indicating a problem with the missing actions
		assertEquals(1, model.eResource().getErrors().size());
	}

	private final static String SIMPLE_TRANSITION_RIM = "" +
			"commands" + LINE_SEP +
			"	GetEntity properties" + LINE_SEP +
			"	GetEntities properties" + LINE_SEP +
			"	PutEntity properties" + LINE_SEP +
			"end" + LINE_SEP +
					
			"initial resource A" + LINE_SEP +
			"	collection ENTITY" + LINE_SEP +
			"	actions { GetEntities }" + LINE_SEP +
			"	GET -> B" + LINE_SEP +
			"end" + LINE_SEP +

			"resource B" +
			"	item ENTITY" + LINE_SEP +
			"	actions { GetEntity, PutEntity }" + LINE_SEP +
			"end" + LINE_SEP +
			"";

	@Test
	public void testParseStatesWithTransition() throws Exception {
		ResourceInteractionModel model = parser.parse(SIMPLE_TRANSITION_RIM);
		assertEquals(0, model.eResource().getErrors().size());
		
		// there should be exactly two states
		assertEquals(2, model.getStates().size());
		State aState = model.getStates().get(0);
	    assertEquals("A", aState.getName());

	    // there should one transition from state A to state B
		assertEquals(1, aState.getTransitions().size());
		assertEquals("B", aState.getTransitions().get(0).getState().getName());
	}

	private final static String TRANSITION_WITH_EXPRESSION_RIM = "" +
			"commands" + LINE_SEP +
			"	GetEntity properties" + LINE_SEP +
			"	GetEntities properties" + LINE_SEP +
			"	PutEntity properties" + LINE_SEP +
			"end" + LINE_SEP +
					
			"initial resource A" + LINE_SEP +
			"	collection ENTITY" + LINE_SEP +
			"	actions { GetEntities }" + LINE_SEP +
			"	GET -> B (OK(B))" + LINE_SEP +
			"	GET -> B (NOT_FOUND(B))" + LINE_SEP +
			"	GET -> B (OK(B) && NOT_FOUND(B))" + LINE_SEP +
			"end" + LINE_SEP +

			"resource B" +
			"	item ENTITY" + LINE_SEP +
			"	actions { GetEntity, PutEntity }" + LINE_SEP +
			"end" + LINE_SEP +
			"";

	@Test
	public void testParseStatesWithTransitionEvalExpression() throws Exception {
		ResourceInteractionModel model = parser.parse(TRANSITION_WITH_EXPRESSION_RIM);
		assertEquals(0, model.eResource().getErrors().size());
		
		// there should be exactly two states
		assertEquals(2, model.getStates().size());
		State aState = model.getStates().get(0);
	    assertEquals("A", aState.getName());

	    // there should two transitions from state A to state B
		assertEquals(3, aState.getTransitions().size());
		// assert expressions on OK transition
		assertEquals("B", aState.getTransitions().get(0).getState().getName());
		assertEquals(1, aState.getTransitions().get(0).getEval().getExpressions().size());
		assertTrue(aState.getTransitions().get(0).getEval().getExpressions().get(0) instanceof OKFunction);
		assertEquals("B", ((OKFunction) aState.getTransitions().get(0).getEval().getExpressions().get(0)).getState().getName());

		// assert expressions on NOT_FOUND transition
		assertEquals("B", aState.getTransitions().get(1).getState().getName());
		assertEquals(1, aState.getTransitions().get(1).getEval().getExpressions().size());
		assertTrue(aState.getTransitions().get(1).getEval().getExpressions().get(0) instanceof NotFoundFunction);
		assertEquals("B", ((NotFoundFunction) aState.getTransitions().get(1).getEval().getExpressions().get(0)).getState().getName());

		// assert expressions with && on transition
		assertEquals("B", aState.getTransitions().get(2).getState().getName());
		Transition twe = aState.getTransitions().get(2);
		assertEquals(2, twe.getEval().getExpressions().size());
		assertTrue(twe.getEval().getExpressions().get(0) instanceof OKFunction);
		assertEquals("B", ((OKFunction) twe.getEval().getExpressions().get(0)).getState().getName());
		assertTrue(twe.getEval().getExpressions().get(1) instanceof NotFoundFunction);
		assertEquals("B", ((NotFoundFunction) twe.getEval().getExpressions().get(1)).getState().getName());

	}

}
