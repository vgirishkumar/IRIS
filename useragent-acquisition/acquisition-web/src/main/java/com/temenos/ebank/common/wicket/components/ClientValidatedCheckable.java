package com.temenos.ebank.common.wicket.components;

/**
 * @author raduf
 * Interface implemented by form components who are either radio choices, or chec box multiple choices
 * They need to decide which is the first radio or check box in the group
 * It will be used for setting client side validation rules on that element
 *
 */
public interface ClientValidatedCheckable {
	public String getFirstCheckableId();
}
