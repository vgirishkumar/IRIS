package com.temenos.ebank.pages.clientAquisition.step3;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Step3 extends EbankWizardStep {

	private static final long serialVersionUID = 1L;

	@SpringBean(name = "step3DomainToWicketMapper")
	private IDomainToWicketMapper<Application, ApplicationWicketModelObject> domainToWicketMapper;

	private static final Integer RESUME_STEP_3 = Integer.valueOf(3);

	public Step3(boolean resumedApplication) {
		this.resumedApplication = resumedApplication;
	}

	public Step3(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}

	@Override
	protected IDomainToWicketMapper<Application, ApplicationWicketModelObject> getDomainToWicketMapper() {
		return domainToWicketMapper;
	}

	@Override
	protected void addStepComponents(IModel<ApplicationWicketModelObject> model) {
		// add employment panels
		// first customer
		add(new EmploymentPanel("customerEmploymentPanel", new CompoundPropertyModel(new PropertyModel(model,
				"customer"))));
		// joint customer, if it is the case
		if (!model.getObject().getIsSole()) {
			add(new EmploymentPanel("secondCustomerEmploymentPanel", new CompoundPropertyModel(new PropertyModel(model,
					"secondCustomer"))));
		} else {
			// I do not want to instantiate and display a whole panel since I am not gonna use it. is it OK to use a
			// Label replacement ?
			add(new Label("secondCustomerEmploymentPanel", ""));
		}
	}

	protected Integer getStepNumber() {
		return RESUME_STEP_3;
	}

}