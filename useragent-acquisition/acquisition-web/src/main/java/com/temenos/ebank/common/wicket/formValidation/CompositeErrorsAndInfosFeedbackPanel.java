package com.temenos.ebank.common.wicket.formValidation;

import static com.temenos.ebank.common.wicket.feedback.FeedbackUtils.getAlertPanel;
import static com.temenos.ebank.common.wicket.feedback.FeedbackUtils.getGenericFormValidationFeedbackPanel;
import static com.temenos.ebank.common.wicket.feedback.FeedbackUtils.getInfoPanel;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

import com.temenos.ebank.common.wicket.wizard.EbankButtonBar;

/**
 * Panel used for composing all alert and info panels in the application.
 * @author vionescu
 *
 */
public class CompositeErrorsAndInfosFeedbackPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public CompositeErrorsAndInfosFeedbackPanel(String id, Form frm, MarkupContainer messagesContainer,
			EbankButtonBar wizardButtonBar) {
		super(id);
		add(getAlertPanel(this));
		add(getGenericFormValidationFeedbackPanel(frm, wizardButtonBar));
		add(getInfoPanel(messagesContainer));
	}

}
