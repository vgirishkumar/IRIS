package com.temenos.ebank.pages.clientAquisition.step4;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import java.math.BigDecimal;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.RangeValidator;

import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;

@SuppressWarnings({"unchecked", "serial"})
public class SecondFinancialDetailsPanelRS extends Panel {
	public SecondFinancialDetailsPanelRS(String id, IModel<ApplicationWicketModelObject> model, boolean isOpeningCurrentAccount) {
		super(id, model);
		//txt - customer mother's maiden name
		EbankTextField custMotherMaidenName = new EbankTextField("customer.motherMaidenName");
		add(addResourceLabelAndReturnBorder(custMotherMaidenName).setVisible(isOpeningCurrentAccount));
		EbankTextField txtDepositAmount = new EbankTextField("depositAmount");
		//TODO: scoate hard code de min max range
		add(addResourceLabelAndReturnBorder(txtDepositAmount.add(new RangeValidator<BigDecimal>(new BigDecimal(500), new BigDecimal(2000)))));
	}
}
