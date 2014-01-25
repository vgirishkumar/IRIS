package com.temenos.ebank.common.wicket.formValidation;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;

import com.temenos.ebank.common.wicket.WicketUtils;

public class DefaultTextFieldFormBehavior extends AbstractBehavior {

	private static final long serialVersionUID = 1L;

	public final static String INIT_DEFAULT_TEXT_JS_CALL = "$.defaultText({css:'defaultTextClass'});";

	@Override
	public void bind(Component component) {
		WicketUtils.addJQueryAndMetadataLibs(component);
		component.add(JavascriptPackageResource.getHeaderContribution(getClass(), "jquery.defaultText.modified.js"));
	}

	@Override
	public final void renderHead(IHeaderResponse response) {
		super.renderHead(response);

		response.renderOnDomReadyJavascript(INIT_DEFAULT_TEXT_JS_CALL);
	}

}
