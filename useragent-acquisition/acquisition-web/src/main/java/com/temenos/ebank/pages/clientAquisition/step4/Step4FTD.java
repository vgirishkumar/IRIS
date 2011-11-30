package com.temenos.ebank.pages.clientAquisition.step4;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

public class Step4FTD extends Step4 {

	private static final long serialVersionUID = 1L;

	public Step4FTD(boolean resumedApplication) {
		super(resumedApplication);
	}

	public Step4FTD(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}
	// TODO: insert (FTD+IASA) condition at cross-sell; we might need overriding addStepComponents
	
	@Override
	protected Panel newSecondFinancialDetailsPanel(IModel<ApplicationWicketModelObject> model) {
		return new SecondFinancialDetailsPanelFTD("secondFinancialDetails", model);
	}
}
