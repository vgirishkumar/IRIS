package com.temenos.ebank.pages.clientAquisition.wizard;

import com.temenos.ebank.common.wicket.wizard.EbankWizard;
import com.temenos.ebank.domain.Application;

public class IBSAWizardPage extends ClientAquisitionWizardPage {

	/**
	 * Constructs a new instance of wizard page for IBSA subscription
	 */
	public IBSAWizardPage() {
		this(null);
	}
	
	/**
	 * Constructs a new instance of wizard page for resuming IBSA  subscription
	 * @param a The application in the database
	 */
	public IBSAWizardPage(Application a) {
		super(a);
	}

	@Override
	protected EbankWizard newWizard(String id, Application a) {
		return new IBSAWizard(id, a);
	}
}
