package com.temenos.ebank.pages.clientAquisition.step4;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FinancialDetailsPanel extends Panel {

	private static final long serialVersionUID = 7026041519886057937L;
	
	@SuppressWarnings("serial")
	public FinancialDetailsPanel(String id, final IModel<ApplicationWicketModelObject> model,
			boolean showEstimatedAnnualDeposit) {
		super(id, model);
		IGenericChoiceRenderer establishmentReasonRenderer = GenericChoiceRendererFactory.getRenderer(
				Choices.establishmentReason, this);
		final EbankDropDownChoice cmbAccEstablishingReason = new EbankDropDownChoice("accEstablishReason",
				establishmentReasonRenderer.getChoices(), establishmentReasonRenderer);
		cmbAccEstablishingReason.setRequired(true);
		add(addResourceLabelAndReturnBorder(cmbAccEstablishingReason));

		// txt other establishment reason
		EbankTextField textOtherEstablishReason = new EbankTextField("otherEstablishReason");
		final Border borderOtherEstablishReason = addResourceLabelAndReturnBorder(textOtherEstablishReason);
		borderOtherEstablishReason.setVisible("OTHER".equals(cmbAccEstablishingReason.getModelValue()));
		borderOtherEstablishReason.setOutputMarkupPlaceholderTag(true);
		add(borderOtherEstablishReason);

		cmbAccEstablishingReason.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean visibleOther = "OTHER".equals(cmbAccEstablishingReason.getConvertedInput());
				borderOtherEstablishReason.setVisible(visibleOther);
				model.getObject().setOtherEstablishReason(null);
				target.addComponent(borderOtherEstablishReason);
			}
		});

		// combo - reason for account usage
		IGenericChoiceRenderer usageRenderer = GenericChoiceRendererFactory.getRenderer(Choices.usageReason, this);
		final EbankDropDownChoice cmbAccUsageReason = new EbankDropDownChoice("accUsageReason",
				usageRenderer.getChoices(), usageRenderer);
		cmbAccUsageReason.setRequired(true);
		add(addResourceLabelAndReturnBorder(cmbAccUsageReason));

		// txt other usage reason
		EbankTextField textOtherUsageReason = new EbankTextField("otherUsageReason");
		final Border borderOtherUsageReason = addResourceLabelAndReturnBorder(textOtherUsageReason);
		borderOtherUsageReason.setVisible("OTHER".equals(cmbAccUsageReason.getModelValue()));
		borderOtherUsageReason.setOutputMarkupPlaceholderTag(true);
		add(borderOtherUsageReason);

		cmbAccUsageReason.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean visibleOther = "OTHER".equals(cmbAccUsageReason.getConvertedInput());
				borderOtherUsageReason.setVisible(visibleOther);
				model.getObject().setOtherUsageReason(null);
				target.addComponent(borderOtherUsageReason);
			}
		});

		if (showEstimatedAnnualDeposit) {
			// drop down - annual deposit
			IGenericChoiceRenderer annualDepositRenderer = GenericChoiceRendererFactory.getRenderer(Choices.annualDeposit,
					this);
			EbankDropDownChoice cmbAnnualDeposit = new EbankDropDownChoice("annualDeposit",
					annualDepositRenderer.getChoices(), annualDepositRenderer);
			cmbAnnualDeposit.setRequired(true);
			add(addResourceLabelAndReturnBorder(cmbAnnualDeposit));
		} else {
			add(new Label("annualDeposit", ""));
		}

		// drop down choice - activities generating money for deposit
		IGenericChoiceRenderer activityRenderer = GenericChoiceRendererFactory.getRenderer(Choices.activityOrigin, this);
		final EbankDropDownChoice cmbActivityOriginDeposit = new EbankDropDownChoice("activityOriginDeposit",
				activityRenderer.getChoices(), activityRenderer);
		cmbActivityOriginDeposit.setRequired(true);
		add(addResourceLabelAndReturnBorder(cmbActivityOriginDeposit));

		// txt other activity origin deposit
		EbankTextField textOtherActivityOrigin = new EbankTextField("otherActivityOrigin");
		final Border borderOtherActivityOrigin = addResourceLabelAndReturnBorder(textOtherActivityOrigin);
		borderOtherActivityOrigin.setVisible("OTHER".equals(cmbActivityOriginDeposit.getModelValue()));
		borderOtherActivityOrigin.setOutputMarkupPlaceholderTag(true);
		add(borderOtherActivityOrigin);

		cmbActivityOriginDeposit.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean visibleOther = "OTHER".equals(cmbActivityOriginDeposit.getConvertedInput());
				borderOtherActivityOrigin.setVisible(visibleOther);
				model.getObject().setOtherActivityOrigin(null);
				target.addComponent(borderOtherActivityOrigin);
			}
		});

		// multiple choice - countries generating initial deposit
		IGenericChoiceRenderer countriesRenderer = GenericChoiceRendererFactory.getRenderer(Choices.COUNTRY, this);
//		ListMultipleChoice mchCountryOriginMoney = new ListMultipleChoice("countriesOriginMoney", new PropertyModel(
//				model, "countriesOriginMoney"), countriesRenderer.getChoices(), countriesRenderer);
		EbankDropDownChoice cmbCountryOriginMoney = new EbankDropDownChoice("countryOriginMoney",
				countriesRenderer.getChoices(), countriesRenderer);
		cmbCountryOriginMoney.setRequired(true);
		add(addResourceLabelAndReturnBorder(cmbCountryOriginMoney));

		// multiple choice - activities generating total wealth
		IGenericChoiceRenderer activityWealthRenderer = GenericChoiceRendererFactory
				.getRenderer(Choices.activityWealth, this);
		final EbankDropDownChoice cmbActivityWealth = new EbankDropDownChoice("activityWealth",
				activityWealthRenderer.getChoices(), activityWealthRenderer);
		cmbActivityWealth.setRequired(true);
		add(addResourceLabelAndReturnBorder(cmbActivityWealth));

		// txt other activity origin deposit
		EbankTextField textOtherActivityWealth = new EbankTextField("otherActivityWealth");
		final Border borderOtherActivityWealth = addResourceLabelAndReturnBorder(textOtherActivityWealth);
		borderOtherActivityWealth.setVisible("OTHER".equals(cmbActivityWealth.getModelValue()));
		borderOtherActivityWealth.setOutputMarkupPlaceholderTag(true);
		add(borderOtherActivityWealth);

		cmbActivityWealth.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean visibleOther = "OTHER".equals(cmbActivityWealth.getConvertedInput());
				borderOtherActivityWealth.setVisible(visibleOther);
				model.getObject().setOtherActivityWealth(null);
				target.addComponent(borderOtherActivityWealth);
			}
		});

	}

}
