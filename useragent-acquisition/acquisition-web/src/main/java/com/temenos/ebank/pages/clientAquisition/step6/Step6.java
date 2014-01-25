package com.temenos.ebank.pages.clientAquisition.step6;

import java.lang.reflect.Constructor;

import org.apache.wicket.extensions.wizard.IWizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.wizard.EbankWizardModel;
import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.pages.clientAquisition.wizard.SupportSnippet;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper;

public class Step6 extends EbankWizardStep {

	private static final long serialVersionUID = 1L;

	private static final Integer RESUME_STEP_6 = Integer.valueOf(6);

	@SpringBean(name = "step6DomainToWicketMapper")
	private IDomainToWicketMapper<Application, ApplicationWicketModelObject> domainToWicketMapper;

	public Step6(boolean resumedApplication) {
		this.resumedApplication = resumedApplication;
	}

	@Override
	protected IDomainToWicketMapper<Application, ApplicationWicketModelObject> getDomainToWicketMapper() {
		return domainToWicketMapper;
	}

	@SuppressWarnings({ "rawtypes", "serial" })
	protected void addStepComponents(IModel<ApplicationWicketModelObject> model) {

		final EbankWizardModel wizardModel = getWizardModel();

		ProductType productType = wizardModel.getProductType();
		Label labelProductType = new Label("productType", new ResourceModel("wizard." + productType.getCode() + ".summary"));
		add(labelProductType);

		add(new SupportSnippet("supportSnippet"));

		Class[] summaryArgTypes = new Class[] { String.class, IModel.class };
		int i = 1;
		// EbankWizardModel wizardModel = (EbankWizardModel) getWizardModel();
		for (IWizardStep s : wizardModel.getSteps()) {
			try {
				if (wizardModel.isLastStep(s)) {
					break;
				}
				Constructor currentClassConstructor = s.getClass().getConstructor(summaryArgTypes);
				add((EbankWizardStep) currentClassConstructor.newInstance("step" + i, model));
				i++;
			} catch (Exception e) {
				// logger.error(String.format("Error instantiating summary constructor with 2 args for step %s", i), e);
				throw new RuntimeException(String.format(
						"Error instantiating summary constructor with 2 args for step %s", i), e);
			}
		}

		add(new Link("step1Link") {
			@Override
			public void onClick() {
				wizardModel.resumeToStep(0);
			}
		}, new Link("step2Link") {
			@Override
			public void onClick() {
				wizardModel.resumeToStep(1);
			}
		}, new Link("step3Link") {
			@Override
			public void onClick() {
				wizardModel.resumeToStep(2);
			}
		}, new Link("step4Link") {
			@Override
			public void onClick() {
				wizardModel.resumeToStep(3);
			}
		}, new Link("step5Link") {
			@Override
			public void onClick() {
				wizardModel.resumeToStep(4);
			}
		});

	}

	// @SuppressWarnings({ "unchecked", "rawtypes" })
	// protected void instanceSummarySteps() {
	// IModel<ApplicationWicketModelObject> model = (IModel<ApplicationWicketModelObject>) getDefaultModel();
	// Class[] summaryArgTypes = new Class[] { String.class, IModel.class };
	// int i = 1;
	// EbankWizardModel wizardModel = (EbankWizardModel) getWizardModel();
	// for (IWizardStep s : wizardModel.getSteps()) {
	// try {
	// Constructor currentClassConstructor = s.getClass().getConstructor(summaryArgTypes);
	// add((EbankWizardStep) currentClassConstructor.newInstance("step" + i, model));
	// if (wizardModel.isLastStep(s)) {
	// break;
	// }
	// i++;
	// } catch (Exception e) {
	// //logger.error(String.format("Error instantiating summary constructor with 2 args for step %s", i), e);
	// throw new RuntimeException(String.format("Error instantiating summary constructor with 2 args for step %s", i),
	// e);
	// }
	// }
	// }

	@Override
	protected Integer getStepNumber() {
		return RESUME_STEP_6;
	}

}