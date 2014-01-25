package com.temenos.ebank.pages.clientAquisition.wizard;

import com.temenos.ebank.common.wicket.wizard.EbankWizard;
import com.temenos.ebank.domain.Application;

/**
 * Page for displaying the client aquisition wizard
 * @author vionescu
 */
public class CAWizardPage extends ClientAquisitionWizardPage {
	/**
	 * Constructs a new instance of wizard page for resuming current account subscription
	 * @param a The application in the database
	 */
	public CAWizardPage(Application a) {
		super(a);
	}

	/**
	 * Constructs a new instance of wizard page for new current account subscription
	 */
	public CAWizardPage() {
		this(null);
	}
	
	@Override
	protected EbankWizard newWizard(String id, Application a) {
		return new CAWizard(id, a);
		
	}
}
