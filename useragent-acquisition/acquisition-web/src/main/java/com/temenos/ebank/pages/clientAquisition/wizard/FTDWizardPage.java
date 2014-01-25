package com.temenos.ebank.pages.clientAquisition.wizard;

import com.temenos.ebank.common.wicket.wizard.EbankWizard;
import com.temenos.ebank.domain.Application;

public class FTDWizardPage extends ClientAquisitionWizardPage {

	/**
	 * Constructs a new instance of wizard page for new fixed term deposit subscription
	 */
	public FTDWizardPage() {
		this(null);
	}
	
	/**
	 * Constructs a new instance of wizard page for resuming fixed term deposit subscription
	 * @param a The application in the database
	 */
	public FTDWizardPage(Application a) {
		super(a);
	}

	@Override
	protected EbankWizard newWizard(String id, Application a) {
		return new FTDWizard(id, a);
	}
}
