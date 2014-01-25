package com.temenos.ebank.pages;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;

import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.pages.clientAquisition.step2.PafAddressPanel;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;
import com.temenos.ebank.wicketmodel.PreviousAddressWicketModelObject;

public class TestPage extends BasePage {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings({"serial", "unchecked"})
	public TestPage() {
		TestFormModel testFormModel = new TestFormModel();
		Form<TestFormModel> form = new Form<TestFormModel>("form", new CompoundPropertyModel<TestFormModel>(
				testFormModel));
		final EbankTextField mankiTextField = new EbankTextField("mankiTextField");
		form.add(addResourceLabelAndReturnBorder(mankiTextField));

		form.add(new PafAddressPanel("pafAddressPanel", new CompoundPropertyModel(
				new PropertyModel<AddressWicketModelObject>(testFormModel, "address"))));

		form.add(new PafAddressPanel("nonEmptyPreviousAddress", new CompoundPropertyModel(
				new PropertyModel<AddressWicketModelObject>(testFormModel, "nonEmptyPreviousAddress"))));

		form.add(new PafAddressPanel("nePafAddressPanel", new CompoundPropertyModel<AddressWicketModelObject>(
				new PropertyModel<AddressWicketModelObject>(testFormModel, "nonEmptyAddress"))));

		AjaxSubmitLink submitBt = new AjaxSubmitLink("testSubmit", form) {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				// break
				form.getDefaultModel().getObject();
			}

		};
		form.add(submitBt);
		add(form);
	}

	public class TestFormModel implements Serializable {
		private static final long serialVersionUID = 1L;
		private String mankiTextField = "ceva";
		private PreviousAddressWicketModelObject address;
		private PreviousAddressWicketModelObject nonEmptyPreviousAddress;
		private AddressWicketModelObject nonEmptyAddress;

		public TestFormModel() {
			nonEmptyAddress = new AddressWicketModelObject();
			nonEmptyAddress.setAdrId(new Long(0));
			nonEmptyAddress.setLine1("line1");
			nonEmptyAddress.setPostcode("posta coda");
			nonEmptyAddress.setCountry("countery");
			nonEmptyAddress.setTown("towna");
			nonEmptyPreviousAddress = new PreviousAddressWicketModelObject();
			nonEmptyPreviousAddress.setAdrId(new Long(0));
			nonEmptyPreviousAddress.setLine1("line1");
			nonEmptyPreviousAddress.setPostcode("posta coda");
			nonEmptyPreviousAddress.setCountry("country");
			nonEmptyPreviousAddress.setTown("towna");
		}

		public void setMankiTextField(String mankiTextField) {
			this.mankiTextField = mankiTextField;
		}

		public String getMankiTextField() {
			return mankiTextField;
		}

		public void setAddress(PreviousAddressWicketModelObject address) {
			this.address = address;
		}

		public PreviousAddressWicketModelObject getAddress() {
			return address;
		}

		public void setNonEmptyAddress(AddressWicketModelObject nonEmptyAddress) {
			this.nonEmptyAddress = nonEmptyAddress;
		}

		public AddressWicketModelObject getNonEmptyAddress() {
			return nonEmptyAddress;
		}

		public void setNonEmptyPreviousAddress(PreviousAddressWicketModelObject nonEmptyPreviousAddress) {
			this.nonEmptyPreviousAddress = nonEmptyPreviousAddress;
		}

		public PreviousAddressWicketModelObject getNonEmptyPreviousAddress() {
			return nonEmptyPreviousAddress;
		}

	}
}
