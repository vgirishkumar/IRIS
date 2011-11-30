/**
 * 
 */
package com.temenos.ebank.wicketmodel.mappers;

import org.apache.wicket.model.IModel;

/**
 * Inerface for mappers between a wicket model and a domain object
 * 
 * @author vionescu
 * 
 */
public interface IDomainToWicketMapper<T, U> {
	U domain2Wicket(T domainObject);

	void wicket2Domain(U wicketModelObject, T domainObject);

	@SuppressWarnings("rawtypes")
	void wicketModel2Domain(IModel wicketModel, T domainObject);
}
