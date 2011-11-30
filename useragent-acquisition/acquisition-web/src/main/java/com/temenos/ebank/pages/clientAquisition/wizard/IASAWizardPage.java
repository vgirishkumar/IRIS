package com.temenos.ebank.pages.clientAquisition.wizard;

import com.temenos.ebank.common.wicket.wizard.EbankWizard;
import com.temenos.ebank.domain.Application;

public class IASAWizardPage extends ClientAquisitionWizardPage {

	/**
	 * Constructs a new instance of wizard page for IASA subscription
	 */
	public IASAWizardPage() {
		this(null);
	}
	
	/**
	 * Constructs a new instance of wizard page for resuming IASA  subscription
	 * @param a the application in the database
	 */
	public IASAWizardPage(Application a) {
		super(a);
	}

	@Override
	protected EbankWizard newWizard(String id, Application a) {
		return new IASAWizard(id, a);
	}
}
