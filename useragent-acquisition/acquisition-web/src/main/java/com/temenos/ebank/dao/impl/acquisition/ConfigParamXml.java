/*
 * Created on Jun 5, 2006
 */
package com.temenos.ebank.dao.impl.acquisition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;

import com.temenos.ebank.dao.interfaces.application.IConfigParamDao;
import com.temenos.ebank.domain.ConfigParamTable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Xml storage implementation DAO for manipulating the configuration info table 
 * @author vionescu
 */
public class ConfigParamXml implements IConfigParamDao {

	protected final Log logger = LogFactory.getLog(getClass());
	/* (non-Javadoc)
	 * @see com.temenos.ebank.dao.interfaces.application.IParamSiteDao#getParamSite()
	 */

	private Resource configParamTableXml;
	
	/**
	 * Injects the configParamTable.xml resource
	 * @param configParamTableXml
	 */
	public void setConfigParamTableXml(Resource configParamTableXml) {
		this.configParamTableXml = configParamTableXml;
	}

	public ConfigParamTable getConfigParamTable() {
		try {
			XStream xstream = new XStream(new DomDriver());
			ConfigParamTable cpt = (ConfigParamTable)xstream.fromXML(configParamTableXml.getInputStream());
			return cpt;
		} catch (Exception e) {
			throw new RuntimeException("Error deserializing config param from xml", e);
		} 
		
//		if (params.size() == 0) {
//			logger.warn("No ParamSiteItem found");
//		}
//
//		return new ConfigParamTable(params);
	}
}