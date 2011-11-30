/**
 * 
 */
package com.temenos.ebank.pages.pageMasters;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Your details panel, a panel containing the email id and the app reference, shown
 * on the right hand side of the page, only for resumed applications.
 * @author vionescu
 */
public class YourDetailsPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** 
	 * Constructs a new instance
	 * @param id
	 * @param model
	 */
	public YourDetailsPanel(String id, IModel<ApplicationWicketModelObject> model, boolean resumedApplication) {
		super(id, model);
		Label appRefLabel = new Label("appRef");
		add(appRefLabel);
		Label emailLabel = new Label("customer.emailAddress");
		add(emailLabel);
		setVisible(resumedApplication);
	}
}
