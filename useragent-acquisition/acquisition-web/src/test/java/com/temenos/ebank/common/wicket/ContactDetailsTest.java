package com.temenos.ebank.common.wicket;

import static org.mockito.Mockito.reset;

import org.apache.wicket.util.tester.FormTester;
import org.junit.Test;

import com.temenos.ebank.pages.clientAquisition.wizard.ClientAquisitionWizard;
import com.temenos.ebank.pages.clientAquisition.wizard.CAWizardPage;

public class ContactDetailsTest extends EbankPageTest {

	// test created for address panel not passing the updated value to the parent model
	// after submitting step2 with changed values in the address panel,
	// make sure the changes were persisted
	@Test
	public void testContactDetailsUpdateResidentialAddress() {
		reset(serviceClientAcquisition);
		
		String refNo = "fe1cfd1b-b805-4a46-ae50-91fb931f0ea4";
		//String emailAddress = "a_j_l@yahoo.com";
		resumeXmlAppAndNavigateToStep(refNo, 2);
		//resumeAndNavigateToStep2(refNo, emailAddress);
		
		FormTester  formTester = tester.newFormTester("wizard:form");
		// change line1
		formTester.setValue("view:contactDetails:addressContainer:address:addressPanel:line1Border:line1", "abcdef");
		// submit page
		formTester.submit("buttons:next");

		// display step3
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
		// go back to step2
		formTester = tester.newFormTester("wizard:form");
		formTester.submit("buttons:previous");

		// display step2
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();

		// check if displayed value is same as the one submitted
		// tester.debugComponentTrees("line1");
		tester.assertModelValue(
				"wizard:form:view:contactDetails:addressContainer:address:addressPanel:line1Border:line1", "abcdef");

		formTester = tester.newFormTester("wizard:form");
		// change the value back for the next run
		formTester.setValue("view:contactDetails:addressContainer:address:addressPanel:line1Border:line1", "fedcba");
		formTester.submit("buttons:next");

		// print the generated HTML (remove comment if necessary)
		// System.out.print( tester.getServletResponse().getDocument() );

		tester.assertRenderedPage(CAWizardPage.class);
		tester.assertComponent("wizard", ClientAquisitionWizard.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
	}
	/*
	@Test
	public void testContactDetailsUpdatePreviousAddress() {
		

		String refNo = "fe1cfd1b-b805-4a46-ae50-91fb931f0ea4";
		String emailAddress = "a_j_l@yahoo.com";
		resumeAndNavigateToStep2(refNo, emailAddress);

				tester.assertInvisible("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses");
		tester.assertModelValue("wizard:form:view:jointContactDetails:emailAddressBorder:emailAddress", "s_j_f@yahoo.com");

		// change residential address duration
		//FormTester formTester = tester.newFormTester("wizard:form");
		//select number of years
		String currentDurationYears = "view:contactDetails:yearsAndMonthsWithAddressBorder:yearsAndMonthsWithAddress:nrYearsBorder:nrYears";
		//formTester.select(currentDurationYears, 0);
		changeYearsAndMonths(tester, "wizard:form:"+currentDurationYears);


		String currentDurationMonths = "view:contactDetails:yearsAndMonthsWithAddressBorder:yearsAndMonthsWithAddress:nrMonthsBorder:nrMonths";
		//formTester.select(currentDurationMonths,7);
		changeYearsAndMonths(tester, "wizard:form:"+currentDurationMonths);
		//tester.executeAjaxEvent("wizard:form:view:contactDetails:yearsAndMonthsWithAddressBorder:yearsAndMonthsWithAddress:nrMonthsBorder:nrMonths", "onchange");

		//make sure previous address is displayed
		tester.assertVisible("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses");

		//scenario below cannot be tested by using plain JUnit. There is a solution of testing AJAX with selenium
		//http://wicketpagetest.sourceforge.net/

		//send postcode and house number to PAF service
		FormTester innerFormTester = tester.newFormTester("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:searchPostCode:searchPostCodeForm");
		innerFormTester.setValue("postCodeBorder:postCode", "11");
		innerFormTester.setValue("houseNoNmBorder:houseNoNm", "11");
		//innerFormTester.submit();
		tester.clickLink("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:searchPostCode:searchPostCodeForm:searchPostCodeBt", true);
		tester.executeAjaxEvent("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:searchPostCode:searchPostCodeForm:searchPostCodeBt", "onclick");
		//innerFormTester.submit();
		//tester.clickLink( "wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:innerPanel:searchPostCodeForm:searchPostCodeBt" , false);
		
		//data still there?
		tester.assertModelValue("wizard:form:view:contactDetails:yearsAndMonthsWithAddressBorder:yearsAndMonthsWithAddress:nrYearsBorder:nrYears", 0);
		
		//select first address
		tester.assertVisible("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:innerPanel:selectAddressForm");
		innerFormTester = tester.newFormTester("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:innerPanel:selectAddressForm");
		innerFormTester.select( "returnedAddressesBorder:returnedAddresses" , 1);
		//innerFormTester.submit("selectAddressBt");
		tester.executeAjaxEvent("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:innerPanel:selectAddressForm:selectAddressBt", "onclick");

		//data still there?
		tester.assertModelValue("wizard:form:view:jointContactDetails:emailAddressBorder:emailAddress", "s_j_f@yahoo.com");
		
		//check that the selected address is shown in form
		tester.assertVisible("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:innerPanel:line1Border:line1");
		tester.assertVisible("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:innerPanel:line2Border:line2");
		String line1Value = formTester.getTextComponentValue("view:contactDetails:previousAddressContainer:previousAddresses:1:previousAddressRepeated:innerPanel:line1Border:line1");
		assertNotNull( line1Value );
		formTester.select("view:contactDetails:previousAddressContainer:previousAddresses:1:durationBorder:duration:nrYearsBorder:nrYears", 5);
		tester.executeAjaxEvent("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:durationBorder:duration:nrYearsBorder:nrYears", "onchange");
		formTester.select("view:contactDetails:previousAddressContainer:previousAddresses:1:durationBorder:duration:nrMonthsBorder:nrMonths", 5);
		tester.executeAjaxEvent("wizard:form:view:contactDetails:previousAddressContainer:previousAddresses:1:durationBorder:duration:nrMonthsBorder:nrMonths", "onchange");

		tester.assertComponent("wizard", ClientAquisitionWizard.class);
		
        // submit page
		formTester.submit("buttons:next");

		tester.assertRenderedPage(CurrentAccountWizardPage.class);
		tester.assertComponent("wizard", ClientAquisitionWizard.class);
		tester.assertNoErrorMessage();
		tester.assertNoInfoMessage();
	}
	*/
	
	// this test asserts that the years and months component does not change after save
	// which is pretty common sense, but well, in our bug, it changes value to -1 month
	@Test
	public void testYearsAndMonths() {
		reset(serviceClientAcquisition);
		String refNo = "fe1cfd1b-b805-4a46-ae50-91fb931f0ea4";
		//String emailAddress = "a_j_l@yahoo.com";
		resumeXmlAppAndNavigateToStep(refNo, 2);
		
		//resumeAndNavigateToStep2(refNo, emailAddress);

		Integer nrYears = (Integer)tester.getComponentFromLastRenderedPage("wizard:form:view:contactDetails:yearsAndMonthsWithAddressBorder:yearsAndMonthsWithAddress:nrYearsBorder:nrYears").getDefaultModelObject();
		Integer nrMonths = (Integer)tester.getComponentFromLastRenderedPage("wizard:form:view:contactDetails:yearsAndMonthsWithAddressBorder:yearsAndMonthsWithAddress:nrMonthsBorder:nrMonths").getDefaultModelObject();

		FormTester formTester = tester.newFormTester("wizard:form");
		formTester.submit("buttons:save");

		tester.assertModelValue("wizard:form:view:contactDetails:yearsAndMonthsWithAddressBorder:yearsAndMonthsWithAddress:nrYearsBorder:nrYears", nrYears);
		tester.assertModelValue("wizard:form:view:contactDetails:yearsAndMonthsWithAddressBorder:yearsAndMonthsWithAddress:nrMonthsBorder:nrMonths", nrMonths);
	}

}
