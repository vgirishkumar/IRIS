package com.temenos.ebank.services.impl.thirdParty;

import java.util.List;

import com.temenos.ebank.dao.impl.IrisDaoHelper;
import com.temenos.ebank.domain.Address;
import com.temenos.ebank.services.interfaces.thirdParty.IServicePafAddresses;
import com.temenos.ebank.services.interfaces.thirdParty.PafAddressResult;
import com.temenos.ebank.services.interfaces.thirdParty.PafResultCode;

public class ServicePafAddressesImpl implements IServicePafAddresses {
	private IrisDaoHelper daoHelper;

	public ServicePafAddressesImpl(String irisUrl) {
		daoHelper = new IrisDaoHelper(irisUrl);
	}
	
	public PafAddressResult getAddressByPostCode(String postCode, String houseNoNm) {
		String filter = "$filter=postcode eq '" + postCode + "'";
		List<Address> list = daoHelper.getAddressEntities("/Address", filter);
		PafResultCode  resultCode = PafResultCode.INVALID_POST_CODE;
		if(list.size() > 0) {
			resultCode = PafResultCode.OK_RESULT;
		}
		PafAddressResult result = new PafAddressResult(resultCode, list);

		return result;
	}

}
