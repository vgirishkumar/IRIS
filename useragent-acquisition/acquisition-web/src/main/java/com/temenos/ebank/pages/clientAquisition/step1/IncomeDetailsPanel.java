/**
 * 
 */
package com.temenos.ebank.pages.clientAquisition.step1;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;

import com.temenos.ebank.common.wicket.AddLabelAndBorderOptions;
import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

/**
 * Income details panel for current account subscription
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class IncomeDetailsPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Component[] componentsToUpdateForSingleJointToggle;

	/**
	 * Returns the components to update when changing single/joint status. These are needed by {@link SingleJointPanel}
	 * in order to perform display logic
	 * 
	 * @return
	 */
	public Component[] getComponentsToUpdateForSingleJointToggle() {
		return componentsToUpdateForSingleJointToggle;
	}

	/**
	 * Constructs the panel components
	 * 
	 * @param id
	 * @param model
	 */
	public IncomeDetailsPanel(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
		// ApplicationWicketModelObject a = model.getObject();
		IGenericChoiceRenderer annualIncomesRenderer = GenericChoiceRendererFactory.getRenderer(Choices.annualIncome, this);
		EbankDropDownChoice cmbAnnualIncome = new EbankDropDownChoice("annualIncome", annualIncomesRenderer.getChoices(),
				annualIncomesRenderer);
		cmbAnnualIncome.setRequired(true);
		final Border borderAnnualIncome = addResourceLabelAndReturnBorder(cmbAnnualIncome,  new AddLabelAndBorderOptions().setStringResModel(new StringResourceModel("annualIncomeLabel.${SoleOrJointAsText}", this, model)));
		borderAnnualIncome.setOutputMarkupPlaceholderTag(true);
		add(borderAnnualIncome);

		IGenericChoiceRenderer depositAmountRenderer = GenericChoiceRendererFactory.getRenderer(
				Choices.estimatedDepositAmount, this);
		EbankDropDownChoice cmbDepositAmount = new EbankDropDownChoice("estimatedDepositAmount", depositAmountRenderer.getChoices(), depositAmountRenderer);
		cmbDepositAmount.setRequired(true);
		final Border borderDepositAmount = addResourceLabelAndReturnBorder(cmbDepositAmount, new AddLabelAndBorderOptions().setStringResModel(new StringResourceModel("estimatedDepositAmountLabel.${SoleOrJointAsText}", this, model)));
		borderDepositAmount.setOutputMarkupPlaceholderTag(true);
		add(borderDepositAmount);
		this.componentsToUpdateForSingleJointToggle = new Component[] { borderAnnualIncome, borderDepositAmount };
	}
}
