package com.temenos.ebank.pages.clientAquisition.step2;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;
import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnLabel;
import static com.temenos.ebank.common.wicket.formValidation.DefaultTextFieldFormBehavior.INIT_DEFAULT_TEXT_JS_CALL;

import java.util.ArrayList;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.temenos.ebank.common.wicket.AddClientValidationVisitor;
import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.common.wicket.components.InfoPanel;
import com.temenos.ebank.common.wicket.components.PhoneAndPrefix;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.ContactDetailsModelObject;
import com.temenos.ebank.wicketmodel.PreviousAddressWicketModelObject;

@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class ContactDetailsPanel extends Panel {

	private static final long serialVersionUID = 5683281999606780763L;

	private PhoneAndPrefix homePhone;

	public PhoneAndPrefix getHomePhone() {
		return homePhone;
	}

	private PhoneAndPrefix workPhone;

	public PhoneAndPrefix getWorkPhone() {
		return workPhone;
	}

	private PhoneAndPrefix mobilePhone;

	public PhoneAndPrefix getMobilePhone() {
		return mobilePhone;
	}
	
	private PhoneAndPrefix faxNumber;

	public PhoneAndPrefix getFaxNumber() {
		return faxNumber;
	}

	private EbankTextField emailAddress;

	public EbankTextField getEmailAddress() {
		return emailAddress;
	}

	private RepeatingView previousAddresses;
	private WebMarkupContainer previousAddressContainer;
	private String lastAddedMarkupId = "";
	private Component lastAddedItem;
	private IModel<ContactDetailsModelObject> model;
	private Boolean lastIsEmpty = false;

	private static final String OTHER_RESIDENTIAL_STATUS = "OTHER";

	public ContactDetailsPanel(String id, final IModel<ContactDetailsModelObject> model, final boolean isJoint,
			IModel<ApplicationWicketModelObject> awm) {
		super(id, model);

		this.model = model;
		
		add(new WebMarkupContainer("jointContactSpacer").setVisible(isJoint));

		homePhone = new PhoneAndPrefix("homePhone", new PropertyModel(model, "homePhone"));
		workPhone = new PhoneAndPrefix("workPhone", new PropertyModel(model, "workPhone"));
		mobilePhone = new PhoneAndPrefix("mobilePhone", new PropertyModel(model, "mobilePhone"), true);
		mobilePhone.setRequired(true);
		faxNumber = new PhoneAndPrefix("faxNumber", new PropertyModel(model, "faxNumber"));
		add(addResourceLabelAndReturnBorder(homePhone));
		add(addResourceLabelAndReturnBorder(workPhone));
		add(addResourceLabelAndReturnBorder(mobilePhone));
		add(addResourceLabelAndReturnBorder(faxNumber).setVisible(false));
		emailAddress = new EbankTextField("emailAddress");
		emailAddress.add(new SimpleAttributeModifier("readonly", "readonly"));
		add(addResourceLabelAndReturnBorder(emailAddress));
		IGenericChoiceRenderer contactChoiceRenderer = GenericChoiceRendererFactory.getRenderer(Choices.contactChoice, this);
		add(addResourceLabelAndReturnBorder(new EbankDropDownChoice("preferredContactMethod", contactChoiceRenderer.getChoices(), contactChoiceRenderer).setRequired(true)));

		IGenericChoiceRenderer residentialStatusRenderer = GenericChoiceRendererFactory.getRenderer(
				Choices.residentialStatus, this);
		final EbankDropDownChoice currentResidentialStatusDDC = new EbankDropDownChoice("residentialStatus", residentialStatusRenderer.getChoices(), residentialStatusRenderer);
		add(addResourceLabelAndReturnBorder(currentResidentialStatusDDC.setRequired(true)));

		// ajax call for showing the field for the other residential status
		final Border otherResidentialStatusBorder = addResourceLabelAndReturnBorder(new EbankTextField(
				"otherResidentialStatus"));
		otherResidentialStatusBorder.setVisible(false);
		if (model.getObject().getResidentialStatus() != null) {
			otherResidentialStatusBorder.setVisible(model.getObject().getResidentialStatus()
					.equals(OTHER_RESIDENTIAL_STATUS));
		}
		otherResidentialStatusBorder.setOutputMarkupPlaceholderTag(true);
		add(otherResidentialStatusBorder);
		OnChangeAjaxBehavior onResidentialStatusChangeAjaxBehaviour = new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (currentResidentialStatusDDC.getConvertedInput().equals(OTHER_RESIDENTIAL_STATUS)
						&& !otherResidentialStatusBorder.isVisible()) {
					otherResidentialStatusBorder.setVisible(true);
					target.addComponent(otherResidentialStatusBorder);
					target.appendJavascript(INIT_DEFAULT_TEXT_JS_CALL);
				} else if (!currentResidentialStatusDDC.getConvertedInput().equals(OTHER_RESIDENTIAL_STATUS)
						&& otherResidentialStatusBorder.isVisible()) {
					otherResidentialStatusBorder.setVisible(false);
					model.getObject().setOtherResidentialStatus("");
					target.addComponent(otherResidentialStatusBorder);
				}
			}
		};
		currentResidentialStatusDDC.add(onResidentialStatusChangeAjaxBehaviour);

		// on the second applicant, ajax check if the second and first applicant addresses are the same
		// first, show/hide container
		WebMarkupContainer secondResidentialSameAsFirstResidentialContainer = (WebMarkupContainer) new WebMarkupContainer(
				"secondResidentialSameAsFirstResidentialContainer");
		secondResidentialSameAsFirstResidentialContainer.setVisible(isJoint);
		// then, build radio group
		final RadioGroup secondResidentialSameAsFirstResidentialRG = new RadioGroup(
				"secondResidentialSameAsFirstResidential", new PropertyModel(awm,
						"secondResidentialSameAsFirstResidential"));
		Radio secondResidentialSameAsFirstResidentialYes = new Radio("secondResidentialSameAsFirstResidentialYes",
				new Model(true));
		secondResidentialSameAsFirstResidentialRG
				.add(addResourceLabelAndReturnLabel(secondResidentialSameAsFirstResidentialYes));
		secondResidentialSameAsFirstResidentialRG.add(secondResidentialSameAsFirstResidentialYes);
		Radio secondResidentialSameAsFirstResidentialNo = new Radio("secondResidentialSameAsFirstResidentialNo",
				new Model(false));
		secondResidentialSameAsFirstResidentialRG
				.add(addResourceLabelAndReturnLabel(secondResidentialSameAsFirstResidentialNo));
		secondResidentialSameAsFirstResidentialRG.add(secondResidentialSameAsFirstResidentialNo);
		secondResidentialSameAsFirstResidentialContainer
				.add(addResourceLabelAndReturnBorder(secondResidentialSameAsFirstResidentialRG));
		add(secondResidentialSameAsFirstResidentialContainer);
		// last, show/hide with ajax
		final WebMarkupContainer addressContainer = (WebMarkupContainer) new WebMarkupContainer("addressContainer")
				.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		addressContainer.setVisible(!isJoint
				|| (isJoint && !awm.getObject().getSecondResidentialSameAsFirstResidential()));
		final PafAddressPanel addressPanel = new PafAddressPanel("address", new CompoundPropertyModel(
				new PropertyModel(model, "address")));
		addressContainer.add(addressPanel);
		add(addressContainer);
		secondResidentialSameAsFirstResidentialRG.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean isSame = (Boolean) secondResidentialSameAsFirstResidentialRG.getConvertedInput();
				if (!addressContainer.isVisible() && !isSame) {
					model.getObject().setAddress(new AddressWicketModelObject());
					addressPanel.setVisiblePanel();
				}
				addressContainer.setVisible(!isSame);
				if (!isSame) {
					target.appendJavascript(INIT_DEFAULT_TEXT_JS_CALL);
				}
				target.addComponent(addressContainer);
			}
		});

		final YearsAndMonths yearsAndMonthsWithAddress = new YearsAndMonths("yearsAndMonthsWithAddress",
				new PropertyModel(model, "residentialAdrPeriod"));
		yearsAndMonthsWithAddress.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		yearsAndMonthsWithAddress.setRequired(true);
		add(addResourceLabelAndReturnBorder(yearsAndMonthsWithAddress));
		yearsAndMonthsWithAddress.addShowHidePreviousAddressesListBehavior(this);

		previousAddressContainer = new WebMarkupContainer("previousAddressContainer");
		previousAddressContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		previousAddresses = new RepeatingView("previousAddresses", new CompoundPropertyModel(new PropertyModel(model,
				"previousAddresses")));
		previousAddressContainer.add(previousAddresses);
		for (PreviousAddressWicketModelObject previousAddress : model.getObject().getPreviousAddresses()) {
			buildPreviousAddressItem(previousAddress);
		}
		previousAddressContainer.setVisible(model.getObject().isPreviousAddressesListNeeded());
		add(previousAddressContainer);

		// if correspondence address is same as address, no need to fill out the address
		final RadioGroup residentialSameAsCorrespondenceRG = new RadioGroup("isCorrespondenceAddressSameAsResidential",
				new PropertyModel(model, "isCorrespondenceAddressSameAsResidential"));
		Radio residentialSameAsCorrespondenceYes = new Radio("residentialSameAsCorrespondenceYes", new Model(true));
		residentialSameAsCorrespondenceRG.add(addResourceLabelAndReturnLabel(residentialSameAsCorrespondenceYes));
		residentialSameAsCorrespondenceRG.add(residentialSameAsCorrespondenceYes);
		Radio residentialSameAsCorrespondenceNo = new Radio("residentialSameAsCorrespondenceNo", new Model(false));
		residentialSameAsCorrespondenceRG.add(addResourceLabelAndReturnLabel(residentialSameAsCorrespondenceNo));
		residentialSameAsCorrespondenceRG.add(residentialSameAsCorrespondenceNo);
		add(addResourceLabelAndReturnBorder(residentialSameAsCorrespondenceRG));

		// if second applicant, then check if the correspondence is same as first applicant's correspondence
		final RadioGroup secondCorrespondenceSameAsFirstRG = new RadioGroup("secondCorrespondenceSameAsFirst",
				new PropertyModel(awm, "secondCorrespondenceSameAsFirst"));
		secondCorrespondenceSameAsFirstRG.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true)
				.setRenderBodyOnly(false);
		final Radio secondCorrespondenceSameAsFirstYes = new Radio("secondCorrespondenceSameAsFirstYes",
				new Model(true));
		secondCorrespondenceSameAsFirstRG.add(addResourceLabelAndReturnLabel(secondCorrespondenceSameAsFirstYes));
		secondCorrespondenceSameAsFirstRG.add(secondCorrespondenceSameAsFirstYes.setOutputMarkupId(true)
				.setRenderBodyOnly(false));
		final Radio secondCorrespondenceSameAsFirstNo = new Radio("secondCorrespondenceSameAsFirstNo", new Model(false));
		secondCorrespondenceSameAsFirstRG.add(addResourceLabelAndReturnLabel(secondCorrespondenceSameAsFirstNo));
		secondCorrespondenceSameAsFirstRG.add(secondCorrespondenceSameAsFirstNo.setOutputMarkupId(true)
				.setRenderBodyOnly(false));

		final WebMarkupContainer secondCorrespondenceSameAsFirstCorrespondenceContainer = new WebMarkupContainer(
				"secondCorrespondenceSameAsFirstCorrespondenceContainer");
		secondCorrespondenceSameAsFirstCorrespondenceContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(
				true);
		secondCorrespondenceSameAsFirstCorrespondenceContainer
				.add(addResourceLabelAndReturnBorder(secondCorrespondenceSameAsFirstRG));

		final WebMarkupContainer correspondenceAddressContainer = new WebMarkupContainer(
				"correspondenceAddressContainer");
		correspondenceAddressContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		final PafAddressPanel correspondencePafAddressPanel = new PafAddressPanel("correspondenceAddress",
				new CompoundPropertyModel(new PropertyModel(model, "correspondenceAddress")));

		InfoPanel debitCardInfoPanel = new InfoPanel("debitCardToCorrespondence");
		// FIXME: show this only if CA is opened along; and create tests for it
		if (!awm.getObject().getProductRef().equals(ProductType.REGULAR_SAVER)) {
			debitCardInfoPanel.setVisible(false);
		}
		correspondenceAddressContainer.add(debitCardInfoPanel);

		correspondenceAddressContainer.add(correspondencePafAddressPanel);

		final WebMarkupContainer correspondenceContainer = new WebMarkupContainer("correspondenceContainer");
		correspondenceContainer.setOutputMarkupId(true).setOutputMarkupPlaceholderTag(true);
		if (model.getObject().getIsCorrespondenceAddressSameAsResidential() != null) {
			correspondenceContainer.setVisible(!model.getObject().getIsCorrespondenceAddressSameAsResidential());
		}
		correspondenceContainer.add(secondCorrespondenceSameAsFirstCorrespondenceContainer.setVisible(isJoint));
		Boolean isCorrespondenceContainerVisible = !isJoint
				|| (isJoint && !model.getObject().getIsCorrespondenceAddressSameAsResidential() && !awm.getObject()
						.getSecondCorrespondenceSameAsFirst());
		correspondenceContainer.add(correspondenceAddressContainer.setVisible(isCorrespondenceContainerVisible));
		add(correspondenceContainer);

		residentialSameAsCorrespondenceRG.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean isSame = (Boolean) residentialSameAsCorrespondenceRG.getConvertedInput();
				// if correspondence container was not visible, but now it will be displayed,
				// then reset the correspondence address model
				if (!correspondenceContainer.isVisible() && !isSame) {
					model.getObject().setCorrespondenceAddress(new AddressWicketModelObject());
					correspondencePafAddressPanel.setVisiblePanel();
				}
				correspondenceContainer.setVisible(!isSame);
				// init default texts for displayed fields after ajax call
				if (!isSame) {
					target.appendJavascript(INIT_DEFAULT_TEXT_JS_CALL);
				}

				correspondenceContainer.add(secondCorrespondenceSameAsFirstCorrespondenceContainer);
				correspondenceContainer.add(correspondenceAddressContainer);
				target.addComponent(correspondenceContainer);
			}
		});

		secondCorrespondenceSameAsFirstRG.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean isSame = (Boolean) secondCorrespondenceSameAsFirstRG.getConvertedInput();
				if (!correspondenceAddressContainer.isVisible() && !isSame) {
					model.getObject().setCorrespondenceAddress(new AddressWicketModelObject());
					correspondencePafAddressPanel.setVisiblePanel();
				}
				correspondenceAddressContainer.setVisible(!isSame);
				if (!isSame) {
					target.appendJavascript(INIT_DEFAULT_TEXT_JS_CALL);
				}
				target.addComponent(correspondenceAddressContainer);
			}
		});

	}

	private Component buildPreviousAddressItem(PreviousAddressWicketModelObject previousAddress) {
		WebMarkupContainer item = new WebMarkupContainer(previousAddresses.newChildId());
		item.setOutputMarkupId(true);
		previousAddresses.add(item);
		item.add(new PafAddressPanel("previousAddressRepeated", new CompoundPropertyModel(previousAddress)));
		// item.add(new AddressPanel("previousAddressRepeated", new CompoundPropertyModel(previousAddress)));
		YearsAndMonths previousDuration = new YearsAndMonths("duration", new PropertyModel(previousAddress, "duration"));
		previousDuration.setRequired(true);
		previousDuration.addPreviousAddressBehavior(this, item.getMarkupId());
		item.add(addResourceLabelAndReturnBorder(previousDuration));
		FormComponent.visitFormComponentsPostOrder(item, new AddClientValidationVisitor());
		lastAddedMarkupId = item.getMarkupId();
		lastAddedItem = item;
		return item;
	}

	public void previousAddressDurationChanged(AjaxRequestTarget target, String changedMarkupId) {

		// if item that changed is the last one, it means now we are open for new forms if less then three years
		// and if model decides, a new form is required for inserting another previous address
		if (!lastIsEmpty && model.getObject().isAnotherPreviousAddressNeeded()) {
			// build item and add item to my repeater
			PreviousAddressWicketModelObject previousAddress = new PreviousAddressWicketModelObject();
			Component item = buildPreviousAddressItem(previousAddress);

			// add new previous address to model
			model.getObject().getPreviousAddresses().add(previousAddress);

			// create element in dom
			target.prependJavascript(String.format(
					"var item=document.createElement('%s');item.id='%s';Wicket.$('%s').appendChild(item);", "span",
					item.getMarkupId(), previousAddressContainer.getMarkupId()));

			// add to target
			target.addComponent(item);

			// call javascript to init default text plugin
			target.appendJavascript(INIT_DEFAULT_TEXT_JS_CALL);
			//target.prependJavascript("ebankValidator.form();alert('4');");
			lastIsEmpty = true;
		}

		if (lastAddedMarkupId.equals(changedMarkupId)) {
			lastIsEmpty = false;
		}

		// another item - not the last one - was changed, it means we can remove the last empty item if
		// we have a total of more than three years
		if (lastIsEmpty && !model.getObject().isAnotherPreviousAddressNeeded()) {
			lastIsEmpty = false;
			// remove element from dom
			target.prependJavascript(String.format("Wicket.$('%s').removeChild(Wicket.$('%s'));",
					previousAddressContainer.getMarkupId(), lastAddedMarkupId));
			model.getObject().getPreviousAddresses().remove(model.getObject().getPreviousAddresses().size() - 1);
			previousAddresses.remove(lastAddedItem);
		}
	}

	public void currentAddressDurationChanged(AjaxRequestTarget target) {
		if (model.getObject().isPreviousAddressesListNeeded() && !previousAddressContainer.isVisible()) {
			// build item and add item to my repeater
			PreviousAddressWicketModelObject previousAddress = new PreviousAddressWicketModelObject();
			lastAddedItem = buildPreviousAddressItem(previousAddress);
			lastIsEmpty = true;

			// add new previous address to model
			model.getObject().getPreviousAddresses().add(previousAddress);

			previousAddressContainer.setVisible(true);
			target.appendJavascript(INIT_DEFAULT_TEXT_JS_CALL);
			//target.prependJavascript("ebankValidator.form();alert('5');");
			target.addComponent(previousAddressContainer);
		} else if (!model.getObject().isPreviousAddressesListNeeded() && previousAddressContainer.isVisible()) {
			model.getObject().setPreviousAddresses(new ArrayList<PreviousAddressWicketModelObject>(0));

			if( lastIsEmpty ){
				previousAddresses.remove(lastAddedItem);
			}
			previousAddressContainer.setVisible(false);
			//target.prependJavascript("ebankValidator.form();alert('6');");
			target.addComponent(previousAddressContainer);
		}

	}
}
