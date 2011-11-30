/**
 * 
 */
package com.temenos.ebank.pages.crossSell;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.pages.clientAquisition.step1.PreAccountCheckPanelCA;
import com.temenos.ebank.pages.clientAquisition.step1.SingleJointPanel;
import com.temenos.ebank.pages.clientAquisition.step4.SecondFinancialDetailsPanelCA;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * @author vbeuran
 * 
 */
public class CSAccountOptionsCAPage extends CSAccountOptionsPage {

	protected Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model,
			SingleJointPanel singleJointPanel) {
		PreAccountCheckPanelCA preAccountCheckPanelCurrentAccount = new PreAccountCheckPanelCA("preaccount", model, null);
		return preAccountCheckPanelCurrentAccount;
	}

	protected ProductType getProduct() {
		return ProductType.INTERNATIONAL;
	}

	protected Panel newSecondFinancialDetailsPanel(IModel<ApplicationWicketModelObject> model) {
		return (new SecondFinancialDetailsPanelCA("secondFinancialDetails", model));
	}

	@Override
	protected Component[] getFinancialDetailsUpdatableComponents(Component secondFinancialPanel) {
		return ((SecondFinancialDetailsPanelCA) secondFinancialPanel)
				.getComponentsToUpdateForSingleJointToggle();
	}

}
