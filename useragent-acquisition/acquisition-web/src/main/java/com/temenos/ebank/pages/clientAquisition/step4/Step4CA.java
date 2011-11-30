package com.temenos.ebank.pages.clientAquisition.step4;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

public class Step4CA extends Step4 {

	private static final long serialVersionUID = 1L;

	public Step4CA(boolean resumedApplication) {
		super(resumedApplication);
	}

	public Step4CA(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}
	
	@Override
	protected Panel newSecondFinancialDetailsPanel(IModel<ApplicationWicketModelObject> model) {
		return new SecondFinancialDetailsPanelCA("secondFinancialDetails", model);
	}
}
