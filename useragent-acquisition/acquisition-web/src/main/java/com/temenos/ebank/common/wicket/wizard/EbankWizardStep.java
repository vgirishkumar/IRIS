package com.temenos.ebank.common.wicket.wizard;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.extensions.wizard.IWizard;
import org.apache.wicket.extensions.wizard.IWizardModel;
import org.apache.wicket.extensions.wizard.IWizardStep;
import org.apache.wicket.extensions.wizard.Wizard;
import org.apache.wicket.extensions.wizard.WizardModel;
import org.apache.wicket.extensions.wizard.dynamic.DynamicWizardModel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.validation.IFormValidator;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.feedback.Alert;
import com.temenos.ebank.common.wicket.partialSave.PartialSaveUtils;
import com.temenos.ebank.common.wicket.summary.IsSummary;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.message.AccountCreationResponse;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.pages.BasePage;
import com.temenos.ebank.pages.pageMasters.YourDetailsPanel;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceClientAquistion;
import com.temenos.ebank.wicket.EbankSession;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper;

@SuppressWarnings("rawtypes")
public abstract class EbankWizardStep extends Panel implements IWizardStep {

	protected static Log logger = LogFactory.getLog(EbankWizardStep.class);

	public static ThreadLocal<Boolean> isEven = new ThreadLocal<Boolean>();

	/**
	 * The last step needs to inform the wizard of the result of the account creation, thus allowing the wizard to
	 * forward the
	 * right information to the "Thank you" page.
	 */
	private static final MetaDataKey<AccountCreationResponse> metadataKeyAccountCreationResponse = new MetaDataKey<AccountCreationResponse>() {
		private static final long serialVersionUID = 1L;
	};

	private static final MetaDataKey<IsSummary> EBANK_IS_SUMMARY = new MetaDataKey<IsSummary>() {
		private static final long serialVersionUID = 1L;
	};

	@SpringBean(name = "serviceClientAcquisition")
	protected IServiceClientAquistion serviceClientAcquisition;

	public static Boolean getIsSummary() {
		IsSummary isSummary = RequestCycle.get().getMetaData(EBANK_IS_SUMMARY);
		return isSummary != null && isSummary.getValue();
	}

	public static void setIsSummary(Boolean isSummaryB) {
		RequestCycle.get().setMetaData(EBANK_IS_SUMMARY, new IsSummary(isSummaryB));
	}

	/**
	 * Stores the result of the account creation as metadata on RequestCycle
	 * 
	 * @param accountCreationResponse
	 *            The result of the account creation to store
	 */
	private static void storeAccountCreationResponse(AccountCreationResponse accountCreationResponse) {
		RequestCycle.get().setMetaData(EbankWizardStep.metadataKeyAccountCreationResponse, accountCreationResponse);
	}

	/**
	 * Returns the result of the account creation, thus allowing the wizard to forward the
	 * right information to the "Thank you" page.
	 * 
	 * @param cleanupStorage
	 *            Whether to cleanup the metadata stored on RequestCycle
	 * @return The result of the account creation
	 */
	public static AccountCreationResponse retrieveAccountCreationResponse(boolean cleanupStorage) {
		AccountCreationResponse accountCreationResponse = RequestCycle.get().getMetaData(
				EbankWizardStep.metadataKeyAccountCreationResponse);
		if (cleanupStorage) {
			storeAccountCreationResponse(null);
		}
		return accountCreationResponse;
	}

	/**
	 * Constructs the wizard step in "summary mode", i.e. for displaying in the summary page
	 * 
	 * @param id
	 * @param model
	 */
	// this constructor takes the step out of the wizard
	// and more importantly, sets the summaryPage flag to true
	public EbankWizardStep(String id, IModel<ApplicationWicketModelObject> model) {
		super(id, model);
		this.resumedApplication = false;
		setIsSummary(true);
		addStepComponents(model);
		addAdditionalPanels(model);
	}

	protected abstract void addStepComponents(IModel<ApplicationWicketModelObject> model);

	/**
	 * Adds additional panels (header, details).
	 * 
	 * @param model
	 */
	protected void addAdditionalPanels(IModel<ApplicationWicketModelObject> model) {
		// apply header to all steps, EXCEPT summary (which has a built-in custom header)
		if (!getIsSummary()) {
			add(new EbankWizardStepHeader("stepHeader"));
		} else {
			add(new WebMarkupContainer("stepHeader"));
		}
		
		YourDetailsPanel yourDetails = new YourDetailsPanel("yourDetails", model, resumedApplication);
		add(yourDetails);
	}

