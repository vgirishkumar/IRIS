package com.temenos.ebank.pages.clientAquisition.step2;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.springframework.beans.BeanUtils;

import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;

public class PafSelectAddressPanel extends Panel {
	private static final long serialVersionUID = 1L;

	Form<PafAddressPanel> selectAddressForm;
	private DropDownChoice<AddressWicketModelObject> returnedAddresses;
	private List<AddressWicketModelObject> addressesList = new ArrayList<AddressWicketModelObject>();

	@SuppressWarnings("serial")
	public PafSelectAddressPanel(String id, final IModel<AddressWicketModelObject> addressModel,
			final PafAddressPanel parentPanel) {
		super(id, addressModel);

		// select the address
		selectAddressForm = new Form<PafAddressPanel>("selectAddressForm");

		returnedAddresses = new EbankDropDownChoice<AddressWicketModelObject>("returnedAddresses", addressModel, addressesList,
				new AddressListChoiceRenderer());
		returnedAddresses.setRequired(true);
		selectAddressForm.add(addResourceLabelAndReturnBorder(returnedAddresses));

		AjaxSubmitLink selectAddressBt = new AjaxSubmitLink("selectAddressBt") {
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// this is the only way that model data can be populated in a repeater's model - by accessing
				// directly the model object and setting each field
				BeanUtils.copyProperties(addressModel.getObject(), parentPanel.getDefaultModelObject());
				parentPanel.switchToAddress(target);
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.prependJavascript("ebankValidator.form(); ");
			}
		};
		selectAddressForm.add(selectAddressBt);
		selectAddressForm.add(new AjaxLink<String>("cantFindLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				parentPanel.switchToCantFindLink(target);
			}
		});
		add(selectAddressForm);

	}

	public List<AddressWicketModelObject> getAddressesList() {
		return addressesList;
	}

	public void setAddressesList(List<AddressWicketModelObject> addressesList) {
		this.addressesList = addressesList;
		returnedAddresses.setChoices(addressesList);
	}

	@SuppressWarnings("serial")
	private class AddressListChoiceRenderer implements IChoiceRenderer<AddressWicketModelObject> {

		public String getDisplayValue(AddressWicketModelObject address) {
			//TODO: display the entire name of the country in the list
/*			Country country = serviceCountries.getCountry(address.getCountry(), "en");
			String countryName = country != null ? (country.getLabel() != null ? country.getLabel() : address
					.getCountry()) : address.getCountry();
*/
			String countryName = address.getCountry();
			
			String delim = ", ";
			StringBuffer addressBuffer = new StringBuffer(address.getLine1())
					.append(address.getLine2() != null ? delim + address.getLine2() : "").append(delim)
					.append(address.getTown()).append(delim).append(countryName);

			return addressBuffer.toString();
		}

		public String getIdValue(AddressWicketModelObject address, int index) {
			return Integer.valueOf(index).toString();
		}
	}

	public DropDownChoice<AddressWicketModelObject> getReturnedAddresses() {
		return returnedAddresses;
	}

}
