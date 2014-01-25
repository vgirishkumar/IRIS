/**
 * 
 */
package com.temenos.ebank.common.validators;

import org.junit.Test;

import com.temenos.ebank.common.wicket.validators.RegexValidator;
import com.temenos.ebank.exceptions.EbankValidationException;
import com.temenos.ebank.testUtils.ValidatorTestUtils;

/**
 * @author vionescu
 * 
 */
public class TestBICValidator {

	/**
	 * Test method for validate.
	 */
	@Test
	public void testInvalidBIC() {
		String[] invalidBics = {
				// special caracters
				"ABABCD_88",
				// special caracters
				"ABABCD88$",
				// special caracters
				"%ABABCD88",
				// not starting with 4 letters
				"8AABCD88",
				// not starting with 4 letters
				"AA8BCD88",
				// not starting with 4 letters
				"AAB8CD88",
				// not letters in position 5 and 6
				"AABC3D88",
				// not letters in position 5 and 6
				"AABCD388"
				};
		RegexValidator validator = new RegexValidator(RegexValidator.BIC, EbankValidationException.BIC_INVALID);
		ValidatorTestUtils.checkInvalidInputs(validator, invalidBics);
	}

	@Test
	public void testValidBIC() {
		String[] validBics = {
				// 8 alaphanumeric chars
				"ABABCD88",
				// 11 alaphanumeric chars
				"ABABCD88ABB", };
		RegexValidator validator = new RegexValidator(RegexValidator.BIC, EbankValidationException.BIC_INVALID);
		ValidatorTestUtils.checkValidInputs(validator, validBics);
	}

}
