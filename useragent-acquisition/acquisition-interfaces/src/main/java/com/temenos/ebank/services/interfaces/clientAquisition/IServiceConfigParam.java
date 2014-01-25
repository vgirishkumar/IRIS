package com.temenos.ebank.services.interfaces.clientAquisition;

import com.temenos.ebank.domain.ConfigParamTable;

/**
 * Service interface for managing the config table
 * @author vionescu
 *
 */
public interface IServiceConfigParam {

	/**
	 * Returns the configuration info table encapsulated by {@link ConfigParamTable}
	 */
	public abstract ConfigParamTable getConfigParamTable();

//	/**
//	 * Stores the configuration info table encapsulated by {@link ConfigParamTable} 
//	 */
//	public abstract void storeConfigParamTable(ConfigParamTable configParam);

}
