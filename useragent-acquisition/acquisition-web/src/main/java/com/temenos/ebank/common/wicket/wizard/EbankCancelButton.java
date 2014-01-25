package com.temenos.ebank.common.wicket.wizard;

import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardButton;

/**
 * Models a cancel button in the wizard. When pressed, it calls {@link Wizard#onCancel()} which
 * should do the real work.
 * 
 * @author vionescu
 */
public class EbankCancelButton extends WizardButton {
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 * 
	 * @param id
	 *            The component id
	 * @param wizard
	 *            The wizard
	 */
	public EbankCancelButton(String id, IWizard wizard) {
		super(id, wizard, "org.apache.wicket.extensions.wizard.cancel");
		setDefaultFormProcessing(false);
	}

	/**
	 * @see org.apache.wicket.Component#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return isVisible();
	}

	/**
	 * @see org.apache.wicket.Component#isVisible()
	 */
	@Override
	public boolean isVisible() {
		if (getWizardModel().isCancelVisible()) {
			// cancel not displayed for last step
			return getWizardModel().isNextAvailable();
		}
		return false;
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.WizardButton#onClick()
	 */
	@Override
	public final void onClick() {
		getWizardModel().cancel();
	}
}