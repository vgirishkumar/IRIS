package com.temenos.interaction.rimdsl.generator.launcher.translators;

import com.google.inject.Injector;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.rimdsl.RIMDslStandaloneSetupSpringPRD;
import com.temenos.interaction.rimdsl.generator.RIMDslGeneratorSpringPRD;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kwieconkowski
 */

/* Class translating RimResource to ResourceStates list */
public class RimResourceTranslatorToResourceStates implements RimResourceTranslatorObject<List<ResourceState>> {
    private RIMDslGeneratorSpringPRD eclipseTranslator;

    public RimResourceTranslatorToResourceStates() {
        Injector injector = new RIMDslStandaloneSetupSpringPRD().createInjectorAndDoEMFRegistration();
        eclipseTranslator = injector.getInstance(RIMDslGeneratorSpringPRD.class);
    }

    @Override
    public List<ResourceState> translate(ResourceInteractionModel rimResource) {
        List<ResourceState> resourceStatesList = new ArrayList<ResourceState>();
        GenericXmlApplicationContext springContext = new GenericXmlApplicationContext();
        Resource resourceSpring = new ByteArrayResource(translateToString(rimResource).getBytes());

        springContext.load(resourceSpring);
        for (String beanName : springContext.getBeanDefinitionNames()) {
            resourceStatesList.add((ResourceState) springContext.getBean(beanName));
        }
        return resourceStatesList;
    }

    protected String translateToString(ResourceInteractionModel rimResource) {
        return eclipseTranslator.toSpringXML(rimResource).toString();
    }
}
