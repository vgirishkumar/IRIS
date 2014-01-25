package com.temenos.ebank.common.wicket.feedback;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import com.temenos.ebank.pages.startPage.ApplyForInternationalAccount;

/**
 * Alert panel for message A-0013 (refno unavailable - the user is trying to resume a completed application)
 * @author vionescu
 */
public class Alert0012Panel extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new instance of the panel
	 * 
	 * @param id
	 * @param model
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Alert0012Panel(String id) {
		super(id);
		Link startNewApplication = new BookmarkablePageLink("startNewApplication", ApplyForInternationalAccount.class);
		add(startNewApplication);
	}

}
