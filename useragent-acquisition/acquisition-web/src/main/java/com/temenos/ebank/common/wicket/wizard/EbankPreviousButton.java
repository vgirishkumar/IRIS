package com.temenos.ebank.common.wicket.wizard;

import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.WizardButton;

/**
 * Models a previous button in the wizard. When pressed, it moves the wizard state to the previous
 * step of the model by calling {@link IWizardModel#previous() previous} on the wizard's model.
 * 
 * @author vionescu
 */
public class EbankPreviousButton extends WizardButton {
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param wizard
	 *            The wizard
	 */
	public EbankPreviousButton(String id, IWizard wizard) {
		super(id, wizard, "org.apache.wicket.extensions.wizard.previous");
		setDefaultFormProcessing(false);
	}

	/**
	 * @see org.apache.wicket.Component#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return isVisible();
	}

	@Override
	public boolean isVisible() {
		// previous not displayed for first and last step
		return getWizardModel().isPreviousAvailable() && getWizardModel().isNextAvailable();
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.WizardButton#onClick()
	 */
	@Override
	public void onClick() {
		getWizardModel().previous();
	}
}