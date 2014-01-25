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
package com.temenos.ebank.common.wicket.components;

import org.apache.wicket.markup.html.form.PasswordTextField;

/**
 * @author slimb
 * 
 */
public class RequiredPasswordTextField extends PasswordTextField {
	private static final long serialVersionUID = 5580273483696554165L;

	public RequiredPasswordTextField(String id) {
		super(id);
		this.setRequired(true);
		// this.add(MinimumLengthValidator.minimumLength(4)) ;
	}
}
