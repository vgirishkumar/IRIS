package com.temenos.ebank.pages.startPage;

import org.apache.wicket.markup.html.link.Link;

import com.temenos.ebank.pages.BasePage;
import com.temenos.ebank.pages.RefreshCachePage;
import com.temenos.ebank.pages.clientAquisition.resumeApplication.ResumeApplication;
import com.temenos.ebank.pages.clientAquisition.wizard.CAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.FTDWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.IASAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.IBSAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.RASAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.RSWizardPage;

public class ApplyForInternationalAccount extends BasePage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings({ "rawtypes", "serial" })
	public ApplyForInternationalAccount() {

		add(new Link("linkApplyCurrAccount") {
			@Override
			public void onClick() {
				setResponsePage(new CAWizardPage(null));
			}

		});
		
		add(new Link("linkApplyFixedTermDeposit") {
			@Override
			public void onClick() {
				setResponsePage(new FTDWizardPage(null));
			}

		});
		
		add(new Link("linkApplyRegularSaver") {
			@Override
			public void onClick() {
				setResponsePage(new RSWizardPage(null));
			}

		});
		
		add(new Link("linkApplyIASA") {
			@Override
			public void onClick() {
				setResponsePage(new IASAWizardPage(null));
			}

		});
		
		add(new Link("linkApplyIBSA") {
			@Override
			public void onClick() {
				setResponsePage(new IBSAWizardPage(null));
			}

		});
		
		add(new Link("linkApplyRASA") {
			@Override
			public void onClick() {
				setResponsePage(new RASAWizardPage(null));
			}

		});
		
		add(new Link("linkResume") {
			@Override
			public void onClick() {
				setResponsePage(new ResumeApplication());
			}

		});
		
		add(new Link("linkRefreshCache") {
			@Override
			public void onClick() {
				setResponsePage(new RefreshCachePage());
			}

		});
		
	}
}
