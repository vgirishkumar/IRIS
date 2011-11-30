package com.temenos.ebank.common.wicket.components;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import com.temenos.ebank.common.wicket.WicketUtils;
import com.temenos.ebank.common.wicket.validators.CountryCodeValidator;
import com.temenos.ebank.common.wicket.validators.PhoneNumberValidator;
import com.temenos.ebank.common.wicket.validators.PhoneWithoutPrefixValidator;

@SuppressWarnings("PMD.DontUseTextField")
public class PhoneAndPrefix extends FormComponentPanel<String> implements CompositeFormComponent {

	private static final long serialVersionUID = -1860884375002626999L;

	private String prefix;
	private String phoneNumber;

	private final TextField<String> prefixField;
	private final TextField<String> phoneNumberField;

	public PhoneAndPrefix(String id, IModel<String> model) {
		this(id, model, false);
	}

	public PhoneAndPrefix(String id, IModel<String> model, Boolean readOnly) {
		super(id, model);

		setType(String.class);

		prefixField = new TextField<String>("prefix", new PropertyModel<String>(this, "prefix"));
		if (readOnly) {
			prefixField.add(new SimpleAttributeModifier("readonly", "readonly"));
		}
		add(prefixField.add(new CountryCodeValidator()));

		phoneNumberField = new TextField<String>("phoneNumber", new PropertyModel<String>(this, "phoneNumber"));
		if (readOnly) {
			phoneNumberField.add(new SimpleAttributeModifier("readonly", "readonly"));
		}
		add(phoneNumberField.add(new PhoneWithoutPrefixValidator()));

		add(new PhoneNumberValidator());
	}

	@Override
	protected void onBeforeRender() {
		getFieldsFromModel();

		super.onBeforeRender();
	}

	public void getFieldsFromModel() {
		String modelObject = getModelObject();
		int pos;
		
		if ((modelObject != null) && ((pos = modelObject.indexOf(" ")) > -1)) {
			// not using DialCode anymore
			prefix = modelObject.substring(1, pos);
			phoneNumber = modelObject.substring(pos + 1);
		}
	}

	@Override
	protected void convertInput() {
		if (WicketUtils.isFormProcessingEnabled(this)) {
			prefix = prefixField.getConvertedInput();
			phoneNumber = phoneNumberField.getConvertedInput();
		} else {
			prefix = prefixField.getInput();
			phoneNumber = phoneNumberField.getInput();
		}
		phoneNumber = StringUtils.stripStart(phoneNumber, "0");

		String phoneAndPrefix = StringUtils
				.defaultIfEmpty(StringUtils.join(new String[] { prefix, " ", phoneNumber }), null);

		if (!StringUtils.isBlank(phoneAndPrefix)) {
			setConvertedInput("+" + phoneAndPrefix);
		} else {
			setConvertedInput(null);
		}
	}

	@Override
	public String getInput() {
		return prefixField.getInput() + phoneNumberField.getInput();
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public FormComponent<?> getFirstInput() {
		return prefixField;
	}
}
