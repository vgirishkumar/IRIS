/**
 * 
 */
package com.temenos.ebank.common.validators;

import org.junit.Test;

import com.temenos.ebank.common.wicket.validators.PhoneNumberValidator;
import com.temenos.ebank.testUtils.ValidatorTestUtils;

/**
 * @author vionescu
 * 
 */
public class TestPhoneNumberValidator {

	@Test
	public void testInvalidPhoneNumber() {
		String[] invalidInputs = {
		// invalid - letters in phone noe
		"1234556789012345678", };

		PhoneNumberValidator validator = new PhoneNumberValidator();
		ValidatorTestUtils.checkInvalidInputs(validator, invalidInputs);
	}

	@Test
	public void testValidPhoneNumber() {
		String[] validInputs = { "407345545",
				// spaces NOT ok
				"407345545", };
		PhoneNumberValidator validator = new PhoneNumberValidator();
		ValidatorTestUtils.checkValidInputs(validator, validInputs);
	}
}
