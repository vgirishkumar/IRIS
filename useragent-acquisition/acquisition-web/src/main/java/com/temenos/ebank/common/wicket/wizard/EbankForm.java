package com.temenos.ebank.common.wicket.wizard;

import org.apache.wicket.MetaDataKey;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;

/**
 * Form used for protection against multiple submits. Subclasses need to implement {@link #onRepeatSubmit()}, which is
 * called upon detection of a multiple submit.
 * 
 * @author gcristescu
 */
public abstract class EbankForm<T> extends Form<T> {
	/**
	 * Key for the MetaData token list stored on the session.
	 */
	private static MetaDataKey<EbankFormToken> PROCESSED = new MetaDataKey<EbankFormToken>() {
	};

	public EbankForm(String id) {
		super(id);
	}

	/**
	 * Method called upon detection of a multiple submit.
	 */
	protected abstract void onRepeatSubmit();

	@Override
	public void process(IFormSubmittingComponent submittingComponent) {
		if (isAlreadyProcessed()) {
			onRepeatSubmit();
			return;
		}
		super.process(submittingComponent);
		updateProcessedForms();
	}

	/**
	 * Create a token that uniquely identifies an instance of a form.
	 * 
	 * @return token
	 */
	private EbankFormToken getToken() {
		return new EbankFormToken(getPage().getPageReference(), getPageRelativePath());
	}

	/**
	 * Decide if the current form was already processed during this session.
	 */
	private synchronized boolean isAlreadyProcessed() {
		EbankFormToken oldToken = getSession().getMetaData(PROCESSED);
		if (oldToken != null) {
			EbankFormToken newToken = getToken();
			return oldToken.equals(newToken);
		}
		return false;
	}

	/**
	 * Update the session with the MetaData list of processed forms.
	 */
	private synchronized void updateProcessedForms() {
		if (hasError()) {
			return;
		}
		EbankFormToken oldToken = getSession().getMetaData(PROCESSED);
		EbankFormToken newToken = getToken();
		if ((oldToken == null) || !oldToken.equals(newToken)) {
			oldToken = newToken;
			getSession().setMetaData(PROCESSED, oldToken);
		}
	}

	@Override
	protected void onBeforeRender() {
		EbankFormToken oldToken = getSession().getMetaData(PROCESSED);
		EbankFormToken newToken = getToken();
		if ((oldToken != null) && oldToken.equals(newToken)) {
			getSession().setMetaData(PROCESSED, null);
		}

		super.onBeforeRender();
	}
}