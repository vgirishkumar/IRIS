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

@InjectWith(RIMDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class PackagesGeneratorTest {
	
	@Inject 
	IGenerator underTest;
	@Inject
	ParseHelper<DomainModel> parseHelper;
	
	private final static String LINE_SEP = System.getProperty("line.separator");

	private final static String MULTIPLE_RIMS = "" +
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

	"rim Test2 {" + LINE_SEP +
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
	 * doGenerate should produce one file per rim
	 */
	@Test
	public void testGenerateMulti() throws Exception {
		DomainModel domainModel = parseHelper.parse(MULTIPLE_RIMS);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(domainModel.eResource(), fsa);
		System.out.println(fsa.getFiles());
		assertEquals(4, fsa.getFiles().size());

		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "blah/TestBehaviour.java"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "blah/Test/AResourceState.java"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "blah/Test2Behaviour.java"));
		assertTrue(fsa.getFiles().containsKey(IFileSystemAccess.DEFAULT_OUTPUT + "blah/Test2/AResourceState.java"));

	}
}
