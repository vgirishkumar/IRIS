/**
 * 
 */
package com.temenos.ebank.services.impl.clientAquisition;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.temenos.ebank.dao.interfaces.application.IApplicationDao;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.Customer;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AccountCreationResponse;
import com.temenos.ebank.message.AccountDetails;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceClientAquistion;
import com.temenos.ebank.services.interfaces.random.IReferenceGenerator;

/**
 * Customer Aquisition
 * 
 * @author vionescu
 * 
 */
public class ServiceClientAquisitionImpl implements IServiceClientAquistion {
	
	protected Log logger = LogFactory.getLog(getClass());
	private IApplicationDao applicationDao;
	private IReferenceGenerator referenceGenerator;
		
	public void setReferenceGenerator(IReferenceGenerator referenceGenerator) {
		this.referenceGenerator = referenceGenerator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.ebank.services.interfaces.clientAquisition.IServiceClientAquistion#getApplicationByReference(java
	 * .lang.String, java.lang.String)
	 */
	public AcquisitionResponse getApplicationByReferenceAndEmail(String reference, String email) {
		//TODO: trebuie tot cu aquisition response facut
		AcquisitionResponse acquisitionResponse = new AcquisitionResponse();
		try {
			Application application = applicationDao.getByReferenceAndEmail(reference, email);
			acquisitionResponse.setApplication(application);
			acquisitionResponse.setResponseCode(ResponseCode.EVERYTHING_OK);
			if (application == null) {
				acquisitionResponse.setResponseCode(ResponseCode.INCORRECT_EMAIL_OR_REFNO);
				return acquisitionResponse;
			}
			
			boolean isExpired = false;
			if (application.getCustomer() == null) {
				//this logic is for mock purposes
				isExpired = true;
			}
			
			//this logic is for mock purposes
			isExpired =  "#MOCK_EXPIRED#".equals(application.getCustomer().getFirstName());
			if (isExpired) {
				acquisitionResponse.setResponseCode(ResponseCode.REFNO_EXPIRED);
				return acquisitionResponse;
			}
	
			boolean isCompleted = false;
			//this logic is for mock purposes
			isCompleted =  "#MOCK_COMPLETED#".equals(application.getCustomer().getFirstName());
			
			if (isCompleted) {
				acquisitionResponse.setResponseCode(ResponseCode.REFNO_UNAVAILABLE);
				return acquisitionResponse;
			}
		} catch (Exception e) {
			logger.error("Error retrieving application", e);
			acquisitionResponse.setResponseCode(ResponseCode.TECHNICAL_ERROR);
		}
		return acquisitionResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.temenos.ebank.services.impl.com.temenos.ebank.pages.clientAquisition.IServiceClientAquistion#saveApplication
	 * (com.temenos.ebank.message.AcquisitionRequest)
	 */
	public AcquisitionResponse saveApplication(AcquisitionRequest acquisitionRequest) {
		/* generate a unique reference number, used for retrieval */
		Application a = acquisitionRequest.getApplication();
		if (StringUtils.isBlank(a.getAppRef())) {
			String reference;
			// loop the reference generation in order to make sure there is no colision with previously saved
			// applications.
			do {
				reference = referenceGenerator.newReference();
			} while (applicationDao.getCountByReference(reference) != 0);
			a.setAppRef(reference);
		}

		if (Boolean.TRUE.equals(a.getIsSole())) {
			// in case of sole application, explicitly remove the joint customer
			Customer secondCustomer = a.getSecondCustomer();
			if (secondCustomer != null && secondCustomer.getCustId() != null) {
				// From the hibernate doc : Note that single valued, many-to-one and one-to-one, associations do not
				// support orphan delete.
				a.setSecondApplicantId(null);
				a.setSecondCustomer(null);
				applicationDao.delete(secondCustomer);
			}
		}
		applicationDao.store(a);
		String requestCode = acquisitionRequest.getRequestCode();
		boolean isSave = "S".equalsIgnoreCase(requestCode.substring(0,1));
		int stepNo = Integer.parseInt(requestCode.substring(1, 2));
		ResponseCode responseCode = ResponseCode.EVERYTHING_OK;
		if (isSave) {
			responseCode = ResponseCode.SAVE_OK;
		} else {
			if (stepNo == 1) {
				responseCode = ResponseCode.ELIGIBLE_OK;
			}
		}
		//create a dummy AcquisitionResponse to comply with the interface
		AcquisitionResponse acquisitionResponse = new AcquisitionResponse(a, responseCode);
		acquisitionResponse.setApplication( a );
		
		//like this you treat the request for finishing wizard
		//populate the response with the list of accounts
		if("C6".equalsIgnoreCase(acquisitionRequest.getRequestCode())){
			AccountCreationResponse accResponse = new AccountCreationResponse();
			accResponse.setAccountList(new ArrayList<AccountDetails>(Arrays.asList(new AccountDetails("EUR", "111-3-22", "889191283", "uu718911I"),
																	new AccountDetails("GBP", "313-3-51", "127361611", "ow122941L"))));
			//accResponse.setUnderlyingAccount(new AccountDetailsUnderlying("GBP", "313-3-51", "127361611", "ow122941L", "extraordinary category" ));																	
			accResponse.setUserId("SmithW");
			accResponse.setDocumentList(new ArrayList<String>(Arrays.asList("BI QLA", "Birth Certificate")));
			acquisitionResponse.setAdditionalInfo(accResponse);
			//accResponse.setSecondDocumentList(new ArrayList<String>(Arrays.asList("Funny Hat certificate", "Criminal record")));
		}
		return acquisitionResponse;
	}

	/* Spring setters */
	public void setApplicationDao(IApplicationDao applicationDao) {
		this.applicationDao = applicationDao;
	}
	/* end Spring setters */
}
