package com.temenos.ebank.pages.clientAquisition.step1;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;


public class Step1FTD extends Step1 {

	public Step1FTD(boolean resumedApplication) {
		super(resumedApplication);
	}

	public Step1FTD(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}
	
	private static final long serialVersionUID = 1L;

	/**
	 * @param model
	 * @param singleJointPanel
	 */
	protected Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model, SingleJointPanel singleJointPanel) {
		//TODO: baga'l pe ala care trebuie
		return new PreAccountCheckPanelFTD("preAccountCheck", model, singleJointPanel);
	}

	@Override
	protected boolean isJointEnabled() {
		return true;
	}
}
