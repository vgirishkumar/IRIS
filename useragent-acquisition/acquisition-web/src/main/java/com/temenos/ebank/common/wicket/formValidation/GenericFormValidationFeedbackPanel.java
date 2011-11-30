package com.temenos.ebank.common.wicket.formValidation;

import static com.temenos.ebank.common.wicket.feedback.FeedbackUtils.hasAlerts;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.feedback.IFeedback;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;
import org.apache.wicket.markup.html.form.SubmitLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.temenos.ebank.common.wicket.wizard.EbankButtonBar;

/**
 * A generic form feedback panel, indicating only that there are validation
 * errors. This component can work in wizard mode or in independent mode.
 * In wizard mode, if there are validation errors when the user pressed next,
 * then an error message is displayed as well as a link inviting the user to
 * save the modifications.
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "serial" })
public class GenericFormValidationFeedbackPanel extends Panel implements IFeedback {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Form form;
	private EbankButtonBar wizardButtonBar;
	private SubmitLink saveLink;

	private boolean isWizardPage;

	public GenericFormValidationFeedbackPanel(String id, final Form form, final EbankButtonBar wizardButtonBar) {
		super(id);
		this.form = form;
		this.wizardButtonBar = wizardButtonBar;

		this.isWizardPage = wizardButtonBar != null;

		WebMarkupContainer errorContainer = new WebMarkupContainer("errorContainer");
		add(errorContainer);

		WebMarkupContainer containerOtherError = new WebMarkupContainer("containerOtherError") {
			@Override
			public boolean isVisible() {
				return !nextWizardBtnPressed();
			}
		};

		errorContainer.add(containerOtherError);

		WebMarkupContainer containerErrorOnContinue = new WebMarkupContainer("containerErrorOnContinue") {
			// @Override
			// public boolean isVisible() {
			// return nextWizardBtnPressed();
			// }
		};
		final IModel<String> toggleContainerErrorOnContinueVisibilityModel = new Model<String>() {
			@Override
			public String getObject() {
				boolean visible = nextWizardBtnPressed();
				return visible ? "display:block;" : "display:none;";
			}
		};
		// wicket does not allow to set isVisible(false) on the sumbitting button, so we do it via css
		containerErrorOnContinue
				.add(new AttributeModifier("style", true, toggleContainerErrorOnContinueVisibilityModel));

		errorContainer.add(containerErrorOnContinue);

		this.saveLink = new SubmitLink("saveLink", form) {
			@Override
			public void onSubmit() {
				if (isWizardPage) {
					// form.process(wizardButtonBar.getSaveButton());
					wizardButtonBar.getSaveButton().onSubmit();
				}
			}
		};
		saveLink.setDefaultFormProcessing(false);
		containerErrorOnContinue.add(EbankButtonBar.addCancelClientSideValidationBehaviour(saveLink));

		final IModel<String> toggleVisibilityModel = new Model<String>() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getObject() {
				boolean visible = false;
				if (!hasAlerts(form) && form.isSubmitted()) {
					// hack to prevent stack overflow, because the method form.findSubmittingButton() visits all
					// children
					// buttons and invokes isVisible(), and saveLink is a children button
					boolean saveLinkClicked = (getRequest().getParameter(saveLink.getInputName()) != null)
							|| (getRequest().getParameter(saveLink.getInputName() + ".x") != null);
					if (!saveLinkClicked) {
						final IFormSubmittingComponent submittingComponent = getSumbitingButton();
						boolean hasErrors = (submittingComponent != null) && form.hasError();
						visible = hasErrors;
					}
				}
				return visible ? "display:block;" : "display:none;";
			}
		};
		// the standard setVisible does not work, because wicket requires that the submitting button is visible
		errorContainer.add(new AttributeModifier("style", true, toggleVisibilityModel));
	}

	private boolean nextWizardBtnPressed() {
		return isWizardPage && getSumbitingButton() == wizardButtonBar.getNextButton();
	}

	private boolean saveLinkClicked() {
		boolean saveLinkClicked = (getRequest().getParameter(saveLink.getInputName()) != null)
				|| (getRequest().getParameter(saveLink.getInputName() + ".x") != null);
		return saveLinkClicked;

	}

	private IFormSubmittingComponent getSumbitingButton() {
		if (saveLinkClicked()) {
			return saveLink;
		}
		if (form.isSubmitted()) {
			try {
				return form.findSubmittingButton();
			} catch (WicketRuntimeException e) {
				// wicket does not like if the submit button is not present
				return null;
			}
		}
		return null;
	}
}
