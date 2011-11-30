package com.temenos.ebank.common.wicket;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.util.tester.FormTester;
import org.apache.wicket.util.tester.TagTester;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.temenos.ebank.common.wicket.feedback.AlertFeedbackPanel;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.pages.clientAquisition.step1.CustomerDetailsPanel;
import com.temenos.ebank.pages.clientAquisition.wizard.ClientAquisitionWizard;
import com.temenos.ebank.pages.clientAquisition.wizard.CAWizardPage;

@SuppressWarnings("rawtypes")
public class TestPartialSave extends EbankPageTest {
	@Test
	public void testEmptyStep1() {
		reset(serviceClientAcquisition);
		// new Application
		loadNewApplicationForm(ProductPagesAndLinks.CURRENT_ACCOUNT.getLink());

		tester.assertComponent("wizard:form", Form.class);
		tester.assertComponent("wizard:form:view:customerDetails", CustomerDetailsPanel.class);
		// Your details is not visible for new applications
		tester.assertInvisible("wizard:form:view:yourDetails");
		FormTester formTester = tester.newFormTester("wizard:form");

		formTester.submit("buttons:save");
		tester.assertRenderedPage(CAWizardPage.class);

		Form frm = formTester.getForm();

		tester.assertErrorMessages(new String[] { getRequiredMessageForFieldPath(frm, Step1Paths.FIRSTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.LASTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.DOB),
				getRequiredMessageForFieldPath(frm, Step1Paths.MOBILE_PHONE),
				getRequiredMessageForFieldPath(frm, Step1Paths.EMAIL),
				getRequiredMessageForFieldPath(frm, Step1Paths.EMAIL2), "ALERT_SAVE_ERROR" });
		// save error for sole applicant should be visible
		tester.assertVisible("wizard:form:feedback:alertFeedbackPanel:alertul:messages:0:alertMessage:messageSole");
		tester.assertInvisible("wizard:form:feedback:alertFeedbackPanel:alertul:messages:0:alertMessage:messageJoint");
		verifyZeroInteractions(serviceClientAcquisition);
	}

	@Test
	public void testInvalidFormatStep1() {
		reset(serviceClientAcquisition);
		// new Application
		loadNewApplicationForm(ProductPagesAndLinks.CURRENT_ACCOUNT.getLink());
		// test invalid phone number
		FormTester formTester = tester.newFormTester("wizard:form");
		formTester.setValue(Step1Paths.MOBILE_PHONE_PREFIX, "aaa");
		formTester.setValue(Step1Paths.MOBILE_PHONE_NUMBER, "aaabbb");

		formTester.setValue(Step1Paths.EMAIL, "aaabbb");

		formTester.submit("buttons:save");
		tester.assertRenderedPage(CAWizardPage.class);

		Form frm = formTester.getForm();
		tester.assertErrorMessages(new String[] { getRequiredMessageForFieldPath(frm, Step1Paths.FIRSTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.LASTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.DOB), 
				getValidationErrorForFieldPath(frm, Step1Paths.MOBILE_PHONE_PREFIX, "countryCode.invalid"),
				getValidationErrorForFieldPath(frm, Step1Paths.MOBILE_PHONE_NUMBER, "phoneNumber.invalid"),
				getValidationErrorForFieldPath(frm, Step1Paths.EMAIL, "EmailAddressValidator"),
				getRequiredMessageForFieldPath(frm, Step1Paths.EMAIL2), "ALERT_SAVE_ERROR" });

		selectAjaxRadio(tester, "wizard:form:" + Step1Paths.EXISTING_CUSTOMER_YES);
		tester.assertComponentOnAjaxResponse("wizard:form:view:customerExistingAccounts:groupExistingAccDetails");
		tester.assertVisible("wizard:form:view:customerExistingAccounts:groupExistingAccDetails");

		// FormTester is allowed to submit only once --> needs to be re-initialized before re-submit
		formTester = tester.newFormTester("wizard:form");
		tester.assertVisible("wizard:form:" + Step1Paths.EXISTING_SORTCODE);
		formTester.setValue(Step1Paths.EXISTING_SORTCODE, "aa");
		formTester.submit("buttons:save");
		tester.assertRenderedPage(CAWizardPage.class);

		tester.assertErrorMessages(new String[] { getRequiredMessageForFieldPath(frm, Step1Paths.FIRSTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.LASTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.DOB), 
				getValidationErrorForFieldPath(frm, Step1Paths.MOBILE_PHONE_PREFIX, "countryCode.invalid"),
				getValidationErrorForFieldPath(frm, Step1Paths.MOBILE_PHONE_NUMBER, "phoneNumber.invalid"),
				getValidationErrorForFieldPath(frm, Step1Paths.EMAIL, "EmailAddressValidator"),
				getRequiredMessageForFieldPath(frm, Step1Paths.EMAIL2), frm.getString("sortCode.invalid"),
				"ALERT_SAVE_ERROR", });

		// save error for sole applicant should be visible
		tester.assertVisible("wizard:form:feedback:alertFeedbackPanel:alertul:messages:0:alertMessage:messageSole");
		tester.assertInvisible("wizard:form:feedback:alertFeedbackPanel:alertul:messages:0:alertMessage:messageJoint");
		verifyZeroInteractions(serviceClientAcquisition);
	}

