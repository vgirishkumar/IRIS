package com.temenos.ebank.pages.clientAquisition.step5;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.AddLabelAndBorderOptions;
import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankCheckBoxMultipleChoice;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper;

@SuppressWarnings("serial")
public class Step5 extends EbankWizardStep {

	private static final Integer RESUME_STEP_5 = Integer.valueOf(5);

	@SpringBean(name = "step45DomainToWicketMapper")
	private IDomainToWicketMapper<Application, ApplicationWicketModelObject> domainToWicketMapper;

	public Step5(boolean resumedApplication) {
		this.resumedApplication = resumedApplication;
	}

	public Step5(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}

	@Override
	protected IDomainToWicketMapper<Application, ApplicationWicketModelObject> getDomainToWicketMapper() {
		return domainToWicketMapper;
	}

	@Override
	protected Integer getStepNumber() {
		return RESUME_STEP_5;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void addStepComponents(final IModel<ApplicationWicketModelObject> model) {
		ApplicationWicketModelObject app = (ApplicationWicketModelObject) getDefaultModelObject();

		Border border = null;

		AddLabelAndBorderOptions escapeModelStringLabelOption = new AddLabelAndBorderOptions();
		escapeModelStringLabelOption.setEscapeModelStrings(false);

		// checkbox - offshore
		CheckBox checkDiscloseIdentity = new CheckBox("customer.flagDiscloseIdentity");
		checkDiscloseIdentity.setRequired(true);
		add(addResourceLabelAndReturnBorder(checkDiscloseIdentity, escapeModelStringLabelOption).setVisible(app.isOffshoreOpenedAccount()));

		// checkbox - data privacy
		CheckBox checkDataPrivacy = new CheckBox("flagDataPrivacy");
		checkDataPrivacy.setRequired(true);
		border = addResourceLabelAndReturnBorder(checkDataPrivacy, escapeModelStringLabelOption);
		add(border);

		// checkbox - terms and conditions
		CheckBox checkTermsAndConditions = new CheckBox("flagTcs");
		checkTermsAndConditions.setRequired(true);
		border = addResourceLabelAndReturnBorder(checkTermsAndConditions, escapeModelStringLabelOption);
		add(border);

		// second customer

		// checkbox - offshore
		CheckBox secCheckDiscloseIdentity = new CheckBox("secondCustomer.flagDiscloseIdentity");
		secCheckDiscloseIdentity.setRequired(true);
		add(addResourceLabelAndReturnBorder(secCheckDiscloseIdentity).setVisible(
				app.isOffshoreOpenedAccount() && !app.getIsSole()));

		// checkbox - joint application declaration
		CheckBox checkJointAppDeclaration = new CheckBox("flagJointApp");
		checkJointAppDeclaration.setRequired(true);
		add(addResourceLabelAndReturnBorder(checkJointAppDeclaration).setVisible(!app.getIsSole()));

		// checkbox group - method of contact - customer
		IGenericChoiceRenderer marketingContactChoiceRenderer = GenericChoiceRendererFactory.getRenderer(
				Choices.marketingContactChoice, this);
		boolean methodOfContactRequired = Boolean.TRUE.equals(!app.getCustomer().getFlagReceiveMarketing());

		final EbankCheckBoxMultipleChoice checkGroup = new EbankCheckBoxMultipleChoice("customer.marketingContactMethods",
				new PropertyModel(model, "customer.marketingContactMethods"),
				marketingContactChoiceRenderer.getChoices(), marketingContactChoiceRenderer) {
		};

		checkGroup.setRequired(true);
		final Border customerMethodOfContactBorder = addResourceLabelAndReturnBorder(checkGroup);
		customerMethodOfContactBorder.setOutputMarkupPlaceholderTag(true);
		customerMethodOfContactBorder.setVisible(methodOfContactRequired);
		add(customerMethodOfContactBorder);

		// checkbox - prefer to receive marketing info - customer
		final AjaxCheckBox checkReceiveMarketing = new AjaxCheckBox("customer.flagReceiveMarketing") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				customerMethodOfContactBorder.setVisible(!customerMethodOfContactBorder.isVisible());
				model.getObject().getCustomer().setMarketingContactMethods(null);
				if (target != null) {
					target.addComponent(customerMethodOfContactBorder);
				}
			}
		};
		border = addResourceLabelAndReturnBorder(checkReceiveMarketing);
		add(border);

		// checkbox group - method of contact - second customer
		final CheckBoxMultipleChoice secondCustomerMethodOfContact = new CheckBoxMultipleChoice(
				"secondCustomer.marketingContactMethods", new PropertyModel(model,
						"secondCustomer.marketingContactMethods"), marketingContactChoiceRenderer.getChoices(),
				marketingContactChoiceRenderer);

		methodOfContactRequired = (!app.getIsSole())
				&& Boolean.TRUE.equals(!app.getSecondCustomer().getFlagReceiveMarketing());
		secondCustomerMethodOfContact.setRequired(true);
		final Border secondCustomerMethodOfContactBorder = addResourceLabelAndReturnBorder(secondCustomerMethodOfContact);
		secondCustomerMethodOfContactBorder.setOutputMarkupPlaceholderTag(true);
		secondCustomerMethodOfContactBorder.setVisible(methodOfContactRequired);
		add(secondCustomerMethodOfContactBorder);

		// checkbox - prefer to receive marketing info - second customer
		final AjaxCheckBox secondCheckReceiveMarketing = new AjaxCheckBox("secondCustomer.flagReceiveMarketing") {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				secondCustomerMethodOfContactBorder.setVisible(!secondCustomerMethodOfContactBorder.isVisible());
				model.getObject().getSecondCustomer().setMarketingContactMethods(null);
				if (target != null) {
					target.addComponent(secondCustomerMethodOfContactBorder);
				}
			}
		};
		border = addResourceLabelAndReturnBorder(secondCheckReceiveMarketing);
		border.setVisible(!app.getIsSole());
		add(border);

		// combo - hear about us
		IGenericChoiceRenderer hearSourceRenderer = GenericChoiceRendererFactory.getRenderer(Choices.hearSource, this);
		EbankDropDownChoice cmbHearSource = new EbankDropDownChoice("feedbackHear",	hearSourceRenderer.getChoices(), hearSourceRenderer);
		cmbHearSource.setRequired(true);
		add(addResourceLabelAndReturnBorder(cmbHearSource));

		// textfield - promo code
		EbankTextField txtPromoCode = new EbankTextField("promoCode", true);
		border = addResourceLabelAndReturnBorder(txtPromoCode);
		add(border);
	}
}