package com.temenos.ebank.common.wicket.summary;

import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.DropDownChoice;

import com.temenos.ebank.common.wicket.formValidation.ValidationErrorFeedbackBorder;

/**
 * @author raduf
 *         Label component used for displaying the value of a DropDownChoice within a summary page
 *         The purpose of this class is setting the defaultModelObject to what we want to display in the label,
 *         taking data from the model of the original input field.
 */
public class DropDownSummaryLabel extends SummaryLabel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static Log logger = LogFactory.getLog(DropDownSummaryLabel.class);	
	private DropDownChoice<String> inputField;

	public DropDownSummaryLabel(String id, DropDownChoice<String> inputField) {
		super(id);
		this.inputField = inputField;
	}

	@Override
	public void onBeforeRender() {
		super.onBeforeRender();
		// hide the parent border if value is not available(it was not required and the user did not fill it in)
		// another option is to fill the label with a String like "N/A"
		Object displayValue = getSummaryLabelModelObject();
		if (displayValue == null) {
			ValidationErrorFeedbackBorder border = this.findParent(ValidationErrorFeedbackBorder.class);
			border.setVisible(false);
		}

	}

	@Override
	protected Object getSummaryLabelModelObject() {
		Object defaultModelObject = getDefaultModelObject();
		Object displayValue = defaultModelObject;

		try {
			if (defaultModelObject != null) {
				displayValue = inputField.getChoiceRenderer().getDisplayValue((String)defaultModelObject);
			}
		} catch (MissingResourceException e) {
			logger.error("Unable to load resources", e);
		}
		return displayValue;
	}
}
