/**
 * 
 */
package com.temenos.ebank.testUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.Validatable;

/**
 * Utility class for testing validators
 * 
 * @author vionescu
 * 
 */
public class ValidatorTestUtils {

	/**
	 * Runs assertFalse on the validator with the inputs supplied.
	 * 
	 * @param invalidInputs
	 *            An array containing the expected invalid inputs
	 * @param validator
	 *            The validator to test
	 */
	public static <T> void checkInvalidInputs(IValidator<T> validator, T[] invalidInputs) {
		for (int i = 0; i < invalidInputs.length; i++) {
			T inputToCheck = invalidInputs[i];
			Validatable<T> validatable = new Validatable<T>(inputToCheck);
			validator.validate(validatable);
			assertFalse(inputToCheck + " was valid but shouldn't be", validatable.isValid());
		}
	}

	/**
	 * Runs assertTrue on the validator with the inputs supplied.
	 * 
	 * @param validInputs
	 *            An array containing the expected valid inputs
	 * @param validator
	 *            The validator to test
	 */
	public static <T> void checkValidInputs(IValidator<T> validator, T[] validInputs) {
		for (int i = 0; i < validInputs.length; i++) {
			T inputToCheck = validInputs[i];
			Validatable<T> validatable = new Validatable<T>(inputToCheck);
			validator.validate(validatable);
			assertTrue(inputToCheck + " wasn't valid but should be", validatable.isValid());
		}
	}
}
