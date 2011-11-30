package com.temenos.ebank.common.wicket.wizard;

import org.apache.wicket.markup.html.panel.Panel;

import com.temenos.ebank.pages.clientAquisition.wizard.SupportSnippet;

/**
 * Header panel for wizard steps.
 * 
 * @see EbankWizardStep#addAdditionalPanels(org.apache.wicket.model.IModel)
 * 
 * @author gcristescu
 */
public class EbankWizardStepHeader extends Panel {

	public EbankWizardStepHeader(String id) {
		super(id);

		add(new SupportSnippet("supportSnippet"));
	}
}
