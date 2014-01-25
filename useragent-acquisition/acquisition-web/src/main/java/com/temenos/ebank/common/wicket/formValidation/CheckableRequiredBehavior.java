package com.temenos.ebank.common.wicket.formValidation;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.Response;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Classes;
import org.apache.wicket.util.string.JavascriptUtils;
import org.apache.wicket.util.string.Strings;

import com.temenos.ebank.common.wicket.WicketUtils;
import com.temenos.ebank.common.wicket.components.ClientValidatedCheckable;

/**
 * @author raduf
 * Checkable is the component holding a group of radios or check boxes
 * When those components are mandatory/required, they can be validated client-side with this behavior
 * The behavior conforms to the client side validation plug-in which looks for checkable validation rules 
 * on the first element of the group. More exactly, it will write the required json string in the class 
 * attribute of the first check box/radio of the group
 *
 */
public class CheckableRequiredBehavior extends AbstractBehavior {

	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(CheckableRequiredBehavior.class);

	private Object clientValidationKey = "required";
	private String key = "Required";
	private Object ruleValue = "true";
	
	private String firstCheckableMarkupID;
	private String jsonClass;

	@Override
	public void bind(final Component component) {
		WicketUtils.addJQueryAndMetadataLibs(component);
		//checkable is the group of radios and check boxes. it must implement ClientValidatedCheckable

		if ((component instanceof ClientValidatedCheckable) == false || (component instanceof FormComponent )==false) {
			throw new IllegalArgumentException("This behavior [" + Classes.simpleName(getClass())
					+ "] can only be added to a FormComponent which implements ClientValidatedCheckable");
		}
		
		ClientValidatedCheckable checkable = (ClientValidatedCheckable)component;
		firstCheckableMarkupID = checkable.getFirstCheckableId();
		if (StringUtils.isEmpty(firstCheckableMarkupID)) {
			logger.warn("Received an empty markupID for the checkable `" + component.getId() + "`. This could be caused by an empty list of choices.");
			return;
		}

		IModel<?> messageModel = new Model<LabelWrapperModelObject>( new LabelWrapperModelObject((FormComponent<?>)component));
		
		final Localizer localizer = component.getLocalizer();
		// if field defines its own message
		String message = localizer.getString(component.getId() + "." + key, component, messageModel, "");
		// look for the more general form of message, set on the validation level
		if (Strings.isEmpty(message)) {
			message = localizer.getString(key, component, messageModel, "");
		}
		
		//escape single quotes in message
		message = message.replace("'", "\\'");
	
		jsonClass = new StringBuffer().append("{").append(clientValidationKey ).append(": ").append(ruleValue)
				.append(", messages: {").append(clientValidationKey).append(": '").append(message.replace("'", "\\'"))
				.append("'}}").toString();
	}

	/*
	//write to the response the javascript that will add the json to the class of the first checkable element
	//so that client side validation will trigger
	 */
	@Override
	public void onRendered( Component component ){
		final Response response = RequestCycle.get().getResponse();
        response.write(JavascriptUtils.SCRIPT_OPEN_TAG);
        StringBuilder javaScript = new StringBuilder();
        javaScript.append( "$(document).ready(function(){ " )
        			.append("$('#" + firstCheckableMarkupID +"').addClass(\"" + jsonClass + "\");")
        			.append("});");
        response.write(javaScript);
        response.write(JavascriptUtils.SCRIPT_CLOSE_TAG);
	}
}
