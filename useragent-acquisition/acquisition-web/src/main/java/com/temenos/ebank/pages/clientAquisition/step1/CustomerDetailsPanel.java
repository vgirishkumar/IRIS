/**
 * 
 */
package com.temenos.ebank.pages.clientAquisition.step1;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;
import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnLabel;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.validation.EqualInputValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.IPartiallySavable;
import com.temenos.ebank.common.wicket.WicketUtils;
import com.temenos.ebank.common.wicket.choiceRenderers.Choices;
import com.temenos.ebank.common.wicket.choiceRenderers.GenericChoiceRendererFactory;
import com.temenos.ebank.common.wicket.choiceRenderers.IGenericChoiceRenderer;
import com.temenos.ebank.common.wicket.components.DateChooser;
import com.temenos.ebank.common.wicket.components.DisableCopyPasteFieldBehavior;
import com.temenos.ebank.common.wicket.components.EbankDropDownChoice;
import com.temenos.ebank.common.wicket.components.EbankTextField;
import com.temenos.ebank.common.wicket.components.PhoneAndPrefix;
import com.temenos.ebank.common.wicket.formValidation.EmailValidationBehavior;
import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.domain.ConfigParamTable.INTEGER;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam;
import com.temenos.ebank.wicketmodel.CustomerWicketModel;

