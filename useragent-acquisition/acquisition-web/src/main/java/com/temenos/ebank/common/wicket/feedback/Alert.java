package com.temenos.ebank.common.wicket.feedback;

import java.io.Serializable;

import org.apache.wicket.model.IModel;

import com.temenos.ebank.exceptions.ResponseCode;
import com.temenos.ebank.message.AcquisitionResponse;

/**
 * Encapsulates an alert condition. An alert is a validation outcome (error or success) raised
 * by the backed or a technical error encountered in the application
 * 
 * @author vionescu
 */
public class Alert implements Serializable {
	private static final long serialVersionUID = 1L;

	private ResponseCode responseCode;
	private IModel<?> pageModel;
	private Serializable additionalInfo;


	/**
	 * Returns the additional info associated with an acquisition response
	 * @return
	 */
	public Serializable getAdditionalInfo() {
		return additionalInfo;
	}


	/**
	 * Constructs a new Alert using the specified code
	 * 
	 * @param code
	 *            alert code
	 */
	public Alert(ResponseCode responseCode) {
		this.responseCode = responseCode;
	}


	/**
	 * Constructs a new Alert using the specified ResponseCode and model
	 * @param responseCode
	 *            Response code return by the back-end
	 * @param pageModel
	 *            The model needed by the alert panel for adding dynamic info
	 */
	public Alert(ResponseCode responseCode, IModel<?> pageModel) {
		this.responseCode = responseCode;
		this.pageModel = pageModel;
	}

	/**
	 * Constructs a new Alert using the specified ResponseCode
	 * @param responseCode
	 *            Response code
	 */
	public Alert(AcquisitionResponse aquisitionResponse) {
		this.responseCode = aquisitionResponse.getResponseCode();
		this.additionalInfo = aquisitionResponse.getAdditionalInfo();
	}
	
	/**
	 * Constructs a new Alert using the specified ResponseCode and model
	 * @param aquisitionResponse
	 *            The acquisition response bean returned by the back-end
	 * @param pageModel
	 *            The model needed by the alert panel for adding dynamic info
	 */
	public Alert(AcquisitionResponse aquisitionResponse, IModel<?> pageModel) {
		this.responseCode = aquisitionResponse.getResponseCode();
		this.additionalInfo = aquisitionResponse.getAdditionalInfo();
		this.pageModel = pageModel;
	}
	
	
	public ResponseCode getResponseCode() {
		return responseCode;
	}

	public IModel<?> getPageModel() {
		return pageModel;
	}

	@Override
	public String toString() {
		return String.format("ALERT_%s", this.responseCode.toString());
	}

}
