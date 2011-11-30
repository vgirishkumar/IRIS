package com.temenos.ebank.common.wicket.validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import com.temenos.ebank.exceptions.EbankValidationException;

/**
 * 
 * Used to validate IBAN account numbers.
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class IBANValidator extends AbstractValidator<String> {
	private static final long serialVersionUID = 1L;

	@Override
	protected void onValidate(IValidatable<String> validatable) {
		String ibanNumber = validatable.getValue().trim();
		boolean valid = isOfTypeIBAN(ibanNumber) && isValidIBANModuloCheck(ibanNumber);
		if (!valid) {
			this.error(validatable, EbankValidationException.IBAN_INVALID);
		}
	}

	/**
	 * Surface validation for the IBAN number
	 * 
	 * @param number
	 * @return
	 */
	protected boolean isOfTypeIBAN(String number) {
		// TODO: add more exhaustive validation
		// String expression = "^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}$";
		String expression = "^[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{15,30}$";
		CharSequence inputStr = number;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(inputStr);
		return (matcher.matches());
	}

	/**
	 * This holds the size of the chunk used to compute the remainder when
	 * dividing a very large number by 97.
	 * <p>
	 * The long range is -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807. The worst case scenario is a remainder
	 * of 96 followed by another chunk. The biggest long starting with "96" can only have 18 digits. The remainder
	 * having 2 digits, this means that the maximum allowed chunk size is 18 - 2 = 16
	 */
	private static final int NO_CHIFFRES = 16;

	/**
	 * Checks if an IBAN is valid using the modulo 97 technique
	 * 
	 * @param ibanNo
	 * @return
	 */
	private static boolean isValidIBANModuloCheck(String ibanNo) {
		/* cut the 4 first characters and append them to the end of the remaining iban */
		String iban = new String(ibanNo.substring(4) + ibanNo.substring(0, 4));
		/* transform the letters into digits */
		for (int i = 0; i < iban.length(); i++) {
			char c = iban.charAt(i);
			if (Character.isLetter(c)) {
				String s2 = String.valueOf(c);
				iban = StringUtils.replace(iban, s2, letterToNumberForIBAN(s2.toUpperCase()));
			}
		}

		/* deletes 0 from beginning of the string, if any */
		/*
		 * we can use replaceFirst because we alter exactly on zone at the
		 * beginning of the String
		 */
		iban = iban.replaceFirst("^0+", "");

		/* Compute the MODULO 97 remainder for (possibly very) long numbers */
		/*
		 * the longest IBAN could have 34 letters, which would translate to a 68
		 * digits number, meaning it will be split in a maximum of five Long
		 * numbers (4 x 16 < 68 <= 5 x 16)
		 */
		List numbers = new ArrayList(5);
		while (iban.length() > NO_CHIFFRES) {// it will break eventually as the iban shrinks at every iteration
			numbers.add(iban.substring(0, NO_CHIFFRES));
			iban = iban.substring(NO_CHIFFRES);
		}
		numbers.add(iban); // do not forget the last chunk

		long r = 0;
		for (int i = 0; i < numbers.size(); i++) {
			if (i != 0) { // i is zero only one time, so theoretically this is the oftener case
				r = Long.parseLong(String.valueOf(r) + (String) numbers.get(i)) % 97;
			} else {
				r = Long.parseLong((String) numbers.get(i)) % 97;
			}
		}
		/* check modulo 97 : the iban is valid if the remainder is 1 */
		return (r == 1);
	}

	private static Map lettersToNumbersIBAN = null;

	private static String letterToNumberForIBAN(String letter) {
		/* lazy initialization of the letters translation table */
		if (lettersToNumbersIBAN == null) {
			lettersToNumbersIBAN = new HashMap();
			lettersToNumbersIBAN.put("A", "10");
			lettersToNumbersIBAN.put("B", "11");
			lettersToNumbersIBAN.put("C", "12");
			lettersToNumbersIBAN.put("D", "13");
			lettersToNumbersIBAN.put("E", "14");
			lettersToNumbersIBAN.put("F", "15");
			lettersToNumbersIBAN.put("G", "16");
			lettersToNumbersIBAN.put("H", "17");
			lettersToNumbersIBAN.put("I", "18");
			lettersToNumbersIBAN.put("J", "19");
			lettersToNumbersIBAN.put("K", "20");
			lettersToNumbersIBAN.put("L", "21");
			lettersToNumbersIBAN.put("M", "22");
			lettersToNumbersIBAN.put("N", "23");
			lettersToNumbersIBAN.put("O", "24");
			lettersToNumbersIBAN.put("P", "25");
			lettersToNumbersIBAN.put("Q", "26");
			lettersToNumbersIBAN.put("R", "27");
			lettersToNumbersIBAN.put("S", "28");
			lettersToNumbersIBAN.put("T", "29");
			lettersToNumbersIBAN.put("U", "30");
			lettersToNumbersIBAN.put("V", "31");
			lettersToNumbersIBAN.put("W", "32");
			lettersToNumbersIBAN.put("X", "33");
			lettersToNumbersIBAN.put("Y", "34");
			lettersToNumbersIBAN.put("Z", "35");
		}
		return (String) lettersToNumbersIBAN.get(letter);
	}
}
