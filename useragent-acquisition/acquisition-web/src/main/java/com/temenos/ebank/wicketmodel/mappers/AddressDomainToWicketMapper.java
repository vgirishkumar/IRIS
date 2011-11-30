package com.temenos.ebank.wicketmodel.mappers;

import org.apache.wicket.model.IModel;
import org.springframework.beans.BeanUtils;

import com.temenos.ebank.domain.Address;
import com.temenos.ebank.wicketmodel.AddressWicketModelObject;

/**
 * 
 * @author raduf
 * 
 */
public class AddressDomainToWicketMapper implements IDomainToWicketMapper<Address, AddressWicketModelObject> {

	/**
	 * @see com.temenos.ebank.wicketmodel.mappers.IDomainToWicketMapper#domain2Wicket(java.lang.Object)
	 */
	public AddressWicketModelObject domain2Wicket(Address address) {
		AddressWicketModelObject awm = new AddressWicketModelObject();
		BeanUtils.copyProperties(address, awm);
		return awm;
	}

	public void wicket2Domain(AddressWicketModelObject addressWMO, Address address) {
		BeanUtils.copyProperties(addressWMO, address);
	}

	public void wicketModel2Domain(IModel wicketModel, Address domainObject) {
	}

}
