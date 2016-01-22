package com.temenos.interaction.rimdsl.generator.launcher;

import com.google.common.collect.Iterables;
import com.google.inject.Injector;
import com.temenos.interaction.rimdsl.RIMDslStandaloneSetupSpringPRD;
import com.temenos.interaction.rimdsl.generator.launcher.translators.*;
import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import java.util.List;
import java.util.Map;

import static com.temenos.interaction.rimdsl.generator.launcher.RimResourceTranslatorImpl.RimResourceTranslationTarget;

/**
 * @author kwieconkowski
 */

public class RimResourceTranslatorImpl implements RimResourceTranslator<RimResourceTranslationTarget> {

    @Override
    public Object translateTo(final Resource rimResource, final RimResourceTranslationTarget target) {
        whenNullThrowException(rimResource, "Method was called with null resource");

        Object collection = createCorrectReturnType(target);
        for (final ResourceInteractionModel rim : getResourceInteractionModels(rimResource)) {
            Object entries = target.translate(rim);
            if (entries instanceof List) {
                ((List) collection).addAll((List) entries);
            } else if (entries instanceof Map) {
                ((Map) collection).putAll((Map) entries);
            }
        }
        return collection;
    }

    private Object createCorrectReturnType(final RimResourceTranslationTarget type) {
        try {
            return type.getReturnTypeClass().getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("One of the RimResourceTransltorObject inside Rim2ResourceStateTranslatorCombined class, have assigned incorrect return type class. Should never happen !");
        }
    }

    private void whenNullThrowException(final Object object, String error_msg) {
        if (object == null) {
            throw new IllegalArgumentException(error_msg);
        }
    }

    private Iterable<ResourceInteractionModel> getResourceInteractionModels(final Resource resource) {
        TreeIterator<EObject> _allContents = resource.getAllContents();
        Iterable<EObject> _iterable = IteratorExtensions.<EObject>toIterable(_allContents);
        return Iterables.<ResourceInteractionModel>filter(_iterable, ResourceInteractionModel.class);
    }

    /* This is internal enum, because there is not need for it in other places, also I wanted method to have private access modifier */
    public enum RimResourceTranslationTarget {
        LIST_OF_RESOURCE_STATES(java.util.ArrayList.class, new RimResourceTranslatorToResourceStates()),
        LIST_OF_RESOURCE_STATES_PURE(java.util.ArrayList.class,
                ((Injector) new RIMDslStandaloneSetupSpringPRD().createInjectorAndDoEMFRegistration()).getInstance(RimResourceTranslatorToResourceStatesPure.class)),
        MAP_STRING_STRING_OF_METHODS_AND_PATHS(java.util.LinkedHashMap.class, new RimResourceTranslatorToWinkPropertyMap()),
        MAP_STRING_STRING_OF_METHODS_AND_PATHS_PURE(java.util.LinkedHashMap.class,
                ((Injector) new RIMDslStandaloneSetupSpringPRD().createInjectorAndDoEMFRegistration()).getInstance(RimResourceTranslatorToWinkPropertyMapPure.class));

        private final Class returnTypeClass;
        private final RimResourceTranslatorObject translatorObject;

        public Class getReturnTypeClass() {
            return returnTypeClass;
        }

        private Object translate(final ResourceInteractionModel rimResource) {
            return translatorObject.translate(rimResource);
        }

        RimResourceTranslationTarget(final Class returnTypeClass, final RimResourceTranslatorObject translatorObject) {
            this.returnTypeClass = returnTypeClass;
            this.translatorObject = translatorObject;
        }
    }
}
