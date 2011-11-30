package com.temenos.ebank.common.wicket.feedback;

import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

/**
 * Alert panel base class for ingeligible alert panels
 * 
 * @author vionescu
 * 
 */
public class IneligiblePanel extends Panel {

	private static final long serialVersionUID = 1L;

	public IneligiblePanel(String id) {
		super(id);
		ExternalLink linkSeeCriteria = new ExternalLink("linkSeeCriteria", new ResourceModel("linkSeeCriteriaAction"));
		PopupSettings popupSettingsSeeCriteria = new PopupSettings();
		popupSettingsSeeCriteria.setHeight(420);
		popupSettingsSeeCriteria.setWidth(550);
		popupSettingsSeeCriteria.setWindowName("alertPopup");
		linkSeeCriteria.setPopupSettings(popupSettingsSeeCriteria);
		add(linkSeeCriteria);
		ExternalLink mailtoCustomerCare = new ExternalLink("mailtoCustomerCare", new ResourceModel("mailtoCustomerCareAction"));
		add(mailtoCustomerCare);
	}

}
