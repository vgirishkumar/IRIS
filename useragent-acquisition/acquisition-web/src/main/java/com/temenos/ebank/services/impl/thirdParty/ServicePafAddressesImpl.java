package com.temenos.ebank.services.impl.thirdParty;

import java.util.ArrayList;
import java.util.List;

import com.temenos.ebank.domain.Address;
import com.temenos.ebank.services.interfaces.thirdParty.IServicePafAddresses;
import com.temenos.ebank.services.interfaces.thirdParty.PafAddressResult;
import com.temenos.ebank.services.interfaces.thirdParty.PafResultCode;

public class ServicePafAddressesImpl implements IServicePafAddresses {

	public PafAddressResult getAddressByPostCode(String postCode, String houseNoNm) {
		List<Address> list = new ArrayList<Address>();
		Address address = new Address();
		address.setLine1("line1 result1");
		address.setLine2("line1 result1");
		address.setCounty("county result1");
		address.setDistrict("district result1");
		address.setTown("town1");
		address.setPostcode(postCode);
		address.setCountry("GB");
		list.add(address);

		address = new Address();
		address.setLine1("line1 result2");
		address.setLine2("line1 result2");
		address.setCounty("county2");
		address.setDistrict("district2");
		address.setTown("town2");
		address.setPostcode(postCode);
		address.setCountry("US"); 
		list.add(address);

		PafResultCode resultCode = PafResultCode.OK_RESULT;
		// for testing the scenario when the service returns only one address instead of multiple
		if (houseNoNm != null && houseNoNm.equals("1")) {
			// for testing the scenario when the service returns no addresses
			list.clear();
			resultCode = PafResultCode.INVALID_POST_CODE;
		}
		else if (houseNoNm != null && houseNoNm.equals("2")) {
			list.clear();
			list.add(address);
			resultCode = PafResultCode.OK_RESULT;
		}

		PafAddressResult result = new PafAddressResult(resultCode, list);

		return result;
	}

}
