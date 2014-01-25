package com.temenos.ebank.pages.clientAquisition.step1;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Step 1 for RASA client aquisition
 * @author vionescu
 *
 */
public class Step1RASA extends Step1 {

	public Step1RASA(boolean resumedApplication) {
		super(resumedApplication);
	}

	public Step1RASA(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}
	
	private static final long serialVersionUID = 1L;

	/**
	 * @param model
	 * @param singleJointPanel
	 */
	protected Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model, SingleJointPanel singleJointPanel) {
		return new PreAccountCheckPanelRASA("preAccountCheck", model, singleJointPanel);
	}

	@Override
	protected boolean isJointEnabled() {
		return true;
	}
}
