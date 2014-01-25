package com.temenos.ebank.common.wicket.feedback;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Panel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Alert panel for unexpected technical error
 * 
 * @author vionescu
 * 
 */
public class Alert0001Panel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Alert0001Panel(String id, Alert alert) {
		super(id);
		//setDefaultModel(alert.getPageModel());
		boolean isSole = ((ApplicationWicketModelObject)alert.getPageModel().getObject()).getIsSole();
		WebMarkupContainer containerMessageSole = new WebMarkupContainer("messageSole");
		containerMessageSole.setVisible(isSole);
		add(containerMessageSole);
		WebMarkupContainer containerMessageJoint = new WebMarkupContainer("messageJoint");
		containerMessageJoint.setVisible(!isSole);
		add(containerMessageJoint);
	}

}
