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
 * Pre-account check panel for IBSA subscription
 * 
 * @author vionescu
 * 
 */
public class PreAccountCheckPanelIBSA extends PreAccountCheckPanel {
	private static final long serialVersionUID = 1L;

	public PreAccountCheckPanelIBSA(String id, IModel<ApplicationWicketModelObject> model, Panel singleJointPanel) {
		super(id, model, ProductType.IBSA);
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
	 * Mock for returning currencies list for IBSA. (Still no T24 available)
	 * 
	 * @return
	 */
	private List<String> getCurrenciesMock() {
		//TODO: check if this is still a mock
		IGenericChoiceRenderer currencyRenderer = GenericChoiceRendererFactory
				.getRenderer(getProductCurrencyPropertyName(), this);
		return currencyRenderer.getChoices();
	}

}
