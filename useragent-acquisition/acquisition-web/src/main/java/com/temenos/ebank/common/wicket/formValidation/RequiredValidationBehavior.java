package com.temenos.ebank.common.wicket.formValidation;

import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

@SuppressWarnings("rawtypes")
public class RequiredValidationBehavior extends JQueryValidationAbstractBehaviour {
	private static final long serialVersionUID = 1L;

	public RequiredValidationBehavior() {
		super(new Model<String>(""));
		propertiesFileKey = "Required";
		clientValidationKey = "required";
		clientRuleValue = "true";
	}

	@Override
	protected void addServerSideValidator(FormComponent component) {
	}


	@SuppressWarnings("unchecked")
	@Override
	public IModel newMessageModel(FormComponent component) {
		return new Model(new LabelWrapperModelObject(component));
	}
}
