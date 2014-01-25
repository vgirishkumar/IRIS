package com.temenos.ebank.common.wicket.components;

import org.apache.wicket.markup.html.form.FormComponent;

/**
 * Interface for marking composite form components. This is useful
 * for matching the label of the composite form component to the 
 * first field of the composite. This way, when clicking the label, 
 * the appropriate input is focused. Otherwise, attaching a label to the
 * composite didn't focus anything.
 * @author vionescu
 *
 */

public interface CompositeFormComponent {

	/**
	 * Returns the form component to be focused when the composite's label is clicked
	 * @return
	 */
	public abstract FormComponent<?> getFirstInput();	
}
