/**
 * 
 */
package com.temenos.ebank.pages.crossSell;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.pages.clientAquisition.step1.PreAccountCheckPanelFTD;
import com.temenos.ebank.pages.clientAquisition.step1.SingleJointPanel;
import com.temenos.ebank.pages.clientAquisition.step4.SecondFinancialDetailsPanelFTD;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * @author vbeuran
 * 
 */
public class CSAccountOptionsFTDPage extends CSAccountOptionsPage {

	protected Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model, SingleJointPanel singleJointPanel){
		return new PreAccountCheckPanelFTD("preaccount", model, singleJointPanel);
	}
	
	protected ProductType getProduct(){
		return ProductType.FIXED_TERM_DEPOSIT;
	}
	protected Panel newSecondFinancialDetailsPanel(IModel<ApplicationWicketModelObject> model) {
		return(new SecondFinancialDetailsPanelFTD("secondFinancialDetails", model));
	}
}