	protected abstract IDomainToWicketMapper<Application, ApplicationWicketModelObject> getDomainToWicketMapper();

	/**
	 * Adds form validators. We don't need this in 2.0 as the hierarchy is know at construction time
	 * from then.
	 */
	private final class AddFormValidatorAction {
		/**
		 * Wrapper for any form validators.
		 */
		final List<FormValidatorWrapper> formValidatorWrappers = new ArrayList<FormValidatorWrapper>();

		public void addValidator(IFormValidator validator) {
			// A wizard step can be used in different contexts, for example in the summary page. In such a context
			// it makes no sense to add the form validators, since the presentation is readonly
			boolean performValidation = wizardModel != null;
			if (performValidation) {
				FormValidatorWrapper fvw = new FormValidatorWrapper();
				fvw.add(validator);
				formValidatorWrappers.add(fvw);
			}
		}

		void execute() {
			Form<?> form = findParent(Form.class);
			for (FormValidatorWrapper formValidatorWrapper : formValidatorWrappers) {
				form.add(formValidatorWrapper);

			}
		}
	}

	/**
	 * Wraps form validators for this step such that they are only executed when this step is
	 * active.
	 */
	private final class FormValidatorWrapper implements IFormValidator {

		private static final long serialVersionUID = 1L;

		private final List<IFormValidator> validators = new ArrayList<IFormValidator>();

		/**
		 * Adds a form validator.
		 * 
		 * @param validator
		 *            The validator to add
		 */
		public void add(IFormValidator validator) {
			validators.add(validator);
		}

		/**
		 * @see org.apache.wicket.markup.html.form.validation.IFormValidator#getDependentFormComponents()
		 */
		public FormComponent<?>[] getDependentFormComponents() {
			if (isActiveStep()) {
				Set<Component> components = new HashSet<Component>();
				for (IFormValidator v : validators) {
					FormComponent<?>[] dependentComponents = v.getDependentFormComponents();
					if (dependentComponents != null) {
						int len = dependentComponents.length;
						for (int j = 0; j < len; j++) {
							components.add(dependentComponents[j]);
						}
					}
				}
				return components.toArray(new FormComponent[components.size()]);
			}
			return null;
		}

		/**
		 * @see org.apache.wicket.markup.html.form.validation.IFormValidator#validate(org.apache.wicket.markup.html.form.Form)
		 */
		public void validate(Form<?> form) {
			if (isActiveStep()) {
				for (IFormValidator v : validators) {
					v.validate(form);
				}
			}
		}

		/**
		 * @return whether the step this wrapper is part of is the current step
		 */
		private boolean isActiveStep() {
			return (wizardModel.getActiveStep().equals(EbankWizardStep.this));
		}
	}

	/**
	 * Default header for wizards.
	 */
	private final class Header extends Panel {
		private static final long serialVersionUID = 1L;

