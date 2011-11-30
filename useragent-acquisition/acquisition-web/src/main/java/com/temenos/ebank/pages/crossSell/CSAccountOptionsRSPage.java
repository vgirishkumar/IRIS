/**
 * 
 */
package com.temenos.ebank.pages.crossSell;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.pages.clientAquisition.step1.PreAccountCheckPanelRS;
import com.temenos.ebank.pages.clientAquisition.step1.SingleJointPanel;
import com.temenos.ebank.pages.clientAquisition.step4.SecondFinancialDetailsPanelRS;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * @author vbeuran
 * 
 */
public class CSAccountOptionsRSPage extends CSAccountOptionsPage {
	protected Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model,
			SingleJointPanel singleJointPanel) {
		return new PreAccountCheckPanelRS("preaccount", model);
	}

	protected ProductType getProduct() {
		return ProductType.REGULAR_SAVER;
	}

	protected Panel newSecondFinancialDetailsPanel(IModel<ApplicationWicketModelObject> model) {
		return (new SecondFinancialDetailsPanelRS("secondFinancialDetails", model, model.getObject().getOpeningCurrentAccount()));
	}
}
