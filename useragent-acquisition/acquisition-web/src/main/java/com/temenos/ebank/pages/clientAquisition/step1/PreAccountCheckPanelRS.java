/**
 * 
 */
package com.temenos.ebank.pages.clientAquisition.step1;

import org.apache.wicket.model.IModel;

import com.temenos.ebank.common.wicket.WicketUtils;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankRadioChoice;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Pre-account check panel for current account subscription
 * 
 * @author vionescu
 * 
 */
public class PreAccountCheckPanelRS extends PreAccountCheckPanel {
	private static final long serialVersionUID = 1L;

	public PreAccountCheckPanelRS(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model, ProductType.REGULAR_SAVER);
		// ApplicationWicketModelObject a = model.getObject();
		IGenericChoiceRenderer currencyRenderer = GenericChoiceRendererFactory.getRenderer(
				getProductCurrencyPropertyName(), this);
		EbankRadioChoice rcCurrency = new EbankRadioChoice("accountCurrency", currencyRenderer.getChoices(), currencyRenderer);
		rcCurrency.setRequired(true);
		add(WicketUtils.addResourceLabelAndReturnBorder(rcCurrency));
	}
}
