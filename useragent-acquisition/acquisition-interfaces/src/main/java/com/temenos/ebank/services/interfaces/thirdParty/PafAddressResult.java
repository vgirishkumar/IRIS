package com.temenos.ebank.services.interfaces.thirdParty;

import java.util.List;

import com.temenos.ebank.domain.Address;

public class PafAddressResult {

	public PafAddressResult(PafResultCode errorCode, List<Address> addresses) {
		this.errorCode = errorCode;
		this.addresses = addresses;
	}

	private PafResultCode errorCode;
	private List<Address> addresses;

	public void setErrorCode(PafResultCode errorCode) {
		this.errorCode = errorCode;
	}

	public PafResultCode getErrorCode() {
		return errorCode;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public List<Address> getAddresses() {
		return addresses;
	}
}
