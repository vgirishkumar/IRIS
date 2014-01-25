package com.temenos.ebank.common.wicket.feedback;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Session;
import org.apache.wicket.feedback.ContainerFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;

import com.temenos.ebank.common.wicket.formValidation.FieldsWithErrorslFeedbackPanel;
import com.temenos.ebank.common.wicket.formValidation.GenericFormValidationFeedbackPanel;
import com.temenos.ebank.common.wicket.wizard.EbankButtonBar;

/**
 * Feedback tools for implementing the feedback messages mechanism.
 * 
 * @author vionescu
 * 
 */
public class FeedbackUtils {
	/**
	 * Returns a feedback panel which displays only messages of type DEBUG, INFO
	 * and WARN
	 * 
	 * @param container
	 * @return
	 */
	public static Panel getInfoPanel(final MarkupContainer container) {
		FeedbackPanel fp = new FeedbackPanel("infoFeedbackPanel", new ContainerFeedbackMessageFilter(container) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean accept(FeedbackMessage message) {
				boolean isValidationError = (message.getReporter() instanceof FormComponent);
				if (isValidationError) {
					// validation errors will be treated on a per field
					// basis. It's important to filter
					// those messages for this panel because
					return message.getLevel() < FeedbackMessage.ERROR;
				} else {
					// let all messages pass
					return true;
				}
			}
		}) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !hasAlerts(container);
			}
		};
		fp.setEscapeModelStrings(false);
		return fp;
	}

	/**
	 * Returns a feedback panel which displays alert messages
	 * 
	 * @param container
	 * @return
	 */
	public static Panel getAlertPanel(MarkupContainer container) {
		AlertFeedbackPanel fp = new AlertFeedbackPanel("alertFeedbackPanel", getMessageFilterForAlerts(container));
		fp.setEscapeModelStrings(false);
		return fp;
	}

	/**
	 * Returns a feedback panel which displays the error message, used only in the Internal Error page
	 * @param container The container containing the erorr messages. Only the errors associated with this continer will be displayed
	 * @return
	 */
	public static Panel getAlertPanelForInternalErrorPage(MarkupContainer container) {
		AlertFeedbackPanel fp = new AlertFeedbackPanel("alertFeedbackPanel", new ContainerFeedbackMessageFilter(container));
		fp.setEscapeModelStrings(false);
		return fp;
	}
	
	/**
	 * Returns a {@link ContainerFeedbackMessageFilter} who retains only the
	 * alert messsages. This is useful for suppressing the field validation
	 * summary panel when dealing with alerts.
	 * 
	 * @param container
	 *            The container containing the messages to filter
	 * @return
	 */
	private static ContainerFeedbackMessageFilter getMessageFilterForAlerts(MarkupContainer container) {
		return new ContainerFeedbackMessageFilter(container) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * Accept only alerts
			 * 
			 * @see org.apache.wicket.feedback.ContainerFeedbackMessageFilter#accept(org.apache.wicket.feedback.FeedbackMessage)
			 */
			@Override
			public boolean accept(FeedbackMessage message) {
				boolean isAlert = message.getMessage() instanceof Alert;
				return isAlert;
			}
		};
	}

	public static boolean hasAlerts(MarkupContainer container) {
		boolean hasAlerts = Session.get().getFeedbackMessages().hasMessage(getMessageFilterForAlerts(container));
		return hasAlerts;
	}

	/**
	 * Returns a feedback panel which displays the names of the fields with
	 * validation errors.
	 * 
	 * @param container
	 * @return
	 */
	public static Panel getFieldsWithErrorsFeedbackPanel(final MarkupContainer container) {
		return new FieldsWithErrorslFeedbackPanel("errorFeedbackPanel", new ContainerFeedbackMessageFilter(container) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean accept(FeedbackMessage message) {
				// display only validation errors
				boolean isValidationError = (message.getReporter() instanceof FormComponent);
				return isValidationError;
			}
		}) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return !hasAlerts(container);
			}
		};
	}

	/**
	 * Returns a feedback panel which displays a generic validation error
	 * 
	 * @param form
	 *            The form for which to display the validation errors
	 * @wizardButtonBar the button bar of the wizard holding the form, if
	 *                  applicable, null otherwise
	 * @return
	 */
	public static Panel getGenericFormValidationFeedbackPanel(Form<?> form, EbankButtonBar wizardButtonBar) {
		return new GenericFormValidationFeedbackPanel("errorFeedbackPanel", form, wizardButtonBar);
	}

}
