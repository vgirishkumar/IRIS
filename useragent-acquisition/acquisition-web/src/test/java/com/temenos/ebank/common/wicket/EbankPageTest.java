package com.temenos.ebank.common.wicket;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.feedback.ComponentFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.ValidationErrorFeedback;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.resource.loader.ClassStringResourceLoader;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.WicketTester;
import org.apache.wicket.validation.ValidationError;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.temenos.ebank.common.wicket.components.PhoneAndPrefix;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ConfigParamTable;
import com.temenos.ebank.domain.Nomencl;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.pages.clientAquisition.resumeApplication.ResumeApplication;
import com.temenos.ebank.pages.clientAquisition.step1.CustomerDetailsPanel;
import com.temenos.ebank.pages.clientAquisition.step1.EligibilityDetailsPanel;
import com.temenos.ebank.pages.clientAquisition.step1.ExistingAccountsPanel;
import com.temenos.ebank.pages.clientAquisition.step1.IncomeDetailsPanel;
import com.temenos.ebank.pages.clientAquisition.step1.PreAccountCheckPanelCA;
import com.temenos.ebank.pages.clientAquisition.step1.SingleJointPanel;
import com.temenos.ebank.pages.clientAquisition.step1.SortCodePanel;
import com.temenos.ebank.pages.clientAquisition.step1.Step1;
import com.temenos.ebank.pages.clientAquisition.step2.AddressPanel;
import com.temenos.ebank.pages.clientAquisition.step2.ContactDetailsPanel;
import com.temenos.ebank.pages.clientAquisition.step2.PafAddressPanel;
import com.temenos.ebank.pages.clientAquisition.step2.PafSearchPostCodePanel;
import com.temenos.ebank.pages.clientAquisition.step2.PafSelectAddressPanel;
import com.temenos.ebank.pages.clientAquisition.step2.YearsAndMonths;
import com.temenos.ebank.pages.clientAquisition.wizard.ClientAquisitionWizard;
import com.temenos.ebank.pages.clientAquisition.wizard.CAWizardPage;
import com.temenos.ebank.pages.startPage.ApplyForInternationalAccount;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceClientAquistion;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam;
import com.temenos.ebank.services.interfaces.nomencl.IServiceNomencl;
import com.temenos.ebank.services.interfaces.resources.IServiceResourceLoader;
import com.temenos.ebank.wicket.ContextDependentPropertiesResourceLoader;
import com.temenos.ebank.wicket.EbankStreamLocator;
import com.temenos.ebank.wicket.EbankWicketApplication;
import com.temenos.ebank.wicket.FlatBundleStringResourceLoader;
import com.temenos.ebank.wicket.FlatClassStringResourceLoader;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
@SuppressWarnings("rawtypes")

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContextForTests.xml"})
//@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)

public abstract class EbankPageTest {
	 
	//private EbankWicketApplication wicketApplication;
	
	@Autowired
	private ApplicationContext ctx;

    @Autowired
    @Qualifier("mock")
    IServiceClientAquistion serviceClientAcquisition;

    @Autowired
    @Qualifier("mock")
    IServiceNomencl serviceNomencl;

    @Autowired
    @Qualifier("mock")
    IServiceConfigParam serviceConfigParam;

    @Autowired
    @Qualifier("mock")
    IServiceResourceLoader serviceResourceLoader;

	private static EbankWicketApplication ebankApp = null;
	protected WicketTester tester = null;

