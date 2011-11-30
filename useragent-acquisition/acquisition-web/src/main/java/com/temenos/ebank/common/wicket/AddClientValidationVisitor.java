package com.temenos.ebank.common.wicket;

import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.StringValidator.MaximumLengthValidator;
import org.apache.wicket.validation.validator.StringValidator.MinimumLengthValidator;

import com.temenos.ebank.common.wicket.components.ClientValidatedCheckable;
import com.temenos.ebank.common.wicket.components.CompositeFormComponent;
import com.temenos.ebank.common.wicket.formValidation.CheckableRequiredBehavior;
import com.temenos.ebank.common.wicket.formValidation.MaxLengthValidationBehavior;
import com.temenos.ebank.common.wicket.formValidation.MinLengthValidationBehavior;
import com.temenos.ebank.common.wicket.formValidation.RadioRequiredValidationBehavior;
import com.temenos.ebank.common.wicket.formValidation.RequiredValidationBehavior;

public class AddClientValidationVisitor extends FormComponent.AbstractVisitor {
	@SuppressWarnings("rawtypes")
	@Override
	protected void onFormComponent(final FormComponent<?> formComponent) {
		if (formComponent.isRequired()) {
			if(formComponent instanceof RadioGroup){
				formComponent.visitChildren(Radio.class, new IVisitor<Radio>(){
					public Object component(Radio radio) {
						radio.add(new RadioRequiredValidationBehavior((RadioGroup)formComponent));
						return STOP_TRAVERSAL;
					}});
			}
			else if(formComponent instanceof ClientValidatedCheckable){
				formComponent.add( new CheckableRequiredBehavior() );
			}
			else if( formComponent instanceof CompositeFormComponent ){
				formComponent.visitChildren(FormComponent.class, new IVisitor<FormComponent>(){
					public Object component(FormComponent child) {
						//marking the composing fields as required will enable client side validation for each of them
						child.setRequired(true);
						//adding the HTML class "inComposite" will advise client side validation to treat them differently 
						child.add(new AttributeAppender("class", true, new Model<String>("inComposite"), " "));
						return CONTINUE_TRAVERSAL;
					}});
			}
			else{
				formComponent.add(new RequiredValidationBehavior());
			}
		}
		for (IValidator<?> v : formComponent.getValidators()) {
			if (v instanceof MinimumLengthValidator) {
				MinimumLengthValidator mlv = (MinimumLengthValidator) v;
				formComponent.add(new MinLengthValidationBehavior(mlv.getMinimum()));
			} else if (v instanceof MaximumLengthValidator) {
				MaximumLengthValidator mlv = (MaximumLengthValidator) v;
				formComponent.add(new MaxLengthValidationBehavior(mlv.getMaximum()));
			}
			// ...we can add the other client validation behaviors here
		}
	}
}

