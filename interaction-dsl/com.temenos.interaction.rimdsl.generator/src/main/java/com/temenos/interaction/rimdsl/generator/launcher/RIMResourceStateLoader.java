/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.temenos.interaction.rimdsl.generator.launcher;

import com.google.common.io.Files;
import com.google.inject.Injector;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.rimdsl.RIMDslStandaloneSetup;
import com.temenos.interaction.rimdsl.rim.DomainModel;
import org.eclipse.emf.ecore.resource.Resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.temenos.interaction.rimdsl.generator.launcher.RimResourceTranslatorImpl.RimResourceTranslationTarget;

/**
 * @author andres
 * @author kwieconkowski
 */
public class RIMResourceStateLoader implements ResourceStateLoader<String> {

    private final RimParserSetup rimParserSetup;
    private final RimResourceTranslator<RimResourceTranslationTarget> translator;

    public RIMResourceStateLoader() {
        rimParserSetup = ((Injector) new RIMDslStandaloneSetup().createInjectorAndDoEMFRegistration()).getInstance(RimParserSetup.class);
        translator = new RimResourceTranslatorImpl();
    }

    @Override
    public List<ResourceState> load(String rimFileName) {
        if (rimFileName == null || rimFileName.isEmpty()) {
            throw new IllegalArgumentException("RIM file name empty");
        }
        return (List<ResourceState>) translator.translateTo(getResourceFromRimFile(rimFileName), RimResourceTranslationTarget.LIST_OF_RESOURCE_STATES_PURE);
    }

    private Resource getResourceFromRimFile(String rimFileName) {
        DomainModel domainModel = rimParserSetup.parseToDomainModel(getFileContext(rimFileName));
        return domainModel.eResource();
    }

    private String getFileContext(String rimFileName) {
        try {
            String location = getClass().getClassLoader().getResource(rimFileName).getFile();
            return Files.toString(new File(location), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
