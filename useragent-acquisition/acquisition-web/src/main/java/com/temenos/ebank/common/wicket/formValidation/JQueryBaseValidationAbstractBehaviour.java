package com.temenos.ebank.common.wicket.formValidation;

import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.string.Strings;

/**
 * @author raduf
 *         Abstract class for applying client side validations. It applies validation by changing the "class" attribute 
 *         of the rendered component. It appends to the class attribute a json string which will be interpreted by the
 *         client side jquery validation plug-in. 
 *         The client side validation should be specified in the inheriting class constructor under the form of Json
 *         rules and messages
 *         This base class is used for both FormComponents, and components which are contained by a parent 
 *         FormComponent - radios and check boxes
 */
public class JQueryBaseValidationAbstractBehaviour extends AttributeAppender {

	private static final long serialVersionUID = 1L;

	protected String propertiesFileKey = "";
	protected String clientValidationKey = "";
	protected String clientRuleValue = "";
	
	public JQueryBaseValidationAbstractBehaviour(IModel<?> appendModel) {
		super("class", true, appendModel, " ");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.behavior.AttributeAppender#newValue(java.lang.String, java.lang.String)
	 * This method replaces existing class name with new json validations.
	 * The Json format is required by the JQuery validation plugin on the client side.
	 * For example, it can replace
	 * "classname {jsonvalidation:true, messages:{jsonvalidation:'json validation message'}}"
	 * with
	 * "classname {jsonvalidation:true, anothervalidation:true, messages:{jsonvalidation:'json validation message',
	 * anothervalidation:'another validation message'}}
	 */
	@Override
	protected String newValue(String currentValue, String appendValue) {
		// first extract the json string from among other classes in currentValue
		// obtain JSON if any from the current value
		String currentJsonString = null;
		if (currentValue != null) {
			int startBracketIndex = currentValue.indexOf("{");
			int endBracketIndex = currentValue.lastIndexOf("}");
			if (startBracketIndex >= 0) {
				currentJsonString = currentValue.substring(startBracketIndex, endBracketIndex + 1);
				// leave other html/css classes intact
				currentValue = new StringBuffer(currentValue).delete(startBracketIndex, endBracketIndex + 1).toString();
			}
		}
	
		// add json to existing or build new json
		JsonRulesAndMessages jsonToAppend = new JsonRulesAndMessages(appendValue);
		JsonRulesAndMessages currentJson = new JsonRulesAndMessages(currentJsonString).appendJSon(jsonToAppend);
	
		// call super logic to append json string
		return super.newValue(currentValue, currentJson.getRulesAndMessagesString());
	}

	/**
	 * method called by beforeRender of the behavior, used for changing the class attribute 
	 * @param component	- the component used for getting the resource from the bundles. 
	 * @param key		- the key used in conjunction with the component to get the resource. example: Required
	 * @param clientValidationKey	- key interpreted by the client validation jquery plug-in. example: maxLength
	 * @param ruleValue		- value of the client side rule. example: true
	 * @param model
	 */
	protected void setClientValidationMessage(Component component, String key, String clientValidationKey, String ruleValue, IModel model) {
		String resource = component.getId() + "." + key;
		final Localizer localizer = component.getLocalizer();
		// if field defines its own message
		String message = localizer.getString(resource, component, model, "");
		// look for the more general form of message, set on the validation level
		if (Strings.isEmpty(message)) {
			message = localizer.getString(key, component, model, "");
		}
	
		String requiredJson = new StringBuffer().append("{").append(clientValidationKey).append(": ").append(ruleValue)
				.append(", messages: {").append(clientValidationKey).append(": '").append(message.replace("'", "\\'"))
				.append("'}}").toString();
	
		IModel<String> replaceModel = (IModel<String>) getReplaceModel();
		replaceModel.setObject(requiredJson);
	}

}
