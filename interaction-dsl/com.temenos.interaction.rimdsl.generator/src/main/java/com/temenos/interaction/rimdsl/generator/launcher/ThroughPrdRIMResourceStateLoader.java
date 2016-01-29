package com.temenos.interaction.rimdsl.generator.launcher;

import com.temenos.interaction.core.hypermedia.ResourceState;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.temenos.interaction.rimdsl.generator.launcher.RimResourceTranslatorImpl.RimResourceTranslationTarget;

/**
 * @author andres
 * @author kwieconkowski
 * @author dgroves
 */
public class ThroughPrdRIMResourceStateLoader extends RIMResourceStateLoaderTemplate {

    @Override
    protected List<ResourceState> getResourceStateList(String rimFileName) {
        return (List<ResourceState>) translator.translateTo(getResourceFromRimFile(rimFileName), RimResourceTranslationTarget.LIST_OF_RESOURCE_STATES);
    }

    @Override
    protected Map<String, String> getPropertiesMap(String rimFileName) {
        return (Map<String, String>) translator.translateTo(getResourceFromRimFile(rimFileName), RimResourceTranslationTarget.MAP_STRING_STRING_OF_METHODS_AND_PATHS);
    }

	@Override
	protected List<ResourceState> getResourceStateList(File rimFile) {
		return (List<ResourceState>) translator.translateTo(getResourceFromRimFile(rimFile), RimResourceTranslatorImpl.RimResourceTranslationTarget.LIST_OF_RESOURCE_STATES_PURE);
	}

	@Override
	protected Map<String, String> getPropertiesMap(File rimFile) {
		return (Map<String, String>) translator.translateTo(getResourceFromRimFile(rimFile), RimResourceTranslatorImpl.RimResourceTranslationTarget.MAP_STRING_STRING_OF_METHODS_AND_PATHS_PURE);
	}
}
