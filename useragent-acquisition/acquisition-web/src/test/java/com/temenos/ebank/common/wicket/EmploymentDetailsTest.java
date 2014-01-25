package com.temenos.ebank.common.wicket;

import static org.mockito.Mockito.reset;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

public class EmploymentDetailsTest extends EbankPageTest {
	@Test
	public void testEmploymentDetailsShowOtherEmploymentStatus() {
		reset(serviceClientAcquisition);
		//reset(serviceClientAcquisition);
		
		String refNo = "fe1cfd1b-b805-4a46-ae50-91fb931f0ea4";
		//String emailAddress = "a_j_l@yahoo.com";
		resumeXmlAppAndNavigateToStep(refNo, 3);
		//resumeAndNavigateToStep3(refNo, emailAddress);
		//switch to other employment status
		FormTester formTester = tester.newFormTester("wizard:form");
		formTester.select("view:customerEmploymentPanel:employmentStatusBorder:employmentStatus", 3);
		tester.executeAjaxEvent("wizard:form:view:customerEmploymentPanel:employmentStatusBorder:employmentStatus", "onchange");
	}
}
