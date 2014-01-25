/**
 * 
 */
package com.temenos.ebank.pages.clientAquisition.step1;

import java.util.List;

import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.common.wicket.WicketUtils;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankRadioChoice;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Pre-account check panel for RASA subscription
 * 
 * @author vionescu
 * 
 */
public class PreAccountCheckPanelRASA extends PreAccountCheckPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PreAccountCheckPanelRASA(String id, IModel<ApplicationWicketModelObject> model, Panel singleJointPanel) {
		super(id, model, ProductType.RASA);
		// ApplicationWicketModelObject a = model.getObject();
		IGenericChoiceRenderer currencyRenderer = GenericChoiceRendererFactory
				.getRenderer(getProductCurrencyPropertyName(), this);
		List<String> currencies = getCurrenciesMock();
		final EbankRadioChoice rcCurrency = new EbankRadioChoice("accountCurrency", currencies, currencyRenderer);
		rcCurrency.setRequired(true);
		add(WicketUtils.addResourceLabelAndReturnBorder(rcCurrency));
		if (singleJointPanel != null) {
			add(singleJointPanel);
		} else {
			add(new EmptyPanel("singleJoint"));
		}

	}

	/**
	 * Mock for returning currencies list for RASA. (Still no T24 available)
	 * 
	 * @return
	 */
	private List<String> getCurrenciesMock() {
		IGenericChoiceRenderer currencyRenderer = GenericChoiceRendererFactory
				.getRenderer(getProductCurrencyPropertyName(), this);
		return currencyRenderer.getChoices();
	}

}
