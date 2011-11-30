package com.temenos.ebank.common.wicket;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.pages.clientAquisition.resumeApplication.ResumeApplication;
import com.temenos.ebank.pages.clientAquisition.wizard.ClientAquisitionWizard;
import com.temenos.ebank.pages.clientAquisition.wizard.CAWizardPage;
import com.temenos.ebank.pages.pageMasters.YourDetailsPanel;

/**
 * Test for resuming applications
 * @author vionescu
 *
 */
public class ResumeApplicationTest extends EbankPageTest {
	
	@Override
	public void setUp() {
		super.setUp();
	}
	
	@Test
	public void testResumeApplication() {
		reset(serviceClientAcquisition);
		//mock service response for resume
		Application applicationOk = new Application();
		applicationOk.setAppId(1L);
		applicationOk.setProductRef(ProductType.INTERNATIONAL.getCode());
		AcquisitionResponse acquisitionResponseOk = new AcquisitionResponse(applicationOk, ResponseCode.EVERYTHING_OK);
		when(serviceClientAcquisition.getApplicationByReferenceAndEmail(anyString(), anyString())).thenReturn(acquisitionResponseOk);
		
		resume("XYEyRcYJ", "a_j_l@yahoo.com");
		
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
		tester.assertRenderedPage(CAWizardPage.class);
		tester.assertComponent("wizard", ClientAquisitionWizard.class);
		//Your details is visible for resumed applications
		tester.assertComponent("wizard:form:view:yourDetails", YourDetailsPanel.class);
		verify(serviceClientAcquisition, times(1)).getApplicationByReferenceAndEmail("XYEyRcYJ", "a_j_l@yahoo.com");
	}
	
	/**
	 * Tests the correct display of Alert 0012 when an application is expired
	 */
	@Test
	public void testExpiredApplication() {
		reset(serviceClientAcquisition);		
		//mock service response for resume
		Application application = new Application();
//		application.setAppId(1L);
//		application.setProductRef(ProductType.INTERNATIONAL.getCode());
		AcquisitionResponse acquisitionResponse = new AcquisitionResponse(application, ResponseCode.REFNO_EXPIRED);
		when(serviceClientAcquisition.getApplicationByReferenceAndEmail(anyString(), anyString())).thenReturn(acquisitionResponse);
		
		String emailAddress = "a_j_l@yahoo.com";
		// TODO: #MOCK_EXPIRED# is a hardcoded value used in the mock service to simulate an expiration condition
		//change the expiration test when implemented in T24		
		//String refno = saveStep1(emailAddress, "#MOCK_EXPIRED#");
		String refno = "someRefno";
		resume(refno, emailAddress);
		tester.assertRenderedPage(ResumeApplication.class);
		tester.assertErrorMessages(new String[] { "ALERT_REFNO_EXPIRED" });
		verify(serviceClientAcquisition, times(1)).getApplicationByReferenceAndEmail(refno, emailAddress);
	}

	/**
	 * Tests the correct display of Alert 0013 when an application is completed
	 */
	@Test
	public void testCompletedApplication() {
		reset(serviceClientAcquisition);
		Application application = new Application();
//		application.setAppId(1L);
//		application.setProductRef(ProductType.INTERNATIONAL.getCode());
		AcquisitionResponse acquisitionResponse = new AcquisitionResponse(application, ResponseCode.REFNO_UNAVAILABLE);
		when(serviceClientAcquisition.getApplicationByReferenceAndEmail(anyString(), anyString())).thenReturn(acquisitionResponse);
		
		String emailAddress = "a_j_l@yahoo.com";
		// TODO: #MOCK_COMPLETED# is a hardcoded value used in the mock service to simulate an applicaiton completed condition
		//change the expiration test when implemented in T24		
		//String refno = saveStep1(emailAddress, "#MOCK_COMPLETED#");
		String refno = "someRefno";
		resume(refno, emailAddress);
		tester.assertRenderedPage(ResumeApplication.class);
		tester.assertErrorMessages(new String[] { "ALERT_REFNO_UNAVAILABLE" });
		verify(serviceClientAcquisition, times(1)).getApplicationByReferenceAndEmail(refno, emailAddress);		
	}
	
}
