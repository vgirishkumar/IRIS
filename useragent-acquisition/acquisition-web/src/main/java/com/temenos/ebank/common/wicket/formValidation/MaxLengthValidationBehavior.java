package com.temenos.ebank.common.wicket.formValidation;

import java.io.Serializable;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@SuppressWarnings("unchecked")
public class MaxLengthValidationBehavior extends JQueryValidationAbstractBehaviour {
	private static final long serialVersionUID = 1L;

	private Integer maxLength;

	public MaxLengthValidationBehavior(Integer maxLength) {
		super(new Model<String>(""));

		this.maxLength = maxLength;
		propertiesFileKey = "StringValidator.maximum";
		clientValidationKey = "maxlength";
		clientRuleValue = String.valueOf(maxLength);
	}

	@SuppressWarnings("unused")
	private class MaxLengthWrapperModelObject implements Serializable {
		private static final long serialVersionUID = 1L;

		private FormComponent component;
		private String input = "";
		private String maximum = "";

		public MaxLengthWrapperModelObject(FormComponent component, Integer maxLength) {
			this.component = component;
			if (component.getLabel() != null) {
				setInput(component.getLabel().getObject());
			}
			setMaximum(String.valueOf(maxLength));
		}

		public void setInput(String input) {
			this.input = input;
		}

		public String getInput() {
			if (component.getLabel() != null) {
				setInput(component.getLabel().getObject());
			}
			return input;
		}

		public void setMaximum(String maximum) {
			this.maximum = maximum;
		}

		public String getMaximum() {
			return maximum;
		}
	}

	@Override
	public IModel newMessageModel(FormComponent component) {
		return new Model(new MaxLengthWrapperModelObject(component, maxLength));
	}

	@Override
	protected void addServerSideValidator(FormComponent component) {
	}

}
