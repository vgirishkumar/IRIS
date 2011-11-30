package com.temenos.ebank.pages.clientAquisition.step2;

import static com.temenos.ebank.common.wicket.formValidation.DefaultTextFieldFormBehavior.INIT_DEFAULT_TEXT_JS_CALL;

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.AbstractFormValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.beans.BeanUtils;

import com.temenos.ebank.wicketmodel.AddressWicketModelObject;

/**
 * Component created for easy input of addresses by post code
 * 
 * The component holds its own model for the input elements used for searching and selecting addresses,
 * but is using parent model for displaying and saving submitted address data.
 * This component is adding itself to the AJAX targets, it switches displayed forms
 * by changing state and refreshing itself.
 * It uses an external service to bring addresses by post code.
 * 
 * @author raduf
 */
@SuppressWarnings("unchecked")
public class PafAddressPanel extends Panel {

	private static final long serialVersionUID = -9102663151661097506L;

	// part of the component's model
	private PafSearchPostCodePanel searchPostCodePanel;
	private PafSelectAddressPanel selectAddressPanel;
	private AddressPanel addressPanel;

	Panel currentPanel;

	private IModel<AddressWicketModelObject> selectAddressModel = new Model<AddressWicketModelObject>(
			new AddressWicketModelObject());

	private PafValidator validator;
	
	private PafSearchPostCodePanel getSearchPostCodePanel() {
		if (searchPostCodePanel == null) {
			searchPostCodePanel = new PafSearchPostCodePanel("searchPostCode", this);
			searchPostCodePanel.setOutputMarkupId(true);
			return searchPostCodePanel;
		} else {
			return searchPostCodePanel;
		}
	}

	private AddressPanel getAddressPanel() {
		if (addressPanel == null) {
			addressPanel = new AddressPanel("addressPanel", (IModel<AddressWicketModelObject>) getDefaultModel(), this);
			addressPanel.setOutputMarkupId(true);
			return addressPanel;
		} else {
			return addressPanel;
		}
	}

	private PafSelectAddressPanel getSelectAddressPanel() {
		if (selectAddressPanel == null) {
			selectAddressPanel = new PafSelectAddressPanel("selectAddress", selectAddressModel, this);
			selectAddressPanel.setOutputMarkupId(true);
			return selectAddressPanel;
		} else {
			return selectAddressPanel;
		}
	}

	public PafAddressPanel(String id, final IModel<AddressWicketModelObject> addressModel) {
		super(id, addressModel);
		
		add(getSearchPostCodePanel().setVisible(false));
		add(getSelectAddressPanel().setVisible(false));
		add(getAddressPanel().setVisible(false));

		setOutputMarkupId(true);

		// when loading screen with data in the model (from the db), no need for showing PAF
		if (!((AddressWicketModelObject) getDefaultModel().getObject()).isEmpty()) {
			currentPanel = getAddressPanel();
			getAddressPanel().setVisible(true);
		} else {
			currentPanel = getSearchPostCodePanel();
			getSearchPostCodePanel().setVisible(true);
		}
	}

	public void setVisiblePanel() {
		// when loading screen with data in the model (from the db), no need for showing PAF
		if (!((AddressWicketModelObject) getDefaultModel().getObject()).isEmpty()) {
			replaceCurrentPanelWith(addressPanel);
		} else {
			replaceCurrentPanelWith(searchPostCodePanel);
		}
	}

	// this method has been created for the containing address component to refresh the list
	// of addresses using its own post code ("Change Address" button)
	// switches to address list
	public void refreshAddressList(AjaxRequestTarget target) {
		switchToList(searchPostCodePanel.getNewAddressesList(((IModel<AddressWicketModelObject>) getDefaultModel())
				.getObject().getPostcode()), target);
		replaceCurrentPanelWith(getSelectAddressPanel());
		target.addComponent(this);
	}

	private void replaceCurrentPanelWith(Panel newPanel) {
		currentPanel.setVisible(false);
		currentPanel = newPanel;
		currentPanel.setVisible(true);
	}

	public void switchToSearch(AjaxRequestTarget target) {		
		replaceCurrentPanelWith(getSearchPostCodePanel());
		target.addComponent(this);
	}
	
	public void switchToList(List<AddressWicketModelObject> addressesByPostCode, AjaxRequestTarget target) {
		getSelectAddressPanel().setAddressesList(addressesByPostCode);
		replaceCurrentPanelWith(getSelectAddressPanel());
		target.addComponent(this);
	}

	public void switchToOverseas(AjaxRequestTarget target) {
		AddressWicketModelObject newAddress = new AddressWicketModelObject();
		BeanUtils.copyProperties(newAddress, getDefaultModel().getObject());
		
		//search again is always visible, comment following
		//getAddressPanel().hideSearchAgainButton();
		
		getAddressPanel().postCodeNotRequired();
		replaceCurrentPanelWith(getAddressPanel());
		target.appendJavascript(INIT_DEFAULT_TEXT_JS_CALL);
		//target.appendJavascript("ebankValidator.valid();");
		target.addComponent(this);
	}

	public void switchToAddress(AjaxRequestTarget target) {
		// ((IModel<AddressWicketModelObject>) getAddressPanel().getDefaultModel()).setObject(addressWicketModelObject);
		// instead of simply calling addressPanel.setDefaultModelObject, this "magic" trick is needed to update
		// the countries dropdown
		getAddressPanel().onModelChanged();
		replaceCurrentPanelWith(getAddressPanel());
		target.appendJavascript(INIT_DEFAULT_TEXT_JS_CALL);
		target.addComponent(this);
	}

	public void switchToCantFindLink(AjaxRequestTarget target) {
		AddressWicketModelObject newAddress = new AddressWicketModelObject();
		newAddress.setPostcode(getSearchPostCodePanel().getPostCode());

		//search again is always visible, comment following
		//getAddressPanel().hideSearchAgainButton();
		
		replaceCurrentPanelWith(getAddressPanel());
		BeanUtils.copyProperties(newAddress, getDefaultModel().getObject());
		target.addComponent(this);
	}

	public void reload(AjaxRequestTarget target) {
		target.addComponent(this);
	}

	@Override
	public void onBeforeRender() {
		// some things happen twice per submit, or twice per display in our app
		// examples: on render on the same component, resource loading, model loading if you have loadabledetachable
		// TODO: find and fix calling things twice
		//until then, check if the validator was initialised before. else, it adds two validators, two error messages
		if (validator == null) {
			Form<?> form = findParent(Form.class);
			validator = new PafValidator(this);
			form.add(validator);
		}
		super.onBeforeRender();
	}

	@SuppressWarnings("serial")
	private class PafValidator extends AbstractFormValidator {

		private PafAddressPanel panel;

		public PafValidator(PafAddressPanel panel) {
			this.panel = panel;
		}

		public FormComponent<?>[] getDependentFormComponents() {
			return new FormComponent[0];
		}

		public void validate(Form<?> form) {
			if( !panel.isVisibleInHierarchy() ){
				return;
			}
			if( currentPanel == searchPostCodePanel ){
				error(panel.getSearchPostCodePanel().getPostCodeTxt(), "PafAddressNotSearched");
			}
			else if( currentPanel == selectAddressPanel ){
				error(panel.getSelectAddressPanel().getReturnedAddresses(), "PafAddressNotSelected");
			}
		}

	}

}