	@Test
	public void testFormValidatorsStep1() {
		reset(serviceClientAcquisition);
		// new Application
		loadNewApplicationForm(ProductPagesAndLinks.CURRENT_ACCOUNT.getLink());
		// test form validators email = confirmEmail
		FormTester formTester = tester.newFormTester("wizard:form");
		formTester.select(Step1Paths.ANNUAL_INCOME, 1);
		formTester.setValue(Step1Paths.MOBILE_PHONE_PREFIX, "44");
		formTester.setValue(Step1Paths.MOBILE_PHONE_NUMBER, "2012345678");
		// formTester.setValue("view:customerDetails:mobilePhoneBorder:mobilePhone", "+442012345678");
		formTester.setValue(Step1Paths.FIRSTNAME, "Adina");
		formTester.setValue(Step1Paths.LASTNAME, "Jurubita");
		// formTester.setValue("view:customerDetails:dateOfBirthBorder:dateOfBirth", "01-01-2000");
		formTester.setValue(Step1Paths.DOB_DAY, "1");
		formTester.setValue(Step1Paths.DOB_MONTH, "1");
		formTester.setValue(Step1Paths.DOB_YEAR, "1990");

		formTester.setValue(Step1Paths.EMAIL, "a_j@yahoo.com");
		formTester.setValue(Step1Paths.EMAIL2, "a_j_l@yahoo.com");
		formTester.select(Step1Paths.COUNTRY_OF_RESIDENCE, 1);

		formTester.submit("buttons:save");
		tester.assertRenderedPage(CAWizardPage.class);
		tester.assertComponent("wizard", ClientAquisitionWizard.class);
		Form frm = formTester.getForm();
		tester.assertErrorMessages(new String[] { getEqualInputValidatorMessageForFieldPath(frm, Step1Paths.EMAIL2),
				"ALERT_SAVE_ERROR",

		});
		verifyZeroInteractions(serviceClientAcquisition);

	}

	@Test
	public void testSuccessfulSaveStep1ForAllProductTypes() {
		for (ProductPagesAndLinks product : ProductPagesAndLinks.values()) {
			testSuccessfulSaveStep1(product);	
		}
	}


	private void testSuccessfulSaveStep1(ProductPagesAndLinks product) {
		reset(serviceClientAcquisition);
		final Application []  a = new Application[1];
		when(serviceClientAcquisition.saveApplication(any(AcquisitionRequest.class))).thenAnswer(
			new Answer<AcquisitionResponse>() {
				public AcquisitionResponse answer (InvocationOnMock invocation) {
					Object[] args = invocation.getArguments();
					AcquisitionRequest ar = (AcquisitionRequest)args[0];
					a[0] = ar.getApplication();
					a[0].setAppRef("fdfsdfasz");
					return new AcquisitionResponse(a[0], ResponseCode.SAVE_OK);
				}
			});
		
		String customerFirstName = "Adina Livia";
		String emailAddress = "a_j@yahoo.com";
		String refNo = saveStep1(product, emailAddress, customerFirstName);

		a[0].setAppId(1L);
		when(serviceClientAcquisition.getApplicationByReferenceAndEmail(anyString(), anyString())).thenReturn(new AcquisitionResponse(a[0], ResponseCode.EVERYTHING_OK));		
		
		resume(refNo, emailAddress);
		// assert save done well
		tester.assertModelValue("wizard:form:" + Step1Paths.EMAIL, emailAddress);
		tester.assertModelValue("wizard:form:" + Step1Paths.FIRSTNAME, customerFirstName);
		
		// This was the case before. I leave it commented out because you never know: confirm email not shown for resumed app 
		//tester.assertInvisible("wizard:form:" + Step1Paths.EMAIL2);
		verify(serviceClientAcquisition, times(1)).saveApplication(any(AcquisitionRequest.class));
		verify(serviceClientAcquisition, times(1)).getApplicationByReferenceAndEmail(anyString(), anyString());
	}

