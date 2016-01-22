package com.temenos.interaction.rimdsl.generator.launcher.translators;

import com.temenos.interaction.rimdsl.rim.ResourceInteractionModel;

/**
 * @author kwieconkowski
 */

/* Interface used when class can translate ResourceInteractionModel to T type object */
public interface RimResourceTranslatorObject<T> {
    public T translate(final ResourceInteractionModel rimResource);
}
