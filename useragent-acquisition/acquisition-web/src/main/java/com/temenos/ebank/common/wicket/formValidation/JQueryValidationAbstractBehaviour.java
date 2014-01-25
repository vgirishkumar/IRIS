package com.temenos.ebank.common.wicket.formValidation;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Classes;

/**
 * @author raduf
 *         Abstract class for applying both server and client side validations, but only for form components.
 *         Not used by radios and check boxes.
 *         The server side validation is implemented by inheriting classes in the addServerSideValidator method
 *         The client side validation should be specified in the inheriting class constructor under the form of Json
 *         rules and messages
 */
public abstract class JQueryValidationAbstractBehaviour extends JQueryBaseValidationAbstractBehaviour {

	private static final long serialVersionUID = 1L;
	private FormComponent<?> mComponent;

	/**
	 * Creates an AttributeModifier that appends the appendModel's value to the current value of the
	 * attribute, and will add the attribute when it is not there already.
	 * 
	 * @param appendModel
	 *            the model supplying the value to append
	 */
	public JQueryValidationAbstractBehaviour(IModel<?> appendModel) {
		super(appendModel);
	}

	@Override
	public final void bind(Component component) {
		super.bind(component);
		checkComponentIsFormComponent(component);
		mComponent = (FormComponent<?>) component;

		addServerSideValidator(mComponent);
	}

	protected final void checkComponentIsFormComponent(Component component) {
		if ((component instanceof FormComponent) == false) {
			throw new IllegalArgumentException("This behavior [" + Classes.simpleName(getClass())
					+ "] can only be added to a FormComponent");
		}
	}

	@SuppressWarnings("rawtypes")
	protected abstract void addServerSideValidator(FormComponent component);

	@Override
	public void beforeRender(Component component) {
		FormComponent<?> cast = (FormComponent<?>) component;
		IModel<String> messageModel = newMessageModel(cast);
		if (messageModel != null) {
			setClientValidationMessage(cast, propertiesFileKey, clientValidationKey, clientRuleValue, messageModel);
		}
	}

	public abstract IModel<String> newMessageModel(FormComponent<?> component);

}
