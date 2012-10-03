package com.temenos.interaction.rimdsl;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;

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
}
