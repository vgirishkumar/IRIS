package com.temenos.ebank.common.wicket.choiceRenderers;

import org.apache.wicket.Component;

public abstract class GenericChoiceRendererFactory {

	public static IGenericChoiceRenderer getRenderer(Choices key, Component component) {
		return new EbankChoiceRenderer(key, component);
	}
}
