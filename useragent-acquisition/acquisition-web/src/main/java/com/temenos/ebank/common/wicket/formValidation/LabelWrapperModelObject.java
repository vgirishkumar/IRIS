package com.temenos.ebank.common.wicket.formValidation;

import java.io.Serializable;

import org.apache.wicket.markup.html.form.FormComponent;

@SuppressWarnings("serial")
public class LabelWrapperModelObject implements Serializable{
	private String label = "";

	public LabelWrapperModelObject(FormComponent<?> component) {
		if (component.getLabel() != null) {
			label = component.getLabel().getObject();
		}
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

}
