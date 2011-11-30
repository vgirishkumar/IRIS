package com.temenos.ebank.common.wicket.summary;

import org.apache.commons.lang.StringUtils;

import com.temenos.ebank.common.wicket.components.PhoneAndPrefix;
import com.temenos.ebank.common.wicket.formValidation.ValidationErrorFeedbackBorder;

public class PhoneAndPrefixSummaryLabel extends SummaryLabel {

	private static final long serialVersionUID = 1L;
	private PhoneAndPrefix phoneAndPrefix;

	public PhoneAndPrefixSummaryLabel(PhoneAndPrefix phoneAndPrefix) {
		super(phoneAndPrefix.getId());
		this.phoneAndPrefix = phoneAndPrefix;
	}

	@Override
	protected void onBeforeRender() {
		super.onBeforeRender();
		String displayValue = (String)getSummaryLabelModelObject();
		// hide the parent border if value is not available(it was not required and the user did not fill it in)
		// another option is to fill the label with a String like "N/A"
		if (StringUtils.isBlank(displayValue)) {
			ValidationErrorFeedbackBorder border = this.findParent(ValidationErrorFeedbackBorder.class);
			border.setVisible(false);
		}

	}
	@Override
	protected Object getSummaryLabelModelObject() {
		phoneAndPrefix.getFieldsFromModel();
		String displayValue = StringUtils.join(new String[] { phoneAndPrefix.getPrefix(),
				phoneAndPrefix.getPhoneNumber() });

		if(StringUtils.isNotBlank(displayValue)) {
			displayValue = "+" + displayValue;
		}
		return displayValue;
		
	}
}
