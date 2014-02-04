package com.temenos.interaction.rimdsl.formatting;

import java.io.IOException;
import java.net.URL;

import javax.inject.Inject;

import junit.framework.Assert;

import org.eclipse.xtext.formatting.INodeModelFormatter;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.nodemodel.ICompositeNode;
import org.eclipse.xtext.parser.IParseResult;
import org.eclipse.xtext.resource.XtextResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.temenos.interaction.rimdsl.RIMDslInjectorProvider;
import com.temenos.interaction.rimdsl.rim.DomainModel;

/**
 * Tests Formatting.
 * 
 * @author aphethean
 */
@RunWith(XtextRunner.class)
@InjectWith(RIMDslInjectorProvider.class)
public class FormatterTest {

	@Inject
	ParseHelper<DomainModel> parser;
	
	@Inject INodeModelFormatter formatter;
	
	
	@Test
	public void testFormatting() throws Exception {
		String text = loadTestRIM();
		DomainModel domainModel = parser.parse(loadTestRIM());
        IParseResult parseResult = ((XtextResource) domainModel.eResource()).getParseResult();
        Assert.assertNotNull(parseResult);
		ICompositeNode rootNode = parseResult.getRootNode();
		String formattedText = formatter.format(rootNode, 0, text.length()).getFormattedText();
		Assert.assertEquals(text, formattedText);
	}

	private String loadTestRIM() throws IOException {
		URL url = Resources.getResource("Simple.rim");
		String rim = Resources.toString(url, Charsets.UTF_8);
		return rim;
	}

}