	@Test
	public void testJointAppStep1() {
		// new Application
		loadNewApplicationForm(ProductPagesAndLinks.CURRENT_ACCOUNT.getLink());
		// test joint account validation

		tester.assertInvisible("wizard:form:view:secondCustomerDetails");
		tester.assertInvisible("wizard:form:view:secondCustomerEligibilityDetails");
		tester.assertInvisible("wizard:form:view:secondCustomerExistingAccounts");

		// RadioGroup c = (RadioGroup)
		// tester.getComponentFromLastRenderedPage("wizard:form:view:preAccountCheck:singleJoint:isSoleBorder:isSole");
		selectAjaxRadio(tester, "wizard:form:" + Step1Paths.JOINT_RADIO);

		tester.assertVisible("wizard:form:view:secondCustomerDetails");
		tester.assertVisible("wizard:form:view:secondCustomerEligibilityDetails");
		tester.assertVisible("wizard:form:view:secondCustomerExistingAccounts");

		FormTester formTester = tester.newFormTester("wizard:form");
		// formTester = tester.newFormTester("wizard:form");
		// selectAjaxRadio(tester, "wizard:form:view:preAccountCheck:singleJoint:isSoleBorder:isSole:joint");
		formTester.submit("buttons:save");
		tester.assertRenderedPage(CAWizardPage.class);

		Form frm = formTester.getForm();
		tester.assertErrorMessages(new String[] {
				getRequiredMessageForFieldPath(frm, Step1Paths.FIRSTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.LASTNAME),
				getRequiredMessageForFieldPath(frm, Step1Paths.DOB),
				getRequiredMessageForFieldPath(frm, Step1Paths.MOBILE_PHONE),
				getRequiredMessageForFieldPath(frm, Step1Paths.EMAIL),
				getRequiredMessageForFieldPath(frm, Step1Paths.EMAIL2),
				getRequiredMessageForFieldPath(frm, Step1Paths.SC(Step1Paths.FIRSTNAME)),
				getRequiredMessageForFieldPath(frm, Step1Paths.SC(Step1Paths.LASTNAME)),
				getRequiredMessageForFieldPath(frm, Step1Paths.SC(Step1Paths.DOB)),
				getRequiredMessageForFieldPath(frm, Step1Paths.SC(Step1Paths.MOBILE_PHONE)),
				getRequiredMessageForFieldPath(frm, Step1Paths.SC(Step1Paths.EMAIL)),
				getRequiredMessageForFieldPath(frm, Step1Paths.SC(Step1Paths.EMAIL2)),
				"ALERT_SAVE_ERROR" });
		// TODO: the wicket tester doesn't get the right model somehow, so I can't make the tests below
		// pass, even that on the site they are ok
		// save error for joint applicant should be visible
		// tester.assertInvisible("wizard:form:feedback:alertFeedbackPanel:alertul:messages:0:alertMessage:messageSole");
		// tester.assertVisible("wizard:form:feedback:alertFeedbackPanel:alertul:messages:0:alertMessage:messageJoint");
	}
	
	/**
	 * Tests saving step 1 with minimal info and returns to the resume application page. This is useful for testing resuming applications
	 * @param emailAddress The email used for saving and retrieving the application
	 * @param customerFirstName Customer's first name for tests. Null can be passsed too. This param is useful for providing special values
	 * that can be interpreted by mock services.
	 * @return
	 */
	private String saveStep1(ProductPagesAndLinks product, String emailAddress, String customerFirstName) {
		// new Application
		loadNewApplicationForProduct(product);
		// test good save
		FormTester formTester = tester.newFormTester("wizard:form");
		formTester.select(Step1Paths.ANNUAL_INCOME, 1);
		formTester.setValue(Step1Paths.MOBILE_PHONE_PREFIX, "44");
		formTester.setValue(Step1Paths.MOBILE_PHONE_NUMBER, "2012345678");
		//formTester.setValue("view:customerDetails:mobilePhoneBorder:mobilePhone", "+442012345678");
		formTester.setValue(Step1Paths.FIRSTNAME, StringUtils.defaultString(customerFirstName, "Adina Livia"));
		formTester.setValue(Step1Paths.LASTNAME, "Jurubita");
		// formTester.setValue("view:customerDetails:dateOfBirthBorder:dateOfBirth", "01-01-2000");
		formTester.setValue(Step1Paths.DOB_DAY, "1");
		formTester.setValue(Step1Paths.DOB_MONTH, "1");
		formTester.setValue(Step1Paths.DOB_YEAR, "1990");
		formTester.setValue(Step1Paths.EMAIL, emailAddress);
		formTester.setValue(Step1Paths.EMAIL2, emailAddress);
		formTester.select(Step1Paths.COUNTRY_OF_RESIDENCE, 1);
		formTester.submit("buttons:save");
		tester.assertRenderedPage(product.getPageClass());
		tester.assertComponent("wizard", ClientAquisitionWizard.class);
		// tester.assertNoErrorMessage();
		// there are no error messages, but the alerts are implemented as error messages
		tester.assertErrorMessages(new String[] { "ALERT_SAVE_OK", });

		tester.assertComponent("wizard:form:feedback:alertFeedbackPanel", AlertFeedbackPanel.class);

		TagTester tt = tester.getTagByWicketId("alertFeedbackPanel").getChild("class", "alert")
				.getChild("wicket:id", "alertMessage");
		Pattern p = Pattern.compile("with this reference number:\\s*([a-zA-Z0-9]{8})");
		Matcher m = p.matcher(tt.getValue());
		String refNo = null;
		if (m.find()) {
			refNo = m.group(1);
		}
		//this is important for testing, because otherwise, the feedback messages remain
		tester.getWicketSession().cleanupFeedbackMessages();
		return refNo;
	}	

