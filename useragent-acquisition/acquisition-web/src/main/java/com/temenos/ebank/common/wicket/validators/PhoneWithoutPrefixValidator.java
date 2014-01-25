package com.temenos.ebank.common.wicket.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public class PhoneWithoutPrefixValidator extends AbstractValidator<String> {

	private static final long serialVersionUID = -2165028630963714450L;

	@Override
	protected void onValidate(IValidatable<String> validatable) {
		String phone = validatable.getValue();
		if (!isPhoneNumber(phone)) {
			this.error(validatable, "phoneNumber.invalid");
		}
	}

	protected boolean isPhoneNumber(String number) {
		// Initialize reg ex for phone data.
		String expression = "^([0-9]{1,14})$";
		CharSequence inputStr = number;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return (matcher.matches());
	}
}
