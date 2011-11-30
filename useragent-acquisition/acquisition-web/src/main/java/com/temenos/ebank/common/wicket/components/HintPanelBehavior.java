package com.temenos.ebank.common.wicket.components;

import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;

public class HintPanelBehavior extends AbstractBehavior {
	private static final long serialVersionUID = 1L;

	public final static String INIT_HINT_PANEL_JS = "initHintPanel();";

	@Override
	public final void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderOnDomReadyJavascript(INIT_HINT_PANEL_JS);
	}


}
