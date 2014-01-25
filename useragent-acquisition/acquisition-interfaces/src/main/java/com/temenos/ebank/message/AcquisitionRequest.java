package com.temenos.ebank.message;

import java.io.Serializable;

import com.temenos.ebank.domain.Application;

/**
 * @author raduf
 * Object created
 *
 */
public class AcquisitionRequest implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AcquisitionRequest(Application application, String requestCode) {
		super();
		this.application = application;
		this.requestCode = requestCode;
	}

	private Application application;
	private String requestCode;
	private Serializable additionalInfo;
	private String language;

	public void setApplication(Application application) {
		this.application = application;
	}

	public Application getApplication() {
		return application;
	}

	public void setRequestCode(String requestCode) {
		this.requestCode = requestCode;
	}

	public String getRequestCode() {
		return requestCode;
	}
	public void setAdditionalInfo(Serializable additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Serializable getAdditionalInfo() {
		return additionalInfo;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}
}
