package com.temenos.ebank.pages.clientAquisition.step4;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import java.math.BigDecimal;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

@SuppressWarnings({"unchecked","rawtypes","serial"})
public class SecondFinancialDetailsPanelFTD extends Panel {
	public SecondFinancialDetailsPanelFTD(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);

		EbankTextField txtDepositAmount = new EbankTextField("depositAmount");
		//TODO: scoate hard code de min max range
		txtDepositAmount.add(new RangeValidator<BigDecimal>(new BigDecimal(10000), new BigDecimal(5000000)));
		add(addResourceLabelAndReturnBorder(txtDepositAmount));
				
		//drop down --> account option 2
		IGenericChoiceRenderer interestRenderer = GenericChoiceRendererFactory.getRenderer(Choices.interestPayment, this);
		EbankDropDownChoice cmbInterestPayment = new EbankDropDownChoice("interestPayment",
				interestRenderer.getChoices(), interestRenderer);
		cmbInterestPayment.setRequired(true);
		add(addResourceLabelAndReturnBorder(cmbInterestPayment));
	}
}
