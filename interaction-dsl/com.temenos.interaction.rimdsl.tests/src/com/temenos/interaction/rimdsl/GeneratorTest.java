package com.temenos.interaction.rimdsl;

import static org.junit.Assert.*;

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

@InjectWith(RIMDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class GeneratorTest {
	
	@Inject 
	IGenerator underTest;
	@Inject
	ParseHelper<DomainModel> parseHelper;
	
	private final static String LINE_SEP = System.getProperty("line.separator");
	
	private final static String SIMPLE_STATES_RIM = "" +
	"rim Simple {" + LINE_SEP +
	"commands" + LINE_SEP +
	"	GetEntity" + LINE_SEP +
	"	GetException" + LINE_SEP +
	"	UpdateEntity" + LINE_SEP +
	"end" + LINE_SEP +
			
	"initial resource A" + LINE_SEP +
	"	collection ENTITY" + LINE_SEP +
	"	view { GetEntity }" + LINE_SEP +
	"end" + LINE_SEP +

	"exception resource E" + LINE_SEP +
	"	collection EXCEPTION" + LINE_SEP +
	"	view { GetException }" + LINE_SEP +
	"end" + LINE_SEP +
	
	"resource B" +
	"	item ENTITY" + LINE_SEP +
	"	actions { UpdateEntity }" + LINE_SEP +
	"end" + LINE_SEP +
	"}" + LINE_SEP +
	"";

	private final static String SIMPLE_STATES_BEHAVIOUR = "" +		
	"package __synthetic0Model;" + LINE_SEP +
	LINE_SEP +
	"import java.util.ArrayList;" + LINE_SEP +
	"import java.util.HashMap;" + LINE_SEP +
	"import java.util.List;" + LINE_SEP +
	"import java.util.Map;" + LINE_SEP +
	"import java.util.Properties;" + LINE_SEP +
	LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.UriSpecification;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.Action;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.CollectionResourceState;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.ResourceFactory;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.ResourceState;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.ResourceStateMachine;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.validation.HypermediaValidator;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.expression.Expression;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.expression.ResourceGETExpression;" + LINE_SEP +
	"import com.temenos.interaction.core.resource.ResourceMetadataManager;" + LINE_SEP +
	LINE_SEP +
	"public class __synthetic0Behaviour {" + LINE_SEP +
	LINE_SEP +
	"    public static void main(String[] args) {" + LINE_SEP +
	"        __synthetic0Behaviour behaviour = new __synthetic0Behaviour();" + LINE_SEP +
	"        ResourceStateMachine hypermediaEngine = new ResourceStateMachine(behaviour.getRIM(), behaviour.getExceptionResource());" + LINE_SEP +
	"        HypermediaValidator validator = HypermediaValidator.createValidator(hypermediaEngine, new ResourceMetadataManager(hypermediaEngine).getMetadata());" + LINE_SEP +
	"        System.out.println(validator.graph());" + LINE_SEP +
	"    }" + LINE_SEP +
	LINE_SEP +
	"    public ResourceState getRIM() {" + LINE_SEP +
	"        Map<String, String> uriLinkageEntityProperties = new HashMap<String, String>();" + LINE_SEP +
	"        Map<String, String> uriLinkageProperties = new HashMap<String, String>();" + LINE_SEP +
	"        List<Expression> conditionalLinkExpressions = null;" + LINE_SEP +
	"        Properties actionViewProperties;" + LINE_SEP +
	LINE_SEP +
	"        ResourceFactory factory = new ResourceFactory();" + LINE_SEP +
	"        ResourceState initial = null;" + LINE_SEP +
	"        // create states" + LINE_SEP +
	"        // identify the initial state" + LINE_SEP +
	"        initial = factory.getResourceState(\"__synthetic0Model.A\");" + LINE_SEP +
	"        return initial;" + LINE_SEP +
	"    }" + LINE_SEP +
	LINE_SEP +
	"    public ResourceState getExceptionResource() {" + LINE_SEP +
	"        ResourceFactory factory = new ResourceFactory();" + LINE_SEP +
	"        ResourceState exceptionState = null;" + LINE_SEP +
	"        exceptionState = factory.getResourceState(\"__synthetic0Model.E\");" + LINE_SEP +
	"        return exceptionState;" + LINE_SEP +
	"    }" + LINE_SEP +
	"}" + LINE_SEP;
	
	@Test
	public void testGenerateSimpleStates() throws Exception {
		DomainModel domainModel = parseHelper.parse(SIMPLE_STATES_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		System.out.println(fsa.getFiles());
		assertEquals(4, fsa.getFiles().size());
		
		// the behaviour class
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/SimpleBehaviour.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		assertEquals(SIMPLE_STATES_BEHAVIOUR, fsa.getFiles().get(expectedKey).toString());
		
		// one class per resource
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Simple/AResourceState.java"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Simple/BResourceState.java"));
		
	}

	
	private final static String SINGLE_STATE_VIEW_COMMAND_ONLY_RIM = "" +
	"rim Test {" + LINE_SEP +
	"commands" + LINE_SEP +
	"	GetEntity" + LINE_SEP +
	"end" + LINE_SEP +
			
	"initial resource A" + LINE_SEP +
	"	collection ENTITY" + LINE_SEP +
	"	view { GetEntity }" + LINE_SEP +
	"end" + LINE_SEP +
	"}" + LINE_SEP +
	"";

	/*
	 * doGenerate should producer one file per resource
	 */
	@Test
	public void testGenerateOneFile() throws Exception {
		DomainModel domainModel = parseHelper.parse(SINGLE_STATE_VIEW_COMMAND_ONLY_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		System.out.println(fsa.getFiles());
		assertEquals(2, fsa.getFiles().size());

		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/TestBehaviour.java"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/AResourceState.java"));

	}

	private final static String SINGLE_STATE_WITH_PACKAGE_RIM = "" +
	"domain blah {" + LINE_SEP +
	"rim Test {" + LINE_SEP +
	"commands" + LINE_SEP +
	"	GetEntity" + LINE_SEP +
	"end" + LINE_SEP +
			
	"initial resource A" + LINE_SEP +
	"	collection ENTITY" + LINE_SEP +
	"	view { GetEntity }" + LINE_SEP +
	"end" + LINE_SEP +
	"}" + LINE_SEP +
	"}" + LINE_SEP +
	"";

	/*
	 * doGenerate should producer one file per resource
	 */
	@Test
	public void testGenerateFileInPackage() throws Exception {
		DomainModel domainModel = parseHelper.parse(SINGLE_STATE_WITH_PACKAGE_RIM);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(domainModel.eResource(), fsa);
		System.out.println(fsa.getFiles());
		assertEquals(2, fsa.getFiles().size());

		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "blah/TestBehaviour.java"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "blah/Test/AResourceState.java"));

	}

	@Test
	public void testGenerateSingleStateViewCommandOnly() throws Exception {
		DomainModel domainModel = parseHelper.parse(SINGLE_STATE_VIEW_COMMAND_ONLY_RIM);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(domainModel.eResource(), fsa);
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		assertTrue(fsa.getFiles().get(expectedKey).toString().contains("new Action(\"GetEntity\", Action.TYPE.VIEW, new Properties())"));
	}

	private final static String SINGLE_STATE_ACTION_COMMANDS_RIM = "" +
	"rim Test {" + LINE_SEP +
	"commands" + LINE_SEP +
	"	GetEntity getkey=getvalue" + LINE_SEP +
	"end" + LINE_SEP +
			
	"initial resource A" + LINE_SEP +
	"	collection ENTITY" + LINE_SEP +
	"	view { GetEntity }" + LINE_SEP +
	"end" + LINE_SEP +
	"}" + LINE_SEP +
	"";

	@Test
	public void testGenerateSingleStateActionCommands() throws Exception {
		DomainModel domainModel = parseHelper.parse(SINGLE_STATE_ACTION_COMMANDS_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));

		String output = fsa.getFiles().get(expectedKey).toString();
		int indexOfFirstNewProperties = output.indexOf("actionViewProperties = new Properties()");
		assertTrue(indexOfFirstNewProperties > 0);
		assertTrue(output.contains("actionViewProperties.put(\"getkey\", \"getvalue\""));
		assertTrue(output.contains("new Action(\"GetEntity\", Action.TYPE.VIEW, actionViewProperties)"));
		
		//No onerror handler so should not define an error state
		assertTrue(output.contains("super(\"ENTITY\", \"A\", createActions(), \"/A\", createLinkRelations(), null, null);"));
	}

	private final static String MULTIPLE_STATES_MULTIPLE_ACTION_COMMANDS_RIM = "" +
	"rim Test {" + LINE_SEP +
	"commands" + LINE_SEP +
	"	DoStuff key=value" + LINE_SEP +
	"	DoSomeStuff keyB=valueB" + LINE_SEP +
	"	DoSomeMoreStuff keyB0=valueB0, keyB1=valueB1" + LINE_SEP +
	"end" + LINE_SEP +
			
	"initial resource A" + LINE_SEP +
	"	collection ENTITY" + LINE_SEP +
	"	actions { DoStuff }" + LINE_SEP +
	"end" + LINE_SEP +

	"initial resource B" + LINE_SEP +
	"	collection ENTITY" + LINE_SEP +
	"	actions { DoSomeStuff; DoSomeMoreStuff }" + LINE_SEP +
	"end" + LINE_SEP +
	"}" + LINE_SEP +
	"";

	@Test
	public void testGenerateMultipleStateMultipleActionCommands() throws Exception {
		DomainModel domainModel = parseHelper.parse(MULTIPLE_STATES_MULTIPLE_ACTION_COMMANDS_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		String resouceAKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(resouceAKey));
		String resourceA = fsa.getFiles().get(resouceAKey).toString();
		int indexOfFirstNewProperties = resourceA.indexOf("actionViewProperties = new Properties()");
		assertTrue(indexOfFirstNewProperties > 0);
		assertTrue(resourceA.contains("actionViewProperties.put(\"key\", \"value\""));
		assertTrue(resourceA.contains("new Action(\"DoStuff\", Action.TYPE.ENTRY, actionViewProperties)"));

		String resouceBKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/BResourceState.java";
		assertTrue(fsa.getFiles().containsKey(resouceBKey));
		String resourceB = fsa.getFiles().get(resouceBKey).toString();
		int indexOfSecondNewProperties = resourceB.indexOf("actionViewProperties = new Properties()", indexOfFirstNewProperties);
		assertTrue(indexOfSecondNewProperties > 0);
		assertTrue(resourceB.contains("actionViewProperties.put(\"keyB\", \"valueB\""));
		assertTrue(resourceB.contains("new Action(\"DoSomeStuff\", Action.TYPE.ENTRY, actionViewProperties)"));
		assertTrue(resourceB.contains("actionViewProperties.put(\"keyB0\", \"valueB0\""));
		assertTrue(resourceB.contains("actionViewProperties.put(\"keyB1\", \"valueB1\""));
		assertTrue(resourceB.contains("new Action(\"DoSomeMoreStuff\", Action.TYPE.ENTRY, actionViewProperties)"));
	
	}

	private final static String TRANSITION_WITH_EXPRESSION_RIM = "" +
			"rim Test {" + LINE_SEP +
			"events" + LINE_SEP +
			"	GET GET" + LINE_SEP +
			"end" + LINE_SEP +
			
			"commands" + LINE_SEP +
			"	GetEntity properties" + LINE_SEP +
			"	GetEntities properties" + LINE_SEP +
			"	PutEntity properties" + LINE_SEP +
			"end" + LINE_SEP +
					
			"initial resource A" + LINE_SEP +
			"	collection ENTITY" + LINE_SEP +
			"	view { GetEntities }" + LINE_SEP +
			"	GET -> B (OK(B))" + LINE_SEP +
			"	GET -> B (NOT_FOUND(B))" + LINE_SEP +
			"	GET -> B (OK(C) && NOT_FOUND(D))" + LINE_SEP +
			"end" + LINE_SEP +

			"resource B" +
			"	item ENTITY" + LINE_SEP +
			"	view { GetEntity }" + LINE_SEP +
			"end" + LINE_SEP +
			"resource C" +
			"	item ENTITY" + LINE_SEP +
			"	view { GetEntity }" + LINE_SEP +
			"end" + LINE_SEP +
			"resource D" +
			"	item ENTITY" + LINE_SEP +
			"	view { GetEntity }" + LINE_SEP +
			"end" + LINE_SEP +
			"}" + LINE_SEP +
			"";

	@Test
	public void testGenerateTransitionsWithExpressions() throws Exception {
		DomainModel domainModel = parseHelper.parse(TRANSITION_WITH_EXPRESSION_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		
		final String NEW_STATEMENT = "conditionalLinkExpressions = new ArrayList<Expression>();";
		final String ADD_TRANSITION = "sA.addTransition(\"GET\", sB, uriLinkageEntityProperties, uriLinkageProperties, 0, conditionalLinkExpressions, \"B\")";
		
		int indexOfNewStatement = output.indexOf(NEW_STATEMENT);
		assertTrue(indexOfNewStatement > 0);
		assertTrue(output.contains("conditionalLinkExpressions.add(new ResourceGETExpression(\"B\", ResourceGETExpression.Function.OK))"));
		int indexOfAddTransition = output.indexOf(ADD_TRANSITION);
		assertTrue(indexOfAddTransition > 0);

		indexOfNewStatement = output.indexOf(NEW_STATEMENT, indexOfNewStatement);
		assertTrue(indexOfNewStatement > 0);
		assertTrue(output.contains("conditionalLinkExpressions.add(new ResourceGETExpression(\"B\", ResourceGETExpression.Function.NOT_FOUND))"));
		indexOfAddTransition = output.indexOf(ADD_TRANSITION, indexOfAddTransition);
		assertTrue(indexOfAddTransition > 0);
		
		indexOfNewStatement = output.indexOf(NEW_STATEMENT, indexOfNewStatement);
		assertTrue(indexOfNewStatement > 0);
		assertTrue(output.contains("conditionalLinkExpressions.add(new ResourceGETExpression(\"C\", ResourceGETExpression.Function.OK))"));
		assertTrue(output.contains("conditionalLinkExpressions.add(new ResourceGETExpression(\"D\", ResourceGETExpression.Function.NOT_FOUND))"));
		indexOfAddTransition = output.indexOf(ADD_TRANSITION, indexOfAddTransition);
		assertTrue(indexOfAddTransition > 0);
	}

	private final static String AUTO_TRANSITION_WITH_URI_LINKAGE_RIM = "" +
			"rim Test {" + LINE_SEP +
			"events" + LINE_SEP +
			"	GET GET" + LINE_SEP +
			"end" + LINE_SEP +
			
			"commands" + LINE_SEP +
			"	GetEntity properties" + LINE_SEP +
			"	GetEntities properties" + LINE_SEP +
			"	CreateEntity properties" + LINE_SEP +
			"end" + LINE_SEP +
					
			"initial resource A" + LINE_SEP +
			"	collection ENTITY" + LINE_SEP +
			"	view { GetEntities }" + LINE_SEP +
			"	POST -> create_pseudo_state" + LINE_SEP +
			"end" + LINE_SEP +

			"resource create_pseudo_state" +
			"	item ENTITY" + LINE_SEP +
			"	actions { CreateEntity }" + LINE_SEP +
			"   GET --> created id=MyId" + LINE_SEP +
			"end" + LINE_SEP +
			"resource created" +
			"	item ENTITY" + LINE_SEP +
			"	view { GetEntity }" + LINE_SEP +
			"end" + LINE_SEP +
			"}" + LINE_SEP +
			"";

	@Test
	public void testGenerateAutoTransitionsWithUriLinkage() throws Exception {
		DomainModel domainModel = parseHelper.parse(AUTO_TRANSITION_WITH_URI_LINKAGE_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/create_pseudo_stateResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		
		assertTrue(output.contains("uriLinkageEntityProperties.put(\"id\", \"MyId\");"));
		assertTrue(output.contains("screate_pseudo_state.addTransition(screated, uriLinkageEntityProperties, uriLinkageProperties);"));
	}

	private final static String RESOURCE_RELATIONS_RIM = "" +
			"rim Test {" + LINE_SEP +
			"commands" + LINE_SEP +
			"	Noop" + LINE_SEP +
			"	Update" + LINE_SEP +
			"end" + LINE_SEP +
			
			"initial resource accTransactions" + LINE_SEP +
			"	collection ENTITY" + LINE_SEP +
			"   view { Noop }" + LINE_SEP +
			"   relations { \"archives\", \"http://www.temenos.com/statement-entries\" }" + LINE_SEP +
			"   GET -> B" + LINE_SEP +
			"end\r\n" + LINE_SEP +
			"resource accTransaction" + LINE_SEP +
			"	item ENTITY" + LINE_SEP +
			"   actions { Update }" + LINE_SEP +
			"   relations { \"edit\" }" + LINE_SEP +
			"end\r\n" + LINE_SEP +
			"}" + LINE_SEP +
			"";
	
	@Test
	public void testGenerateResourcesWithRelations() throws Exception {
		DomainModel domainModel = parseHelper.parse(RESOURCE_RELATIONS_RIM);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		// collection
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/accTransactionsResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String accTransactionsOutput = fsa.getFiles().get(expectedKey).toString();
		// the constructor part
		assertTrue(accTransactionsOutput.contains("\"/accTransactions\", createLinkRelations()"));
		// createLinkRelations method
		String expectedAccTransactionsRelArray = "" +
			"        String accTransactionsRelationsStr = \"\";" + LINE_SEP +
			"        accTransactionsRelationsStr += \"archives \";" + LINE_SEP +
			"        accTransactionsRelationsStr += \"http://www.temenos.com/statement-entries \";" + LINE_SEP +
			"        String[] accTransactionsRelations = accTransactionsRelationsStr.trim().split(\" \");" + LINE_SEP +
			"";
		assertTrue(accTransactionsOutput.contains(expectedAccTransactionsRelArray));
		
		// item
		expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/accTransactionResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String accTransactionOutput = fsa.getFiles().get(expectedKey).toString();
		// the constructor part
		assertTrue(accTransactionOutput.contains("\"/accTransaction\", createLinkRelations()"));
		// createLinkRelations method
		String expectedAccTransactionRelArray = "" +
			"        String accTransactionRelationsStr = \"\";" + LINE_SEP +
			"        accTransactionRelationsStr += \"edit \";" + LINE_SEP +
			"        String[] accTransactionRelations = accTransactionRelationsStr.trim().split(\" \");" + LINE_SEP +
			"";
		assertTrue(accTransactionOutput.contains(expectedAccTransactionRelArray));
	}

	private final static String TRANSITION_WITH_UPDATE_EVENT = "" +
			"rim Test {" + LINE_SEP +
			"events" + LINE_SEP +
			"	GET GET" + LINE_SEP +
			"	UPDATE PUT" + LINE_SEP +
			"end" + LINE_SEP +
			
			"commands" + LINE_SEP +
			"	GetEntities properties" + LINE_SEP +
			"	GetEntity properties" + LINE_SEP +
			"	PutEntity properties" + LINE_SEP +
			"end" + LINE_SEP +
					
			"initial resource A" + LINE_SEP +
			"	collection ENTITY" + LINE_SEP +
			"	view { GetEntities }" + LINE_SEP +
			"	GET *-> B" + LINE_SEP +
			"end" + LINE_SEP +

			"resource B" +
			"	item ENTITY" + LINE_SEP +
			"	view { GetEntity }" + LINE_SEP +
			"	UPDATE -> B_pseudo" + LINE_SEP +
			"end" + LINE_SEP +

			"resource B_pseudo" +
			"	item ENTITY" + LINE_SEP +
			"	actions { PutEntity }" + LINE_SEP +
			"	GET --> A (NOT_FOUND(B))" + LINE_SEP +
			"	GET --> B  (OK(B))" + LINE_SEP +
			"end" + LINE_SEP +

			"}" + LINE_SEP +
			"";

	@Test
	public void testGenerateUpdateTransition() throws Exception {
		DomainModel domainModel = parseHelper.parse(TRANSITION_WITH_UPDATE_EVENT);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		String resourceAKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(resourceAKey));
		String resourceA = fsa.getFiles().get(resourceAKey).toString();
		assertTrue(resourceA.contains("sA.addTransitionForEachItem(\"GET\", sB, uriLinkageEntityProperties, uriLinkageProperties, conditionalLinkExpressions, \"B\");"));

		String resourceBKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/BResourceState.java";
		assertTrue(fsa.getFiles().containsKey(resourceBKey));
		String resourceB = fsa.getFiles().get(resourceBKey).toString();
		assertTrue(resourceB.contains("sB.addTransition(\"PUT\", sB_pseudo, uriLinkageEntityProperties, uriLinkageProperties, 0, conditionalLinkExpressions, \"B_pseudo\");"));

		String resourceB_pseudoKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/B_pseudoResourceState.java";
		assertTrue(fsa.getFiles().containsKey(resourceB_pseudoKey));
		String resourceB_pseudo = fsa.getFiles().get(resourceB_pseudoKey).toString();
		assertTrue(resourceB_pseudo.contains("sB_pseudo.addTransition(sA, uriLinkageEntityProperties, uriLinkageProperties, conditionalLinkExpressions);"));
		assertTrue(resourceB_pseudo.contains("sB_pseudo.addTransition(sB, uriLinkageEntityProperties, uriLinkageProperties, conditionalLinkExpressions);"));
	}
	
	private final static String RESOURCE_ON_ERROR = "" +
			"rim Test {" + LINE_SEP +
			"commands" + LINE_SEP +
			"	GetEntity" + LINE_SEP +
			"	Noop" + LINE_SEP +
			"end" + LINE_SEP +
					
			"initial resource A" + LINE_SEP +
			"	collection ENTITY" + LINE_SEP +
			"	view { GetEntity }" + LINE_SEP +
			"	onerror --> AE" + LINE_SEP +
			"end" + LINE_SEP +

			"resource AE" + LINE_SEP +
			"	item ERROR" + LINE_SEP +
			"	view { Noop }" + LINE_SEP +
			"end" + LINE_SEP +
			"}" + LINE_SEP +
			"";
	
	@Test
	public void testGenerateOnErrorResource() throws Exception {
		DomainModel domainModel = parseHelper.parse(RESOURCE_ON_ERROR);
		ResourceInteractionModel model = (ResourceInteractionModel) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		
		// collection
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Model/Test/AResourceState.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output.contains("super(\"ENTITY\", \"A\", createActions(), \"/A\", createLinkRelations(), null, factory.getResourceState(\"__synthetic0Model.AE\"));"));
	}
}
