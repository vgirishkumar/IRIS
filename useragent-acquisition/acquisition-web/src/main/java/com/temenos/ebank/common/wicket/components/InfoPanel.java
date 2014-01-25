package com.temenos.ebank.common.wicket.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.ResourceModel;

public class InfoPanel extends Panel {
	
	private static final long serialVersionUID = 7026041519886057937L;

	public InfoPanel(String id) {
		super(id);
		add(new Label("title", new ResourceModel(id + "TITLE")).setEscapeModelStrings(false));
		add(new Label("content", new ResourceModel(id + "TEXT")).setEscapeModelStrings(false));		
	}

}
