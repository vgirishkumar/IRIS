package com.temenos.ebank.dao.interfaces.application;

import com.temenos.ebank.domain.ConfigParamTable;

/**
 * Interface DAO for manipulating the configuration info table 
 * @author vionescu
 */
public interface IConfigParamDao {

	/**
	 * Returns the configuration info table encapsulated by {@link ConfigParamTable}
	 */
	public abstract ConfigParamTable getConfigParamTable();

//	/**
//	 * Stores the configuration info table encapsulated by {@link ConfigParamTable} 
//	 */
//	public abstract void storeConfigParamTable(ConfigParamTable configParam);
//
//	/**
//	 * Deletes the configuration info table encapsulated by {@link ConfigParamTable}
//	 */
//	public abstract void deleteConfigParamTable(ConfigParamTable configParam);
	
}