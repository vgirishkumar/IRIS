/**
 * 
 */
package com.temenos.ebank.pages.crossSell;

import static com.temenos.ebank.common.wicket.WicketUtils.addResourceLabelAndReturnBorder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.border.Border;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.link.PopupSettings;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import com.temenos.ebank.common.wicket.formValidation.CompositeErrorsAndInfosFeedbackPanel;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.message.AccountCreationResponse;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.pages.BasePage;
import com.temenos.ebank.pages.clientAquisition.step1.SingleJointPanel;
import com.temenos.ebank.pages.clientAquisition.wizard.SupportSnippet;
import com.temenos.ebank.pages.thankYou.ThankYouPage;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceCrossSell;
import com.temenos.ebank.wicketmodel.ApplicationWicketModelObject;
import com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper;

/**
 * @author vbeuran
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class CSAccountOptionsPage extends BasePage {

	@SpringBean(name = "step1DomainToWicketMapper")
	private IDomainToWicketMapper<Application, ApplicationWicketModelObject> domainToWicketMapper;

	@SpringBean(name = "serviceCrossSell")
	private IServiceCrossSell serviceCrossSell;

	private Application crossSellApplication;

	//expose form object to inheriting classes
	//CurrentAccount sub class needs it to add validator
	protected Form form;
	protected Page tyPage;

	public CSAccountOptionsPage() {
		tyPage = RequestCycle.get().getRequest().getPage();
		final Application previousApplication = getEbankSession().getClientAquisitionApplication();
		crossSellApplication = getCrossSellApplicationObject(previousApplication);
		final IModel<ApplicationWicketModelObject> model = new CompoundPropertyModel<ApplicationWicketModelObject>(
				domainToWicketMapper.domain2Wicket(crossSellApplication));
		setDefaultModel(model);
		form = new Form("form");
		form.add(new Label("product",  new ResourceModel(String.format("wizard.%s.title", getProduct().getCode()) ) ) );
		add(form);
		form.addOrReplace(new CompositeErrorsAndInfosFeedbackPanel("feedback", form, this, null));
		
		form.add(new SupportSnippet("supportSnippet"));
		//naming the window for referencing in cross-window javascript 
		add(new AbstractBehavior() {
			private static final long serialVersionUID = 1L;
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				response.renderOnLoadJavascript("window.name='crossSell';");
			}
		});

		//sole-joint radio is visible in CS only after submitting a joint application, but never for Regular Saver products
		boolean isJointApplicationAllowed = !previousApplication.getIsSole() && !ProductType.REGULAR_SAVER.equals(getProduct());
		
		// joint applicant Declaration
		final WebMarkupContainer groupSecondApplicantWebContainer = !isJointApplicationAllowed? new WebMarkupContainer("secondApplicantDeclaration")
		: getCSDeclarationPanel("secondApplicantDeclaration", model, previousApplication, false);
		// visible(false) for page init; visibility is afterwards modified via Ajax
		groupSecondApplicantWebContainer.setVisible(false).setVisibilityAllowed(isJointApplicationAllowed)
				.setEnabled(isJointApplicationAllowed).setOutputMarkupPlaceholderTag(true);


		// sole/joint application selection
		Component secondFinancialDetailsPanel = newSecondFinancialDetailsPanel(model).setOutputMarkupId(true);
		Component singleJointPanel = !isJointApplicationAllowed ? new WebMarkupContainer("singleJoint")
						: new SoleJointPanel("singleJoint", model, previousApplication, crossSellApplication,
						groupSecondApplicantWebContainer, getFinancialDetailsUpdatableComponents(secondFinancialDetailsPanel));
		form.add(singleJointPanel);
		// preaccountCheck - product specific = first block of Step 1
		form.add(newPreaccountCheckPanel(model, null));

		// second financial details - product specific = second block of Step 4
		form.add(secondFinancialDetailsPanel);

		// checkbox - terms and conditions - first line of Step 5
		WebMarkupContainer declaration = getCSDeclarationPanel("declaration", model, previousApplication, true);
		declaration.add(groupSecondApplicantWebContainer);
		form.add(declaration);
		form.add(new Link("previous"){
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick() {
				setResponsePage(tyPage);
			}
		});
		
		form.add(new Button("finish", new ResourceModel("submit")) {
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				domainToWicketMapper.wicket2Domain(model.getObject(), crossSellApplication);
				AcquisitionRequest request = new AcquisitionRequest(crossSellApplication, "");
				request.setAdditionalInfo(previousApplication.getAppRef());
				request.setLanguage( getLocale().getLanguage() );
				AcquisitionResponse response = serviceCrossSell.createCrossSell(request);
				setResponsePage(new ThankYouPage((AccountCreationResponse) (response.getAdditionalInfo()), false, response.getApplication()));
			}
		});

	}
	
	@Override
	protected
	boolean supportsModalSessionScript() {
		return true;
	}

	/**
	 * Draws the first line of the declaration page (Step 5) : Terms&Conditions for main applicant or the Joint
	 * application agreement if applicable
	 * 
	 * @param id
	 * @param model
	 * @param previousApplication
	 *            - contains data from the previous application for identifying the joint applicant
	 * @param firstApplicant
	 *            - if true creates the T&C checkbox , otherwise creates the joint application agreement checkbox
	 * @return the declaration component
	 */
	public WebMarkupContainer getCSDeclarationPanel(String id, IModel<ApplicationWicketModelObject> model,
			Application previousApplication, boolean firstApplicant) {
		WebMarkupContainer result = new WebMarkupContainer(id, model);
		Border border = null;
		if (firstApplicant) {
			CheckBox checkTermsAndConditions = new CheckBox("flagTcs");
			checkTermsAndConditions.setRequired(true);
			ExternalLink tcsLink = new ExternalLink("tcsLink", new ResourceModel("tcsLink"), new ResourceModel("tcsLinkText"))
					.setPopupSettings(getPopupSettings());			
			border = addResourceLabelAndReturnBorder(checkTermsAndConditions);
			border.add(tcsLink);
//			border.add(crmLink);
		} else {
			CheckBox checkJointAppDeclaration = new CheckBox("flagJointApp");
			checkJointAppDeclaration.setRequired(true);
			border = addResourceLabelAndReturnBorder(checkJointAppDeclaration);
			border.setVisible(!previousApplication.getIsSole());
		}
		result.add(border);
		return result;
	}

	/**
	 * Creates a "settings object" for the popup pages to be opened by this page
	 * 
	 * @return
	 */
	private PopupSettings getPopupSettings() {
		int popupHeight = 600;
		int popupWidth = 800;
		PopupSettings settings = new PopupSettings(PopupSettings.MENU_BAR | PopupSettings.RESIZABLE
				| PopupSettings.SCROLLBARS | PopupSettings.STATUS_BAR).setHeight(popupHeight).setWidth(popupWidth);
		settings.setTop(200);
		settings.setLeft(300);
		return settings;
	}

	/**
	 * Creates a new application object to be populated with "cross sell"-specific data by the form
	 * 
	 * @return
	 */
	protected Application getCrossSellApplicationObject(Application previousApplication) {		
		final Application a = new Application();
		a.setProductRef(getProduct().getCode());
		if (ProductType.INTERNATIONAL.getCode().equalsIgnoreCase(getProduct().getCode()) ||
				(ProductType.REGULAR_SAVER.getCode().equalsIgnoreCase(getProduct().getCode()))) {
			a.setCustomer(previousApplication.getCustomer());
		}
		return a;
	}

	protected Component[] getFinancialDetailsUpdatableComponents(Component secondFinancialPanel) {
		return null;
	}
	
	@Override
	protected void onBeforeRender() {
		// add support for web analyitcs
		addAnalytics();
		super.onBeforeRender();
	}
	/**
	 * Register analytics behaviour and parameters on base page.
	 */
	@SuppressWarnings({ "unchecked" })
	private void addAnalytics() {
		Map parameters = new HashMap();
		Application originalApp = getEbankSession().getClientAquisitionApplication();

		parameters.put("appid", originalApp.getAppRef());
		parameters.put("existingCustomer", originalApp.getCustomer().getIsExistingCustomer() ? "Yes" : "No");
		parameters.put("isJoint", originalApp.getIsSole() ? "Single" : "Joint");
		parameters.put("product", originalApp.getProductRef());
		parameters.put("currency", originalApp.getAccountCurrency());

		parameters.put("productCS", crossSellApplication.getProductRef());

		// TODO: for the moment, we use only "StepX" for the name of the step
		parameters.put("step", "StepX2");
		Date currentDate = new Date();
		parameters.put("date", new SimpleDateFormat("MM/dd/yy").format(currentDate));
		parameters.put("time", new SimpleDateFormat("hh:mm:ss").format(currentDate));
		((BasePage) getPage()).addAnalyticsParameters(parameters);
	}

	/**
	 * generates a product-specific preaccount check panel (first block of the Step 1 page)
	 * 
	 * @param model
	 * @param singleJointPanel
	 * @return
	 */
	protected abstract Panel newPreaccountCheckPanel(IModel<ApplicationWicketModelObject> model,
			SingleJointPanel singleJointPanel);

	/**
	 * returns the product type for which the cross sell is being processed
	 * 
	 * @return
	 */
	protected abstract ProductType getProduct();

	/**
	 * generates a product-specific financial details panel (second one from the Step 4 page)
	 * 
	 * @param model
	 * @param singlePanel
	 * @return
	 */
	protected abstract Panel newSecondFinancialDetailsPanel(IModel<ApplicationWicketModelObject> model);
}
