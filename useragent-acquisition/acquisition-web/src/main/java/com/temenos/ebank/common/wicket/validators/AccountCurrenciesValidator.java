package com.temenos.ebank.common.wicket.validators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

/**
 * Validates account currencies selection, when there are checkboxes for currrencies.
 * The validator checks whether there is at least on checkbox selected and whether the
 * number of selected currencies does not exceed the maximum configured number. 
 * @author vionescu
 *
 */
//@SuppressWarnings("rawtypes")
public class AccountCurrenciesValidator extends AbstractValidator<List<String>>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int maxNoOfAccounts;
	
	public AccountCurrenciesValidator(int maxNoOfAccounts) {
		this.maxNoOfAccounts = maxNoOfAccounts;

	}
	
	@Override
	protected void onValidate(IValidatable<List<String>> validatable) {
		int noOfSelectedCurrencies = validatable.getValue().size();
		if(noOfSelectedCurrencies == 0) {
			error(validatable, "AccountCurrenciesValidatorAtLeastOne");
		}
		if(noOfSelectedCurrencies > maxNoOfAccounts) {
			Map<String, Object> resParams = new HashMap<String, Object>();
			resParams.put("maxNoOfAccounts", maxNoOfAccounts);
			error(validatable, "AccountCurrenciesValidatorMaxExceeded", resParams);
		}
	}

}
