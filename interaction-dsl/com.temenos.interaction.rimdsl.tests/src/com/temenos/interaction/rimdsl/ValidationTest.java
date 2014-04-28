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

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.temenos.interaction.rimdsl.rim.DomainDeclaration;
import com.temenos.interaction.rimdsl.rim.DomainModel;

@InjectWith(RIMDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class ValidationTest {

	private final static String LINE_SEP = System.getProperty("line.separator");

	@Inject
	ParseHelper<DomainModel> parser;
	@Inject
	private IResourceValidator validator;
	
	private final static String DUPLICATE_EVENT_DEF_WITH_USE_RIMS = "" +
			"domain TestDomain {" + LINE_SEP +	
			"    use Common.Global.*" + LINE_SEP +
			"    use Common2.Global.*" + LINE_SEP +
			"    use TestDomain.ONE.*" + LINE_SEP +
			"    rim ONE {" + LINE_SEP +
			"        command NoopGET" + LINE_SEP +
			"        initial resource A {" + LINE_SEP +
			"	         type: collection" + LINE_SEP +
			"	         entity: ENTITY" + LINE_SEP +
			"	         view: NoopGET" + LINE_SEP +
			"	         GET -> TestDomain.TWO.B" + LINE_SEP +
			"        }" + LINE_SEP +
			"    }" + LINE_SEP +  // end rim
			"    rim TWO {" + LINE_SEP +
			"        command NoopGET" + LINE_SEP +
			"        initial resource B {" + LINE_SEP +
			"	         type: collection" + LINE_SEP +
			"	         entity: ENTITY" + LINE_SEP +
			"	         view: NoopGET" + LINE_SEP +
			"	         GET -> ONE.A" + LINE_SEP +
			"	         GET -> A" + LINE_SEP +
			"        }" + LINE_SEP +
			"    }" + LINE_SEP +  // end rim
			"}" + LINE_SEP +  // end domain
			"" + LINE_SEP;

	@Test
	public void testParseDuplicateEventDefWithUse() throws Exception {
		// load Common.rim which already defines a GET event
		new org.eclipse.emf.mwe.utils.StandaloneSetup().setPlatformUri("../");
		Injector injector = new RIMDslStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtextResourceSet resourceSet = injector.getInstance(XtextResourceSet.class);
		resourceSet.addLoadOption(XtextResource.OPTION_RESOLVE_ALL, Boolean.TRUE);
		Resource commonResource = resourceSet.getResource(URI.createURI("src/Common.rim"), true);
		DomainModel model = (DomainModel) commonResource.getContents().get(0);
		assertEquals("Common", ((DomainDeclaration)model.getRims().get(0)).getName());
		Resource common2Resource = resourceSet.getResource(URI.createURI("src/Common2.rim"), true);
		DomainModel model2 = (DomainModel) common2Resource.getContents().get(0);
		assertEquals("Common2", ((DomainDeclaration)model2.getRims().get(0)).getName());

		DomainModel rootModel = parser.parse(DUPLICATE_EVENT_DEF_WITH_USE_RIMS, resourceSet);
		EList<Resource.Diagnostic> rootErrors = rootModel.eResource().getErrors();
		assertEquals(0, rootErrors.size());
		List<Issue> issues = validator.validate(rootModel.eResource(), CheckMode.ALL, null);
		// don't know, something broken here.
		assertEquals(3, issues.size());
		assertEquals("Couldn't resolve reference to Event 'GET'.", issues.get(0).getMessage());
		assertEquals("Couldn't resolve reference to Event 'GET'.", issues.get(1).getMessage());
		assertEquals("Couldn't resolve reference to Event 'GET'.", issues.get(2).getMessage());
	}

}
