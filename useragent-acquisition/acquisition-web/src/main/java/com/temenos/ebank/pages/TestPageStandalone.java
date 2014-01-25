package com.temenos.ebank.pages;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

import com.temenos.ebank.common.wicket.formValidation.ClientSideValidatedFormBehavior;
import com.temenos.ebank.pages.clientAquisition.step2.PafAddressPanel;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;

public class TestPageStandalone extends BasePage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TestPageStandalone() {
		Form form = new Form("form");
		form.add(new ClientSideValidatedFormBehavior());
		AddressWicketModelObject address = new AddressWicketModelObject();
		form.add(new PafAddressPanel("pafAddressPanel", new Model<AddressWicketModelObject>(address))
				.setOutputMarkupId(true));
		add(form);
	}
}
