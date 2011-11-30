package com.temenos.ebank.common.wicket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.temenos.ebank.common.wicket.components.PhoneAndPrefix;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ConfigParamTable;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.pages.clientAquisition.step1.CustomerDetailsPanel;
import com.temenos.ebank.pages.clientAquisition.step1.EligibilityDetailsPanel;
import com.temenos.ebank.pages.clientAquisition.step1.ExistingAccountsPanel;
import com.temenos.ebank.pages.clientAquisition.step1.IncomeDetailsPanel;
import com.temenos.ebank.pages.clientAquisition.step1.PreAccountCheckPanelCA;
import com.temenos.ebank.pages.clientAquisition.step1.SingleJointPanel;
import com.temenos.ebank.pages.clientAquisition.step1.SortCodePanel;
import com.temenos.ebank.pages.clientAquisition.step1.Step1;
import com.temenos.ebank.pages.clientAquisition.wizard.CAWizardPage;

@SuppressWarnings("rawtypes")
public class Step1Test extends EbankPageTest{
	@Before
	public void setUp() {
		super.setUp();
		loadProperties(PhoneAndPrefix.class, Step1.class, CustomerDetailsPanel.class, IncomeDetailsPanel.class, EligibilityDetailsPanel.class, 
				ExistingAccountsPanel.class, SingleJointPanel.class, SortCodePanel.class, PreAccountCheckPanelCA.class);
	}

	/**
	 * Tests the required messages when submitting blank form
	 */
	@Test
	public void testContinueRequiredFields() {
		loadNewApplicationForm(ProductPagesAndLinks.CURRENT_ACCOUNT.getLink());
		tester.assertComponent("wizard:form", Form.class);
		tester.assertComponent("wizard:form:view:customerDetails", CustomerDetailsPanel.class);
		//Your details is not visible for new applications
		tester.assertInvisible("wizard:form:view:yourDetails");
		FormTester formTester = tester.newFormTester("wizard:form");
		//formTester.setValue(FieldPathsConstants.STEP1_CURRACCOUNT_ACC_CURRENCIES_FIRST_CURRENCY, "true");
		//formTester.select(FieldPathsConstants.STEP1_ANNUAL_INCOME, 1);
		formTester.submit("buttons:next");
		tester.assertRenderedPage(CAWizardPage.class);
		Form frm = formTester.getForm();
		
		tester.assertErrorMessages(new String[] {
				getRequiredMessageForFieldPath(frm, Step1Paths.CURRACCOUNT_ACC_CURRENCIES),
				getRequiredMessageForFieldPath(frm, Step1Paths.TITLE),
				getRequiredMessageForFieldPath(frm, Step1Paths.FIRSTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.LASTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.DOB_DAY),
				getRequiredMessageForFieldPath(frm, Step1Paths.DOB_MONTH),
				getRequiredMessageForFieldPath(frm, Step1Paths.DOB_YEAR),
				getRequiredMessageForFieldPath(frm, Step1Paths.MOBILE_PHONE_PREFIX),
				getRequiredMessageForFieldPath(frm, Step1Paths.GENDER),
				getRequiredMessageForFieldPath(frm, Step1Paths.MOBILE_PHONE_NUMBER),
				getRequiredMessageForFieldPath(frm, Step1Paths.EMAIL),
				getRequiredMessageForFieldPath(frm, Step1Paths.EMAIL2),
				getRequiredMessageForFieldPath(frm, Step1Paths.COUNTRY_OF_RESIDENCE),
				getRequiredMessageForFieldPath(frm, Step1Paths.NATIONALITY),
				getRequiredMessageForFieldPath(frm, Step1Paths.COUNTRY_OF_BIRTH),				
				getRequiredMessageForFieldPath(frm, Step1Paths.TOWN_OF_BIRTH),
				getRequiredMessageForFieldPath(frm, Step1Paths.MARITAL_STATUS),
				getRequiredMessageForFieldPath(frm, Step1Paths.EXISTING_CUSTOMER),
				getRequiredMessageForFieldPath(frm, Step1Paths.ANNUAL_INCOME),
				getRequiredMessageForFieldPath(frm, Step1Paths.DEPOSIT_AMMOUNT),
				});
	}
	
	
	/**
	 * Tests selecting more than 3 currencies 
	 */
	@Test
	public void testMultipleAccountCurrenciesCurrentAccount() {
		reset(serviceClientAcquisition);
		reset(serviceConfigParam);
		when(serviceConfigParam.getConfigParamTable()).thenAnswer(
				new Answer<ConfigParamTable>() {
					public ConfigParamTable answer (InvocationOnMock invocation) {
						ConfigParamTable cpt =  loadConfigParamFromXml();
						//allow user to select max 10 accounts
						cpt.set(ConfigParamTable.INTEGER.MAX_NO_ACCOUNTS, 10);
						return cpt;
					}
				});
		resumeXmlAppAndNavigateToStep("XYEyRcYJ", 1);
		FormTester formTester = tester.newFormTester("wizard:form");
		formTester.selectMultiple(Step1Paths.CURRACCOUNT_ACC_CURRENCIES, new int[] {0, 1, 2, 3}, true);		
		final Application []  a = new Application[1];
		when(serviceClientAcquisition.saveApplication(any(AcquisitionRequest.class))).thenAnswer(
			new Answer<AcquisitionResponse>() {
				public AcquisitionResponse answer (InvocationOnMock invocation) {
					Object[] args = invocation.getArguments();
					AcquisitionRequest ar = (AcquisitionRequest)args[0];
					a[0] = ar.getApplication();
					assertEquals(4, a[0].getAccountCurrencies().size());
					return new AcquisitionResponse(a[0], ResponseCode.ELIGIBLE_OK);
				}
			});		
		
		formTester.submit("buttons:next");
		tester.assertErrorMessages(new String[] { "ALERT_ELIGIBLE_OK" });
		verify(serviceClientAcquisition, times(1)).saveApplication(any(AcquisitionRequest.class));
	}
		
}


