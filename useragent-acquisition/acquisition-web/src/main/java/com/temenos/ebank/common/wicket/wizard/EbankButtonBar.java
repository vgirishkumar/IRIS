package com.temenos.ebank.common.wicket.wizard;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.WizardButton;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.template.TextTemplateHeaderContributor;

import com.temenos.ebank.common.wicket.WicketUtils;
import com.temenos.ebank.domain.ConfigParamTable.BOOLEAN;
import com.temenos.ebank.pages.clientAquisition.wizard.SupportSnippet;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam;

public class EbankButtonBar extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private WizardButton saveButton;
	private WizardButton saveAndCancelButton;
	private EbankCancelButton cancelButton;
	private WizardButton nextButton;

	@SpringBean(name = "serviceConfigParam")
	private IServiceConfigParam serviceConfigParam;
	
	@SuppressWarnings({ "unchecked", "serial", "rawtypes" })
	public EbankButtonBar(String id, IWizard wizard) {
		super(id);
		add(new SupportSnippet("supportSnippet"));

		add(addCancelClientSideValidationBehaviour(new EbankPreviousButton("previous", wizard)));

		this.nextButton = new EbankNextButton("next", wizard);
		add(nextButton);

		// add(new YourLastButton("last", wizard));

		// add cancel class to button's HTML class so that client side validation is skipped (required by JQuery
		// validation plug-in)
		this.cancelButton = new EbankCancelButton("cancel", wizard);
		add(addCancelClientSideValidationBehaviour(cancelButton));
		add(new EbankFinishButton("finish", wizard));
		
		this.saveButton = new EbankSaveButton("save", wizard, false);
		saveButton.setDefaultFormProcessing(false);
		add(addCancelClientSideValidationBehaviour(saveButton));

		this.saveAndCancelButton = new EbankSaveButton("saveAndCancel", wizard, true);
		saveAndCancelButton.setDefaultFormProcessing(false);
		saveAndCancelButton.setVisible(false);
		add(addCancelClientSideValidationBehaviour(saveAndCancelButton));

		add(new Label("confirmText", new ResourceModel("confirmText")));
		IModel variablesModel = new AbstractReadOnlyModel() {
			public Map getObject() {
				Map<String, String> variables = new HashMap<String, String>();
				variables.put("formMarkupId", (findParent(Form.class).getMarkupId()));

				variables.put("dialogMarkupId", "confirmDialog");
				variables.put("saveButtonMarkupId", saveButton.getMarkupId());
				variables.put("saveAndCancelButtonMarkupId", saveAndCancelButton.getMarkupId());
				variables.put("cancelButtonMarkupId", cancelButton.getMarkupId());

				variables.put("confirmTitle", getString("confirmTitle"));
				variables.put("back", getString("back"));
				variables.put("exit", getString("exit"));
				variables.put("saveAndExit", getString("saveAndExit"));
				
				return variables;
			}
		};

		// add onclick functionalty
		// retrieve config value
		Boolean analyticsEnabled = serviceConfigParam.getConfigParamTable().get(BOOLEAN.ANALYTICS_ENABLED);

		if (analyticsEnabled) {
			cancelButton.add(new AttributeAppender("onclick", new Model("callAnalytics('cancel')"), ";"));
			saveButton.add(new AttributeAppender("onclick", new Model("callAnalytics('save');"), ";"));
		}
		
		cancelButton.add(new AttributeAppender("onclick", new Model("doCancel(); return false;"), ";"));
		
		// add libraries
		WicketUtils.addJQueryUILib(this);
		add(TextTemplateHeaderContributor.forJavaScript(this.getClass(), "ebankButtonBar.js", variablesModel));
		add(JavascriptPackageResource.getHeaderContribution("js/lib/AnalyticsButtons.js"));
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	/**
	 * Adds behaviour to cancel client-side validation
	 * 
	 * @param b
	 * @return
	 */
	public static Component addCancelClientSideValidationBehaviour(Component b) {
		return b.add(new AttributeAppender("class", new Model<String>("cancelClientSideValidation"), " "));
	}

	public WizardButton getSaveButton() {
		return saveButton;
	}

	public EbankCancelButton getCancelButton() {
		return cancelButton;
	}

	public WizardButton getNextButton() {
		return nextButton;
	}
}