		/**
		 * Construct.
		 * 
		 * @param id
		 *            The component id
		 * @param wizard
		 *            The containing wizard
		 */
		public Header(final String id, final IWizard wizard) {
			super(id);
			setDefaultModel(new CompoundPropertyModel<IWizard>(wizard));
			add(new Label("title", new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return getTitle();
				}
			}).setEscapeModelStrings(false));
			add(new Label("summary", new AbstractReadOnlyModel<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getObject() {
					return getSummary();
				}
			}).setEscapeModelStrings(false));
		}
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Marks this step as being fully configured. Only when this is <tt>true</tt> can the wizard
	 * progress. True by default as that works best with normal forms. Clients can set this to false
	 * if some intermediate step, like a file upload, needs to be completed before the wizard may
	 * progress.
	 */
	private boolean complete = true;

	/**
	 * Marks this step as being confirmed if confirmation is necessary.
	 */
	private boolean confirmed = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.temenos.ebank.pages.clientAquisition.wizard.IClientAquisitionWizardStep#isConfirmed()
	 */
	public boolean isConfirmed() {
		return confirmed;
	}

	/**
	 * @param confirmed
	 */
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	private transient AddFormValidatorAction onAttachAction;

	/**
	 * A summary of this step, or some usage advice.
	 */
	private IModel<String> summary;

	/**
	 * The title of this step.
	 */
	private IModel<String> title;

	/**
	 * The wizard model.
	 */
	private EbankWizardModel wizardModel;

	protected boolean resumedApplication;

	/**
	 * Construct without a title and a summary. Useful for when you provide a custom header by
	 * overiding {@link #getHeader(String, Component, IWizard)}.
	 */
	public EbankWizardStep() {
		super(Wizard.VIEW_ID);
	}

	// /**
	// * Creates a new step with the specified title and summary. The title and summary are displayed
	// * in the wizard title block while this step is active.
	// *
	// * @param title
	// * the title of this step.
	// * @param summary
	// * a brief summary of this step or some usage guidelines.
	// */
	// public EbankWizardStep(IModel<String> title, IModel<String> summary) {
	// this(title, summary, null);
	// }

	// /**
	// * Creates a new step with the specified title and summary. The title and summary are displayed
	// * in the wizard title block while this step is active.
	// *
	// * @param title
	// * the title of this step.
	// * @param summary
	// * a brief summary of this step or some usage guidelines.
	// * @param model
	// * Any model which is to be used for this step
	// */
	// public EbankWizardStep(IModel<String> title, IModel<String> summary, IModel<?> model) {
	// super(Wizard.VIEW_ID, model);
	//
	// this.title = wrap(title);
	// this.summary = wrap(summary);
	// }

	// /**
	// * Constructs a wizard without title and summary
	// *
	// * @param model
	// * Any model which is to be used for this step
	// */
	// public EbankWizardStep(IModel<?> model) {
	// super(Wizard.VIEW_ID, model);
	// }

	// /**
	// * Creates a new step with the specified title and summary. The title and summary are displayed
	// * in the wizard title block while this step is active.
	// *
	// * @param title
	// * the title of this step.
	// * @param summary
	// * a brief summary of this step or some usage guidelines.
	// */
	// public EbankWizardStep(String title, String summary) {
	// this(title, summary, null);
	// }

	// /**
	// * Creates a new step with the specified title and summary. The title and summary are displayed
	// * in the wizard title block while this step is active.
	// *
	// * @param title
	// * the title of this step.
	// * @param summary
	// * a brief summary of this step or some usage guidelines.
	// * @param model
	// * Any model which is to be used for this step
	// */
	// public EbankWizardStep(String title, String summary, IModel<?> model) {
	// this(new Model<String>(title), new Model<String>(summary), model);
	// }

	/**
	 * @param id
	 */
	public EbankWizardStep(String id) {
		super(id);
	}

	// /**
	// * @param id
	// * @param model
	// */
	// public EbankWizardStep(String id, IModel<?> model) {
	// super(id, model);
	// }

	/**
	 * Adds a form validator.
	 * 
	 * @param validator
	 */
	public final void add(IFormValidator validator) {
		if (onAttachAction == null) {
			onAttachAction = new AddFormValidatorAction();
		}
		// onAttachAction.formValidatorWrapper.add(validator);
		onAttachAction.addValidator(validator);
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.IWizardStep#applyState()
	 */
	public void applyState() {
		AcquisitionResponse aquisitionResponse = persistToDb(true);
		boolean saved = aquisitionResponse.getResponseCode().isOk();
		if (saved) {
			// pass further the information on the account creation response
			if (getWizardModel().isLastStep(this)) {
				AccountCreationResponse accountCreationResponse = (AccountCreationResponse) aquisitionResponse
						.getAdditionalInfo();
				storeAccountCreationResponse(accountCreationResponse);
				getEbankSession().addAppRefToSubmittedList(aquisitionResponse.getApplication().getAppRef());
			}
		} else {
			error(new Alert(aquisitionResponse, getDefaultModel()));
		}

		setConfirmed(saved);
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.IWizardStep#getHeader(java.lang.String, org.apache.wicket.Component,
	 *      org.apache.wicket.extensions.wizard.IWizard)
	 */
	public Component getHeader(String id, Component parent, IWizard wizard) {
		return new Header(id, wizard);
	}

	/**
	 * Gets the summary of this step. This will be displayed in the title of the wizard while this
	 * step is active. The summary is typically an overview of the step or some usage guidelines for
	 * the user.
	 * 
	 * @return the summary of this step.
	 */
	public String getSummary() {
		return (summary != null) ? summary.getObject() : null;
	}

	/**
	 * Gets the title of this step.
	 * 
	 * @return the title of this step.
	 */
	public String getTitle() {
		return (title != null) ? title.getObject() : null;
	}

	/**
	 * @see org.apache.wicket.extensions.wizard.IWizardStep#getView(java.lang.String, org.apache.wicket.Component,
	 *      org.apache.wicket.extensions.wizard.IWizard)
	 */
	public final Component getView(String id, Component parent, IWizard wizard) {
		// cleanup all step components
		removeAll();
		// here the step adds the components, allowing the step to stay fresh
		refresh();
		return this;
	}

	/**
	 * This is where the wizard step performs the display logic
	 */
	@SuppressWarnings({ "unchecked" })
	public void refresh() {
		Application application = getClientAquisitionApplication();
		ApplicationWicketModelObject awm = getDomainToWicketMapper().domain2Wicket(application);
		IModel<ApplicationWicketModelObject> model = new CompoundPropertyModel(new Model<ApplicationWicketModelObject>(
				awm));

		// IModel<ApplicationWicketModelObject> model = new CompoundPropertyModel(
		// new LoadableDetachableModel<ApplicationWicketModelObject>() {
		// @Override
		// protected ApplicationWicketModelObject load() {
		// Application application = getClientAquisitionApplication();
		// ApplicationWicketModelObject awm = getDomainToWicketMapper().domain2Wicket(application);
		// return awm;
		// }
		//
		// });
		setDefaultModel(model);
		addStepComponents(model);
		addAdditionalPanels(model);

	}

	/**
	 * Called to initialize the step. When this method is called depends on the kind of wizard model
	 * that is used.
	 * 
	 * The {@link WizardModel static wizard model} knows all the steps upfront and initializes themm
	 * when starting up. This method will be called when the wizard is {@link #init(IWizardModel)
	 * initializing}.
	 * 
	 * The {@link DynamicWizardModel dynamic wizard model} initializes steps every time they are
	 * encountered.
	 * 
	 * This method sets the wizard model and then calls template method {@link #onInit(IWizardModel)}
	 * 
	 * @param wizardModel
	 *            the model to which the step belongs.
	 */
	public final void init(IWizardModel wizardModel) {
		this.wizardModel = (EbankWizardModel) wizardModel;
		//int stepNo = getStepNumber();
		String productType =  getWizardModel().getProductType().getCode();
		setTitleModel(new ResourceModel(String.format("wizard.%s.title", productType)));
		setSummaryModel(new ResourceModel(String.format("wizard.%s.summary", productType)));
		onInit(wizardModel);
	}

	/**
	 * Checks if this step is compete. This method should return true if the wizard can proceed to
	 * the next step. This property is bound and changes can be made at anytime by calling {@link #setComplete(boolean)}
	 * .
	 * 
	 * @return <tt>true</tt> if the wizard can proceed from this step, <tt>false</tt> otherwise.
	 * @see #setComplete
	 */
	public boolean isComplete() {
		return complete;
	}

	/**
	 * Marks this step as compete. The wizard will not be able to proceed from this step until this
	 * property is configured to <tt>true</tt>.
	 * 
	 * @param complete
	 *            <tt>true</tt> to allow the wizard to proceed, <tt>false</tt> otherwise.
	 * @see #isComplete
	 */
	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	/**
	 * Sets summary.
	 * 
	 * @param summary
	 *            summary
	 */
	public void setSummaryModel(IModel<String> summary) {
		this.summary = wrap(summary);
	}

	/**
	 * Sets title.
	 * 
	 * @param title
	 *            title
	 */
	public void setTitleModel(IModel<String> title) {
		this.title = wrap(title);
	}

	/**
	 * @see org.apache.wicket.Component#detachModel()
	 */
	@Override
	protected void detachModel() {
		super.detachModel();
		if (title != null) {
			title.detach();
		}
		if (summary != null) {
			summary.detach();
		}
	}

	/**
	 * Workaround for adding the form validators.
	 * 
	 * @see org.apache.wicket.Component#onBeforeRender()
	 */
	@Override
	public void onBeforeRender() {
		if (onAttachAction != null) {
			onAttachAction.execute();
			onAttachAction = null;
		}

		// add support for web analyitcs
		addAnalytics();

		super.onBeforeRender();
	}

	/**
	 * Template method that is called when the step is being initialized.
	 * 
	 * @param wizardModel
	 * @see #init(IWizardModel)
	 */
	protected void onInit(IWizardModel wizardModel) {
	}

	/**
	 * @return wizard model
	 */
	public EbankWizardModel getWizardModel() {
		return wizardModel;
	}

	/**
	 * Helper method for retrieving the propper session class
	 * 
	 * @return
	 */
	public EbankSession getEbankSession() {
		return ((EbankSession) getSession());
	}

	public static Boolean getEven() {
		Boolean returnB = isEven.get();
		if (returnB == null) {
			returnB = true;
		}
		isEven.set(!returnB);
		return returnB;
	}

	@Override
	public String getVariation() {
		if (getIsSummary()) {
		//if ((getWizardModel() != null) && (getWizardModel().getActiveStep() != null) && (((EbankWizardStep) getWizardModel().getActiveStep()).getStepNumber() == 6)) {
			return "summary";
		}
		return super.getVariation();
	}

	/**
	 * Register analytics behaviour and parameters on base page.
	 */
	@SuppressWarnings({ "unchecked" })
	private void addAnalytics() {
		// sometimes the rendering is called more than once, with a null wizard
		// model
		if (getWizardModel() != null) {
			Map parameters = new HashMap();

			ApplicationWicketModelObject app = (ApplicationWicketModelObject) getDefaultModelObject();

			parameters.put("appid", app.getAppRef());
			parameters.put("product", app.getProductRef());

			String s = getWizardModel().getActiveStep().getClass().getName();
			// TODO for the moment, we use only "StepX" for the name of the step
			parameters.put("step", s.substring(s.indexOf("Step"), s.indexOf("Step") + 5));

			parameters.put("existingCustomer", BooleanUtils.isTrue(app.getCustomer().getIsExistingCustomer()) ? "Yes" : "No");
			parameters.put("isJoint", app.getIsSole() ? "Single" : "Joint");
			parameters.put("currency", app.getAccountCurrency());

			Date currentDate = new Date();
			parameters.put("date", new SimpleDateFormat("MM/dd/yy").format(currentDate));
			parameters.put("time", new SimpleDateFormat("hh:mm:ss").format(currentDate));

			((BasePage) getPage()).addAnalyticsParameters(parameters);
		}
	}

	protected Application getClientAquisitionApplication() {
		return getEbankSession().getClientAquisitionApplication();
	}

	protected abstract Integer getStepNumber();

	/**
	 * This method is called whenever the user presses save while this step is active.
	 * <p>
	 * This method will only be called if {@link IWizardModel#isNextAvailable} and {@link #isComplete} return true.
	 */
	public void saveState() {
		// need this to update models with entered data, since Save button has defaultFormProcessing on false; null
		// param = no obligatory fields
		boolean valid = PartialSaveUtils.validatePartialForm(findParent(Form.class), null, null);
		if (valid) {
			AcquisitionResponse aquisitionResponse = persistToDb(false);
			// show result in feedback panel
			error(new Alert(aquisitionResponse.getResponseCode(), getDefaultModel()));
		}
		setConfirmed(false);
	}

	/**
	 * Persists the application to the back-end, taking charge of marshalling the model object to the
	 * domain object and back. Also updates the user session to store the persisted application.
	 * 
	 * @param isContinue
	 * @return
	 */
	protected AcquisitionResponse persistToDb(boolean isContinue) {
		logger.debug("Saving step " + getStepNumber());
		String requestCode = isContinue ? "C" + getStepNumber() : "S" + getStepNumber();
		Application a = getClientAquisitionApplication();
		getDomainToWicketMapper().wicketModel2Domain(getDefaultModel(), a);
		a.setResumeStep(getStepNumber());
		prepareApplicationForPersist(a);
		AcquisitionRequest acquisitionRequest = new AcquisitionRequest(a, requestCode);
		
		//add the language for dynamic strings coming from T24 displayed in Acquisition front-end 
		acquisitionRequest.setLanguage(getLocale().getLanguage());
		AcquisitionResponse aquisitionResponse = serviceClientAcquisition.saveApplication(acquisitionRequest);
		Application responseApp = aquisitionResponse.getApplication();
		// important - needed for cluster mode
		getEbankSession().setClientAquisitionApplication(responseApp);
		// marshall back the updated application to the wicket model, to reflect the changes in back-end
		// TODO: see if it would be safe or not to replace this operation by only updating a few fields.
		ApplicationWicketModelObject awm = getDomainToWicketMapper().domain2Wicket(responseApp);
		setDefaultModelObject(awm);
		return aquisitionResponse;
	}

	/**
	 * Prepares the application for persistence. This method provides a hook for inheritors that need
	 * to populate step-specific fields prior to application persistence.
	 * Override this in order to provide step-specific initialization
	 */
	protected void prepareApplicationForPersist(Application a) {
	}

}
