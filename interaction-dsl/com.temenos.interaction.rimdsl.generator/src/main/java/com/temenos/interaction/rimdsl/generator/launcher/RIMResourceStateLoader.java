package com.temenos.interaction.rimdsl.generator.launcher;

import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.ResourceStateLoader;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author kwieconkowski
 */

public class RIMResourceStateLoader extends RIMResourceStateLoaderTemplate {
	private static final Logger logger = LoggerFactory.getLogger(RIMResourceStateLoader.class);
    	
	@Override
    protected List<ResourceState> getResourceStateList(String rimFileName) {
    	return (List<ResourceState>) translator.translateTo(getResourceFromRimFile(rimFileName), RimResourceTranslatorImpl.RimResourceTranslationTarget.LIST_OF_RESOURCE_STATES_PURE);
    }
    
    protected List<ResourceState> getResourceStateList(File rimFile){
    	return (List<ResourceState>) translator.translateTo(getResourceFromRimFile(rimFile), RimResourceTranslatorImpl.RimResourceTranslationTarget.LIST_OF_RESOURCE_STATES_PURE);
    }

    @Override
    protected Map<String, String> getPropertiesMap(String rimFileName) {
        return (Map<String, String>) translator.translateTo(getResourceFromRimFile(rimFileName), RimResourceTranslatorImpl.RimResourceTranslationTarget.MAP_STRING_STRING_OF_METHODS_AND_PATHS_PURE);
    }
    
    protected Map<String, String> getPropertiesMap(File rimFile){
    	return (Map<String, String>) translator.translateTo(getResourceFromRimFile(rimFile), RimResourceTranslatorImpl.RimResourceTranslationTarget.MAP_STRING_STRING_OF_METHODS_AND_PATHS_PURE);
    }
}
