package com.temenos.ebank.dao.impl.nomencl;

import java.util.List;

import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.temenos.ebank.dao.impl.IrisDaoHelper;
import com.temenos.ebank.dao.interfaces.nomencl.INomenclDao;
import com.temenos.ebank.domain.Nomencl;

@Repository
public class NomenclDaoImpl extends HibernateDaoSupport implements INomenclDao {
	private IrisDaoHelper daoHelper;
	
	public NomenclDaoImpl(String irisUrl) {
		daoHelper = new IrisDaoHelper(irisUrl);
	}
	
	@Transactional(readOnly = true)
	public List<Nomencl> getNomencl(String language, String group) {
		String filter = "$filter=language eq '" + language + "'";
		filter += " and groupCode eq '" + group + "'";
		String orderby = "$orderby=sortOrder,label asc";
		return daoHelper.getNomenclEntities("/Nomencl", filter, orderby);
		
		/*
		return getSession().createCriteria(Nomencl.class).add(Restrictions.eq("language", language))
				.add(Restrictions.eq("groupCode", group)).addOrder(Order.asc("sortOrder")).addOrder(Order.asc("label"))
				.list();
				*/
	}

	@Transactional
	public void insertNomencl(Nomencl nomencl) {
		createHibernateTemplate(getSessionFactory()).saveOrUpdate(nomencl);
	}

	@SuppressWarnings("rawtypes")
	public Nomencl findNomencl(Nomencl nomencl) {
		List result = (List) getSession().createCriteria(Nomencl.class).add(Example.create(nomencl)).list();
		if (!CollectionUtils.isEmpty(result)) {
			return (Nomencl) result.get(0);
		}
		return null;
	}

}
