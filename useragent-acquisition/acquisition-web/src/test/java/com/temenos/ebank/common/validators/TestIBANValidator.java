/**
 * 
 */
package com.temenos.ebank.common.validators;

import org.junit.Test;

import com.temenos.ebank.common.wicket.validators.IBANValidator;
import com.temenos.ebank.testUtils.ValidatorTestUtils;

/**
 * @author vionescu
 * 
 */
public class TestIBANValidator {

	/**
	 * Test method for validate.
	 */
	@Test
	public void testInvalidIBAN() {

		String[] invalidInputs = {
				// IBAN UK ko (transposition of two letters)
				"CH9300672011623852957",
				// IBAN ko - too short
				"CH9300672011623852",
				// IBAN ko - too long
				"CH930067201162385212345678908765433", };

		IBANValidator validator = new IBANValidator();
		ValidatorTestUtils.checkInvalidInputs(validator, invalidInputs);
	}

	@Test
	public void testValidIBAN() {

		String[] validInputs = {
				// IBAN Greece Ok
				"GR1601101250000000012300695",
				// IBAN UK Ok
				"GB29NWBK60161331926819",
				// IBAN UK Ok
				"CH9300762011623852957",

		};
		IBANValidator validator = new IBANValidator();
		ValidatorTestUtils.checkValidInputs(validator, validInputs);
	}

}
