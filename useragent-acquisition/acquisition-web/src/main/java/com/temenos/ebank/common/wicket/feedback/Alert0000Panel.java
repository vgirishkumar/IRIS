package com.temenos.ebank.common.wicket.feedback;

import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import com.temenos.ebank.pages.startPage.ApplyForInternationalAccount;

/**
 * Alert panel for message A-0000 (wrong combination of email refno when trying to resume application)
 * @author vionescu
 */
public class Alert0000Panel extends Panel {

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
	public Alert0000Panel(String id) {
		super(id);
		Link startNewApplication = new BookmarkablePageLink("startNewApplication", ApplyForInternationalAccount.class);
		add(startNewApplication);
	}

}
