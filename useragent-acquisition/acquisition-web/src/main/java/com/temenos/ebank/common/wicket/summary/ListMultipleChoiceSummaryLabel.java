package com.temenos.ebank.common.wicket.summary;

import java.util.ArrayList;
import java.util.MissingResourceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.markup.html.form.ListMultipleChoice;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ListMultipleChoiceSummaryLabel extends SummaryLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ListMultipleChoice inputField;
	protected static Log logger = LogFactory.getLog(ListMultipleChoiceSummaryLabel.class);
	
	public ListMultipleChoiceSummaryLabel(String id, ListMultipleChoice inputField) {
		super(id);
		this.inputField = inputField;
	}

	@Override
	protected Object getSummaryLabelModelObject() {
		Object defaultModelObject = inputField.getDefaultModelObject();

		StringBuffer displayValues = new StringBuffer();
		// these are ListMultipleChoice or CheckBoxMultipleChoice using the LocalizedLabelRenderer
		try {
			if (defaultModelObject != null) {
				ArrayList<String> selectedValues = (ArrayList<String>) defaultModelObject;
				Boolean firstString = true;
				for (String selected : selectedValues) {
					Object value = inputField.getChoiceRenderer().getDisplayValue(selected);
					if(!firstString){
						displayValues.append(", ");
					}
					displayValues.append(value.toString());
				}
			}
		} catch (MissingResourceException e) {
			logger.error("Unable to load resources", e);
			
		}
		return displayValues.toString();

	}
}
