package com.temenos.ebank.common.wicket.choiceRenderers;

import java.util.List;

import org.apache.wicket.markup.html.form.IChoiceRenderer;

public interface IGenericChoiceRenderer extends IChoiceRenderer<String> {

	public List<String> getChoices();

}
