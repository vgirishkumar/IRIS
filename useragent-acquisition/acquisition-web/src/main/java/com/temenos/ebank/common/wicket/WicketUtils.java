/**
 * 
 */
package com.temenos.ebank.common.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.Component.IVisitor;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentLabel;
import org.apache.wicket.markup.html.form.IFormSubmittingComponent;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;

import com.temenos.ebank.common.wicket.components.CompositeFormComponent;
import com.temenos.ebank.common.wicket.components.FieldInfoPanel;
import com.temenos.ebank.common.wicket.components.HintPanel;
import com.temenos.ebank.common.wicket.components.PhoneAndPrefix;
import com.temenos.ebank.common.wicket.formValidation.DisplayRequiredComponentLabel;
import com.temenos.ebank.common.wicket.formValidation.ValidationErrorFeedbackBorder;
import com.temenos.ebank.common.wicket.summary.DropDownSummaryLabel;
import com.temenos.ebank.common.wicket.summary.ListMultipleChoiceSummaryLabel;
import com.temenos.ebank.common.wicket.summary.PhoneAndPrefixSummaryLabel;
import com.temenos.ebank.common.wicket.summary.SummaryLabel;
import com.temenos.ebank.common.wicket.summary.YearsAndMonthsSummaryLabel;
import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.pages.clientAquisition.step2.YearsAndMonths;

