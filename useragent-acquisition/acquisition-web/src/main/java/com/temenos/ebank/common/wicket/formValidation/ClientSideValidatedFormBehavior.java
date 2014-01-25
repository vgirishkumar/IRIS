package com.temenos.ebank.common.wicket.formValidation;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;

import com.temenos.ebank.common.wicket.WicketUtils;

public class ClientSideValidatedFormBehavior extends AbstractBehavior {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void bind(final Component component) {
		WicketUtils.addJQueryAndMetadataLibs(component);
		component.add(JavascriptPackageResource.getHeaderContribution(getClass(), "jquery.validate.modified.js"));

		@SuppressWarnings({ "rawtypes", "serial" })
		IModel variablesModel = new AbstractReadOnlyModel() {
			public Map getObject() {
				Map<String, CharSequence> variables = new HashMap<String, CharSequence>(7);
				variables.put("formMarkupId", component.getMarkupId());
				return variables;
			}
		};
		component.add(TextTemplateHeaderContributor.forJavaScript(getClass(), "ebank.validate.js", variablesModel));
	};

}