	/**
	 * Lazy initialization of the wicket application. Insures that only one instnce is initialized, otherwise, there would be problems
	 * when adding bookmarkable pages
	 * @return
	 */
	protected EbankWicketApplication getEbankWicketApplication() {
		if (ebankApp == null) {
			ebankApp = new EbankWicketApplication() {
				@Override
				protected void init() {
					addComponentInstantiationListener(new SpringComponentInjector(this, ctx, true));

					getResourceSettings().addResourceFolder("/WEB-INF/bundle/");
					//getResourceSettings().addStringResourceLoader(0, new FlatValidatorStringResourceLoader());
					getResourceSettings().addStringResourceLoader(0, 
							new FlatClassStringResourceLoader(EbankWicketApplication.class));
					getResourceSettings().addStringResourceLoader(0, new FlatBundleStringResourceLoader());
					getResourceSettings().setResourceStreamLocator(new EbankStreamLocator());
					getResourceSettings().addStringResourceLoader(new ContextDependentPropertiesResourceLoader());
				}
			};
		}
		return ebankApp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 * Generates a session to work similar to the OpenSessionInView mechanism
	 */
	@Before
	public void setUp() {
		tester = new EbankWicketTester(getEbankWicketApplication());
		tester.setupRequestAndResponse();
		loadProperties(PhoneAndPrefix.class, Step1.class, CustomerDetailsPanel.class, IncomeDetailsPanel.class, EligibilityDetailsPanel.class, 
				ExistingAccountsPanel.class, SingleJointPanel.class, SortCodePanel.class, PreAccountCheckPanelCA.class);
		loadProperties(AddressPanel.class, ContactDetailsPanel.class, PafAddressPanel.class, PafSearchPostCodePanel.class, PafSelectAddressPanel.class, YearsAndMonths.class);
		//mock nomencls to load from xml
		reset(serviceNomencl);
		when(serviceNomencl.getNomencl(anyString(), anyString())).thenAnswer(
				new Answer<List<Nomencl>>() {
					public List<Nomencl> answer (InvocationOnMock invocation) {
						Object[] args = invocation.getArguments();
						String groupName = (String)args[1];
						return loadNomenclFromXml(groupName);
					}
				});
		reset(serviceConfigParam);
		when(serviceConfigParam.getConfigParamTable()).thenAnswer(
				new Answer<ConfigParamTable>() {
					public ConfigParamTable answer (InvocationOnMock invocation) {
						return loadConfigParamFromXml();
					}
				});
		
	}

	@After
	public void tearDown() {
		//nothing here yet
	}

	/**
	 * Load specific properties files
	 * 
	 * @param classesWithProperties
	 */
	public void loadProperties(Class... classesWithProperties) {
		for (Class clazz : classesWithProperties) {
			getEbankWicketApplication().getResourceSettings().addStringResourceLoader(new ClassStringResourceLoader(clazz));
		}
	}

	/**
	 * Returns the message key for Required field from properties file, replacing the label of the required field
	 * 
	 * @param frm
	 * @param requiredLabel
	 * @return
	 */
	public static String getRequiredResourceMessage(Component frm, String requiredLabel) {
		return StringUtils.replace(frm.getString("Required"), "${label}", requiredLabel);
	}
	
	/** Returns a required validation error message for a field that is currently in error during the test. 
	 * The method returns null for a field that is not currently in error.
	 * Usually the key for required messages resides in EbankWicketApplication.properties,
	 * but on can override the message in component's scope, adding the key <fieldName>.Required
	 * @param frm The form holding the field
	 * @param fieldPathInForm The path of the field as wicket component, relative to the parent form 
	 * @return
	 */
	public String getRequiredMessageForFieldPath(Component frm, String fieldPathInForm) {
		return getValidationErrorForFieldPath(frm, fieldPathInForm, "Required");
	}

	/** Returns a "equal input" validation error message for a field that is currently in error during the test. 
	 * The method returns null for a field that is not currently in error.
	 * @param frm The form holding the field
	 * @param fieldPathInForm The path of the field as wicket component, relative to the parent form 
	 * @return
	 */
	public String getEqualInputValidatorMessageForFieldPath(Component frm, String fieldPathInForm) {
		return getValidationErrorForFieldPath(frm, fieldPathInForm, "EqualInputValidator");
	}

	/** Returns a validation error message for a field that is currently in error during the test. 
	 * The method returns null for a field that is not currently in the error condigion designated by the errorkey param.
	 * Usually the key for generic validation messages resides in EbankWicketApplication.properties,
	 * but on can override the message in component's scope, adding the key <fieldName>.<errorKey> 
	 * @param frm The form holding the field
	 * @param fieldPathInForm The path of the field as wicket component, relative to the parent form
	 * @param errorKey The key of the error condition (ex: Required, EqualInputValidator) 
	 * @return
	 */
	
	public String getValidationErrorForFieldPath(Component frm, String fieldPathInForm, String errorKey) {
		String fieldPath = frm.getPageRelativePath() + ":" + fieldPathInForm;
		FormComponent field = (FormComponent)tester.getComponentFromLastRenderedPage(fieldPath);
		List<FeedbackMessage> allFeedbackMessagesForComponent = org.apache.wicket.Session.get().getFeedbackMessages().messages(new ComponentFeedbackMessageFilter(field));
		for (FeedbackMessage fm : allFeedbackMessagesForComponent) {
			if(! (fm.getMessage() instanceof ValidationErrorFeedback)) {
				continue;
			}
			ValidationErrorFeedback feedbackErr = (ValidationErrorFeedback)fm.getMessage();
			boolean isRequiredErr = ((ValidationError)feedbackErr.getError()).getKeys().contains(errorKey);
			if (isRequiredErr) {
				return feedbackErr.getMessage();	
			}
		}
		return null;
	}
	
	/**
	 * Resumes application with given refNo and emailAddress. As consequence, navigates to Step1
	 * @param refNo
	 *            - id of Application to load
	 * @param emailAddress
	 *            - email of the applicant
	 */
	protected void resume(String refNo, String emailAddress) {
		tester.startPage(new ResumeApplication());
		tester.assertRenderedPage(ResumeApplication.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
		FormTester formTester = tester.newFormTester("form");
		// identify the applicant
		formTester.setValue("refnoBorder:refno", refNo);
		formTester.setValue("emailAddressBorder:emailAddress", emailAddress);
		formTester.submit("continue");
	}
	
	/**
	 * Resumes an application by taking data from an xml file.
	 * @param appRef Prefix of the xml file containing the serialized application.
	 * @param stepNo
	 */
	protected void resumeXmlAppAndNavigateToStep(String appRef, int stepNo) {
		Application a = loadApplicationFromXml(appRef);
		when(serviceClientAcquisition.getApplicationByReferenceAndEmail(anyString(), anyString())).thenReturn(new AcquisitionResponse(a, ResponseCode.EVERYTHING_OK));		
		resume(a.getAppRef(), a.getCustomer().getEmailAddress());
		// all the applications are resumed at step1
		for (int i = 1; i < stepNo; i++) {
			FormTester formTester = tester.newFormTester("wizard:form");
			// display step1
			tester.assertRenderedPage(CAWizardPage.class);
			tester.assertComponent("wizard", ClientAquisitionWizard.class);
			if (i == 2) {
				// alert message for eligible
				tester.assertErrorMessages(new String[] { "ALERT_ELIGIBLE_OK" });
				
			} else {
				tester.assertNoErrorMessage();
			}
			if (i == 1) {
				when(serviceClientAcquisition.saveApplication(any(AcquisitionRequest.class))).thenReturn(new AcquisitionResponse(a, ResponseCode.ELIGIBLE_OK));
			} else {
				when(serviceClientAcquisition.saveApplication(any(AcquisitionRequest.class))).thenReturn(new AcquisitionResponse(a, ResponseCode.EVERYTHING_OK));
			}
			tester.assertNoInfoMessage();
			
			formTester.submit("buttons:next");
		}

		tester.assertRenderedPage(CAWizardPage.class);
		tester.assertComponent("wizard", ClientAquisitionWizard.class);
		tester.assertComponent("wizard:form", Form.class);		
		if (stepNo == 2) {
			// alert message for eligible
			tester.assertErrorMessages(new String[] { "ALERT_ELIGIBLE_OK" });
		} else {
			tester.assertNoErrorMessage();
		}
		tester.assertNoInfoMessage();
		//verifies number of method calls
		verify(serviceClientAcquisition, times(stepNo -1)).saveApplication(any(AcquisitionRequest.class));
		verify(serviceClientAcquisition, times(1)).getApplicationByReferenceAndEmail(anyString(), anyString());
		
	}
	/**
	 * Clicks on the link "Apply for Internatonal account" and lands on step 1 of the client acquisition wizard
	 * @param linkToClick TODO
	 */
	public void loadNewApplicationForm(String linkToClick) {
		// new application
		tester.startPage(new ApplyForInternationalAccount());
		tester.assertRenderedPage(ApplyForInternationalAccount.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
	
		tester.clickLink(linkToClick);
		tester.assertRenderedPage(CAWizardPage.class);
		tester.assertComponent("wizard", ClientAquisitionWizard.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
		//Your details is not visible for new applications
		tester.assertInvisible("wizard:form:view:yourDetails");
	}
	
	/**
	 * Starts Wizard for the given product
	 * @param product to load
	 */
	public void loadNewApplicationForProduct(ProductPagesAndLinks product) {
		// new application
		tester.startPage(product.getPage());
		tester.assertRenderedPage(product.getPageClass());
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
		//Your details is not visible for new applications
		tester.assertInvisible("wizard:form:view:yourDetails");
	}
	
	/**
	 * Loads an Application's data which was previously saved to xml format with Xstream tool
	 * @param refno The refno of the application
	 * @return
	 */
	protected static Application loadApplicationFromXml(String refno) {
		try {
			XStream xstream = new XStream(new DomDriver());
			String xmlPah = "com/temenos/ebank/common/wicket/testData/applications/" + refno + ".xml";
			InputStream is = EbankPageTest.class.getClassLoader().getResourceAsStream(xmlPah);
			Application a = (Application)xstream.fromXML(is);
			return a;
		} catch (Exception e) {
			throw new RuntimeException("Error deserializing app from xml", e);
		}
	}
	
	/**
	 * Loads a Nomencl's data which was previously saved to xml format with Xstream tool
	 * @param groupName The name of the nomencl
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static List<Nomencl> loadNomenclFromXml(String groupName) {
		try {
			XStream xstream = new XStream(new DomDriver());
			//TODO:rename the files to lowercase, in order to avoid surprises
			String xmlPah = "com/temenos/ebank/common/wicket/testData/nomencl/" + groupName + ".xml";
			InputStream is = EbankPageTest.class.getClassLoader().getResourceAsStream(xmlPah);
			List<Nomencl> nomencls = (List<Nomencl>)xstream.fromXML(is);
			return nomencls;
		} catch (Exception e) {
			throw new RuntimeException("Error deserializing nomencl " + groupName +  " from xml", e);
		}
	}
	
	/**
	 * Loads an Application's data which was previously saved to xml format with Xstream tool
	 * @return
	 */
	protected static ConfigParamTable loadConfigParamFromXml() {
		try {
			XStream xstream = new XStream(new DomDriver());
			//TODO:rename the files to lowercase, in order to avoid surprises
			String xmlPah = "com/temenos/ebank/common/wicket/testData/configparam/configParamTable.xml";
			InputStream is = EbankPageTest.class.getClassLoader().getResourceAsStream(xmlPah);
			ConfigParamTable cpt = (ConfigParamTable)xstream.fromXML(is);
			return cpt;
		} catch (Exception e) {
			throw new RuntimeException("Error deserializing config param from xml", e);
		}
	}
	
	/**
	 * Selects and Ajax Radio button, triggering the specific {@link AjaxFormChoiceComponentUpdatingBehavior} behavior
	 * 
	 * @param tester
	 * @param radioPath
	 */
	public static void selectAjaxRadio(final WicketTester tester, final String radioPath) {
		final Radio radio = (Radio) tester.getComponentFromLastRenderedPage(radioPath);
		final RadioGroup radioGroup = radio.findParent(RadioGroup.class);
		AjaxFormChoiceComponentUpdatingBehavior behavior = null;
		// find first AjaxFormChoiceComponentUpdatingBehavior for RadioGroup
		for (final IBehavior b : radioGroup.getBehaviors()) {
			if (b instanceof AjaxFormChoiceComponentUpdatingBehavior) {
				behavior = (AjaxFormChoiceComponentUpdatingBehavior) b;
				break;
			}
		}
		executeBehaviourWithParam(tester, behavior, radioGroup.getInputName(), radio.getValue());
	}
	
	/**
	 * WicketTester executeBehavior method calls
	 * WicketTester.getServletRequest().setRequestToRedirectString(url.toString())
	 * which sets the selected value to null. We need to reset the submitted parameter value after the call of
	 * setRequestToRedirectString
	 * 
	 * @param tester
	 * @param behavior
	 * @param paramName
	 * @param paramValue
	 * @see http://blog.xebia.com/2009/08/25/testing-wicket-ajaxbehavior/
	 */
	private static void executeBehaviourWithParam(final WicketTester tester, final AbstractAjaxBehavior behavior,
			final String paramName, final String paramValue) {
		assert behavior != null;
		final CharSequence url = behavior.getCallbackUrl(false);
		final WebRequestCycle cycle = tester.setupRequestAndResponse(true);
		tester.getServletRequest().setRequestToRedirectString(url.toString());
		tester.getServletRequest().setParameter(paramName, paramValue);
		tester.processRequestCycle(cycle);
	}
	
	/**
	 * Triggers the on change behavior for the YearsAndMonths component
	 * 
	 * @param tester
	 * @param yearsAndMonthsPath
	 */
	public static void changeYearsAndMonths(final WicketTester tester, final String yearsAndMonthsPath) {
		//TODO: this method does not seem to be used anymore. Someone has to use it or delete it I guess
		final DropDownChoice yearsAndMonths = (DropDownChoice) tester.getComponentFromLastRenderedPage(yearsAndMonthsPath);
		OnChangeAjaxBehavior behavior = null;
		// find first AjaxFormChoiceComponentUpdatingBehavior for RadioGroup
		for (final IBehavior b : yearsAndMonths.getBehaviors()) {
			if (b instanceof OnChangeAjaxBehavior) {
				behavior = (OnChangeAjaxBehavior) b;
				break;
			}
		}
		executeBehaviourWithParam(tester, behavior, yearsAndMonths.getInputName(), yearsAndMonths.getValue());
	}	
}
