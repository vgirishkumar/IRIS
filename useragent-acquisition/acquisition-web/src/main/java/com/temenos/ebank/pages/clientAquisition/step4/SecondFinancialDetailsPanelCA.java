package com.temenos.ebank.pages.clientAquisition.step4;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

@SuppressWarnings({"rawtypes","serial"})
public class SecondFinancialDetailsPanelCA extends Panel {
	private Component[] componentsToUpdateForSingleJointToggle;
	
	public SecondFinancialDetailsPanelCA(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
		//txt - customer mother's maiden name
		final FormComponent custMotherMaidenName = new EbankTextField("customer.motherMaidenName");
		add(addResourceLabelAndReturnBorder(custMotherMaidenName));
		
		//txt - second Customer mother's maiden name, visible if app.isSole == false
		final FormComponent secondCustMotherMaidenName = new EbankTextField("secondCustomer.motherMaidenName");
		Border border = addResourceLabelAndReturnBorder(secondCustMotherMaidenName);
		
		border.setVisible(!model.getObject().getIsSole()).setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);	
		add(border);
		this.componentsToUpdateForSingleJointToggle = new Component[] {border};
	}
	
	public Component[] getComponentsToUpdateForSingleJointToggle() {
		return componentsToUpdateForSingleJointToggle;
	}
}
