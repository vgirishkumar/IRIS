package com.temenos.interaction.rimdsl.generator.launcher.translators;

import com.google.inject.Injector;
import com.temenos.interaction.rimdsl.RIMDslStandaloneSetupSpringPRD;
import com.temenos.interaction.rimdsl.generator.RIMDslGeneratorSpringPRD;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author kwieconkowski
 */

/* Class translating RimResource to properties map (used by Apache WINK) */
public class RimResourceTranslatorToWinkPropertyMap implements RimResourceTranslatorObject<Map<String, String>> {
    private RIMDslGeneratorSpringPRD eclipseTranslator;

    public RimResourceTranslatorToWinkPropertyMap() {
        Injector injector = new RIMDslStandaloneSetupSpringPRD().createInjectorAndDoEMFRegistration();
        eclipseTranslator = injector.getInstance(RIMDslGeneratorSpringPRD.class);
    }

    @Override
    public Map<String, String> translate(ResourceInteractionModel rimResource) {
        String propertiesFile = eclipseTranslator.toBeanMap(rimResource).toString();
        return extractToMap(propertiesFile);

    }

    protected Map<String, String> extractToMap(String propertieFile) {
        Map<String, String> propertiesMap = new LinkedHashMap<String, String>();

        String[] keyValuePair = null;
        String[] propertieEntry = propertieFile.split("\n");
        for (int i = 1; i < propertieEntry.length; i++) {
            keyValuePair = propertieEntry[i].trim().split("=");
            propertiesMap.put(keyValuePair[0], keyValuePair[1]);
        }
        return propertiesMap;
    }
}
