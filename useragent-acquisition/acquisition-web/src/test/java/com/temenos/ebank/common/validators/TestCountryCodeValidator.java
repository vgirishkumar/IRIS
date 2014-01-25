/**
 * 
 */
package com.temenos.ebank.common.validators;

import org.junit.Test;

import com.temenos.ebank.common.wicket.validators.CountryCodeValidator;
import com.temenos.ebank.testUtils.ValidatorTestUtils;

/**
 * @author vionescu
 * 
 */
public class TestCountryCodeValidator {

	@Test
	public void testValidCountryCode() {
		CountryCodeValidator validator = new CountryCodeValidator();
		String[] validInputs = { "40",
		// spaces NOT ok
				"1234", };
		ValidatorTestUtils.checkValidInputs(validator, validInputs);
	}

	public void testInvalidCountryCode() {
		CountryCodeValidator validator = new CountryCodeValidator();
		String[] invalidInputs = { "40 ",
		// spaces NOT ok
				"12345", };
		ValidatorTestUtils.checkInvalidInputs(validator, invalidInputs);
	}
}
