package com.temenos.ebank.common.wicket.wizard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.WizardButton;

import com.temenos.ebank.common.wicket.feedback.Alert;
import com.temenos.ebank.exceptions.ResponseCode;

public class EbankNextButton extends WizardButton {
	protected static Log logger = LogFactory.getLog(EbankNextButton.class);
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 * @param wizard
	 */
	public EbankNextButton(String id, IWizard wizard) {
		super(id, wizard, "org.apache.wicket.extensions.wizard.next");
	}

	/**
	 * @see org.apache.wicket.Component#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return getWizardModel().isNextAvailable();
	}

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
			logger.error("Unexpected error encountered on next button", e);
			step.setConfirmed(false);
			//clear previous feedback messages
			step.getEbankSession().getFeedbackMessages().clear();
			step.error(new Alert(ResponseCode.TECHNICAL_ERROR));
			return;
		}

		// if the step completed after applying the state, move the
		// model onward
		if (step.isConfirmed()) {
			if (step.isComplete()) {
				wizardModel.next();
			} else {
				error(getLocalizer().getString("org.apache.wicket.extensions.wizard.NextButton.step.did.not.complete",
						this));
			}
		}
	}

	/**
	 * @see org.apache.wicket.Component#onBeforeRender()
	 */
	@Override
	protected void onBeforeRender() {
		getForm().setDefaultButton(this);
		super.onBeforeRender();
	}
}
