package com.temenos.interaction.rimdsl;

import static org.junit.Assert.assertEquals;

import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.parser.IEncodingProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;
import com.temenos.interaction.rimdsl.encoding.RIMEncodingProvider;

/**
 * 
 * Test for Encoding RIM
 *
 * @author vgirishkumar
 *
 */
@InjectWith(RIMDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class RIMEncodingTest {

    @Inject
    private IEncodingProvider encodingProvider;

    @Test
    public void testEncodingProvider() throws Exception {
        assertEquals(RIMEncodingProvider.class, encodingProvider.getClass());
        assertEquals("UTF-8", encodingProvider.getEncoding(null));
    }
}
