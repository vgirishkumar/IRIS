package com.temenos.ebank.common.wicket;

import java.lang.reflect.Constructor;

import com.temenos.ebank.pages.BasePage;
import com.temenos.ebank.pages.clientAquisition.wizard.ClientAquisitionWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.CAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.FTDWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.IASAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.IBSAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.RASAWizardPage;
import com.temenos.ebank.pages.clientAquisition.wizard.RSWizardPage;

/**
 * Enumeration for encapsulating product pages and links for all products 
 * @author radu 
 */

public enum ProductPagesAndLinks {
	CURRENT_ACCOUNT(CAWizardPage.class, "linkApplyCurrAccount"), 
	FIXED_TERM_DEPOSIT(FTDWizardPage.class, "linkApplyFixedTermDeposit"), 
	REGULAR_SAVER(RSWizardPage.class,	"linkApplyRegularSaver"), 
	IASA(IASAWizardPage.class, "linkApplyIASA"), 
	IBSA(IBSAWizardPage.class, "linkApplyIBSA"), 
	RASA(RASAWizardPage.class, "linkApplyRASA");

	private Class<? extends ClientAquisitionWizardPage> pageClass;
	private String link;

	ProductPagesAndLinks(Class<? extends ClientAquisitionWizardPage> pageClass, String link) {
		this.pageClass = pageClass;
		this.link = link;
	}
	
	/**
	 * @return
	 */
	public String getLink() {
		return link;
	}

	public BasePage getPage() {
		try {
			Constructor<? extends ClientAquisitionWizardPage> constructor = pageClass.getConstructor();
			BasePage page = constructor.newInstance();
			return page;
		} catch (Exception e) {
			throw new RuntimeException("Error instantiating page constructor", e );
		}
	}

	public Class<? extends ClientAquisitionWizardPage> getPageClass() {
		return pageClass;
	}
}
