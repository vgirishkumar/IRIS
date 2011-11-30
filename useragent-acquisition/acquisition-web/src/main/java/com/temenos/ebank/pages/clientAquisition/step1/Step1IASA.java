package com.temenos.ebank.pages.clientAquisition.step1;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;


/**
 * Step 1 for IASA client aquisition
 * @author vionescu
 *
 */
public class Step1IASA extends Step1 {

	public Step1IASA(boolean resumedApplication) {
		super(resumedApplication);
	}

	public Step1IASA(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}
	
	private static final long serialVersionUID = 1L;

	/**
	 * @param model
	 * @param singleJointPanel
	 */
	protected Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model, SingleJointPanel singleJointPanel) {
		return new PreAccountCheckPanelIASA("preAccountCheck", model, singleJointPanel);
	}


	@Override
	protected boolean isJointEnabled() {
		return true;
	}
}
