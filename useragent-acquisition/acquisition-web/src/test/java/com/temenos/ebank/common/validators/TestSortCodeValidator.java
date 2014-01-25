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
public class TestSortCodeValidator {

	@Test
	public void testInvalidSortCode() {

		String[] invalidInputs = {
				// letters ko
				"56a666"
				};

		RegexValidator validator = new RegexValidator(RegexValidator.DIGITS, EbankValidationException.SORT_CODE_INVALID);
		ValidatorTestUtils.checkInvalidInputs(validator, invalidInputs);
	}

	@Test
	public void testValidSortCode() {

		String[] validInputs = {
		// 6 numbers - ok
		"993355", };

		RegexValidator validator = new RegexValidator(RegexValidator.DIGITS, EbankValidationException.SORT_CODE_INVALID);
		ValidatorTestUtils.checkValidInputs(validator, validInputs);
	}

}
