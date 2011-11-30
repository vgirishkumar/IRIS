package com.temenos.ebank.pages.clientAquisition.wizard;

import com.temenos.ebank.common.wicket.wizard.EbankWizard;
import com.temenos.ebank.domain.Application;

public class RSWizardPage extends ClientAquisitionWizardPage {


	/**
	 * Constructs a new instance of wizard page for resuming regular saver subscription
	 * @param a The application in the database
	 */
	public RSWizardPage(Application a) {
		super(a);
	}
	
	/**
	 * Constructs a new instance of wizard page for new regular saver subscription
	 */
	public RSWizardPage() {
		this(null);

	}	

	@Override
	protected EbankWizard newWizard(String id, Application a) {
		return new RSWizard(id, a);
	}

}
