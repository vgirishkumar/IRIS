package com.temenos.ebank.pages.clientAquisition.step4;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper;

public class Step4 extends EbankWizardStep {
	private static final long serialVersionUID = 1L;

	private static final Integer RESUME_STEP_4 = Integer.valueOf(4);

	@SpringBean(name = "step45DomainToWicketMapper")
	private IDomainToWicketMapper<Application, ApplicationWicketModelObject> domainToWicketMapper;

	public Step4(boolean resumedApplication) {
		this.resumedApplication = resumedApplication;
	}

	public Step4(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}

	@Override
	protected IDomainToWicketMapper<Application, ApplicationWicketModelObject> getDomainToWicketMapper() {
		return domainToWicketMapper;
	}

	@Override
	protected void addStepComponents(IModel<ApplicationWicketModelObject> model) {
		FinancialDetailsPanel financialDetails = new FinancialDetailsPanel("financialDetails", model, true);
		add(financialDetails);

		add(newSecondFinancialDetailsPanel(model));
	}

	@Override
	protected Integer getStepNumber() {
		return RESUME_STEP_4;
	}

	protected Panel newSecondFinancialDetailsPanel(IModel<ApplicationWicketModelObject> model) {
		return (Panel) (new Panel("secondFinancialDetails").setVisible(false));
	}

}