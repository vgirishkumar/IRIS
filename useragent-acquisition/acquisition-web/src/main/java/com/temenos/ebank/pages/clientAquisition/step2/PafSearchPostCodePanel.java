package com.temenos.ebank.pages.clientAquisition.step2;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;
import static com.temenos.ebank.services.interfaces.thirdParty.PafResultCode.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.beans.BeanUtils;

import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.domain.Address;
import com.temenos.ebank.services.interfaces.thirdParty.IServicePafAddresses;
import com.temenos.ebank.services.interfaces.thirdParty.PafAddressResult;
import com.temenos.ebank.services.interfaces.thirdParty.PafResultCode;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;
import com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper;

public class PafSearchPostCodePanel extends Panel {
	private static final long serialVersionUID = 1L;

	@SpringBean(name = "servicePafAddresses")
	private IServicePafAddresses servicePafAddresses;

	@SpringBean(name = "addressDomainToWicketMapper")
	private IDomainToWicketMapper<Address, AddressWicketModelObject> domainToWicketMapper;

	final Label pafResultLabel = new Label("pafResult", new ResourceModel("ALERT_0011"));

	Form<PafAddressPanel> searchAddressesForm;
	// part of the component's model
	private String postCode;
	private String houseNoNm;
	private AjaxLink<?> overseasLink;

	private EbankTextField postCodeTxt;

	@SuppressWarnings("serial")
	public PafSearchPostCodePanel(String id, final PafAddressPanel parentPanel) {
		super(id);

		searchAddressesForm = new Form<PafAddressPanel>("searchPostCodeForm",
				new CompoundPropertyModel<PafAddressPanel>(this));
		
		searchAddressesForm.add(pafResultLabel.setVisible(false));
		//searchAddressesForm.add(pafResultLabel);

		postCodeTxt = new EbankTextField("postCode",
				new PropertyModel<String>(this, "postCode"));
		searchAddressesForm.add(addResourceLabelAndReturnBorder(postCodeTxt));

		// house number or name
		EbankTextField houseNoNmTextField = new EbankTextField("houseNoNm", new PropertyModel<String>(this,
				"houseNoNm"), true);
		searchAddressesForm.add(addResourceLabelAndReturnBorder(houseNoNmTextField));

		AjaxSubmitLink searchPostCodeBt = new AjaxSubmitLink("searchPostCodeBt") {
			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				PafAddressResult pafResult = servicePafAddresses.getAddressByPostCode(postCode, houseNoNm);
				PafResultCode pafErrorCode = pafResult.getErrorCode();
				if (pafErrorCode.equals(OK_RESULT)) {
					List<AddressWicketModelObject> addressesByPostCode = getModelObjectAddresses(pafResult);
					if (pafResultLabel.isVisible()) {
						pafResultLabel.setVisible(false);
					}
					if (addressesByPostCode.size() == 0) {
						pafResultLabel.setDefaultModel(new ResourceModel(pafErrorCode.toString()));
						pafResultLabel.setVisible(true);
						target.addComponent(PafSearchPostCodePanel.this);
					} else if (addressesByPostCode.size() == 1) {
						// we only got one address, no passing through the list state

						// this is the only way that model data can be populated in a repeater's model - by accessing
						// directly the model object and setting each field
						BeanUtils.copyProperties(addressesByPostCode.get(0), parentPanel.getDefaultModelObject());
						// this is working everywhere else but not in our repeater view
						// ((IModel<AddressWicketModelObject>)parentPanel.getDefaultModel()).setObject(addressesByPostCode.get(0));
						parentPanel.switchToAddress(target);
					} else if (addressesByPostCode.size() > 1) {
						// returnedAddresses.setChoices(addressesByPostCode);
						parentPanel.switchToList(addressesByPostCode, target);
					}
				}
				else{
					pafResultLabel.setDefaultModel(new ResourceModel(pafErrorCode.toString()));
					pafResultLabel.setVisible(true);
					target.addComponent(PafSearchPostCodePanel.this);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget target, Form<?> form) {
				target.prependJavascript("ebankValidator.form(); ");
			}
		};
		searchAddressesForm.add(searchPostCodeBt);

		overseasLink = new AjaxLink<Object>("overseasLink") {
			@Override
			public void onClick(AjaxRequestTarget target) {
				parentPanel.switchToOverseas(target);
			}
		};
		searchAddressesForm.add(overseasLink);
		add(searchAddressesForm);

	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setHouseNoNm(String houseNoNm) {
		this.houseNoNm = houseNoNm;
	}

	public String getHouseNoNm() {
		return houseNoNm;
	}

	public List<AddressWicketModelObject> getNewAddressesList(String addrPostCode) {
		return getModelObjectAddresses(servicePafAddresses.getAddressByPostCode(addrPostCode, houseNoNm));
	}
	public List<AddressWicketModelObject> getModelObjectAddresses( PafAddressResult addressResult ) {
		List<AddressWicketModelObject> list = new ArrayList<AddressWicketModelObject>();
		for (Address address : addressResult.getAddresses())
			list.add(domainToWicketMapper.domain2Wicket(address));
		return list;
	}

	public EbankTextField getPostCodeTxt() {
		return postCodeTxt;
	}

}
