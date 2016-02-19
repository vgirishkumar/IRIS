package com.temenos.interaction.rimdsl.generator.launcher;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author kwieconkowski
 */

/* Interface with translate Resource, to any given type described in passed Enum type */
public interface RimResourceTranslator<E extends Enum> {
    public Object translateTo(final Resource rimResource, final E target);
}
