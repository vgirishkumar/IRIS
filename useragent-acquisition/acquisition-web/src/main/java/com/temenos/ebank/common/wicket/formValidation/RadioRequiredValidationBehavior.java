package com.temenos.ebank.common.wicket.formValidation;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class RadioRequiredValidationBehavior extends JQueryBaseValidationAbstractBehaviour {

	private static final long serialVersionUID = 1L;
	
	private RadioGroup<?> parentGroup;

	public RadioRequiredValidationBehavior(RadioGroup parentGroup) {
		super(new Model<String>(""));
		propertiesFileKey = "Required";
		clientValidationKey = "required";
		clientRuleValue = "true";
		this.parentGroup = parentGroup;
	}

	@Override
	public final void bind(Component component) {
		super.bind(component);
	}

	@Override
	public void beforeRender(Component component) {
		IModel messageModel = new Model( new LabelWrapperModelObject(parentGroup));
		if (messageModel != null) {
			setClientValidationMessage(parentGroup, propertiesFileKey, clientValidationKey, clientRuleValue, messageModel);
		}
	}
}
