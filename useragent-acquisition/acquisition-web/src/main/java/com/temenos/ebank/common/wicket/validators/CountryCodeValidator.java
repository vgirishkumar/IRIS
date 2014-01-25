package com.temenos.ebank.common.wicket.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

public class CountryCodeValidator extends AbstractValidator<String> {

	private static final long serialVersionUID = -2165028630963714450L;

	@Override
	protected void onValidate(IValidatable<String> validatable) {
		// not using DialCode anymore
		//String countryCode = DialCode.getCountryCode(validatable.getValue());
		String countryCode = validatable.getValue();
		if ((countryCode == null) || !isPhoneNumber(validatable.getValue())) {
			this.error(validatable, "countryCode.invalid");
		}
	}

	protected boolean isPhoneNumber(String number) {
		// Initialize reg ex for phone data.
		String expression = "^([0-9]{1,4})$";
		CharSequence inputStr = number;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return (matcher.matches());
	}
}
