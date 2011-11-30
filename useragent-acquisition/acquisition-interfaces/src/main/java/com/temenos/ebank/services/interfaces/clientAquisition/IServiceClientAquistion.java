package com.temenos.ebank.services.interfaces.clientAquisition;

import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;

public interface IServiceClientAquistion {

	public abstract AcquisitionResponse getApplicationByReferenceAndEmail(String reference, String email);

	public abstract AcquisitionResponse saveApplication(AcquisitionRequest acquisitionRequest);

}