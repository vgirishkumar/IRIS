package com.temenos.ebank.dao.impl.acquisition;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.temenos.ebank.dao.interfaces.application.IApplicationDao;
import com.temenos.ebank.domain.Application;
import com.temenos.ebank.domain.Customer;

@Repository
public class ApplicationDaoImpl extends HibernateDaoSupport implements IApplicationDao {
	private static final Log LOG = LogFactory.getLog(ApplicationDaoImpl.class);

	@Transactional(readOnly = true)
	public Application getById(Long id) {
		LOG.debug("getting Application instance with id: " + id);
		try {
			Application instance = (Application) getSession().get(Application.class, id);
			if (instance == null) {
				LOG.debug("get successful, no instance found");
			} else {
				LOG.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			LOG.error("get failed", re);
			/* let's not be mean and do not propagate the exception */
			return null;
		}
	}

	@Transactional(readOnly = true)
	public int getCountByReference(String reference) {
		LOG.debug("getting Application instance with reference : " + reference);
		Criteria appCriteria = getSession().createCriteria(Application.class, "app")
				.createAlias("app.customer", "customer").add(Restrictions.eq("app.appRef", reference))
				.setProjection(Projections.rowCount());
		Number appRefCount = (Number) appCriteria.uniqueResult();
		return appRefCount.intValue();
	}

	@SuppressWarnings("rawtypes")
	@Transactional(readOnly = true)
	public Application getByReferenceAndEmail(String reference, String email) {
		LOG.debug("getting Application instance with reference and email: " + reference + ", " + email);

		try {
			Criteria appCriteria = getSession().createCriteria(Application.class, "app")
					.createAlias("app.customer", "customer").add(Restrictions.eq("app.appRef", reference))
					.add(Restrictions.eq("customer.emailAddress", email));
			List result = appCriteria.list();
			switch (result.size()) {
			case 1:
				LOG.debug("get successful, instance found");
				return (Application) result.get(0);

			case 0:
				LOG.debug("get successful, no instance found");
				return null;

			default:
				// this should not really happen.
				LOG.warn("get successful, more than one instance found");
				return null;
			}
		} catch (RuntimeException re) {
			LOG.error("get failed", re);
			/* let's not be mean and do not propagate the exception */
			return null;
		}
	}

	@Transactional
	public void store(Application app) {
		getHibernateTemplate().saveOrUpdate(app);
		// TODO cascade insert/update for related Customers ??? for now, let's see how Hibernate behaves.
		// UPDATE: save-update is cascaded. delete-orphan cannot be cascaded for many-to-one associations.
	}

	@Transactional
	public void delete(Customer secondCustomer) {
		getHibernateTemplate().delete(secondCustomer);
	}

}