/**
 * Customer details
 * 
 * @author vionescu
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class CustomerDetailsPanel extends Panel implements IPartiallySavable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//private boolean isFirstCustomer = false;
	
	@SpringBean(name = "serviceConfigParam")
	private IServiceConfigParam serviceConfigParam;

	private static List<String> requiredFieldsIdsForSave = Arrays.asList("firstName", "lastName", "emailAddress", "emailAddress2", "dateOfBirth", "mobilePhone");

	public CustomerDetailsPanel(String id, EbankWizardStep s, final IModel<CustomerWicketModel> model,
			boolean isFirstCustomer) {
		super(id, model);
		//this.showConfirmationEmail = showConfirmationEmail;
		//this.isFirstCustomer = isFirstCustomer;
		CustomerWicketModel c = model.getObject();

		IGenericChoiceRenderer jointRelationshipRenderer = GenericChoiceRendererFactory.getRenderer(
				Choices.jointRelationship, this);
		EbankDropDownChoice cmbJointRelationship = new EbankDropDownChoice("jointRelationship",
				jointRelationshipRenderer.getChoices(), jointRelationshipRenderer);
		cmbJointRelationship.setRequired(true);
		Border border = addResourceLabelAndReturnBorder(cmbJointRelationship);
		border.setVisible(!isFirstCustomer);
		add(border);

		IGenericChoiceRenderer titlesRenderer = GenericChoiceRendererFactory.getRenderer(Choices.title, this);
		EbankDropDownChoice cmbTitle = new EbankDropDownChoice("title", titlesRenderer.getChoices(), titlesRenderer);
		cmbTitle.setRequired(true);
		// add(cmbTitle);
		border = addResourceLabelAndReturnBorder(cmbTitle);
		add(border);
		FormComponent txtFirstName = new EbankTextField("firstName");
		add(WicketUtils.addResourceLabelAndReturnBorder(txtFirstName));

		FormComponent txtLastName = new EbankTextField("lastName");
		add(WicketUtils.addResourceLabelAndReturnBorder(txtLastName));		

		// TODO: Trebuie sa se poata face show/hide si din javascript, eventual cu id'ul lui WEbMArkupContainer
		final WebMarkupContainer groupPreviousNameWebContainer = new WebMarkupContainer("groupPreviousName");
		groupPreviousNameWebContainer.setVisible(c != null && c.getPreviousName() != null);
		groupPreviousNameWebContainer.setOutputMarkupPlaceholderTag(true);
		add(groupPreviousNameWebContainer);
		
		FormComponent txtPreviousName = new EbankTextField("previousName");
		txtPreviousName.setOutputMarkupPlaceholderTag(true);
		border = addResourceLabelAndReturnBorder(txtPreviousName);
		groupPreviousNameWebContainer.add(border);

		final RadioGroup formerNameRG = new RadioGroup("hasFormerName", new Model<Boolean>(c != null
				&& c.getPreviousName() != null));
		formerNameRG.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				boolean hasOtherName = (Boolean) formerNameRG.getModelObject();
				groupPreviousNameWebContainer.setVisible(hasOtherName);
				model.getObject().setPreviousName(null);
				target.addComponent(groupPreviousNameWebContainer);
			}
		});
		Radio formerNameYes = new Radio("othernameYes", new Model(true));
		formerNameRG.add(addResourceLabelAndReturnLabel(formerNameYes));
		formerNameRG.add(formerNameYes);

		Radio formerNameNo = new Radio("othernameNo", new Model(false));
		formerNameRG.add(addResourceLabelAndReturnLabel(formerNameNo));
		formerNameRG.add(formerNameNo);
		add(addResourceLabelAndReturnBorder(formerNameRG));

		// DateYearTextField dateOfBirth = new DateYearTextField("dateOfBirth",
		// new CompoundPropertyModel<Date>(new PropertyModel<Date>(model, "dateOfBirth")),
		// /*((SimpleDateFormat)DateFormat.getDateInstance(DateFormat.SHORT, getLocale())).toPattern()*/
		// "dd-MM-yyyy");
		// dateOfBirth.setRequired(true);
		// border = addResourceLabelAndReturnBorder(dateOfBirth);
		// add(border);

		//config value for start year
		Integer minYearForBirthDate = serviceConfigParam.getConfigParamTable().get(INTEGER.MIN_BIRTH_DATE_YEAR);
		DateChooser dc = new DateChooser("dateOfBirth", new CompoundPropertyModel<Date>(new PropertyModel<Date>(model,
				"dateOfBirth")), minYearForBirthDate);
		dc.setRequired(true);
		border = addResourceLabelAndReturnBorder(dc);
		add(border);

		RadioGroup genderRG = new RadioGroup("gender", new PropertyModel(model, "gender"));
		genderRG.setRenderBodyOnly(false);
		genderRG.setRequired(true);
		Radio genderMale = new Radio("genderMale", new Model("M"));

		genderRG.add(addResourceLabelAndReturnLabel(genderMale));
		genderRG.add(genderMale);
		Radio genderFemale = new Radio("genderFemale", new Model("F"));
		genderRG.add(addResourceLabelAndReturnLabel(genderFemale));
		genderRG.add(genderFemale);
		border = addResourceLabelAndReturnBorder(genderRG);
		add(border);

		IGenericChoiceRenderer nationalityRenderer = GenericChoiceRendererFactory.getRenderer(Choices.NATIONALITY, this);
		EbankDropDownChoice cmbNationality = new EbankDropDownChoice("nationality", nationalityRenderer.getChoices(), nationalityRenderer);
		cmbNationality.setRequired(true);
		border = addResourceLabelAndReturnBorder(cmbNationality);
		add(border);

		IGenericChoiceRenderer maritalStatusRenderer = GenericChoiceRendererFactory.getRenderer(Choices.maritalStatus, this);
		EbankDropDownChoice cmbMaritalStatus = new EbankDropDownChoice("maritalStatus", maritalStatusRenderer.getChoices(), maritalStatusRenderer);
		cmbMaritalStatus.setRequired(true);
		border = addResourceLabelAndReturnBorder(cmbMaritalStatus);
		add(border);

		FormComponent txtTownOfBirth = new EbankTextField("townOfBirth");
		border = addResourceLabelAndReturnBorder(txtTownOfBirth);
		add(border);

		IGenericChoiceRenderer countriesRenderer = GenericChoiceRendererFactory.getRenderer(Choices.COUNTRY, this);
		EbankDropDownChoice cmbCountryOfBirth = new EbankDropDownChoice("countryOfBirth", countriesRenderer.getChoices(), countriesRenderer);
		// CmbCountry cmbCountryOfBirth = new CmbCountry("countryOfBirth", serviceCountries);
		cmbCountryOfBirth.setRequired(true);
		border = addResourceLabelAndReturnBorder(cmbCountryOfBirth);
		add(border);

		FormComponent txtMobilePhone = new PhoneAndPrefix("mobilePhone", new PropertyModel(model, "mobilePhone"));
		txtMobilePhone.setRequired(true);
		// txtMobilePhone.add(new PhoneNumberValidator());
		add(addResourceLabelAndReturnBorder(txtMobilePhone));

		FormComponent txtEmailAddress = new EbankTextField("emailAddress");
		txtEmailAddress.add(new EmailValidationBehavior());
		border = addResourceLabelAndReturnBorder(txtEmailAddress);
		add(border);

		if (!EbankWizardStep.getIsSummary()) {
			// don't print confirmation email on summary page
			FormComponent txtEmailAddress2 = new EbankTextField("emailAddress2", new Model<String>(c.getEmailAddress()));
			txtEmailAddress2.add(new EmailValidationBehavior());
			txtEmailAddress2.add(new DisableCopyPasteFieldBehavior());
			border = addResourceLabelAndReturnBorder(txtEmailAddress2);
			s.add(new EqualInputValidator(txtEmailAddress, txtEmailAddress2));
			//border.setVisible(showConfirmationEmail);
			add(border);
		}

	}

	/* (non-Javadoc)
	 * @see com.temenos.ebank.common.wicket.IPartiallySavable#getObligatoryComponentsIds()
	 */
	public List<String> getObligatoryComponentsIds() {
			return requiredFieldsIdsForSave;
	}

}
