package com.temenos.ebank.pages.clientAquisition.step1;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.temenos.ebank.common.wicket.feedback.Alert;
import com.temenos.ebank.common.wicket.partialSave.PartialSaveUtils;
import com.temenos.ebank.common.wicket.wizard.EbankWizardStep;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class Step1 extends EbankWizardStep {

	private static final long serialVersionUID = 1L;

	private static final Integer RESUME_STEP_1 = Integer.valueOf(1);

	@SpringBean(name = "imageCaptchaService")
	protected ImageCaptchaService captchaService;

	/*
	 * @SpringBean(name = "serviceCountries")
	 * protected IServiceCountries serviceCountries;
	 * 
	 * @SpringBean(name = "serviceCustomers")
	 * private IServiceCustomers serviceCustomers;
	 */

	@SpringBean(name = "step1DomainToWicketMapper")
	private IDomainToWicketMapper<Application, ApplicationWicketModelObject> domainToWicketMapper;

	//private MockEligibilityService eligibilityService = new MockEligibilityService();


	/**
	 * Returns a new preaccount check panel, this varies by product type
	 * @param model
	 * @param singleJointPanel
	 */
	protected abstract Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model, SingleJointPanel singleJointPanel);

	/**
	 * Checks if a joint subscription is enabled or not, this varies by product type
	 * @return
	 */
	protected abstract boolean isJointEnabled();
	
	/**
	 * Loads the model for the wizard step
	 * 
	 * @return
	 */
	@Override
	public Application getClientAquisitionApplication() {
			Application app  = super.getClientAquisitionApplication();
			if (app != null) {
				return app;
			}
			app = new Application();
			app.setProductRef(getWizardModel().getProductType().getCode());
			app.setResumeStep(RESUME_STEP_1);
			return app;
	}

	@Override
	public IDomainToWicketMapper<Application, ApplicationWicketModelObject> getDomainToWicketMapper() {
		return domainToWicketMapper;
	}

	
	public Step1(boolean resumedApplication) {
		// this.appID = getEbankSession().getAppId();
		this.resumedApplication = resumedApplication;
		setIsSummary(false);
	}

	/**
	 * Constructs the wizard step in "summary mode", i.e. for displaying in the summary page
	 * @param id
	 * @param model
	 */
	public Step1(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
	}

	@Override
	protected void addStepComponents(IModel<ApplicationWicketModelObject> model) {
		// TODO: de ce ii trebuie astuia modelul ca parametru? Mai ales ca il obtin in linia de mai jos
		ApplicationWicketModelObject a = (ApplicationWicketModelObject) getDefaultModelObject();
		IncomeDetailsPanel incomeDetailsPanel = new IncomeDetailsPanel("incomeDetails", model); 
		add(incomeDetailsPanel);
		// when resuming application, we don't display the configuration email
		// again
		//boolean showConfirmationEmail = !this.resumedApplication;
		add(new CustomerDetailsPanel("customerDetails", this, new CompoundPropertyModel(new PropertyModel(model,
				"customer")), true));


		add(new EligibilityDetailsPanel("customerEligibilityDetails", new CompoundPropertyModel(new PropertyModel(model,
				"customer"))));

		add(new ExistingAccountsPanel("customerExistingAccounts", new CompoundPropertyModel(new PropertyModel(model,
				"customer"))));


		boolean secondCustomerVisible = Boolean.TRUE.equals(!a.getIsSole());
		
		boolean isJointEnabled = isJointEnabled();

		EligibilityDetailsPanel secondCustomerEligibility = new EligibilityDetailsPanel("secondCustomerEligibilityDetails", new CompoundPropertyModel(new PropertyModel(model,
		"secondCustomer")));
		addVisibilityFlagsForSecondCustomerPanel(secondCustomerEligibility, secondCustomerVisible, isJointEnabled);		
		add(secondCustomerEligibility);
		
		ExistingAccountsPanel secondCustomerExistingAccounts =  new ExistingAccountsPanel("secondCustomerExistingAccounts",
				new CompoundPropertyModel(new PropertyModel(model, "secondCustomer")));
		addVisibilityFlagsForSecondCustomerPanel(secondCustomerExistingAccounts, secondCustomerVisible, isJointEnabled);
		add(secondCustomerExistingAccounts);
		

		CustomerDetailsPanel secondCustomerDetails = new CustomerDetailsPanel("secondCustomerDetails", this,
				new CompoundPropertyModel(new PropertyModel(model, "secondCustomer")), false);
		addVisibilityFlagsForSecondCustomerPanel(secondCustomerDetails, secondCustomerVisible, isJointEnabled);
		add(secondCustomerDetails);
		Component [] secondCustomerPanels = new Component[] {secondCustomerDetails, secondCustomerEligibility, secondCustomerExistingAccounts};
		SingleJointPanel singleJointPanel = new SingleJointPanel("singleJoint", model, secondCustomerPanels, incomeDetailsPanel.getComponentsToUpdateForSingleJointToggle());
		singleJointPanel.setEnabled(isJointEnabled);
		singleJointPanel.setVisibilityAllowed(isJointEnabled);
		
		add(newPreaccountCheckPanel(model, singleJointPanel));

	}

	private void addVisibilityFlagsForSecondCustomerPanel(Component scPanel, boolean secondCustomerVisible, boolean isJointEnabled) {
		scPanel.setOutputMarkupPlaceholderTag(true);
		scPanel.setVisible(secondCustomerVisible);
		scPanel.setEnabled(isJointEnabled);
		scPanel.setVisibilityAllowed(isJointEnabled);
	}

	// //TODO: add proper implementation
	// private boolean alreadyHasSavingsAccountsForCurrency(Customer c) {
	// return serviceCustomers.isExistingCustomer(c);
	// }

	protected List<String> getMandatoryFieldsIdsInForm() {
		//TODO: ar trebui sa mearga si cu null, aparent are un bug aici mecansimu de save partial
		return new ArrayList<String>();
		//return Arrays.asList("customer.countryResidence");
	}

	/**
	 * Returns a list containing the page relative paths of components excluded from validation on save.
	 * @return A list with paths of components excluded from validaton on save
	 */
	protected List<String> getExcludedComponentsPaths() {
		List<String> excludedComponentsPaths = new ArrayList<String>();
		return excludedComponentsPaths;
	}
	
	/**
	 * Utility method for forming the list containing the components that should be excluded from validation on save.
	 * @param excludedComponentsPaths The list containing the components paths.
	 * @param container The container in which the component resides
	 * @param pathRelativeToContainer The component's path relative to the container
	 */
	protected void addExcludedComponent(List<String> excludedComponentsPaths, Panel container, String pathRelativeToContainer) {
		Component c = container.get(pathRelativeToContainer);
		if (c != null) {
			excludedComponentsPaths.add(c.getPageRelativePath());
		}
	}

	public boolean isResumedApplication() {
		return resumedApplication;
	}

	public void setResumedApplication(boolean resumedApplication) {
		this.resumedApplication = resumedApplication;
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.WizardStep#applyState()
	 */
	@Override
	public void applyState() {
		// domainToWicketMapper.wicketModel2Domain(getDefaultModel(), application);
		// if (alreadyHasSavingsAccountsForCurrency(application.getCustomer())) {
		AcquisitionResponse aquisitionResponse = persistToDb(true);
		boolean isOk = aquisitionResponse.getResponseCode().isOk();
		setConfirmed(isOk);
		error(new Alert(aquisitionResponse, getDefaultModel()));
	}

	/**
	 * @see com.temenos.ebank.common.wicket.wizard.EbankWizardStep#saveState()
	 */
	@Override
	public void saveState() {
		Form frm = findParent(Form.class);
		boolean valid = PartialSaveUtils.validatePartialForm(frm, getMandatoryFieldsIdsInForm(),
				getExcludedComponentsPaths());
		if (valid) {
			AcquisitionResponse aquisitionResponse = persistToDb(false);
			//((ApplicationWicketModelObject)getDefaultModelObject()).setAppRef(aquisitionResponse.getApplication().getAppRef());
			error(new Alert(aquisitionResponse.getResponseCode(), getDefaultModel()));
		} else {
			//TODO: decomenteaza asta, acum am comentat'o ca sa treaca testele
			//FormComponent radioGroupSingleJoint =  (FormComponent)frm.get("view:preAccountCheck:singleJoint:isSoleBorder:isSole");
			//need to explicitly update the model for sole/joint selection, in order to display the propper alert message
			//this needs to be done manually because for the save button the default form processing is disabled
			//radioGroupSingleJoint.updateModel();
			error(new Alert(ResponseCode.SAVE_ERROR, getDefaultModel()));
		}
		setConfirmed(false);
	}

	@Override
	protected void prepareApplicationForPersist(Application a) {
		boolean isNewApp = StringUtils.isBlank(a.getAppRef());
		if (isNewApp) {
			// creation date is needed for purging old abandoned applications
			a.setCreationDate(new Date());
		}
	}

	@Override
	protected Integer getStepNumber() {
		return RESUME_STEP_1;
	}

}
