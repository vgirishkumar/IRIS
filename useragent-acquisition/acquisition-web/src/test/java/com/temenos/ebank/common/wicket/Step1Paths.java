package com.temenos.ebank.common.wicket;


/**
 * Constants for fields paths
 * @author vionescu
 *
 */
/**
 * @author vionescu
 *
 */
public class Step1Paths {
	
	public static final String TITLE = "view:customerDetails:titleBorder:title";
	public static final String COUNTRY_OF_BIRTH = "view:customerDetails:countryOfBirthBorder:countryOfBirth";
	public static final String TOWN_OF_BIRTH = "view:customerDetails:townOfBirthBorder:townOfBirth";
	public static final String MARITAL_STATUS = "view:customerDetails:maritalStatusBorder:maritalStatus";
	public static final String EMAIL = "view:customerDetails:emailAddressBorder:emailAddress";
	public static final String EMAIL2 = "view:customerDetails:emailAddress2Border:emailAddress2";
	public static final String FIRSTNAME ="view:customerDetails:firstNameBorder:firstName";
	public static final String LASTNAME ="view:customerDetails:lastNameBorder:lastName";
	public static final String GENDER = "view:customerDetails:genderBorder:gender";
	public static final String MOBILE_PHONE = "view:customerDetails:mobilePhoneBorder:mobilePhone";
	public static final String MOBILE_PHONE_PREFIX = "view:customerDetails:mobilePhoneBorder:mobilePhone:prefix";
	public static final String MOBILE_PHONE_NUMBER = "view:customerDetails:mobilePhoneBorder:mobilePhone:phoneNumber";
	public static final String DOB = "view:customerDetails:dateOfBirthBorder:dateOfBirth";
	public static final String DOB_DAY = "view:customerDetails:dateOfBirthBorder:dateOfBirth:dayBorder:day";
	public static final String DOB_MONTH = "view:customerDetails:dateOfBirthBorder:dateOfBirth:monthBorder:month";
	public static final String DOB_YEAR = "view:customerDetails:dateOfBirthBorder:dateOfBirth:yearBorder:year";	
	public static final String NATIONALITY = "view:customerDetails:nationalityBorder:nationality";
	
	public static final String ANNUAL_INCOME = "view:incomeDetails:annualIncomeBorder:annualIncome";
	public static final String DEPOSIT_AMMOUNT = "view:incomeDetails:estimatedDepositAmountBorder:estimatedDepositAmount";
	public static final String CURRACCOUNT_ACC_CURRENCIES = "view:preAccountCheck:accountCurrenciesBorder:accountCurrencies";
	public static final String COUNTRY_OF_RESIDENCE = "view:customerEligibilityDetails:countryResidenceBorder:countryResidence";
	public static final String EXISTING_CUSTOMER = "view:customerExistingAccounts:isExistingCustomerBorder:isExistingCustomer";
	public static final String EXISTING_CUSTOMER_YES = "view:customerExistingAccounts:isExistingCustomerBorder:isExistingCustomer:existingCustomerYes";
	public static final String EXISTING_SORTCODE =  "view:customerExistingAccounts:groupExistingAccDetails:existingSortCodeBorder:existingSortCode";
	//public static final String EXISTING_SORTCODE_F1 =  "view:customerExistingAccounts:groupExistingAccDetails:existingSortCodeBorder:existingSortCode:sortCode1";
	public static final String JOINT_RADIO = "view:preAccountCheck:singleJoint:isSoleBorder:isSole:joint";
	public static final String SOLE_RADIO = "view:preAccountCheck:singleJoint:isSoleBorder:isSole:sole";

	
	
	/**
	 * Correspondence table between the panels for the first customer and the panels for the second customer 
	 */
	private final static String[] secondCustomerPaths = new String[] {"customerDetails", "secondCustomerDetails", "customerEligibilityDetails", "secondCustomerEligibilityDetails", "customerExistingAccounts", "secondCustomerExistingAccounts"};
	
	/**
	 * Returns the path for a field in the second customer group. Useful for avoiding duplication of same constants for first customer and
	 * second customer.
	 * @param pathForFirstCustomer Path of a field for first customer 
	 * @return Corresponding path of the field, but for second customer, null if no correspondence exists
	 */
	public static final String SC(String pathForFirstCustomer) {
		for (int i = 0, noOfCorrespEntries = secondCustomerPaths.length; i < noOfCorrespEntries; i+=2) {
			String searchInFirstCustomer = "^(view:)" + secondCustomerPaths[i] + "(:.*)$";
			if (pathForFirstCustomer.matches(searchInFirstCustomer)) {
				String replaceForSecondCustomer = "$1" + secondCustomerPaths[i+1] + "$2"; 
				return pathForFirstCustomer.replaceAll(searchInFirstCustomer, replaceForSecondCustomer);
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(FIRSTNAME);
		System.out.println(SC(FIRSTNAME));
	}
}
