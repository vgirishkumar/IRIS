package com.temenos.ebank.pages.clientAquisition.wizard;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.extensions.wizard.IWizardStep;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.LoadableDetachableModel;

import com.temenos.ebank.common.wicket.AddClientValidationVisitor;
import com.temenos.ebank.common.wicket.formValidation.ClientSideValidatedFormBehavior;
import com.temenos.ebank.common.wicket.formValidation.CompositeErrorsAndInfosFeedbackPanel;
import com.temenos.ebank.common.wicket.formValidation.DefaultTextFieldFormBehavior;
import com.temenos.ebank.common.wicket.wizard.EbankButtonBar;
import com.temenos.ebank.common.wicket.wizard.EbankForm;
import com.temenos.ebank.common.wicket.wizard.EbankWizard;
import com.temenos.ebank.common.wicket.wizard.EbankWizardModel;
import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.pages.startPage.ApplyForInternationalAccount;
import com.temenos.ebank.pages.thankYou.ThankYouPage;
import com.temenos.ebank.wicket.EbankSession;

/**
 * Client aquisition wizard
 * 
 * @author vionescu
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public abstract class ClientAquisitionWizard extends EbankWizard {

	private final EbankWizardModel wizardModel = new EbankWizardModel();

	private EbankButtonBar buttonBar;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 */
	public ClientAquisitionWizard(String id, final Application a) {
		super(id);

		wizardModel.setProductType(getProductType());
		boolean resumedApplication = a != null;
		if (resumedApplication) {
			// persist Application object on session
			((EbankSession) getSession()).setClientAquisitionApplication(a);
		}

		List<Class> steps = getProductSpecificSteps();
		try {
			for (Class currentStepClass : steps) {
				Constructor currentClassConstructor = currentStepClass.getConstructor(Boolean.TYPE);
				wizardModel.add((EbankWizardStep) currentClassConstructor.newInstance(resumedApplication));
			}
		} catch (Exception e) {
			throw new RuntimeException("Error in wizard intialization", e);
		}
				
		// initialize the wizard
		init(wizardModel);

//		Label labelStepNumberOfTotalSteps = new Label("stepNoOfTotalSteps", new LoadableDetachableModel<String>() {
//			@Override
//			protected String load() {
//				Map<String, Integer> stepIndexAndTotalNoOfSteps = new HashMap<String, Integer>();
//				stepIndexAndTotalNoOfSteps.put("stepIndex", getActiveStepIndex() + 1);
//				stepIndexAndTotalNoOfSteps.put("totalSteps", stepNames.size());
//				return getString("stepNoOfTotalSteps", new MapModel<String, Integer>(stepIndexAndTotalNoOfSteps));
//			}
//		});
//		add(labelStepNumberOfTotalSteps);


		// this should stay false, I've let it like that for
		// programming purposses
		boolean resumeToLastSavedStep = false;
		if (resumeToLastSavedStep) {
			// this code should not be executed in prod
			int indexOfActiveStep = 0;
			if (resumedApplication) {
				int resumeStep = a.getResumeStep();
				indexOfActiveStep = resumeStep == 0 ? 0 : resumeStep - 1;
			}
			// Not needed, always display the first step
			wizardModel.resumeToStep(indexOfActiveStep);
		}

	}

	/**
	 * Template method returning the wizard steps, depending on the selected product
	 * @return
	 */
	protected abstract List getProductSpecificSteps();

	
	/**
	 * Template method for setting the product type in the wizard model 
	 * @return
	 */
	protected abstract ProductType getProductType();
	
	/**
	 * Returns the index of the active step, 0 if not found
	 * 
	 * @return
	 */
	private int getActiveStepIndex() {
		int activeStepIndex = 0;
		// TODO: the wizard model provided by Wicket doesn't allow access to the internal steps list, it provides
		// only an iterator, which is ugly. Make an enhancement proposal to the wicket team in order to fix this
		Iterator<IWizardStep> stepsIterator = wizardModel.stepIterator();
		if (stepsIterator != null) {
			IWizardStep correspStep = null;
			for (int i = 0; stepsIterator.hasNext(); i++) {
				correspStep = stepsIterator.next();
				if (wizardModel.getActiveStep() == correspStep) {
					activeStepIndex = i;
				}
			}
		}
		return activeStepIndex;
	}

	@Override
	protected <E> Form<E> newForm(String id) {
		final Form frm = new EbankForm("form") {
			@Override
			protected void onRepeatSubmit() {
				if (wizardModel.isNextAvailable()) {
					wizardModel.next();
				}
			}
		};
		frm.add(new DefaultTextFieldFormBehavior()).add(new ClientSideValidatedFormBehavior());
		Label labelStepDescription = new Label("stepDescription", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				return getString("step." + (getActiveStepIndex() + 1) + ".description");
			}
		});
		frm.add(labelStepDescription);
		
		final List<String> stepNames = Arrays.asList("1", "2", "3", "4", "5", "6");
		final String[] stepCssClasses = new String[] {"one", "two", "three", "four", "five", "six"};
		
		ListView<String> stepHeader = new ListView<String>("progressBar", stepNames) {
			private int activeStepIndex = 0;

			@Override
			protected void onBeforeRender() {
				// finding the active step, I do this here for optimization, because populateItem gets call on every
				// item, and o(n) beatas 0(n2)
				this.activeStepIndex = getActiveStepIndex();
				super.onBeforeRender();
			}

			@Override
			protected void populateItem(ListItem<String> item) {
				String stepName = item.getModelObject();
				String descriptionText = getString("step." + stepName + ".description");
				Label labelDescription = new Label("description", descriptionText);
				Label labelDescriptionCompleted = new Label("descriptionCompleted", descriptionText);
				Label labelDescriptionActive = new Label("descriptionActive", descriptionText);
				item.add(labelDescription, labelDescriptionCompleted, labelDescriptionActive);
				
				Iterator<IWizardStep> stepsIterator = wizardModel.stepIterator();
				if (stepsIterator != null) {
					int currStepIndex = item.getIndex();
					boolean isCompleteStep = currStepIndex < activeStepIndex;
					boolean isActiveStep = currStepIndex == activeStepIndex;
					labelDescription.setVisible(!(isCompleteStep || isActiveStep));
					labelDescriptionCompleted.setVisible(isCompleteStep);
					labelDescriptionActive.setVisible(isActiveStep);
					//boolean isLastStep = currStepIndex == stepNames.size() - 1;
					String stepClass = stepCssClasses[currStepIndex];
					item.add(new SimpleAttributeModifier("class", stepClass));
//					item.add(new SimpleAttributeModifier("class", isLastStep ? "last" : isCompleteStep ? "complete"
//							: isActiveStep ? "current" : ""));
				}
			}
		};
		frm.add(stepHeader);		
		return frm;
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.Wizard#onCancel()
	 */
	@Override
	public void onCancel() {
		setResponsePage(ApplyForInternationalAccount.class);
	}

	/**
	 * The thank you page can be displayed after a main application or a cross sell application.
	 * This is the main application call. 
	 * @see org.apache.wicket.extensions.wizard.Wizard#onFinish()
	 */
	@Override
	public void onFinish() {
		//TODO : accountcreationResponse is already on the request, see if the first argument can be eliminated
		setResponsePage(new ThankYouPage(EbankWizardStep.retrieveAccountCreationResponse(true), true, ((EbankSession) getSession()).getClientAquisitionApplication()));
	}

	@Override
	protected Component newFeedbackPanel(String id) {
		return new CompositeErrorsAndInfosFeedbackPanel(id, getForm(), this, this.buttonBar);
	}

	@Override
	protected void onBeforeRender() {
		if (wizardModel.isLastStep(wizardModel.getActiveStep())) {
			EbankWizardStep.setIsSummary(true);
		}
		getForm().visitFormComponents(new AddClientValidationVisitor());
		super.onBeforeRender();
	}

	@Override
	protected EbankButtonBar newButtonBar(String id) {
		this.buttonBar = new EbankButtonBar(id, this);
		return buttonBar;
	}
}
