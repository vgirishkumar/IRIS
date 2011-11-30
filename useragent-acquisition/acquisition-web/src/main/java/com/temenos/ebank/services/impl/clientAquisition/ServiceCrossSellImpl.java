package com.temenos.ebank.services.impl.clientAquisition;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.CrossSellProduct;
import com.temenos.ebank.domain.ProductType;
import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AccountCreationResponse;
import com.temenos.ebank.message.AccountDetails;
import com.temenos.ebank.message.AccountDetailsUnderlying;
import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceCrossSell;
import com.temenos.ebank.services.interfaces.random.IReferenceGenerator;

public class ServiceCrossSellImpl implements IServiceCrossSell {
	protected Log logger = LogFactory.getLog(getClass());
	
	private IReferenceGenerator referenceGenerator;
	
	public void setReferenceGenerator(IReferenceGenerator referenceGenerator) {
		this.referenceGenerator = referenceGenerator;
	}

	// TODO : this is just a mock method used for development purposes
	public AcquisitionResponse getCrossSellProducts(AcquisitionRequest req) {
		ArrayList<CrossSellProduct> a = new ArrayList<CrossSellProduct>(Arrays.asList(new CrossSellProduct(null, null,
				ProductType.REGULAR_SAVER.getCode(), null), new CrossSellProduct(null, null,
				ProductType.RASA.getCode(), null)));
		AcquisitionResponse response = new AcquisitionResponse();
		response.setCrossSellProducts(a);
		return response;
	}

	//like this you treat the request for finishing wizard
	//populate the response with the list of accounts
	public AcquisitionResponse createCrossSell(AcquisitionRequest req) {
		ResponseCode responseCode = ResponseCode.EVERYTHING_OK;
		Application a = req.getApplication();
		a.setAppRef(referenceGenerator.newReference());
		//create a dummy AcquisitionResponse for StepX3;  
		AcquisitionResponse acquisitionResponse = new AcquisitionResponse(a, responseCode);
		AccountCreationResponse accResponse = new AccountCreationResponse();
		accResponse.setAccountList(new ArrayList<AccountDetails>(Arrays.asList(new AccountDetails("EUR", "111-3-22", "889191283", "uu718911I"),
																new AccountDetails("GBP", "313-3-51", "127361611", "ow122941L"))));
		accResponse.setUnderlyingAccount(new AccountDetailsUnderlying("GBP", "313-3-51", "127361611", "ow122941L", "extraordinary category" ));
		accResponse.setUserId("SmithW");
		accResponse.setDocumentList(new ArrayList<String>(Arrays.asList("BI QLA", "Birth certificate")));
		acquisitionResponse.setAdditionalInfo(accResponse);
		return acquisitionResponse;
	}

	public boolean postMeMyDocuments(String appRef, boolean mainApplication) {
		return false;
	}	
}