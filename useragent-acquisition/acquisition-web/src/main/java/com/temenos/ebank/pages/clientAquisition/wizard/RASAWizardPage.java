package com.temenos.ebank.pages.clientAquisition.wizard;

import com.temenos.ebank.common.wicket.wizard.EbankWizard;
import com.temenos.ebank.domain.Application;

public class RASAWizardPage extends ClientAquisitionWizardPage {

	/**
	 * Constructs a new instance of wizard page for RASA subscription
	 */
	public RASAWizardPage() {
		this(null);
	}
	
	/**
	 * Constructs a new instance of wizard page for resuming RASA subscription
	 * @param a The application in the database
	 */
	public RASAWizardPage(Application a) {
		super(a);
	}

	@Override
	protected EbankWizard newWizard(String id, Application a) {
		return new RASAWizard(id, a);
	}
}