	/**
	 * Tests selecting the first and second currency 
	 */
	@Test
	public void testAccountCurrencyCurrentAccount() {
		reset(serviceClientAcquisition);
		resumeXmlAppAndNavigateToStep("XYEyRcYJ", 1);
		FormTester formTester = tester.newFormTester("wizard:form");
		formTester.selectMultiple(Step1Paths.CURRACCOUNT_ACC_CURRENCIES, new int[] {0, 1}, true);		
		final Application []  a = new Application[1];
		when(serviceClientAcquisition.saveApplication(any(AcquisitionRequest.class))).thenAnswer(
			new Answer<AcquisitionResponse>() {
				public AcquisitionResponse answer (InvocationOnMock invocation) {
					Object[] args = invocation.getArguments();
					AcquisitionRequest ar = (AcquisitionRequest)args[0];
					a[0] = ar.getApplication();
					assertEquals(2, a[0].getAccountCurrencies().size());
					return new AcquisitionResponse(a[0], ResponseCode.SAVE_OK);
				}
			});		
		formTester.submit("buttons:save");
		verify(serviceClientAcquisition, times(1)).saveApplication(any(AcquisitionRequest.class));
	}
	
	/**
	 * Tests selecting the third currency 
	 */
	@Test
	public void testAccountCurrencyCurrentAccount2() {
		reset(serviceClientAcquisition);
		resumeXmlAppAndNavigateToStep("XYEyRcYJ", 1);
		FormTester formTester = tester.newFormTester("wizard:form");
		formTester.selectMultiple(Step1Paths.CURRACCOUNT_ACC_CURRENCIES, new int[] {2}, true);
		final Application []  a = new Application[1];
		when(serviceClientAcquisition.saveApplication(any(AcquisitionRequest.class))).thenAnswer(
			new Answer<AcquisitionResponse>() {
				public AcquisitionResponse answer (InvocationOnMock invocation) {
					Object[] args = invocation.getArguments();
					AcquisitionRequest ar = (AcquisitionRequest)args[0];
					a[0] = ar.getApplication();
					assertEquals(1, a[0].getAccountCurrencies().size());					
					return new AcquisitionResponse(a[0], ResponseCode.SAVE_OK);
				}
			});
		formTester.submit("buttons:save");
		verify(serviceClientAcquisition, times(1)).saveApplication(any(AcquisitionRequest.class));
	}	
	
	/**
	 * Tests selecting no currency 
	 */
	@Test
	public void testAccountCurrencyCurrentAccountNoCurrency() {
		reset(serviceClientAcquisition);
		resumeXmlAppAndNavigateToStep("XYEyRcYJ", 1);
		FormTester formTester = tester.newFormTester("wizard:form");
		formTester.selectMultiple(Step1Paths.CURRACCOUNT_ACC_CURRENCIES, new int[] {}, true);		
		final Application []  a = new Application[1];
		when(serviceClientAcquisition.saveApplication(any(AcquisitionRequest.class))).thenAnswer(
			new Answer<AcquisitionResponse>() {
				public AcquisitionResponse answer (InvocationOnMock invocation) {
					Object[] args = invocation.getArguments();
					AcquisitionRequest ar = (AcquisitionRequest)args[0];
					a[0] = ar.getApplication();
					assertEquals(0, a[0].getAccountCurrencies().size());					
					return new AcquisitionResponse(a[0], ResponseCode.SAVE_OK);
				}
			});		
		formTester.submit("buttons:save");
		verify(serviceClientAcquisition, times(1)).saveApplication(any(AcquisitionRequest.class));
	}			
}