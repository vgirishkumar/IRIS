package com.temenos.ebank.common.wicket.formValidation;

import java.io.Serializable;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.EmailAddressValidator;

@SuppressWarnings("unchecked")
public class EmailValidationBehavior extends JQueryValidationAbstractBehaviour {

	public EmailValidationBehavior() {
		super(new Model<String>(""));
		propertiesFileKey = "EmailAddressValidator";
		clientValidationKey = "email";
		clientRuleValue = String.valueOf("true");
	}

	private static final long serialVersionUID = 1L;

	@Override
	protected void addServerSideValidator(FormComponent component) {
		component.add(EmailAddressValidator.getInstance());
	}

	@Override
	public IModel newMessageModel(FormComponent component) {
		return new Model(new EmailLabelWrapperModelObject(component));
	}

	@SuppressWarnings("unused")
	private class EmailLabelWrapperModelObject implements Serializable {
		private static final long serialVersionUID = 1L;

		private FormComponent component;
		private String input = "";

		public EmailLabelWrapperModelObject(FormComponent component) {
			this.component = component;
			if (component.getLabel() != null) {
				setInput(component.getLabel().getObject());
			}
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
	}

}
