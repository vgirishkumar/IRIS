package com.temenos.ebank.common.wicket.components;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.model.StringResourceModel;

import com.temenos.ebank.common.wicket.WicketUtils;

public class DisableCopyPasteFieldBehavior extends AbstractBehavior {

	private static final long serialVersionUID = 1L;

	private String componentMarkupID;
	private StringResourceModel pasteDisabledMessage;

	@Override
	public void bind(final Component component) {
		WicketUtils.addJQueryAndMetadataLibs(component);
		this.componentMarkupID = component.getMarkupId();
		this.pasteDisabledMessage = new StringResourceModel("pasteDisabled", component, null);
		component.add(JavascriptPackageResource.getHeaderContribution(getClass(), "disableCopyPaste.js"));
	}

	
	@Override
	public final void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.renderOnLoadJavascript(String.format("disablePaste('%1$s','%2$s')", this.componentMarkupID, this.pasteDisabledMessage.getString()));
	}
}
