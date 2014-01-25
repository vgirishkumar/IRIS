package com.temenos.ebank.pages.clientAquisition.step2;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class Step2 extends EbankWizardStep {

	@SpringBean(name = "step2DomainToWicketMapper")
	private IDomainToWicketMapper<Application, ApplicationWicketModelObject> domainToWicketMapper;

	private static final Integer RESUME_STEP_2 = Integer.valueOf(2);

	public Step2(boolean resumedApplication) {
		this.resumedApplication = resumedApplication;
	}

	public Step2(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}

	@Override
	public IDomainToWicketMapper<Application, ApplicationWicketModelObject> getDomainToWicketMapper() {
		return domainToWicketMapper;
	}

	@Override
	protected void addStepComponents(IModel<ApplicationWicketModelObject> modelAWM) {
		final boolean isJointCustomer = true;

		ContactDetailsPanel contactDetails = new ContactDetailsPanel("contactDetails", new CompoundPropertyModel(
				new PropertyModel(modelAWM, "contactDetails")), !isJointCustomer, modelAWM);
		add(contactDetails);

		ContactDetailsPanel secondContactDetails = new ContactDetailsPanel("jointContactDetails",
				new CompoundPropertyModel(new PropertyModel(modelAWM, "jointContactDetails")), isJointCustomer,
				modelAWM);
		add(secondContactDetails);

		addDependentValidations(secondContactDetails);
		secondContactDetails.setVisible(!modelAWM.getObject().getIsSole());

	}

	private void addDependentValidations(ContactDetailsPanel... panels) {
		for (ContactDetailsPanel panel : panels) {
			add(new AtLeastOnePhoneValidator(panel.getHomePhone(), panel.getWorkPhone(), panel.getMobilePhone()));
		}
	}

	private class AtLeastOnePhoneValidator extends AbstractFormValidator {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/** form components to be checked. */
		private final FormComponent[] components;

		public AtLeastOnePhoneValidator(FormComponent... formComponents) {
			components = formComponents;
		}

		/**
		 * @see wicket.markup.html.form.validation.IFormValidator#getDependentFormComponents()
		 */
		public FormComponent[] getDependentFormComponents() {
			return components;
		}

		public void validate(Form form) {
			boolean valid = false;
			for (FormComponent component : components) {
				if (component.getInput() != null && component.getInput().length() > 0) {
					valid = true;
				}
			}
			if (!valid) {
				components[0].error("You must provide at least one phone number.");
			}
		}
	}

	@Override
	public Integer getStepNumber() {
		return RESUME_STEP_2;
	}

}