/**
 * Wicket tools
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class WicketUtils {
	public static final String JQUERY_1_5 = "js/lib/jquery-1.5.1.js";

	/**
	 * Static class containing suffixes for property keys used by the field-creation mechanism (see
	 * {@link WicketUtils#addResourceLabelAndReturnBorder(FormComponent, AddLabelAndBorderOptions)}).
	 * 
	 * @author gcristescu
	 */
	public static final class SUFFIX {
		/**
		 * Property key suffix for Border element.
		 */
		public static final String BORDER = "Border";
		/**
		 * Property key suffix for Label element.
		 */
		public static final String LABEL = "Label";
		/**
		 * Property key suffix for hint tooltip (alt text).
		 */
		public static final String HINT_IMG_ALT = ".hintImgAlt";
		/**
		 * Property key suffix for hint tooltip (title text).
		 */
		public static final String HINT_IMG_TITLE = ".hintImgTitle";
		/**
		 * Property key suffix for FieldInfo element.
		 */
		public static final String INFO = "Info";
		/**
		 * Property key suffix for minimum field length validation.
		 */
		public static final String MIN_LENGTH = ".minLength";
		/**
		 * Property key suffix for maximum field length validation.
		 */
		public static final String MAX_LENGTH = ".maxLength";
	};
	
	/**
	 * Automates creating labels and borders for input fields, using the convention that labelId is fieldId + Label and borderId  is
	 * fieldId + Border.
	 * @param inputField
	 * @return
	 */
	public static Border addResourceLabelAndReturnBorder(final FormComponent inputField) {
		return addResourceLabelAndReturnBorder(inputField, null);
	}
	
	/**
	 * Automates creating labels and borders for input fields, using the convention that labelId is fieldId + Label and borderId  is
	 * fieldId + Border. Passing an {@link AddLabelAndBorderOptions} argument allows to override parts of this convention and to 
	 * customize visibility and other aspects of the components involved in this mechanism.
	 * Adds the html for the field's hint image, if the field as an associated hint. A field has associated hint if the resource 
	 * string {fieldId}Hint exists in the properties file
	 * @param inputField The form component for whom to attach the label
	 * @param labelAndBorderOptions An {@link AddLabelAndBorderOptions} encapsulating parameters for this method
	 * @return
	 */
	public static Border addResourceLabelAndReturnBorder(final FormComponent inputField, AddLabelAndBorderOptions labelAndBorderOptions) {
		if (labelAndBorderOptions == null) {
			labelAndBorderOptions = new AddLabelAndBorderOptions();
		}
		String fieldId = inputField.getId();
		String borderId = fieldId + SUFFIX.BORDER;
		String fieldInfoId = fieldId + SUFFIX.INFO;
		Border border = new ValidationErrorFeedbackBorder(borderId);
		String labelResourceKey = fieldId + SUFFIX.LABEL;
		String labelId = labelResourceKey;
		//If the field has metadata indicating that a different text should be displayed in the label
		//For example the field "First Name" uses the label "First Name" for validation purposes, but
		//we need to display the label "Name" 
		
		boolean hasLabelWithDifferentResourceKey = labelAndBorderOptions.hasLabelWithDifferentResourceKey(); 
		if (hasLabelWithDifferentResourceKey) {
			labelId = labelAndBorderOptions.getLabelWithDifferentResourceKey();
		}
		// resolve label model
		IModel labelModel;
		if (labelAndBorderOptions.getStringResModel() != null) {
			labelModel = labelAndBorderOptions.getStringResModel();
		} else {
			labelModel = new ResourceModel(labelResourceKey);
		}

		
		// if not summary page
		if (!EbankWizardStep.getIsSummary()) {
			inputField.setLabel(labelModel);
			FormComponent componentToFocusOnLabelClick = inputField;
			boolean requiredCompositeLabel = false;
			if (inputField instanceof CompositeFormComponent) {				
				componentToFocusOnLabelClick = ((CompositeFormComponent) inputField).getFirstInput();
				componentToFocusOnLabelClick.setLabel(labelModel);
				requiredCompositeLabel = inputField.isRequired();
			}
			FormComponentLabel txtLabel = null;
			//if contained in composite and it has a label, we don't want to display required markup
			if( labelAndBorderOptions.isContainedInComposite() ){
				txtLabel = new SimpleFormComponentLabel(labelId, inputField);
			}
			else{
				if (hasLabelWithDifferentResourceKey ) {
					//non standard label, using other key than <fieldName>Label
					txtLabel = new DisplayRequiredComponentLabel(labelId, new ResourceModel(labelId), componentToFocusOnLabelClick, requiredCompositeLabel);
				} else {
					txtLabel = new DisplayRequiredComponentLabel(labelId, componentToFocusOnLabelClick, requiredCompositeLabel);
				}
			}
			
			txtLabel.setEscapeModelStrings(labelAndBorderOptions.getEscapeModelStrings());
			border.add(txtLabel.setVisible(labelAndBorderOptions.getVisibleLabel()));
			border.add(inputField);
			HintPanel hintPanel = new HintPanel("hintPanel", fieldId, labelAndBorderOptions.getVisibleHint());
			border.add(hintPanel);
		} else {
			Label label = null;
			if (hasLabelWithDifferentResourceKey) {
				//non standard label, using other key than <fieldName>Label				
				label  = new Label(labelId, new ResourceModel(labelId));
			} else {
				label = new Label(labelId, labelModel);	
			}	
			label.setEscapeModelStrings(labelAndBorderOptions.getEscapeModelStrings());
			border.add(label.setVisible(labelAndBorderOptions.getVisibleLabel()));
			// resolve value
			prepareInputAndBorderForSummaryPage(inputField, border);
		}
		
		FieldInfoPanel fieldInfo = new FieldInfoPanel("fieldInfoPanel", fieldInfoId, !EbankWizardStep.getIsSummary() && labelAndBorderOptions.getVisibleFieldInfo());
		border.add(fieldInfo);
		return border;
	}


	/**
	 * Transforms the input field and the border for usage in the summary page.
	 * The input field becomes a label and the border gets decorated with a css class for
	 * alternating colors for even and odd rows
	 * 
	 * @param inputField
	 *            Field to prepare for summary display
	 * @param border
	 *            Border to prepare for summary display
	 */
	private static void prepareInputAndBorderForSummaryPage(final FormComponent inputField, Border border) {
		// if (!EbankWizardStep.getIsSummary()) {
		// //prevent accidental usage
		// return;
		// }
		//TODO: move this factory logic elsewhere
		String fieldId = inputField.getId();
		Label valueLabel;
		if (inputField instanceof DropDownChoice<?>) {
			valueLabel = new DropDownSummaryLabel(fieldId, (DropDownChoice) inputField);
		} else if (inputField instanceof RadioGroup) {
			Radio choice = (Radio) inputField.visitChildren(Radio.class, new IVisitor<Radio>() {
				public Object component(Radio component) {
					if (inputField.getModelObject() != null) {
						if(inputField.getModelObject().equals(component.getModelObject())) {
							return component;
						}
					} else if (Boolean.FALSE.equals(component.getModelObject())){
						return component;
					}
					return CONTINUE_TRAVERSAL;
				}
			});
			//TODO: if sole customer, then the field of gender for the second customer is found empty
			// which means the choice will be null. To avoid NPE, the check below was introduced.
			// the TODO is: maybe we can find a better solution
			if(choice!=null){
				valueLabel = new Label(fieldId, choice.getLabel());
			}
			else{
				valueLabel=new Label(fieldId, new Model(""));
			}
			//valueLabel = new RadioGroupSummaryLabel(fieldId, (RadioGroup) inputField);
		} else if (inputField instanceof ListMultipleChoice<?>) {
			valueLabel = new ListMultipleChoiceSummaryLabel(fieldId, (ListMultipleChoice) inputField);
		} else if (inputField instanceof CheckBox) {
			valueLabel = new SummaryLabel(fieldId, inputField.getModel()) {
				@Override
				protected Object getSummaryLabelModelObject() {
					Object defaultModelObject = getDefaultModelObject();
					String displayValue = Boolean.TRUE.equals(defaultModelObject) ? getString("yesLabel")
							: getString("noLabel");
					return displayValue;
				}
			};
		} else if (inputField instanceof YearsAndMonths) {
			valueLabel = new YearsAndMonthsSummaryLabel((YearsAndMonths) inputField);
		} else if (inputField instanceof PhoneAndPrefix) {
			valueLabel = new PhoneAndPrefixSummaryLabel((PhoneAndPrefix) inputField);
		} else {
			valueLabel = new Label(fieldId, inputField.getModel());
		}
		border.add(valueLabel);

		// white/gray
		border.add(new AbstractBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			public void onComponentTag(final Component component, final ComponentTag tag) {
				tag.append("class", EbankWizardStep.getEven() ? " summaryRowG" : " summaryRowW", " ");
			}
		});
	}

	// public void
	public static FormComponentLabel addResourceLabelAndReturnLabel(Radio radioField) {
		String textFielId = radioField.getId();
		String labelID = textFielId + SUFFIX.LABEL;
		radioField.setLabel(new ResourceModel(labelID));
		SimpleFormComponentLabel txtLabel = new SimpleFormComponentLabel(labelID, radioField);
		return txtLabel;
	}

	/**
	 * Returns true if the submitting form button has defaultFormProcessing set on true or if the form hasn't been
	 * submitted by a button
	 * 
	 * @param component
	 * @return
	 */
	public static boolean isFormProcessingEnabled(FormComponent component) {
		IFormSubmittingComponent submitButton = component.getForm().findSubmittingButton();
		if (submitButton != null) {
			return submitButton.getDefaultFormProcessing();
		}

		return true;
	}


	public static void addJQueryAndMetadataLibs(Component component) {
		component.add(JavascriptPackageResource.getHeaderContribution(JQUERY_1_5));
		component.add(JavascriptPackageResource.getHeaderContribution("js/lib/jquery.metadata.js"));
	}

	public static void addJQueryUILib(Component component) {
		component.add(CSSPackageResource.getHeaderContribution("css/jquery-ui.css"));
		component.add(JavascriptPackageResource.getHeaderContribution(JQUERY_1_5));
		component.add(JavascriptPackageResource.getHeaderContribution("js/lib/jquery-ui.min.js"));
	}
	
	public static void addAnalyticsLib(Component component) {
		component.add(JavascriptPackageResource.getHeaderContribution("js/lib/webtrends.js"));
	}
}