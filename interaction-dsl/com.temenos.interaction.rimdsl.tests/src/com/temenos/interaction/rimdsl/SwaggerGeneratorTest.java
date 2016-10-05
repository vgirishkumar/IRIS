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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.InMemoryFileSystemAccess;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.temenos.interaction.rimdsl.rim.DomainDeclaration;
import com.temenos.interaction.rimdsl.rim.DomainModel;

@InjectWith(RIMDslSwaggerInjectorProvider.class)
@RunWith(XtextRunner.class)
public class SwaggerGeneratorTest {
	
	 private final static Logger LOGGER = Logger.getLogger(SwaggerGeneratorTest.class.getName());
	
	@Inject 
	IGenerator underTest;
	@Inject
	ParseHelper<DomainModel> parseHelper;
	
	private final static String LINE_SEP = System.getProperty("line.separator");
	
	private final static String SIMPLE_STATES_RIM = "" +
	"domain SimpleDomain {" + LINE_SEP +
	"rim Simple @ Api : tags ( \"Simple\" ) {" + LINE_SEP +
	"	event POST {" + LINE_SEP +
	"	    method: POST" + LINE_SEP +
	"	}" + LINE_SEP +

	"	command GetEntity" + LINE_SEP +
	"	command GetException" + LINE_SEP +
	"	command UpdateEntity" + LINE_SEP +
	
	" basepath: \"/Simple\"" + LINE_SEP +
			
	"initial resource A {" + LINE_SEP +
	"	type: collection" + LINE_SEP +
	"	entity: ENTITY" + LINE_SEP +
	"	view: GetEntity" + LINE_SEP +
	"	path: \"/A\"" + LINE_SEP +
	"	POST -> B" + LINE_SEP +
	"}" + LINE_SEP +

	"exception resource E {" + LINE_SEP +
	"	type: collection" + LINE_SEP +
	"	entity: EXCEPTION" + LINE_SEP +
	"	view: GetException" + LINE_SEP +
	"}" + LINE_SEP +
	
	"resource B {" +
	"	type: item" + LINE_SEP +
	"	entity: ENTITY" + LINE_SEP +
	"	actions [ UpdateEntity ]" + LINE_SEP +
	"	path: \"/B\"" + LINE_SEP +
	"}" + LINE_SEP +
	"}" + LINE_SEP +
	"}" + LINE_SEP +
	"";

