/**
 * 
 */
package com.temenos.ebank.pages.clientAquisition.step1;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.wicketmodel.CustomerWicketModel;

/**
 * Eligibility details panel
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class EligibilityDetailsPanel extends Panel {
	private static final long serialVersionUID = 1L;
	
	public EligibilityDetailsPanel(String id, IModel<CustomerWicketModel> model) {
		super(id, model);

		IGenericChoiceRenderer countriesRenderer = GenericChoiceRendererFactory.getRenderer(Choices.COUNTRY, this);
		final EbankDropDownChoice cmbCountryOResidence = new EbankDropDownChoice("countryResidence",
				countriesRenderer.getChoices(), countriesRenderer);
		cmbCountryOResidence.setRequired(true);
		Border border = addResourceLabelAndReturnBorder(cmbCountryOResidence);
		add(border);

		EbankDropDownChoice cmbCountryMoving = new EbankDropDownChoice("countryMoving", countriesRenderer.getChoices(),
				countriesRenderer);

		cmbCountryMoving.setRequired(false);
		border = addResourceLabelAndReturnBorder(cmbCountryMoving);
		add(border);
	}
}
