package com.temenos.interaction.rimdsl.generator.launcher;

import com.google.common.io.Files;
import com.google.inject.Inject;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.rimdsl.RIMDslInjectorProvider;
import com.temenos.interaction.rimdsl.rim.DomainModel;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static com.temenos.interaction.rimdsl.generator.launcher.RimResourceTranslatorImpl.RimResourceTranslationTarget;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author kwieconkowski
 */
@InjectWith(RIMDslInjectorProvider.class)
@RunWith(XtextRunner.class)
public class RimResourceTranslatorImplTest {

    @Inject
    private ParseHelper<DomainModel> parser;
    private RimResourceTranslator<RimResourceTranslationTarget> translator = new RimResourceTranslatorImpl();

    @Test
    public void testTranslateRimToPropertiesMap() throws Exception {
        Map<String, String> map = (Map<String, String>) translator.translateTo(getResourceFromRimFile("Airline.rim"), RimResourceTranslationTarget.MAP_STRING_STRING_OF_METHODS_AND_PATHS);
        assertNotNull(map);
        assertTrue(!map.isEmpty());
    }

    @Test
    public void testTranslateRimToResourceStates() throws Exception {
        List<ResourceState> list = (List<ResourceState>) translator.translateTo(getResourceFromRimFile("Airline.rim"), RimResourceTranslationTarget.LIST_OF_RESOURCE_STATES);
        assertNotNull(list);
        assertTrue(!list.isEmpty());
    }

    @Test
    public void testTranslateRimToPropertiesMap_Pure() throws Exception {
        Map<String, String> map = (Map<String, String>) translator.translateTo(getResourceFromRimFile("Airline.rim"), RimResourceTranslationTarget.MAP_STRING_STRING_OF_METHODS_AND_PATHS_PURE);
        assertNotNull(map);
        assertTrue(!map.isEmpty());
    }

    @Test
    public void testTranslateRimToResourceStates_Pure() throws Exception {
        List<ResourceState> list = (List<ResourceState>) translator.translateTo(getResourceFromRimFile("Airline.rim"), RimResourceTranslationTarget.LIST_OF_RESOURCE_STATES_PURE);
        assertNotNull(list);
        assertTrue(!list.isEmpty());
    }

    private Resource getResourceFromRimFile(String name) throws Exception {
        String rimExample = getFileContext("Airline.rim");
        DomainModel domainModel = parser.parse(rimExample);
        return domainModel.eResource();
    }

    private String getFileContext(String name) {
        try {
            String location = getClass().getClassLoader().getResource(name).getFile();
            return Files.toString(new File(location), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}