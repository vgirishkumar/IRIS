package com.temenos.ebank.pages.clientAquisition.step1;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.domain.ProductType;

@SuppressWarnings("serial")
public class PreAccountCheckPanel extends Panel {
	
	private ProductType productType;

	public PreAccountCheckPanel(String id, IModel<?> model, ProductType productType) {
		super(id, model);
		this.productType = productType;
	}

	public ProductType getProductType() {
		return productType;
	}
	
	public Choices getProductCurrencyPropertyName(){
		return Choices.valueOf(productType.getCode() + "_accountCurrency");
	}
}
