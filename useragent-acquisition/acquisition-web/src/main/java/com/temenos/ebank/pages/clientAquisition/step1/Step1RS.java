package com.temenos.ebank.pages.clientAquisition.step1;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;


public class Step1RS extends Step1 {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Step1RS(boolean resumedApplication) {
		super(resumedApplication);
	}

	public Step1RS(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}	

	@Override
	protected Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model,
			SingleJointPanel singleJointPanel) {
		return new PreAccountCheckPanelRS("preAccountCheck", model);
	}

	@Override
	protected boolean isJointEnabled() {
		return false;
	}


}