	private final static String SIMPLE_STATES_SWAGGER = "" +		
	"{" + LINE_SEP + 
	"    \"swagger\": \"2.0\"," + LINE_SEP + 
	"    \"info\": {" + LINE_SEP + 
	"        \"title\": \"\"," + LINE_SEP + 
	"        \"description\": \"\"," + LINE_SEP + 
	"        \"version\": \"1.0.0\"" + LINE_SEP + 
	"    }," + LINE_SEP + 
	"    \"produces\": [\"application/json\",\"application/xml\"]," + LINE_SEP + 
	"\"paths\": {" + LINE_SEP +
	"    \"/B\": {" + LINE_SEP + 
	"        \"post\": {" + LINE_SEP + 
	"            \"description\": \"\"," + LINE_SEP + 
	"            \"consumes\": [\"application/json\",\"application/xml\"]," + LINE_SEP + 
	"            \"produces\": [\"application/json\",\"application/xml\"]," + LINE_SEP + 
	"            \"parameters\": [" + LINE_SEP + 
	"                {" + LINE_SEP + 
	"                    \"in\": \"body\"," + LINE_SEP + 
	"                    \"name\": \"body\"," + LINE_SEP + 
	"                    \"description\": \"-\"," + LINE_SEP + 
	"                    \"required\": true," + LINE_SEP + 
	"                    \"schema\": {" + LINE_SEP + 
	"                        \"$ref\": \"#/definitions/ENTITY\"" + LINE_SEP + 
	"                    }" + LINE_SEP + 
	"                }" + LINE_SEP + 
	"            ]," + LINE_SEP + 
	"             \"tags\": [\"Simple\"" + LINE_SEP + 
	"            ]," + LINE_SEP + 
	"                \"responses\": {" + LINE_SEP + 
	"                \"201\": {" + LINE_SEP + 
	"                    \"description\": \"Created\"," + LINE_SEP + 
	"                    \"schema\": {" + LINE_SEP + 
	"                        \"type\": \"array\"," + LINE_SEP + 
	"                        \"items\": {" + LINE_SEP + 
	"                            \"$ref\": \"#/definitions/ENTITY\"" + LINE_SEP + 
	"                        }" + LINE_SEP + 
	"                    }" + LINE_SEP + 
	"                }," + LINE_SEP + 
	"                \"400\": {" + LINE_SEP + 
	"                    \"description\": \"Bad request\"," + LINE_SEP + 
	"                    \"schema\": {" + LINE_SEP + 
	"                        \"type\": \"array\"," + LINE_SEP + 
	"                        \"items\": {" + LINE_SEP + 
	"                            \"$ref\": \"#/definitions/ErrorsMvGroup\"" + LINE_SEP + 
	"                        }" + LINE_SEP + 
	"                    }" + LINE_SEP + 
	"                }," + LINE_SEP + 
	"                \"401\": {" + LINE_SEP + 
	"                    \"description\": \"Authentication/Authorization error\"," + LINE_SEP + 
	"                    \"schema\": {" + LINE_SEP + 
	"                        \"type\": \"array\"," + LINE_SEP + 
	"                        \"items\": {" + LINE_SEP + 
	"                            \"$ref\": \"#/definitions/ErrorsMvGroup\"" + LINE_SEP + 
	"                        }" + LINE_SEP + 
	"                    }" + LINE_SEP + 
	"                }," + LINE_SEP + 
	"                \"404\": {" + LINE_SEP + 
	"                    \"description\": \"Resource not found\"" + LINE_SEP + 
	"                }," + LINE_SEP + 
	"                \"default\": {" + LINE_SEP + 
	"                    \"description\": \"Unexpected output\"," + LINE_SEP + 
	"                    \"schema\": {" + LINE_SEP + 
	"                        \"$ref\": \"#/definitions/ErrorsMvGroup\"" + LINE_SEP + 
	"                    }" + LINE_SEP + 
	"                }" + LINE_SEP + 
	"            }" + LINE_SEP + 
	"        }" + LINE_SEP + 
	"    }" + LINE_SEP + 
	"}," + LINE_SEP + 
	"\"definitions\": {" + LINE_SEP + 
	"    \"ErrorsMvGroup\": {" + LINE_SEP + 
	"        \"type\": \"object\"," + LINE_SEP + 
	"        \"properties\": {" + LINE_SEP + 
	"            \"Text\": {" + LINE_SEP + 
	"                \"type\": \"string\"" + LINE_SEP + 
	"            }," + LINE_SEP + 
	"            \"Type\": {" + LINE_SEP + 
	"                \"type\": \"string\"" + LINE_SEP + 
	"            }," + LINE_SEP + 
	"            \"Info\": {" + LINE_SEP + 
	"                \"type\": \"string\"" + LINE_SEP + 
	"            }," + LINE_SEP + 
	"            \"Code\": {" + LINE_SEP + 
	"                \"type\": \"string\"" + LINE_SEP + 
	"            }" + LINE_SEP + 
	"        }" + LINE_SEP + 
	"    }" + LINE_SEP + 
	"}" + LINE_SEP + 
	"}" + LINE_SEP;
	
	@Test
	public void testGenerateSimpleStates() throws Exception {
		DomainModel domainModel = parseHelper.parse(SIMPLE_STATES_RIM);
		DomainDeclaration domainDeclaration = (DomainDeclaration) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(domainDeclaration.eResource(), fsa);		
		assertEquals(1, fsa.getFiles().size());	
		
		// the behaviour class
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "api-docs-SimpleDomain-Simple.json";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		assertEquals(SIMPLE_STATES_SWAGGER, fsa.getFiles().get(expectedKey).toString());	
	}

	@Test
	public void testGenerateSimple() throws Exception {
		DomainModel domainModel = parseHelper.parse(loadTestRIM());
		DomainDeclaration domainDeclaration = (DomainDeclaration) domainModel.getRims().get(0);
		InMemoryFileSystemAccess fsa = new InMemoryFileSystemAccess();
		underTest.doGenerate(domainDeclaration.eResource(), fsa);
		assertEquals(1, fsa.getFiles().size());
		
		// the behaviour class
		String expectedKey = IFileSystemAccess.DEFAULT_OUTPUT + "api-docs-SimpleModel-Simple.json";
		assertTrue(fsa.getFiles().containsKey(expectedKey));
		String output = fsa.getFiles().get(expectedKey).toString();
		assertTrue(output.contains("/notes"));
				
	}
	
	private String loadTestRIM() throws IOException {
		URL url = Resources.getResource("Simple.rim");
		String rim = Resources.toString(url, Charsets.UTF_8);
		return rim;
	}

}
