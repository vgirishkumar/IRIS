package com.temenos.ebank.pages.clientAquisition.wizard;

import org.apache.wicket.RestartResponseException;

import com.temenos.ebank.common.wicket.wizard.EbankWizard;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.pages.BackButtonWarningPage;
import com.temenos.ebank.pages.BasePage;
import com.temenos.ebank.wicket.EbankSession;

/**
 * Page for displaying the client aquisition wizard
 * 
 * @author vionescu
 */
public abstract class ClientAquisitionWizardPage extends BasePage {
	/**
	 * Construct.
	 * 
	 * @param <C>
	 * 
	 * @param wizardClass
	 *            class of the wizard component
	 */
	public ClientAquisitionWizardPage(Application a) {
		//cleanup user session
		getEbankSession().setClientAquisitionApplication(null);
		EbankWizard wizard = newWizard("wizard", a);
		add(wizard);
	}
	
	@Override
	protected boolean supportsModalSessionScript() {
		return true;
	}
	
	protected abstract EbankWizard newWizard(String id, Application a) ;
	
	@Override
	protected void onBeforeRender() {
		if (((EbankSession) getSession()).getClientAquisitionApplication() != null
				&& ((EbankSession) getSession()).isSubmittedAppRef(((EbankSession) getSession())
						.getClientAquisitionApplication().getAppRef())) {
			throw new RestartResponseException(new BackButtonWarningPage());
		}
		super.onBeforeRender();
	}
}
