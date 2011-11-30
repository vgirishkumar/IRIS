package com.temenos.ebank.dao.interfaces.application;

import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.Customer;

public interface IApplicationDao {
	public abstract Application getById(Long appID);

	public abstract int getCountByReference(String reference);

	public abstract Application getByReferenceAndEmail(String reference, String email);

	public abstract void store(Application app);

	public abstract void delete(Customer secondCustomer);
}
