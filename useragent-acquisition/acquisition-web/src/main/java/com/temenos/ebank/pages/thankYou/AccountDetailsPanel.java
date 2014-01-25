package com.temenos.ebank.pages.thankYou;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;

import com.temenos.ebank.message.AccountDetails;


public class AccountDetailsPanel extends Panel {

	private static final long serialVersionUID = 1L;
	String number = null;
	
	public AccountDetailsPanel(String id, CompoundPropertyModel<AccountDetails> details, int index) {
		super(id);
		number = String.valueOf(index + 1);
		setDefaultModel(details);		
		add(generateNumberedLabel("accountCurrencyLabel"));
		add(generateNumberedLabel("sortCodeLabel"));
		add(generateNumberedLabel("accountNoLabel"));
		add(generateNumberedLabel("ibanLabel"));
		add(new Label("accountCurrency"));
		add(new Label("sortCode"));
		add(new Label("accountNo"));
		add(new Label("ibanNo"));
	}
	
	protected Label generateNumberedLabel(String id) {
		return (new Label(id, 
				new StringResourceModel(id, this, null, new String[] { number })));
	}

}
