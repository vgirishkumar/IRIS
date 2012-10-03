package com.temenos.interaction.rimdsl;

import static org.junit.Assert.*;

import org.eclipse.xtext.generator.AbstractFileSystemAccess;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;

@InjectWith(RIMDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class GeneratorTest {
	
	@Inject 
	IGenerator underTest;
	@Inject
	ParseHelper<ResourceInteractionModel> parseHelper;
	
	private final static String LINE_SEP = System.getProperty("line.separator");
	
	private final static String SIMPLE_STATES_RIM = "" +
	"initial state A" + LINE_SEP +
	"end\r\n" + LINE_SEP +

	"state B\r\n" +
	"end\r\n" + LINE_SEP +
	"";

	private final static String SIMPLE_STATES_BEHAVIOUR = "" +
	"import java.util.HashMap;" + LINE_SEP +
	"import java.util.HashSet;" + LINE_SEP +
	"import java.util.Map;" + LINE_SEP +
	"import java.util.Set;" + LINE_SEP +
	LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.Action;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.CollectionResourceState;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.ResourceState;" + LINE_SEP +
	"import com.temenos.interaction.core.hypermedia.ResourceStateMachine;" + LINE_SEP +
	LINE_SEP +
	"public class __synthetic0Behaviour {" + LINE_SEP +
	"	" + LINE_SEP +
	"	public static void main(String[] args) {" + LINE_SEP +
	"		System.out.println(new ASTValidation().graph(new __synthetic0Behaviour().getRIM()));" + LINE_SEP +
	"	}" + LINE_SEP +
	"	" + LINE_SEP +
	"	public ResourceStateMachine getRIM() {" + LINE_SEP +
	"		ResourceState initial = null;" + LINE_SEP +
	"		// create states" + LINE_SEP +
	"		// identify the initial state" + LINE_SEP +
	"		initial = sA;" + LINE_SEP +
	LINE_SEP +
	"		// create regular transitions" + LINE_SEP +
	LINE_SEP +
    "        // create foreach transitions" + LINE_SEP +
	LINE_SEP +
    "        // create AUTO transitions" + LINE_SEP +
	LINE_SEP +
	"	    return new ResourceStateMachine(initial);" + LINE_SEP +
	"	}" + LINE_SEP +
	"}" + LINE_SEP;
	
	@Test
	public void testParseSimpleStates() throws Exception {
		ResourceInteractionModel model = parseHelper.parse(SIMPLE_STATES_RIM);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(model.eResource(), fsa);
		System.out.println(fsa.getFiles());
		assertEquals(1, fsa.getFiles().size());
		
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "__synthetic0Behaviour.java";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		assertEquals(SIMPLE_STATES_BEHAVIOUR, fsa.getFiles().get(expectedKey).toString());
		
	}
	
}
