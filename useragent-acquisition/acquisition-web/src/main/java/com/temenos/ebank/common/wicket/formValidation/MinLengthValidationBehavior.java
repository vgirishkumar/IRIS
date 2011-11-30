package com.temenos.ebank.common.wicket.formValidation;

import java.io.Serializable;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@SuppressWarnings("unchecked")
public class MinLengthValidationBehavior extends JQueryValidationAbstractBehaviour {
	private static final long serialVersionUID = 1L;

	private Integer minLength;

	public MinLengthValidationBehavior(Integer minLength) {
		super(new Model<String>(""));

		this.minLength = minLength;
		propertiesFileKey = "StringValidator.minimum";
		clientValidationKey = "minlength";
		clientRuleValue = String.valueOf(minLength);
	}

	@SuppressWarnings("unused")
	private class MinLengthWrapperModelObject implements Serializable {
		private static final long serialVersionUID = 1L;

		private FormComponent component;
		private String input = "";
		private String minimum = "";

		public MinLengthWrapperModelObject(FormComponent component, Integer minLength) {
			this.component = component;
			if (component.getLabel() != null) {
				setInput(component.getLabel().getObject());
			}
			minimum = String.valueOf(minLength);
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

		public void setMinimum(String minimum) {
			this.minimum = minimum;
		}

		public String getMinimum() {
			return minimum;
		}
	}

	@Override
	public IModel newMessageModel(FormComponent component) {
		return new Model(new MinLengthWrapperModelObject(component, minLength));
	}

	@Override
	protected void addServerSideValidator(FormComponent component) {
	}

}
