package com.temenos.ebank.common.wicket.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import com.temenos.ebank.exceptions.EbankValidationException;

/**
 * Used to validate fields against a regular expression.
 * 
 * @author gcristescu
 */
public class RegexValidator extends AbstractValidator<String> {
	private static final long serialVersionUID = 1L;

	public final static String DIGITS = "^[0-9]*$";
	public final static String BIC = "^[a-zA-Z]{4}[a-zA-Z]{2}[a-zA-Z0-9]{2}([a-zA-Z0-9]{3})?$";

	private String regex;
	private String exceptionKey;

	/**
	 * Constructor.
	 * 
	 * @param regex
	 *            regular expression to use for validation
	 * @param exceptionKey
	 *            property key for exception to throw in case of error (see {@link EbankValidationException}
	 */
	public RegexValidator(final String regex, final String exceptionKey) {
		this.regex = regex;
		this.exceptionKey = exceptionKey;
	}

	@Override
	protected void onValidate(IValidatable<String> validatable) {
		String value = validatable.getValue().trim();
		if (!isValid(value)) {
			this.error(validatable, exceptionKey);
		}
	}

	protected boolean isValid(String number) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(number);
		return (matcher.matches());
	}
}
