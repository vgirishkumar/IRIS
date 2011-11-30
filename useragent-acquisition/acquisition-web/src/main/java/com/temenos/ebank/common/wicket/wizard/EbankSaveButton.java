package com.temenos.ebank.common.wicket.wizard;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.WizardButton;

import com.temenos.ebank.common.wicket.feedback.Alert;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.pages.startPage.ApplyForInternationalAccount;

/**
 * Models a save button in the wizard. When pressed, it calls {@link EbankWizardStep#saveState()} on
 * the active wizard step, and then moves the wizard state to the next step of the model by calling
 * {@link IWizardModel#next() next} on the wizard's model.
 * 
 * @author vionescu
 */
public class EbankSaveButton extends WizardButton {
	private static final long serialVersionUID = 1L;
	private Boolean isCancel = false;
	protected static Log logger = LogFactory.getLog(EbankSaveButton.class);
	/**
	 * Construct.
	 * 
	 * @param id
	 * @param wizard
	 * @param isCancel
	 *            true if the wizard should be canceled after save
	 */
	public EbankSaveButton(String id, IWizard wizard, Boolean isCancel) {
		super(id, wizard, "org.apache.wicket.extensions.wizard.next");
		this.isCancel = isCancel;
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
		try {		
			// let the step apply any state
			step.saveState();
		} catch (Exception e) {
			logger.error("Unexpected error encountered on save button", e);
			//clear previous feedback messages
			step.getEbankSession().getFeedbackMessages().clear();
			step.error(new Alert(ResponseCode.TECHNICAL_ERROR));
			return;
		}
		
		// if the step completed after applying the state, move the
		// model onward
		if (isCancel) {
			setResponsePage(ApplyForInternationalAccount.class);
		} else {
			if (step.isConfirmed() && step.isComplete()) {
				wizardModel.next();
			}
		}
	}
}
