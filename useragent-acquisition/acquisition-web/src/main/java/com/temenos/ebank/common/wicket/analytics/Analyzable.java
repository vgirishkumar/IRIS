package com.temenos.ebank.common.wicket.analytics;

import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.pages.BasePage;

/**
 * Interface used as a marker for wicket components that wish to contribute to the global set of parameters used for web
 * analytics ({@link BasePage#analyticsParameters}).
 * 
 * @author gcristescu
 */
public interface Analyzable {

	/**
	 * Use the implementation found in {@link EbankWizardStep#addAnalytics()} as a model. This method MUST be called by
	 * the component that implements it, at the beginning of <code>onbeforeRender()</code>.
	 * <p>
	 * TODO find a more elegant way to force this functionality on the coder.
	 */
	void addAnalytics();
}
