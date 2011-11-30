package com.temenos.ebank.pages;

import org.apache.wicket.markup.html.pages.PageExpiredErrorPage;

public class Logout extends PageExpiredErrorPage {
	public Logout() {
		getSession().invalidate();
	}
}
