package com.temenos.ebank.common.wicket.wizard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.IWizardStep;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardButton;

import com.temenos.ebank.common.wicket.feedback.Alert;
import com.temenos.ebank.exceptions.ResponseCode;

/**
 * Models a finish button in the wizard. When pressed, it calls {@link IWizardStep#applyState()} on
 * the active wizard step, and then {@link Wizard#onFinish()} on the wizard.
 * 
 * @author vionescu
 */
public class EbankFinishButton extends WizardButton {
	private static final long serialVersionUID = 1L;
	protected static Log logger = LogFactory.getLog(EbankFinishButton.class);
	
	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param wizard
	 *            The wizard
	 */
	public EbankFinishButton(String id, IWizard wizard) {
		super(id, wizard, "org.apache.wicket.extensions.wizard.finish");
	}

	/**
	 * @see org.apache.wicket.Component#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		IWizardStep activeStep = getWizardModel().getActiveStep();
		return (activeStep != null && getWizardModel().isLastStep(activeStep));
	}

	/**
	 * @see org.apache.wicket.Component#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return isEnabled();
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.WizardButton#onClick()
	 */
	@Override
	public void onClick() {
		IWizardModel wizardModel = getWizardModel();
		EbankWizardStep step = (EbankWizardStep) wizardModel.getActiveStep();
		// let the step apply any state
		try {		
			step.applyState();
		} catch (Exception e) {
			logger.error("Unexpected error encountered on finish button", e);
			//in order to prevent the wizard finish navigation
			step.setConfirmed(false);
			//clear previous feedback messages
			step.getEbankSession().getFeedbackMessages().clear();
			step.error(new Alert(ResponseCode.TECHNICAL_ERROR));
			return;
		}
		
		if (step.isConfirmed()) {
			if (step.isComplete()) {
				// if the step completed after applying the state, notify the wizard
				getWizardModel().finish();
			} else {
				error(getLocalizer().getString("org.apache.wicket.extensions.wizard.FinishButton.step.did.not.complete",
						this));
			}
		}
	}
}
