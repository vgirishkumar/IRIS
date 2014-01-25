/*
 * Copyright (c) 2002-2010 FE-Mobile Ltd, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of FE-Mobile
 *  Ltd. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with FE-Mobile Ltd.
 *
 * FE-Mobile Ltd MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. FE-Mobile Ltd SHALL NOT
 * BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.temenos.ebank.common.wicket.validators;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.validator.AbstractValidator;

import com.temenos.ebank.exceptions.EbankValidationException;

/**
 * 
 * Used to validate phone numbers. uses DialCode as the business process/rule.
 * 
 * @author slimb
 * 
 */
public class PhoneNumberValidator extends AbstractValidator<String> {
	private static final long serialVersionUID = -6412476734410580236L;

	@Override
	protected void onValidate(IValidatable<String> validatable) {
		String phoneNo = validatable.getValue();
		// TODO reuse countrycode/phonewithoutprefix validators
		if (phoneNo.length() > 17) {
			this.error(validatable, EbankValidationException.PHONE_NO_INVALID);
		}
	}
}
