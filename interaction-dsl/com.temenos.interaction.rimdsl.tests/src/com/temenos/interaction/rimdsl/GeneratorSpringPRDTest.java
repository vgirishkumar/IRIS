package com.temenos.interaction.rimdsl;

/*
 * #%L
 * com.temenos.interaction.rimdsl.tests
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

import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.temenos.interaction.rimdsl.rim.DomainModel;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;

@InjectWith(RIMDslSpringPRDInjectorProvider.class)
@RunWith(XtextRunner.class)
public class GeneratorSpringPRDTest {

	@Inject
	IGenerator underTest;
	@Inject
	ParseHelper<DomainModel> parseHelper;

	private final static String LINE_SEP = System.getProperty("line.separator");

	private final static String SIMPLE_STATES_RIM = "" + "rim Simple {" + LINE_SEP + "	command GetEntity" + LINE_SEP
			+ "	command GetException" + LINE_SEP + "	command UpdateEntity" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "}" + LINE_SEP +

			"exception resource E {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: EXCEPTION" + LINE_SEP
			+ "	view: GetException" + LINE_SEP + "}" + LINE_SEP +

			"resource B {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP + "	actions [ UpdateEntity ]"
			+ LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "";

	private final static String SIMPLE_STATES_BEHAVIOUR = "       <bean id=\"A\" class=\"com.temenos.interaction.core.hypermedia.CollectionResourceState\">"
			+ LINE_SEP + "<constructor-arg name=\"entityName\" value=\"ENTITY\" />";
	private final static String INCOMPLETE_RIM = "" + "rim Test {" + LINE_SEP + "	command GetEntity" + LINE_SEP
			+ "	command Noop" + LINE_SEP + "}" + LINE_SEP + "";

	private static final String BEAN_ID_INITIAL_STATE = "initialState";
	private static final String BEAN_ID_COLLECTION_RESOURCE_STATE = "com.temenos.interaction.core.hypermedia.CollectionResourceState";
	private static final String FACTORY_BEAN = "com.temenos.interaction.springdsl.TransitionFactoryBean";
	private static final String RESOURCE_STATE = "com.temenos.interaction.core.hypermedia.ResourceState";
	private static final String ACTION = "com.temenos.interaction.core.hypermedia.Action";
	private static final String TRANSITION_FACTORY_BEAN = "com.temenos.interaction.springdsl.TransitionFactoryBean";

	private String beanId;
	private String beanClass;
	private String path;


	private enum PROCESSING_STATE {
		INIT, RESOURCE_STATE, ACTION, FACTORY_BEAN, COLLECTION_RESOURCE_STATE
	};

	private PROCESSING_STATE processState = PROCESSING_STATE.INIT;

	private final static String SINGLE_STATE_VIEW_COMMAND_ONLY_RIM = "" + "rim Test {" + LINE_SEP
			+ "	command GetEntity" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "";

	/**
	 * Test generate one file.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testGenerateOneFile() throws Exception {
		DomainModel domainModel = parseHelper.parse(SINGLE_STATE_VIEW_COMMAND_ONLY_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		System.out.println(fsa.getFiles());
		assertEquals(2, fsa.getFiles().size());

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "TestServiceDocumentIRIS-PRD.xml"));
/*
 		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "TestBehaviour.java"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "Test/AResourceState.java"));
 */
	}

	private final static String SINGLE_STATE_WITH_PACKAGE_RIM = "" + "domain blah {" + LINE_SEP + "rim Test {"
			+ LINE_SEP + "	command GetEntity" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "";

	/**
	 * Test generate file in package.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testGenerateFileInPackage() throws Exception {
		DomainModel domainModel = parseHelper.parse(SINGLE_STATE_WITH_PACKAGE_RIM);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(domainModel.eResource(), fsa);
		System.out.println(fsa.getFiles());
		assertEquals(2, fsa.getFiles().size());

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-blah_Test_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output
				.contains("<bean id=\"blah_Test_A\" class=\"com.temenos.interaction.core.hypermedia.CollectionResourceState\">"));
		assertTrue(output.contains("<bean class=\"com.temenos.interaction.core.hypermedia.Action\">"));
		assertTrue(output.contains("<constructor-arg name=\"path\" value=\"/A\" />"));
		assertTrue(output.contains("<property name=\"transitions\">"));
		
		/*
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output.contains("package blah;"));
		assertTrue(output.contains("public class TestBehaviour {"));
		assertTrue(output.contains("getRIM"));
		assertTrue(output.contains("factory.getResourceState(\"blah.Test.A\");"));

		String expectedRSKey = IFileSystemAccess.DEFAULT_OUTPUT + "blah/Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedRSKey));
		String outputRS = fsa.getFiles().get(expectedRSKey).toString();
		assertTrue(outputRS.contains("package blah.Test;"));		 
		 */

	}

	/**
	 * Test generate single state view command only.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testGenerateSingleStateViewCommandOnly() throws Exception {
		DomainModel domainModel = parseHelper.parse(SINGLE_STATE_VIEW_COMMAND_ONLY_RIM);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(domainModel.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();


		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml";
		String output = fsa.getFiles().get(expectedKey).toString();

		assertTrue(fsa.getFiles().containsKey(expectedKey));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "TestServiceDocumentIRIS-PRD.xml"));
		assertTrue(output
				.contains("<bean id=\"Test_A\" class=\"com.temenos.interaction.core.hypermedia.CollectionResourceState\">"));
		assertTrue(output.contains("<constructor-arg name=\"entityName\" value=\"ENTITY\" />"));
		assertTrue(output.contains("<constructor-arg name=\"name\" value=\"A\" />"));
		assertTrue(output.contains("<bean class=\"com.temenos.interaction.core.hypermedia.Action\">"));
		assertTrue(output.contains("<constructor-arg value=\"GetEntity\" />"));
		assertTrue(output.contains("<constructor-arg value=\"VIEW\" />"));
		assertTrue(output.contains("<constructor-arg name=\"path\" value=\"/A\" />"));
		assertTrue(output.contains("<property name=\"transitions\">"));
		
		//String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "Test/AResourceState.java";
		//assertTrue(fsa.getFiles().containsKey(expectedKey));
		//assertTrue(fsa.getFiles().get(expectedKey).toString().contains("new Action(\"GetEntity\", Action.TYPE.VIEW, new Properties())"));
	}

	private final static String SINGLE_STATE_ACTION_COMMANDS_RIM = "" + "rim Test {" + LINE_SEP
			+ "	command GetEntity {" + LINE_SEP + "		properties [ getkey=getvalue ]" + LINE_SEP + "	}" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "";

	@Test
	public void testGenerateSingleStateActionCommands() throws Exception {
		DomainModel domainModel = parseHelper.parse(SINGLE_STATE_ACTION_COMMANDS_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey));

		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output.contains("<bean id=\"Test_A\" class=\"com.temenos.interaction.core.hypermedia.CollectionResourceState\">"));
		assertTrue(output.contains("<constructor-arg name=\"entityName\" value=\"ENTITY\" />"));
		assertTrue(output.contains("<constructor-arg name=\"name\" value=\"A\" />"));
		assertTrue(output.contains("<bean class=\"com.temenos.interaction.core.hypermedia.Action\">"));
		assertTrue(output.contains("<constructor-arg value=\"GetEntity\" />"));
		assertTrue(output.contains("<constructor-arg value=\"VIEW\" />"));
		assertTrue(output.contains("constructor-arg name=\"path\" value=\"/A\" />"));
		
		//int indexOfFirstNewProperties = output.indexOf("actionViewProperties = new Properties()");
		//assertTrue(indexOfFirstNewProperties > 0);
		//assertTrue(output.contains("actionViewProperties.put(\"getkey\", \"getvalue\""));
		//assertTrue(output.contains("new Action(\"GetEntity\", Action.TYPE.VIEW, actionViewProperties)"));

		// No onerror handler so should not define an error state
		//assertTrue(output.contains("super(\"ENTITY\", \"A\", createActions(), \"/A\", createLinkRelations(), null, null);"));
	}

	private final static String MULTIPLE_STATES_MULTIPLE_ACTION_COMMANDS_RIM = "" + "rim Test {" + LINE_SEP
			+ "	command DoStuff {" + LINE_SEP + "		properties [ key=value ]" + LINE_SEP + "	}" + LINE_SEP
			+ "	command DoSomeStuff {" + LINE_SEP + "		properties [ keyB=valueB ]" + LINE_SEP + "	}" + LINE_SEP
			+ "	command DoSomeMoreStuff {" + LINE_SEP + "		properties [ keyB0=valueB0, keyB1=valueB1 ]" + LINE_SEP
			+ "	}" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	actions [ DoStuff ]" + LINE_SEP + "}" + LINE_SEP +

			"initial resource B {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	actions [ DoSomeStuff, DoSomeMoreStuff ]" + LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "";

	@Test
	public void testGenerateMultipleStateMultipleActionCommands() throws Exception {
		DomainModel domainModel = parseHelper.parse(MULTIPLE_STATES_MULTIPLE_ACTION_COMMANDS_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		String resouceAKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(resouceAKey));
		String resourceA = fsa.getFiles().get(resouceAKey).toString();
		assertTrue(resourceA.contains("<bean class=\"com.temenos.interaction.core.hypermedia.Action\">"));
		assertTrue(resourceA.contains("<constructor-arg value=\"DoStuff\" />"));
		assertTrue(resourceA.contains("<constructor-arg value=\"ENTRY\" />"));

		//assertTrue(resourceB.contains("new Action(\"DoSomeMoreStuff\", Action.TYPE.ENTRY, actionViewProperties)"));

		//int indexOfFirstNewProperties = resourceA.indexOf("actionViewProperties = new Properties()");
		//assertTrue(indexOfFirstNewProperties > 0);
		//assertTrue(resourceA.contains("actionViewProperties.put(\"key\", \"value\""));
		//assertTrue(resourceA.contains("new Action(\"DoStuff\", Action.TYPE.ENTRY, actionViewProperties)"));

		String resouceBKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_B-PRD.xml";
		//assertTrue(fsa.getFiles().containsKey(resouceBKey));
		String resourceB = fsa.getFiles().get(resouceBKey).toString();
		assertTrue(resourceB.contains("<bean class=\"com.temenos.interaction.core.hypermedia.Action\">"));
		assertTrue(resourceB.contains("constructor-arg value=\"DoSomeMoreStuff\" />"));
		assertTrue(resourceB.contains("<constructor-arg value=\"ENTRY\" />"));
		assertTrue(resourceB.contains("<constructor-arg name=\"path\" value=\"/B\" />"));
		assertTrue(resourceB.contains(""));
		assertTrue(resourceB.contains(""));
		//int indexOfSecondNewProperties = resourceB.indexOf("actionViewProperties = new Properties()", indexOfFirstNewProperties);
		//assertTrue(indexOfSecondNewProperties > 0);
		//assertTrue(resourceB.contains("actionViewProperties.put(\"keyB\", \"valueB\""));
		//assertTrue(resourceB.contains("new Action(\"DoSomeStuff\", Action.TYPE.ENTRY, actionViewProperties)"));
		//assertTrue(resourceB.contains("actionViewProperties.put(\"keyB0\", \"valueB0\""));
		//assertTrue(resourceB.contains("actionViewProperties.put(\"keyB1\", \"valueB1\""));
		//assertTrue(resourceB.contains("new Action(\"DoSomeMoreStuff\", Action.TYPE.ENTRY, actionViewProperties)"));

	}

	private final static String TRANSITION_WITH_EXPRESSION_RIM = "" + "rim Test {" + LINE_SEP + "	event GET {"
			+ LINE_SEP + "		method: GET" + LINE_SEP + "	}" + LINE_SEP +

			"	command GetEntity" + LINE_SEP + "	command GetEntities" + LINE_SEP + "	command PutEntity" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntities" + LINE_SEP + "	GET -> B { condition: OK(B) }" + LINE_SEP
			+ "	GET -> B { condition: NOT_FOUND(B) }" + LINE_SEP + "	GET -> B { condition: OK(C) && NOT_FOUND(D) }"
			+ LINE_SEP + "}" + LINE_SEP +

			"resource B {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP + "	view: GetEntity" + LINE_SEP
			+ "}" + LINE_SEP + "resource C {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "}" + LINE_SEP + "resource D {" + "	type: item" + LINE_SEP
			+ "	entity: ENTITY" + LINE_SEP + "	view: GetEntity" + LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "";

	@Test
	public void testGenerateTransitionsWithExpressions() throws Exception {
		DomainModel domainModel = parseHelper.parse(TRANSITION_WITH_EXPRESSION_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();

		//final String NEW_STATEMENT = "conditionalLinkExpressions = new ArrayList<Expression>();";
		//final String ADD_TRANSITION = ".method(\"GET\").target(sB).uriParameters(uriLinkageProperties).evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null).label(\"B\")";

		//int indexOfNewStatement = output.indexOf(NEW_STATEMENT);
		//assertTrue(indexOfNewStatement > 0);
		
		//assertTrue(output.contains("conditionalLinkExpressions.add(new ResourceGETExpression(factory.getResourceState(\"Test.B\"), ResourceGETExpression.Function.OK))"));
		assertTrue(output.contains("Test.B , ResourceGETExpression.Function.OK;"));
		assertTrue(output.contains("<bean class=\"com.temenos.interaction.springdsl.TransitionFactoryBean\">"));
		assertTrue(output.contains("<property name=\"method\" value=\"GET\" />"));
		assertTrue(output.contains("<property name=\"target\" ref=\"Test_B\" />"));


		//int indexOfAddTransition = output.indexOf(ADD_TRANSITION);
		//assertTrue(indexOfAddTransition > 0);

		//indexOfNewStatement = output.indexOf(NEW_STATEMENT, indexOfNewStatement);
		//assertTrue(indexOfNewStatement > 0);
		//assertTrue(output.contains("conditionalLinkExpressions.add(new ResourceGETExpression(factory.getResourceState(\"Test.B\"), ResourceGETExpression.Function.NOT_FOUND))"));
		assertTrue(output.contains("Test.B , ResourceGETExpression.Function.NOT_FOUND;"));
		
		int indexOfAddTransition = output.indexOf("<property name=\"functionList\">");
		assertTrue(indexOfAddTransition > 0);

		//indexOfNewStatement = output.indexOf(NEW_STATEMENT, indexOfNewStatement);
		//assertTrue(indexOfNewStatement > 0);
		//assertTrue(output.contains("conditionalLinkExpressions.add(new ResourceGETExpression(factory.getResourceState(\"Test.C\"), ResourceGETExpression.Function.OK))"));
		assertTrue(output.contains("Test.C , ResourceGETExpression.Function.OK;"));
		
		//assertTrue(output.contains("conditionalLinkExpressions.add(new ResourceGETExpression(factory.getResourceState(\"Test.D\"), ResourceGETExpression.Function.NOT_FOUND))"));
		assertTrue(output.contains("Test.D , ResourceGETExpression.Function.NOT_FOUND;"));
		
		//indexOfAddTransition = output.indexOf(ADD_TRANSITION, indexOfAddTransition);
		assertTrue(indexOfAddTransition > 0);
	}

	private final static String TRANSITION_WITH_MISSING_TARGET_RIM = "" + "rim Test {" + LINE_SEP + "	event GET {"
			+ LINE_SEP + "		method: GET" + LINE_SEP + "	}" + LINE_SEP +

			"	command GetEntities" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntities" + LINE_SEP + "	GET -> B" + LINE_SEP + "}" + LINE_SEP +

			"}" + LINE_SEP + "";

	@Test
	public void testGenerateTransitionsWithMissingTarget() throws Exception {
		DomainModel domainModel = parseHelper.parse(TRANSITION_WITH_MISSING_TARGET_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();

		// should not be adding the transition to a broken / missing state
		assertFalse(output.contains("<bean class=\"com.temenos.interaction.springdsl.TransitionFactoryBean\">"));
		assertFalse(output.contains("<property name=\"method\" value=\"GET\" />"));
		assertFalse(output.contains("<property name=\"target\" ref=\"B\" />"));
		assertFalse(output.contains("<property name=\"uriParameters\" ref=\"uriLinkageMap\" />"));
		assertFalse(output.contains("<property name=\"evaluation\" ref=\"conditionalLinkExpressions\" />"));
		assertFalse(output.contains("<property name=\"label\" value=\"B\" />"));
		
		//assertFalse(output.contains("sA.addTransition(new Transition.Builder()"));
		//assertFalse(output.contains("factory.getResourceState(\"\");"));
	}

	private final static String TRANSITION_WITH_STRING_TARGET_RIM = "" + "rim Test {" + LINE_SEP + "	event GET {"
			+ LINE_SEP + "		method: GET" + LINE_SEP + "	}" + LINE_SEP +

			"	command GetEntities" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntities" + LINE_SEP + "	GET -> \"B\"" + LINE_SEP + "}" + LINE_SEP +

			"}" + LINE_SEP + "";

	@Test
	public void testGenerateTransitionsWithStringTarget() throws Exception {
		DomainModel domainModel = parseHelper.parse(TRANSITION_WITH_STRING_TARGET_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();

		// should find the transition to state
		assertTrue(output.contains("<bean class=\"com.temenos.interaction.springdsl.TransitionFactoryBean\">"));
		assertTrue(output.contains("<property name=\"method\" value=\"GET\" />"));
		assertTrue(output.contains("<property name=\"target\" ref=\"B\" />"));
		assertTrue(output.contains("<property name=\"uriParameters\"><util:map></util:map></property>"));
		assertTrue(output.contains("<property name=\"functionList\"><util:list></util:list></property>"));
		assertTrue(output.contains("<property name=\"label\" value=\"B\" />"));
		//assertTrue(output.contains("sA.addTransition(new Transition.Builder()"));
		//assertTrue(output.contains("factory.getResourceState(\"B\");"));
	}

	private final static String AUTO_TRANSITION_WITH_URI_LINKAGE_RIM = "" + "rim Test {" + LINE_SEP + "	event GET {"
			+ LINE_SEP + "		method: GET" + LINE_SEP + "	}" + LINE_SEP +

			"	command GetEntity" + LINE_SEP + "	command GetEntities" + LINE_SEP + "	command CreateEntity" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntities" + LINE_SEP + "	POST -> create_pseudo_state" + LINE_SEP + "}" + LINE_SEP +

			"resource create_pseudo_state {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	actions [ CreateEntity ]" + LINE_SEP + "   GET --> created { parameters [ id=\"{MyId}\" ] }" + LINE_SEP
			+ "}" + LINE_SEP + "resource created {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "";

	@Test
	public void testGenerateAutoTransitionsWithUriLinkage() throws Exception {
		DomainModel domainModel = parseHelper.parse(AUTO_TRANSITION_WITH_URI_LINKAGE_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_create_pseudo_state-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();

		assertTrue(output.contains("<bean class=\"com.temenos.interaction.springdsl.TransitionFactoryBean\">"));
		assertTrue(output.contains("<property name=\"flags\"><util:constant static-field=\"com.temenos.interaction.core.hypermedia.Transition.AUTO\"/></property>"));
		assertTrue(output.contains("<property name=\"target\" ref=\"Test_created\" />"));
		assertTrue(output.contains("<!-- <property name=\"method\" value=\"GET\" /> -->"));
		assertTrue(output.contains("<property name=\"uriParameters\"><util:map>"));
		assertTrue(output.contains("<entry key=\"id\" value=\"{MyId}\"/>"));
		assertTrue(output.contains("</util:map>"));

		//assertTrue(output.contains("uriLinkageProperties.put(\"id\", \"{MyId}\");"));
		//assertTrue(output.contains("screate_pseudo_state.addTransition(new Transition.Builder()"));
		//assertTrue(output.contains("Transition.AUTO"));
		//assertTrue(output.contains(".target(screated)"));
	}

	private final static String REDIRECT_TRANSITION_WITH_URI_LINKAGE_RIM = "" + "rim Test {" + LINE_SEP
			+ "	event GET {" + LINE_SEP + "		method: GET" + LINE_SEP + "	}" + LINE_SEP +

			"	command GetEntity" + LINE_SEP + "	command GetEntities" + LINE_SEP + "	command DeleteEntity" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntities" + LINE_SEP + "	DELETE -> delete_pseudo_state" + LINE_SEP + "}" + LINE_SEP +

			"resource delete_pseudo_state {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	actions [ DeleteEntity ]" + LINE_SEP + "   GET ->> deleted { parameters [ id=\"{MyId}\" ] }" + LINE_SEP
			+ "}" + LINE_SEP + "resource deleted {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "";

	@Test
	public void testGenerateRedirectTransitionsWithUriLinkage() throws Exception {
		DomainModel domainModel = parseHelper.parse(REDIRECT_TRANSITION_WITH_URI_LINKAGE_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "Test/delete_pseudo_stateResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();

		assertTrue(output.contains("uriLinkageProperties.put(\"id\", \"{MyId}\");"));
		assertTrue(output.contains("sdelete_pseudo_state.addTransition(new Transition.Builder()"));
		assertTrue(output.contains("Transition.REDIRECT"));
		assertTrue(output.contains(".target(sdeleted)"));
	}

	private final static String EMBEDDED_TRANSITION_RIM = "" + "rim Test {" + LINE_SEP + "	event GET {" + LINE_SEP
			+ "		method: GET" + LINE_SEP + "	}" + LINE_SEP +

			"	command GetEntity" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "	GET +-> B" + LINE_SEP + "}" + LINE_SEP +

			"resource B {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP + "	view: GetEntity" + LINE_SEP
			+ "}" + LINE_SEP + "}" + LINE_SEP + "";

	@Test
	public void testGenerateEmbeddedTransitions() throws Exception {
		DomainModel domainModel = parseHelper.parse(EMBEDDED_TRANSITION_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();

		assertTrue(output.contains("sA.addTransition(new Transition.Builder()"));
		assertTrue(output.contains(".target(sB)"));
		assertTrue(output.contains(".flags(Transition.EMBEDDED)"));

		String expectedBKey = IFileSystemAccess.DEFAULT_OUTPUT + "Test/BResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedBKey));
		
		/*
		assertTrue(output.contains("sA.addTransition(new Transition.Builder()"));
		assertTrue(output.contains(".target(sB)"));
		assertTrue(output.contains(".flags(Transition.EMBEDDED)"));

		String expectedBKey = IFileSystemAccess.DEFAULT_OUTPUT + "Test/BResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedBKey));		 
		 */
	}

	private final static String RESOURCE_RELATIONS_RIM = "" + "rim Test {" + LINE_SEP + "	command Noop" + LINE_SEP
			+ "	command Update" + LINE_SEP +

			"initial resource accTransactions {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY"
			+ LINE_SEP + "   view: Noop" + LINE_SEP
			+ "   relations [ \"archives\", \"http://www.temenos.com/statement-entries\" ]" + LINE_SEP
			+ "   GET -> accTransaction" + LINE_SEP + "}\r\n" + LINE_SEP + "resource accTransaction {" + LINE_SEP
			+ "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP + "   actions [ Update ]" + LINE_SEP
			+ "   relations [ \"edit\" ]" + LINE_SEP + "}\r\n" + LINE_SEP + "}" + LINE_SEP + "";

	@Test
	public void testGenerateResourcesWithRelations() throws Exception {
		DomainModel domainModel = parseHelper.parse(RESOURCE_RELATIONS_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		// collection
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_accTransactions-PRD.xml";
		assertTrue(fsa.getAllFiles().containsKey(expectedKey));
		String accTransactionsOutput = fsa.getAllFiles().get(expectedKey).toString();
	
		/*
		 * <constructor-arg name="rels">
		 *   	<array>
		 *      	<value>archives</value>
		 *      	<value>http://www.temenos.com/statement-entries</value>
		 *   	</array>
         * </constructor-arg>
		 */

		// the constructor part
		assertTrue(accTransactionsOutput.contains("<constructor-arg name=\"rels\">"));
		assertTrue(accTransactionsOutput.contains("<value><![CDATA[archives]]></value>"));
		assertTrue(accTransactionsOutput.contains("<value><![CDATA[http://www.temenos.com/statement-entries]]></value>"));
		assertTrue(accTransactionsOutput.contains("<constructor-arg name=\"uriSpec\"><null /></constructor-arg>"));
		assertTrue(accTransactionsOutput.contains("<constructor-arg name=\"errorState\"><null /></constructor-arg>"));

		// item
		expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_accTransaction-PRD.xml";
		assertTrue(fsa.getAllFiles().containsKey(expectedKey));
		String accTransactionOutput = fsa.getAllFiles().get(expectedKey).toString();

		/*
		 * <constructor-arg name="rels">
		 *   	<array>
		 *      	<value>edit</value>
		 *   	</array>
         * </constructor-arg>
		 */

		// the constructor part
		assertTrue(accTransactionOutput.contains("<constructor-arg name=\"rels\">"));
		assertTrue(accTransactionOutput.contains("<value><![CDATA[edit]]></value>"));
	}

	private final static String GLOBAL_RESOURCE_RELATIONS_RIM = "" + "rim Test {" + LINE_SEP + "	command Noop"
			+ LINE_SEP + "	command Update" + LINE_SEP +

			"	relation archiveRel {" + LINE_SEP + "		fqn: \"archive\"" + LINE_SEP + "	}" + LINE_SEP +

			"	relation editRel {" + LINE_SEP + "		fqn: \"edit\"" + LINE_SEP
			+ "		description: \"See 'edit' in http://www.iana.org/assignments/link-relations/link-relations.xhtml\""
			+ LINE_SEP + "	}" + LINE_SEP +

			"initial resource accTransactions {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY"
			+ LINE_SEP + "   view: Noop" + LINE_SEP
			+ "   relations [ archiveRel, \"http://www.temenos.com/statement-entries\" ]" + LINE_SEP
			+ "   PUT -> accTransaction" + LINE_SEP + "}\r\n" + LINE_SEP + "resource accTransaction {" + LINE_SEP
			+ "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP + "   actions [ Update ]" + LINE_SEP
			+ "   relations [ editRel ]" + LINE_SEP + "}\r\n" + LINE_SEP + "}" + LINE_SEP + // end
																							// rim
			"";

	@Test
	public void testGenerateResourcesWithGlobalRelations() throws Exception {
		DomainModel domainModel = parseHelper.parse(GLOBAL_RESOURCE_RELATIONS_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		// collection
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "Test/accTransactionsResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String accTransactionsOutput = fsa.getFiles().get(expectedKey).toString();
		// the constructor part
		assertTrue(accTransactionsOutput.contains("\"/accTransactions\", createLinkRelations()"));
		// createLinkRelations method
		String expectedAccTransactionsRelArray = "" + "        String accTransactionsRelationsStr = \"\";" + LINE_SEP
				+ "        accTransactionsRelationsStr += \"archive \";" + LINE_SEP
				+ "        accTransactionsRelationsStr += \"http://www.temenos.com/statement-entries \";" + LINE_SEP
				+ "        String[] accTransactionsRelations = accTransactionsRelationsStr.trim().split(\" \");"
				+ LINE_SEP + "";
		assertTrue(accTransactionsOutput.contains(expectedAccTransactionsRelArray));

		// item
		expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "Test/accTransactionResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String accTransactionOutput = fsa.getFiles().get(expectedKey).toString();
		// the constructor part
		assertTrue(accTransactionOutput.contains("\"/accTransaction\", createLinkRelations()"));
		// createLinkRelations method
		String expectedAccTransactionRelArray = "" + "        String accTransactionRelationsStr = \"\";" + LINE_SEP
				+ "        accTransactionRelationsStr += \"edit \";" + LINE_SEP
				+ "        String[] accTransactionRelations = accTransactionRelationsStr.trim().split(\" \");"
				+ LINE_SEP + "";
		assertTrue(accTransactionOutput.contains(expectedAccTransactionRelArray));
	}

	private final static String TRANSITION_WITH_UPDATE_EVENT = "" + "rim Test {" + LINE_SEP + "	event GET {" + LINE_SEP
			+ "		method: GET" + LINE_SEP + "	}" + LINE_SEP + "	event UPDATE {" + LINE_SEP + "		method: PUT" + LINE_SEP
			+ "	}" + LINE_SEP +

			"	command GetEntities" + LINE_SEP + "	command GetEntity" + LINE_SEP + "	command PutEntity" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntities" + LINE_SEP + "	GET *-> B" + LINE_SEP + "}" + LINE_SEP +

			"resource B {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP + "	view: GetEntity" + LINE_SEP
			+ "	UPDATE -> B_pseudo" + LINE_SEP + "}" + LINE_SEP +

			"resource B_pseudo {" + "	type: item" + LINE_SEP + "	entity: ENTITY" + LINE_SEP + "	actions [ PutEntity ]"
			+ LINE_SEP + "	GET --> A { condition: NOT_FOUND(B) }" + LINE_SEP + "	GET --> B { condition: OK(B) }"
			+ LINE_SEP + "}" + LINE_SEP +

			"}" + LINE_SEP + "";

	@Test
	public void testGenerateUpdateTransition() throws Exception {
		DomainModel domainModel = parseHelper.parse(TRANSITION_WITH_UPDATE_EVENT);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();
		
		String resourceAKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(resourceAKey));
		String resourceA = fsa.getFiles().get(resourceAKey).toString();
		
		assertTrue(resourceA.contains("<bean class=\"com.temenos.interaction.springdsl.TransitionFactoryBean\">"));
		assertTrue(resourceA.contains("<property name=\"flags\"><util:constant static-field=\"com.temenos.interaction.core.hypermedia.Transition.FOR_EACH\"/></property>"));
		assertTrue(resourceA.contains("<property name=\"method\" value=\"GET\" />"));
		assertTrue(resourceA.contains("<property name=\"target\" ref=\"Test_B\" />"));
		assertTrue(resourceA.contains("<property name=\"uriParameters\"><util:map></util:map></property>"));
		assertTrue(resourceA.contains("<property name=\"functionList\"><util:list></util:list></property>"));
		assertTrue(resourceA.contains("<property name=\"label\" value=\"B\" />"));

		//
		//assertTrue(resourceA.contains("sA.addTransition(new Transition.Builder()"));
		//assertTrue(resourceA.contains(".flags(Transition.FOR_EACH)"));
		//assertTrue(resourceA.contains(".method(\"GET\")"));
		//assertTrue(resourceA.contains(".target(sB)"));
		//assertTrue(resourceA.contains(".uriParameters(uriLinkageProperties)"));
		//assertTrue(resourceA.contains(".evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null)"));
		//assertTrue(resourceA.contains(".label(\"B\")"));

		String resourceBKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_B-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(resourceBKey));
		String resourceB = fsa.getFiles().get(resourceBKey).toString();
		
		//assertTrue(resourceB.contains("sB.addTransition(new Transition.Builder()"));
		//assertTrue(resourceB.contains(".method(\"PUT\")"));
		//assertTrue(resourceB.contains(".target(sB_pseudo)"));
		//assertTrue(resourceB.contains(".uriParameters(uriLinkageProperties)"));
		//assertTrue(resourceB.contains(".evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null)"));
		//assertTrue(resourceB.contains(".label(\"B_pseudo\")"));		
		assertTrue(resourceB.contains("<bean class=\"com.temenos.interaction.springdsl.TransitionFactoryBean\">"));
		assertTrue(resourceB.contains("<property name=\"method\" value=\"PUT\" />"));
		assertTrue(resourceB.contains("<property name=\"target\" ref=\"Test_B_pseudo\" />"));
		assertTrue(resourceB.contains("<property name=\"uriParameters\"><util:map></util:map></property>"));
		assertTrue(resourceB.contains("<property name=\"functionList\"><util:list></util:list></property>"));
		assertTrue(resourceB.contains("<property name=\"label\" value=\"B_pseudo\" />"));
	
		String resourceB_pseudoKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_B_pseudo-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(resourceB_pseudoKey));
		String resourceB_pseudo = fsa.getFiles().get(resourceB_pseudoKey).toString();
		//assertTrue(resourceB_pseudo.contains("sB_pseudo.addTransition(new Transition.Builder()"));
		//assertTrue(resourceB_pseudo.contains(".flags(Transition.AUTO)"));
		//assertTrue(resourceB_pseudo.contains(".target(sA)"));
		//assertTrue(resourceB_pseudo.contains(".uriParameters(uriLinkageProperties)"));
		//assertTrue(resourceB_pseudo.contains(".evaluation(conditionalLinkExpressions != null ? new SimpleLogicalExpressionEvaluator(conditionalLinkExpressions) : null)"));
		assertTrue(resourceB_pseudo.contains("<bean class=\"com.temenos.interaction.springdsl.TransitionFactoryBean\">"));
		assertTrue(resourceB_pseudo.contains("<property name=\"flags\"><util:constant static-field=\"com.temenos.interaction.core.hypermedia.Transition.AUTO\"/></property>"));
		assertTrue(resourceB_pseudo.contains("<property name=\"target\" ref=\"Test_A\" />"));
		assertTrue(resourceB_pseudo.contains("<property name=\"uriParameters\"><util:map>"));
		assertTrue(resourceB_pseudo.contains("</util:map>"));
		assertTrue(resourceB_pseudo.contains("<property name=\"functionList\"><!-- super(\"ENTITY\", \"B_pseudo\", createActions(state), \"/B_pseudo\", \"createLinkRelations()\", null, null);  -->"));
		assertTrue(resourceB_pseudo.contains(""));
		assertTrue(resourceB_pseudo.contains(""));	
	}

	private final static String RESOURCE_ON_ERROR = "" + "rim Test {" + LINE_SEP + "	command GetEntity" + LINE_SEP
			+ "	command Noop" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "	onerror --> AE" + LINE_SEP + "}" + LINE_SEP +

			"resource AE {" + LINE_SEP + "	type: item" + LINE_SEP + "	entity: ERROR" + LINE_SEP + "	view: Noop"
			+ LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + "";

	/**
	 * Test generate on error resource.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testGenerateOnErrorResource() throws Exception {
		DomainModel domainModel = parseHelper.parse(RESOURCE_ON_ERROR);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();
		
		// collection
		/*
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output.contains("super(\"ENTITY\", \"A\", createActions(), \"/A\", createLinkRelations(), null, factory.getResourceState(\"Test.AE\"));"));
		 
		 */
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_AE-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "TestServiceDocumentIRIS-PRD.xml"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml"));
		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output
				.contains("<bean id=\"Test_AE\" class=\"com.temenos.interaction.core.hypermedia.CollectionResourceState\">"));
		assertTrue(output.contains("<constructor-arg name=\"entityName\" value=\"ERROR\" />"));
		assertTrue(output.contains("<constructor-arg name=\"name\" value=\"AE\" />"));
		assertTrue(output.contains("<bean class=\"com.temenos.interaction.core.hypermedia.Action\">"));
		assertTrue(output.contains("<constructor-arg value=\"Noop\" />"));
		assertTrue(output.contains("<constructor-arg value=\"VIEW\" />"));
		assertTrue(output.contains("<constructor-arg name=\"path\" value=\"/AE\" />"));
	}

	private final static String RESOURCE_ON_ERROR_SEPARATE_RIM = "" + "domain ErrorTest {" + LINE_SEP + "rim Test {"
			+ LINE_SEP + "	command GetEntity" + LINE_SEP + "initial resource A {" + LINE_SEP + "	type: collection"
			+ LINE_SEP + "	entity: ENTITY" + LINE_SEP + "	view: GetEntity" + LINE_SEP + "	onerror --> Error.AE"
			+ LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP
			+ // end Test rim

			"rim Error {" + LINE_SEP + "	command Noop" + LINE_SEP + "resource AE {" + LINE_SEP + "	type: item"
			+ LINE_SEP + "	entity: ERROR" + LINE_SEP + "	view: Noop" + LINE_SEP + "}" + LINE_SEP + "}" + LINE_SEP + // end
																													// Error
																													// rim

			"}" + LINE_SEP + "";

	/**
	 * Test generate on error resource separate rim.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testGenerateOnErrorResourceSeparateRIM() throws Exception {
		DomainModel domainModel = parseHelper.parse(RESOURCE_ON_ERROR_SEPARATE_RIM);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(domainModel.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();
		
		// collection
		/*
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "ErrorTest/Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output.contains("super(\"ENTITY\", \"A\", createActions(), \"/A\", createLinkRelations(), null, factory.getResourceState(\"ErrorTest.Error.AE\"));"));
		 
		 */
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-ErrorTest_Test_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "ErrorTest/ErrorServiceDocumentIRIS-PRD.xml"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "ErrorTest/TestServiceDocumentIRIS-PRD.xml"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-ErrorTest_Error_AE-PRD.xml"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-ErrorTest_Test_A-PRD.xml"));

		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output.contains("<bean id=\"ErrorTest_Test_A\" class=\"com.temenos.interaction.core.hypermedia.CollectionResourceState\">"));
		assertTrue(output.contains("<constructor-arg name=\"entityName\" value=\"ENTITY\" />"));
		assertTrue(output.contains("<constructor-arg name=\"name\" value=\"A\" />"));
		assertTrue(output.contains("<bean class=\"com.temenos.interaction.core.hypermedia.Action\">"));
		assertTrue(output.contains("<constructor-arg value=\"GetEntity\" />"));
		assertTrue(output.contains("<constructor-arg value=\"VIEW\" />"));
		assertTrue(output.contains("<constructor-arg name=\"path\" value=\"/A\" />"));


	}

	private final static String RESOURCE_WITH_BASEPATH = "" + "rim Test {" + LINE_SEP + "	command GetEntity" + LINE_SEP
			+ "	command Noop" + LINE_SEP + "	basepath: \"/{companyid}\"" + LINE_SEP +

			"initial resource A {" + LINE_SEP + "	type: collection" + LINE_SEP + "	entity: ENTITY" + LINE_SEP
			+ "	view: GetEntity" + LINE_SEP + "	path: \"/A\"" + LINE_SEP + "}" + LINE_SEP +

			"}" + LINE_SEP + "";

	/**
	 * Test generate resource with basepath.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testGenerateResourceWithBasepath() throws Exception {
		DomainModel domainModel = parseHelper.parse(RESOURCE_WITH_BASEPATH);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);

		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		/*
		// collection
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output.contains("super(\"ENTITY\", \"A\", createActions(), \"/{companyid}/A\", createLinkRelations(), null, null);"));		 
		 */
		
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Test_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output
				.contains("<bean id=\"Test_A\" class=\"com.temenos.interaction.core.hypermedia.CollectionResourceState\">"));
		assertTrue(output.contains("<constructor-arg name=\"entityName\" value=\"ENTITY\" />"));
		assertTrue(output.contains("<constructor-arg name=\"name\" value=\"A\" />"));
		assertTrue(output.contains("<bean class=\"com.temenos.interaction.core.hypermedia.Action\">"));
		assertTrue(output.contains("<constructor-arg value=\"GetEntity\" />"));
		assertTrue(output.contains("<constructor-arg value=\"VIEW\" />"));
		assertTrue(output.contains("<constructor-arg name=\"path\" value=\"/{companyid}/A\" />"));
	}

	/**
	 * Test generate from incomplete rim.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testGenerateFromIncompleteRIM() throws Exception {
		DomainModel model = parseHelper.parse(INCOMPLETE_RIM);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		boolean exceptionThrown = false;
		try {
			underTest.doGenerate(model.eResource(), fsa);
		} catch (RuntimeException e) {
			exceptionThrown = true;
		}
		assertFalse(exceptionThrown);
	}

	/**
	 * Test generate with null.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testGenerateWithNull() throws Exception {
		boolean exceptionThrown = false;
		try {
			InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
			underTest.doGenerate(null, fsa);
		} catch (RuntimeException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
	}

	/**
	 * Test generate simple states.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testGenerateSimpleStates() throws Exception {
		DomainModel domainModel = parseHelper.parse(SIMPLE_STATES_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		System.out.println(fsa.getFiles());
		assertEquals(4, fsa.getFiles().size());

		// Verify keys
		String expectedKey1 = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Simple_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey1));

		String expectedKey2 = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Simple_B-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey2));

		String expectedKey3 = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Simple_E-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey3));

		String expectedKey4 = IFileSystemAccess.DEFAULT_OUTPUT + "SimpleServiceDocumentIRIS-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey4));

	}

	/**
	 * Test generate resource root.
	 * 
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testGenerateResourceRoot() throws Exception {

		DomainModel domainModel = parseHelper.parse(SIMPLE_STATES_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		System.out.println(fsa.getFiles());
		assertEquals(4, fsa.getFiles().size());

		// Verify keys
		String expectedKey1 = IFileSystemAccess.DEFAULT_OUTPUT + "SimpleServiceDocumentIRIS-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey1));

		String root = (String) allFiles.get(expectedKey1).toString();
		Reader reader = new StringReader(root);

		XMLInputFactory factory = XMLInputFactory.newInstance();
		// XMLStreamReader xmlStreamReader =
		// factory.createXMLStreamReader(reader);
		XMLEventReader eventReader = factory.createXMLEventReader(reader);
		String tagContent = null;

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// reach the start of an item
			if (event.isStartElement()) {

				StartElement startElement = event.asStartElement();
				System.out.println("startElement = " + startElement.getName().getLocalPart());

				if (startElement.getName().getLocalPart() == "bean") {
					// item = new Item();
					System.out.println("start bean...");
					// attribute
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						System.out.println("attribute.getName() = " + attribute.getName().toString());

						if (attribute.getName().toString().equals("id")) {
							beanId = attribute.getValue();
							System.out.println("beanId = " + attribute.getValue());
						} else if (attribute.getName().toString().equals("class")) {
							beanClass = attribute.getValue();
							System.out.println("beanClass = " + attribute.getValue());
							if ((beanId != null) && (beanId.equals(BEAN_ID_INITIAL_STATE))) {
								processState = PROCESSING_STATE.RESOURCE_STATE;
								beanId = "";
							} else if (beanClass.equals(ACTION)) {
								processState = PROCESSING_STATE.ACTION;
							} else if (beanClass.equals(TRANSITION_FACTORY_BEAN)) {
								processState = PROCESSING_STATE.FACTORY_BEAN;
							}
							assertTrue(beanClass.equals(ACTION) || beanClass.equals(TRANSITION_FACTORY_BEAN)
									|| beanClass.equals(RESOURCE_STATE));

						}
					}
					resetState();
					continue;
				}

				if (startElement.getName().getLocalPart() == "property") {
					if (processState == PROCESSING_STATE.ACTION) {
						processInitialStateProperty(startElement);
					} else if (processState == PROCESSING_STATE.FACTORY_BEAN) {
						processTransitionFactoryProperty(startElement);
					}
					continue;
				}

				// Process constructor-arg for current beanClass
				if (startElement.getName().getLocalPart() == "constructor-arg") {
					if (processState == PROCESSING_STATE.ACTION) {
						processActionConstructorArgs(startElement, beanClass);
					} else if (processState == PROCESSING_STATE.RESOURCE_STATE) {
						processResourceStateConstructorArgs(startElement, beanClass);
					}
				}

				// data
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals("thetext")) {
						event = eventReader.nextEvent();
						System.out.println("thetext: " + event.asCharacters().getData());
						/*
						 * if(item.getFirstText() == null){
						 * System.out.println("thetext: " +
						 * event.asCharacters().getData());
						 * item.setFirstText("notnull"); continue; }else{
						 * continue; }
						 */

					}
				}
			}

			// reach the end of an item
			if (event.isAttribute()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == "item") {
					System.out.println("--end of an item\n");
					// item = null;
				}
			}
			// reach the end of an item
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == "item") {
					System.out.println("--end of an item\n");
					// item = null;
				}
			}

		}
	}

	/**
	 * @param startElement
	 */
	private void processInitialStateProperty(StartElement startElement) {
		// item = new Item();
		System.out.println("start property...");
		// attribute
		String propertyValue = null;

		Iterator<Attribute> attributes = startElement.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			System.out.println("attribute.getName() = " + attribute.getName().toString());

			if (attribute.getName().toString().equals("name")) {
				propertyValue = attribute.getValue();
				System.out.println("propertyValue = " + propertyValue);

			}
		}

		assertTrue(propertyValue.equals("transitions"));

		resetState();
	}

	/**
	 * @param startElement
	 */
	private void processTransitionFactoryProperty(StartElement startElement) {
		// item = new Item();
		System.out.println("start property...");
		// attribute
		String propertyName1 = null;
		String propertyValue1 = null;

		Iterator<Attribute> attributes = startElement.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			System.out.println("attribute.getName() = " + attribute.getName().toString());

			if (attribute.getName().toString().equals("name")) {
				propertyName1 = attribute.getValue();
				System.out.println("propertyName1 = " + propertyName1);
			}

			if (attribute.getName().toString().equals("ref")) {
				propertyValue1 = attribute.getValue();
				System.out.println("propertyValue1 = " + propertyValue1);
			}

			if (attribute.getName().toString().equals("value")) {
				propertyValue1 = attribute.getValue();
				System.out.println("propertyValue1 = " + propertyValue1);
			}

		}

		if (propertyName1.equals("target")) {
			assertTrue((propertyValue1.equals("A")) || (propertyValue1.equals("B")) || (propertyValue1.equals("E")));
		} else {
			assertTrue((propertyName1.equals("method")) || (propertyValue1.equals("GET")));
		}
		resetState();
	}

	/**
	 * 
	 */
	private void resetState() {
		// constructorValue1 = null;
		// constructorValue2 = null;
		beanClass = null;
		beanId = null;
	}

	/**
	 * Process resource constructor args.
	 * 
	 * @param startElement
	 *            the start element
	 * @param beanClass
	 *            the bean class
	 */
	private void processActionConstructorArgs(StartElement startElement, String beanClass) {

		// item = new Item();
		System.out.println("constructor...");
		// attribute
		String constructorName = null;
		String constructorValue1 = null;
		String constructorValue2 = null;

		Iterator<Attribute> attributes = startElement.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			System.out.println("attribute.getName() = " + attribute.getName().toString());
			System.out.println("attribute.getValue() = " + attribute.getValue().toString());

			if (constructorValue1 == null) {
				constructorValue1 = attribute.getValue();
				System.out.println("constructorValue1 = " + attribute.getValue());
				assertTrue(constructorValue1.equals("GETServiceDocument") || constructorValue1.equals("VIEW")
						|| constructorValue1.equals("path") || constructorValue1.equals("ENTRY")|| constructorValue1.equals("UpdateEntity") || constructorValue1.equals("GetEntity") || constructorValue1.equals("GETEntities")  || constructorValue1.equals("GetException"));
			} else if (constructorValue2 == null) {
				constructorValue2 = attribute.getValue();
				System.out.println("constructorValue2 = " + attribute.getValue());
				assertTrue(constructorValue2.equals("/A") || constructorValue2.equals("/")
						|| constructorValue2.equals("/B") || constructorValue2.equals("/E"));

			}

		}

		resetState();
	}

	/**
	 * Process collection resource state constructor args.
	 * 
	 * @param startElement
	 *            the start element
	 * @param beanClass
	 *            the bean class
	 */
	private void processCollectionResourceStateConstructorArgs(StartElement startElement, String beanClass) {

		// item = new Item();
		System.out.println("constructor...");
		// attribute
		String constructorValue1 = null;
		String constructorValue2 = null;

		Iterator<Attribute> attributes = startElement.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			System.out.println("attribute.getName() = " + attribute.getName().toString());
			System.out.println("attribute.getValue() = " + attribute.getValue().toString());

			if (constructorValue1 == null) {
				constructorValue1 = attribute.getValue();
				System.out.println("constructorValue1 = " + attribute.getValue());
				assertTrue((constructorValue1.equals("entityName")) || (constructorValue1.equals("name")));
			} else if (constructorValue2 == null) {
				constructorValue2 = attribute.getValue();
				System.out.println("constructorValue2 = " + attribute.getValue());
				assertTrue(constructorValue2.equals("ENTITY") || constructorValue2.equals("A")
						|| constructorValue2.equals("B") || constructorValue2.equals("E")
						|| constructorValue2.equals("EXCEPTION"));

			}

		}

		resetState();
	}

	/*
	 * 
	 * <constructor-arg name="name" value="A" /> <constructor-arg> <list> <bean
	 * class="com.temenos.interaction.core.hypermedia.Action"> <constructor-arg
	 * value="GETEntities" /> <constructor-arg value="VIEW" /> </bean> </list>
	 * </constructor-arg> <constructor-arg name="path" value="/A" /> <property
	 * name="transitions"> <list>
	 * 
	 * <!-- Start property transitions list --> <property name="transitions">
	 * <list>
	 */
	@Test
	public void testGenerateResourceB() throws Exception {
		DomainModel domainModel = parseHelper.parse(SIMPLE_STATES_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		System.out.println(fsa.getFiles());
		assertEquals(4, fsa.getFiles().size());

		// Verify keys
		String expectedKey2 = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Simple_B-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey2));

		String root = (String) allFiles.get(expectedKey2).toString();
		Reader reader = new StringReader(root);

		XMLInputFactory factory = XMLInputFactory.newInstance();
		// XMLStreamReader xmlStreamReader =
		// factory.createXMLStreamReader(reader);
		XMLEventReader eventReader = factory.createXMLEventReader(reader);
		String tagContent = null;

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// reach the start of an item
			if (event.isStartElement()) {

				StartElement startElement = event.asStartElement();
				System.out.println("startElement = " + startElement.getName().getLocalPart());

				if (startElement.getName().getLocalPart() == "bean") {
					// item = new Item();
					System.out.println("start bean...");
					// attribute
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						System.out.println("attribute.getName() = " + attribute.getName().toString());

						if (attribute.getName().toString().equals("id")) {
							beanId = attribute.getValue();
							System.out.println("beanId = " + attribute.getValue());
						} else if (attribute.getName().toString().equals("class")) {
							beanClass = attribute.getValue();
							System.out.println("beanClass = " + attribute.getValue());
							if (beanClass.equals(BEAN_ID_COLLECTION_RESOURCE_STATE)) {
								processState = PROCESSING_STATE.COLLECTION_RESOURCE_STATE;
								assertTrue(beanId.equals("A") || beanId.equals("Simple_B"));
							} else if (beanClass.equals(ACTION)) {
								processState = PROCESSING_STATE.ACTION;
							} else {
								throw new Exception("Invalid beanClass");
							}

						}
					}
					resetState();
					continue;
				}

				if (startElement.getName().getLocalPart() == "property") {
					if (processState == PROCESSING_STATE.ACTION) {
						processInitialStateProperty(startElement);
					} else if (processState == PROCESSING_STATE.FACTORY_BEAN) {
						processTransitionFactoryProperty(startElement);
					}
					continue;
				}

				// Process constructor-arg for current beanClass
				if (startElement.getName().getLocalPart() == "constructor-arg") {
					if (processState == PROCESSING_STATE.ACTION) {
						processActionConstructorArgs(startElement, beanClass);
					} else if (processState == PROCESSING_STATE.RESOURCE_STATE) {
						processResourceStateConstructorArgs(startElement, beanClass);
					} else if (processState == PROCESSING_STATE.COLLECTION_RESOURCE_STATE) {
						processCollectionResourceStateConstructorArgs(startElement, beanClass);
					}

				}

				// data
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals("thetext")) {
						event = eventReader.nextEvent();
						System.out.println("thetext: " + event.asCharacters().getData());
						/*
						 * if(item.getFirstText() == null){
						 * System.out.println("thetext: " +
						 * event.asCharacters().getData());
						 * item.setFirstText("notnull"); continue; }else{
						 * continue; }
						 */

					}
				}
			}

			// reach the end of an item
			if (event.isAttribute()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == "item") {
					System.out.println("--end of an item\n");
					// item = null;
				}
			}
			// reach the end of an item
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == "item") {
					System.out.println("--end of an item\n");
					// item = null;
				}
			}

		}
	}

	/**
	 * Process resource constructor args.
	 * 
	 * @param startElement
	 *            the start element
	 * @param beanClass
	 *            the bean class
	 */
	private void processResourceStateConstructorArgs(StartElement startElement, String beanClass) {

		// item = new Item();
		System.out.println("constructor...");
		// attribute
		String constructorName = null;
		String constructorValue1 = null;

		Iterator<Attribute> attributes = startElement.getAttributes();
		while (attributes.hasNext()) {
			Attribute attribute = attributes.next();
			System.out.println("attribute.getName() = " + attribute.getName().toString());
			System.out.println("attribute.getValue() = " + attribute.getValue().toString());

			if (constructorName == null) {
				constructorName = attribute.getValue();
				System.out.println("constructorName = " + attribute.getValue());

			} else if (constructorValue1 == null) {
				constructorValue1 = attribute.getValue();
				System.out.println("constructorValue = " + attribute.getValue());
			}

		}

		if ((constructorName != null) && (constructorValue1 != null)) {
			assertTrue(constructorName.equals("entityName") || constructorName.equals("name"));
			assertTrue(constructorValue1.equals("ServiceDocument"));
		}
	}

	/*
	 * 
	 * <constructor-arg name="name" value="A" /> <constructor-arg> <list> <bean
	 * class="com.temenos.interaction.core.hypermedia.Action"> <constructor-arg
	 * value="GETEntities" /> <constructor-arg value="VIEW" /> </bean> </list>
	 * </constructor-arg> <constructor-arg name="path" value="/A" /> <property
	 * name="transitions"> <list>
	 * 
	 * <!-- Start property transitions list --> <property name="transitions">
	 * <list>
	 */
	@Test
	public void testGenerateResourceA() throws Exception {
		DomainModel domainModel = parseHelper.parse(SIMPLE_STATES_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		System.out.println(fsa.getFiles());
		assertEquals(4, fsa.getFiles().size());

		// Verify keys
		String expectedKey2 = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Simple_A-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey2));
		
		String root = (String) allFiles.get(expectedKey2).toString();
		Reader reader = new StringReader(root);

		XMLInputFactory factory = XMLInputFactory.newInstance();
		// XMLStreamReader xmlStreamReader =
		// factory.createXMLStreamReader(reader);
		XMLEventReader eventReader = factory.createXMLEventReader(reader);
		String tagContent = null;

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// reach the start of an item
			if (event.isStartElement()) {

				StartElement startElement = event.asStartElement();
				System.out.println("startElement = " + startElement.getName().getLocalPart());

				if (startElement.getName().getLocalPart() == "bean") {
					// item = new Item();
					System.out.println("start bean...");
					// attribute
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						System.out.println("attribute.getName() = " + attribute.getName().toString());

						if (attribute.getName().toString().equals("id")) {
							beanId = attribute.getValue();
							System.out.println("beanId = " + attribute.getValue());
						} else if (attribute.getName().toString().equals("class")) {
							beanClass = attribute.getValue();
							System.out.println("beanClass = " + attribute.getValue());
							if (beanClass.equals(BEAN_ID_COLLECTION_RESOURCE_STATE)) {
								processState = PROCESSING_STATE.COLLECTION_RESOURCE_STATE;
								assertTrue(beanId.equals("Simple_A"));
							} else if (beanClass.equals(ACTION)) {
								processState = PROCESSING_STATE.ACTION;
							} else {
								throw new Exception("Invalid beanClass");
							}

						}
					}
					resetState();
					continue;
				}

				if (startElement.getName().getLocalPart() == "property") {
					if (processState == PROCESSING_STATE.ACTION) {
						processInitialStateProperty(startElement);
					} else if (processState == PROCESSING_STATE.FACTORY_BEAN) {
						processTransitionFactoryProperty(startElement);
					}
					continue;
				}

				// Process constructor-arg for current beanClass
				if (startElement.getName().getLocalPart() == "constructor-arg") {
					if (processState == PROCESSING_STATE.ACTION) {
						processActionConstructorArgs(startElement, beanClass);
					} else if (processState == PROCESSING_STATE.RESOURCE_STATE) {
						processResourceStateConstructorArgs(startElement, beanClass);
					} else if (processState == PROCESSING_STATE.COLLECTION_RESOURCE_STATE) {
						processCollectionResourceStateConstructorArgs(startElement, beanClass);
					}

				}

				// data
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals("thetext")) {
						event = eventReader.nextEvent();
						System.out.println("thetext: " + event.asCharacters().getData());
						/*
						 * if(item.getFirstText() == null){
						 * System.out.println("thetext: " +
						 * event.asCharacters().getData());
						 * item.setFirstText("notnull"); continue; }else{
						 * continue; }
						 */

					}
				}
			}

			// reach the end of an item
			if (event.isAttribute()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == "item") {
					System.out.println("--end of an item\n");
					// item = null;
				}
			}
			// reach the end of an item
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == "item") {
					System.out.println("--end of an item\n");
					// item = null;
				}
			}

		}
	}

	/*
	 * 
	 * <constructor-arg name="name" value="A" /> <constructor-arg> <list> <bean
	 * class="com.temenos.interaction.core.hypermedia.Action"> <constructor-arg
	 * value="GETEntities" /> <constructor-arg value="VIEW" /> </bean> </list>
	 * </constructor-arg> <constructor-arg name="path" value="/A" /> <property
	 * name="transitions"> <list>
	 * 
	 * <!-- Start property transitions list --> <property name="transitions">
	 * <list>
	 */
	@Test
	public void testGenerateResourceE() throws Exception {
		DomainModel domainModel = parseHelper.parse(SIMPLE_STATES_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		Map<String, Object> allFiles = fsa.getAllFiles();
		Set<String> keys = allFiles.keySet();

		System.out.println(fsa.getFiles());
		assertEquals(4, fsa.getFiles().size());

		// Verify keys
		String expectedKey2 = IFileSystemAccess.DEFAULT_OUTPUT + "IRIS-Simple_E-PRD.xml";
		assertTrue(fsa.getFiles().containsKey(expectedKey2));

		String root = (String) allFiles.get(expectedKey2).toString();
		Reader reader = new StringReader(root);


		XMLInputFactory factory = XMLInputFactory.newInstance();
		// XMLStreamReader xmlStreamReader =
		// factory.createXMLStreamReader(reader);
		XMLEventReader eventReader = factory.createXMLEventReader(reader);
		String tagContent = null;

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			// reach the start of an item
			if (event.isStartElement()) {

				StartElement startElement = event.asStartElement();
				System.out.println("startElement = " + startElement.getName().getLocalPart());

				if (startElement.getName().getLocalPart() == "bean") {
					// item = new Item();
					System.out.println("start bean...");
					// attribute
					Iterator<Attribute> attributes = startElement.getAttributes();
					while (attributes.hasNext()) {
						Attribute attribute = attributes.next();
						System.out.println("attribute.getName() = " + attribute.getName().toString());

						if (attribute.getName().toString().equals("id")) {
							beanId = attribute.getValue();
							System.out.println("beanId = " + attribute.getValue());
						} else if (attribute.getName().toString().equals("class")) {
							beanClass = attribute.getValue();
							System.out.println("beanClass = " + attribute.getValue());
							if (beanClass.equals(BEAN_ID_COLLECTION_RESOURCE_STATE)) {
								processState = PROCESSING_STATE.COLLECTION_RESOURCE_STATE;
								assertTrue(beanId.equals("A") || beanId.equals("B") || beanId.equals("Simple_E"));
							} else if (beanClass.equals(ACTION)) {
								processState = PROCESSING_STATE.ACTION;
							} else {
								throw new Exception("Invalid beanClass");
							}

						}
					}
					resetState();
					continue;
				}

				if (startElement.getName().getLocalPart() == "property") {
					if (processState == PROCESSING_STATE.ACTION) {
						processInitialStateProperty(startElement);
					} else if (processState == PROCESSING_STATE.FACTORY_BEAN) {
						processTransitionFactoryProperty(startElement);
					}
					continue;
				}

				// Process constructor-arg for current beanClass
				if (startElement.getName().getLocalPart() == "constructor-arg") {
					if (processState == PROCESSING_STATE.ACTION) {
						processActionConstructorArgs(startElement, beanClass);
					} else if (processState == PROCESSING_STATE.RESOURCE_STATE) {
						processResourceStateConstructorArgs(startElement, beanClass);
					} else if (processState == PROCESSING_STATE.COLLECTION_RESOURCE_STATE) {
						processCollectionResourceStateConstructorArgs(startElement, beanClass);
					}

				}

				// data
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().equals("thetext")) {
						event = eventReader.nextEvent();
						System.out.println("thetext: " + event.asCharacters().getData());
						/*
						 * if(item.getFirstText() == null){
						 * System.out.println("thetext: " +
						 * event.asCharacters().getData());
						 * item.setFirstText("notnull"); continue; }else{
						 * continue; }
						 */

					}
				}
			}

			// reach the end of an item
			if (event.isAttribute()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == "item") {
					System.out.println("--end of an item\n");
					// item = null;
				}
			}
			// reach the end of an item
			if (event.isEndElement()) {
				EndElement endElement = event.asEndElement();
				if (endElement.getName().getLocalPart() == "item") {
					System.out.println("--end of an item\n");
					// item = null;
				}
			}

		}
	}
}
