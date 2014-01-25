package com.temenos.ebank.common.wicket.components;

import java.util.MissingResourceException;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.validator.StringValidator.MaximumLengthValidator;
import org.apache.wicket.validation.validator.StringValidator.MinimumLengthValidator;

import com.temenos.ebank.common.wicket.WicketUtils.SUFFIX;

/**
 * A text field that is by default required, and validates its minimum/maximum length, if those values are defined (see
 * {@link SUFFIX}).
 * 
 * @author gcristescu
 */
@SuppressWarnings("unchecked")
public class EbankTextField extends TextField {
	private String id;

	/**
	 * @param id
	 *            the id of component
	 */
	public EbankTextField(String id) {
		this(id, null, false);
	}

	/**
	 * @param id
	 *            the id of component
	 * @param optional
	 *            set to true if this field is optional
	 */
	public EbankTextField(String id, Boolean optional) {
		this(id, null, optional);
	}

	/**
	 * @param id
	 *            the id of component
	 */
	public EbankTextField(String id, IModel<String> model) {
		this(id, model, false);
	}

	/**
	 * @param id
	 *            the id of component
	 * @param optional
	 *            set to true if this field is optional
	 */
	public EbankTextField(String id, IModel<String> model, Boolean optional) {
		super(id, model);

		this.id = id;
		this.setRequired(!optional);
	}

	@Override
	@SuppressWarnings("PMD.EmptyCatchBlock")
	protected void onInitialize() {
		super.onInitialize();

		try {
			int minLength = Integer.parseInt(getString(id + ".minLength"));

			this.add(MinimumLengthValidator.minimumLength(minLength));
		} catch (MissingResourceException e) {
			// nothing to see here, move along
		}
		
		try {
			int maxLength = Integer.parseInt(getString(id + ".maxLength"));

			this.add(MaximumLengthValidator.maximumLength(maxLength));
			
			add(new AttributeModifier("maxlength", true, new Model<Integer>(maxLength)));
		} catch (MissingResourceException e) {
			// nothing to see here, move along
		}
	}
}
