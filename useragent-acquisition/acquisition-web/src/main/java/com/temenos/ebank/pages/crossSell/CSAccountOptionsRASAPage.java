/**
 * 
 */
package com.temenos.ebank.pages.crossSell;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.pages.clientAquisition.step1.PreAccountCheckPanelRASA;
import com.temenos.ebank.pages.clientAquisition.step1.SingleJointPanel;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * @author vbeuran
 * 
 */
public class CSAccountOptionsRASAPage extends CSAccountOptionsPage {
	protected Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model,
			SingleJointPanel singleJointPanel) {
		return new PreAccountCheckPanelRASA("preaccount", model, singleJointPanel);
	}

	protected ProductType getProduct() {
		return ProductType.RASA;
	}

	protected Panel newSecondFinancialDetailsPanel(IModel<ApplicationWicketModelObject> model) {
		return ((Panel) (new Panel("secondFinancialDetails").setVisible(false)));
	}
}
