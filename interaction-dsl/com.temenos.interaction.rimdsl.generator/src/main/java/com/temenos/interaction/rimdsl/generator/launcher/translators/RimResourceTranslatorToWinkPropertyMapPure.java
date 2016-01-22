package com.temenos.interaction.rimdsl.generator.launcher.translators;

import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;
import com.temenos.interaction.rimdsl.rim.State;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author kwieconkowski
 */

/* Class translating RimResource to properties map (used by Apache WINK) with 'PURE' concept. The PURE concept is explained in RimResourceTranslatorPureAbstract abstract class */
public class RimResourceTranslatorToWinkPropertyMapPure extends RimResourceTranslatorPureAbstract implements RimResourceTranslatorObject<Map<String, String>> {

    @Override
    public Map<String, String> translate(ResourceInteractionModel rimResource) {
        Map<String, String> propertieMap = new LinkedHashMap<String, String>();

        for (final State state : rimResource.getStates()) {
            propertieMap.put(getStateName(state), String.format("%s %s", getMethods(rimResource, state), getPath(rimResource, state)));
        }
        return propertieMap;
    }
}
