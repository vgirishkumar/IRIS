package com.temenos.ebank.pages.thankYou;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.StringResourceModel;

import com.temenos.ebank.message.AccountDetails;

public class AccountDetailsPanelUnderlying extends AccountDetailsPanel {
	private static final long serialVersionUID = 1L;

	public AccountDetailsPanelUnderlying(String id, CompoundPropertyModel<AccountDetails> details, int index) {
		super(id, details, index);
		add(generateNumberedLabel("categoryLabel"));
		add(new Label("category"));
	}

	protected Label generateNumberedLabel(String id) {
		return new Label(id, new StringResourceModel(id + ".underlying", null));
	}
}
