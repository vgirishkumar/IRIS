package com.temenos.interaction.rimdsl.generator.launcher;

import com.temenos.interaction.core.hypermedia.ResourceState;

import java.util.List;
import java.util.Map;

/**
 * @author kwieconkowski
 */

public class RIMResourceStateLoader extends RIMResourceStateLoaderTemplate {

    @Override
    protected List<ResourceState> getResourceStateList(String rimFileName) {
        return (List<ResourceState>) translator.translateTo(getResourceFromRimFile(rimFileName), RimResourceTranslatorImpl.RimResourceTranslationTarget.LIST_OF_RESOURCE_STATES_PURE);
    }

    @Override
    protected Map<String, String> getPropertiesMap(String rimFileName) {
        return (Map<String, String>) translator.translateTo(getResourceFromRimFile(rimFileName), RimResourceTranslatorImpl.RimResourceTranslationTarget.MAP_STRING_STRING_OF_METHODS_AND_PATHS_PURE);
    }
}
