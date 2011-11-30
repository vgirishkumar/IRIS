package com.temenos.ebank.common.wicket.feedback;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

import com.temenos.ebank.pages.startPage.ApplyForInternationalAccount;

/**
 * Alert panel for message A-0012 (refno expired when trying to resume application)
 * @author vionescu
 */
public class Alert0013Panel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new instance of the panel
	 * @param id
	 * @param model
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Alert0013Panel(String id) {
		super(id);
		Link startNewApplication = new BookmarkablePageLink("startNewApplication", ApplyForInternationalAccount.class);
		add(startNewApplication);
		ExternalLink mailtoCustomerCare = new ExternalLink("mailtoCustomerCare", new ResourceModel("mailtoCustomerCareAction"));
		add(mailtoCustomerCare);
	}

}
