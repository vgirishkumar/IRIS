package com.temenos.ebank.services.interfaces.thirdParty;


public interface IServicePafAddresses {

	public abstract PafAddressResult getAddressByPostCode(String postCode, String houseNoNm);

}