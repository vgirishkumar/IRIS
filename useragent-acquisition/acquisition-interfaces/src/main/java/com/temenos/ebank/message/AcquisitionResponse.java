package com.temenos.ebank.message;

import java.io.Serializable;
import java.util.List;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.CrossSellProduct;
import com.temenos.ebank.exceptions.ResponseCode;
/**
 * Wrapper for the response received from the back-end (T24) when saving or updating a client aquisition application
 * @author vionescu
 *
 */
public class AcquisitionResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public AcquisitionResponse() {
	}
	
	
	/**
	 * Constructs a response given a client acquisition application and a response code
	 * @param application a client acquisition application
	 * @param responseCode the response code received from the back-end
	 */
	public AcquisitionResponse(Application application, ResponseCode responseCode) {
		this.application = application;
		this.responseCode = responseCode;
	}

	/**
	 * Constructs a response given a client acquisition application, a response code, and an object holding free-form additional info
	 * @param application a client acquisition application
	 * @param responseCode the response code received from the back-end
	 * @param additionalInfo an object holding free-form additional info
	 */
	public AcquisitionResponse(Application application, ResponseCode responseCode, Serializable additionalInfo) {
		this.application = application;
		this.responseCode = responseCode;
		this.additionalInfo = additionalInfo;
	}
	
	private Application application;
	private ResponseCode responseCode;
	private Serializable additionalInfo;
	private List<CrossSellProduct> crossSellProducts;

	public void setApplication(Application application) {
		this.application = application;
	}

	public Application getApplication() {
		return application;
	}

	public void setResponseCode(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}

	public ResponseCode getResponseCode() {
		return responseCode;
	}

	public void setAdditionalInfo(Serializable additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Serializable getAdditionalInfo() {
		return additionalInfo;
	}


	public List<CrossSellProduct> getCrossSellProducts() {
		return crossSellProducts;
	}


	public void setCrossSellProducts(List<CrossSellProduct> crossSellProducts) {
		this.crossSellProducts = crossSellProducts;
	}

}
