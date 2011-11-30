package com.temenos.ebank.common.wicket;

import static org.mockito.Mockito.reset;

import org.junit.Test;

public class SummaryPageTest extends EbankPageTest {

	@Test
	public void testSummaryPageSoleData() {
		reset(serviceClientAcquisition);
		resumeXmlAppAndNavigateToStep("OcF8F5iM", 6);
	}
	
	@Test
	public void testSummaryPageJointData() {
		reset(serviceClientAcquisition);
		resumeXmlAppAndNavigateToStep("fe1cfd1b-b805-4a46-ae50-91fb931f0ea4", 6);
	}

	@Test
	public void testSummaryPageJointData2() {
		reset(serviceClientAcquisition);
		resumeXmlAppAndNavigateToStep("5DdZow6O", 6);
	}

	@Test
	public void testSameAddressForResidentialCorrespondenceFirstAndSecond() {
		reset(serviceClientAcquisition);
		resumeXmlAppAndNavigateToStep("XYEyRcYJ", 6);		
	}
}
