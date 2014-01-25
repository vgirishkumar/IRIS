package com.temenos.ebank.pages;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;

public class BackButtonWarningPage extends BasePage{
	@SuppressWarnings({ "rawtypes", "serial" })
	public BackButtonWarningPage() {
		Form form = new Form("form");
		add(form);
		form.add(new Link("previous"){
			@Override
			public void onClick() {
				setResponsePage(getApplication().getHomePage());
			}
		});
	}
}
