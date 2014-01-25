package com.temenos.ebank.services.interfaces.clientAquisition;

import com.temenos.ebank.message.AcquisitionRequest;
import com.temenos.ebank.message.AcquisitionResponse;

/**
 * Interface to the cross-sell services exposed by the back office
 * 
 * @author vbeuran
 * 
 */
public interface IServiceCrossSell {
	public abstract AcquisitionResponse getCrossSellProducts(AcquisitionRequest request);

	public abstract AcquisitionResponse createCrossSell(AcquisitionRequest request);
	
	public abstract boolean postMeMyDocuments(String appRef, boolean mainApplication );
}
