package com.temenos.ebank.services.impl.clientAquisition;

import com.temenos.ebank.dao.interfaces.application.IConfigParamDao;
import com.temenos.ebank.domain.ConfigParamTable;
import com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam;

/**
 * Service interface for managing the config table
 * @author vionescu
 *
 */

public class ServiceConfigParamImpl implements IServiceConfigParam {

	private IConfigParamDao configParamDao;
	

	/* (non-Javadoc)
	 * @see com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam#getConfigParamTable()
	 */
	public ConfigParamTable getConfigParamTable() {
		ConfigParamTable cpt = configParamDao.getConfigParamTable();
		//This is useful for producing test data for the unit tests
		//In order to run this from site, one must alter the classpath to include xstream libraries
		//SerializeTestObjectsToXmlUtils.serializeConfigParamToXml(cpt,"D:\\temp\\configparam");
		return cpt;
	}

//	/* (non-Javadoc)
//	 * @see com.temenos.ebank.services.interfaces.clientAquisition.IServiceConfigParam#storeConfigParamTable(com.temenos.ebank.domain.ConfigParamTable)
//	 */
//	public void storeConfigParamTable(ConfigParamTable configParam) {
//		configParamDao.storeConfigParamTable(configParam);
//	}

	/**
	 * Injects the dao implementation
	 * @param configParamDao
	 */
	public void setConfigParamDao(IConfigParamDao configParamDao) {
		this.configParamDao = configParamDao;
	}
	
}
