package com.temenos.ebank.common.wicket.formValidation;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.LabeledWebMarkupContainer;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;
import org.apache.wicket.model.IModel;

/**
 * {@link SimpleFormComponentLabel} decorated to display the required (*) image alongside required fields
 * @author vionescu
 *
 */
public class DisplayRequiredComponentLabel extends SimpleFormComponentLabel {
	private static final long serialVersionUID = 1L;
	public final static String REQUIRED_MARKUP = "&nbsp;<em>*</em>";
	
	// keeps track of original "isRequired" flag for labels that get reassigned
	// from a composite component to that composite's first input
	private boolean requiredCompositeLabel;
	public DisplayRequiredComponentLabel(String id, LabeledWebMarkupContainer labelProvider, boolean requiredCompositeLabel) {
		super(id, labelProvider);
		this.requiredCompositeLabel = requiredCompositeLabel;
	}

	/**
	 * Constructs a new instance. 
	 * @param id The id of the label
	 * @param labelProvider The component for whom to attach the label
	 */
	public DisplayRequiredComponentLabel(String id, IModel<String> resourceModel, LabeledWebMarkupContainer labelProvider, boolean requiredCompositeLabel) {
		super(id, labelProvider);
		this.requiredCompositeLabel = requiredCompositeLabel;
		//take this resource model if provided. If not provided, than the field's label is used (the call to super above)
		if (resourceModel != null) {
			setDefaultModel(resourceModel);
		}
	}

	@SuppressWarnings("rawtypes")	
	@Override
	protected void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
		CharSequence replacement = null;		
		if (getFormComponent() instanceof FormComponent && (requiredCompositeLabel || ((FormComponent) getFormComponent()).isRequired()))
			replacement = getDefaultModelObjectAsString() +  REQUIRED_MARKUP;
		else
			replacement = getDefaultModelObjectAsString(); 

		replaceComponentTagBody(markupStream, openTag, replacement);
	}

}
