/*
 * Created on Jun 5, 2006
 */
package com.temenos.ebank.dao.impl.acquisition;

import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.Order;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.temenos.ebank.dao.interfaces.application.IConfigParamDao;
import com.temenos.ebank.domain.ConfigParamTable;
import com.temenos.ebank.domain.ConfigParam;

/**
 * Hibernate implementation DAO for manipulating the configuration info table 
 * @author acirlomanu
 */
@SuppressWarnings("rawtypes")
public class ConfigParamDaoImpl extends HibernateDaoSupport implements IConfigParamDao {

	/* (non-Javadoc)
	 * @see com.temenos.ebank.dao.interfaces.application.IParamSiteDao#getParamSite()
	 */
	public ConfigParamTable getConfigParamTable() {
		
		final List params = getSession().createCriteria(ConfigParam.class)
			.addOrder(Order.asc("codeParam"))
			.setCacheable(true)
			.list();

		if (params.size() == 0) {
			logger.warn("No ParamSiteItem found");
		}

		return new ConfigParamTable(params);
	}

	/* (non-Javadoc)
	 * @see com.temenos.ebank.dao.interfaces.application.IParamSiteDao#storeParamSite(com.temenos.ebank.domain.ParamSite)
	 */
	public void storeConfigParamTable(ConfigParamTable configParamTable) {
		for (Iterator it = configParamTable.getConfigParamItems().iterator(); it.hasNext(); ) {
			// update each item 
			getHibernateTemplate().merge(it.next());
		}
	}
//	
//	/**
//	 * @see com.viveo.vrci.dao.interfaces.ParametrageDao#existsParamSite(java.lang.String)
//	 */
//	public boolean existsParamSite(String identSociete) {
//		Integer paramsCount = (Integer) getSession().createCriteria(ParamSiteItem.class)
//			.add(Restrictions.eq("id.identSociete", identSociete))
//			.setProjection(Projections.rowCount())
//			.uniqueResult();
//		return paramsCount.intValue() > 0;
//	}

	/* (non-Javadoc)
	 * @see com.temenos.ebank.dao.interfaces.application.IParamSiteDao#deleteParamSite(com.temenos.ebank.domain.ParamSite)
	 */
	public void deleteConfigParamTable(ConfigParamTable paramSite) {
		for (Iterator it = paramSite.getConfigParamItems().iterator(); it.hasNext(); ) {
			// delete each ParamSiteItem
			getHibernateTemplate().delete(it.next());
		}
	}
	
}