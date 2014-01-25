package com.temenos.ebank.dao.impl.resources;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Example;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.temenos.ebank.dao.interfaces.resources.IResourcesDao;
import com.temenos.ebank.domain.TextResource;

@Repository
public class ResourcesDaoImpl extends HibernateDaoSupport implements IResourcesDao {

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<TextResource> getResources(TextResource input) {
		if (input == null) {
			return null;
		}
		Criteria textResourceCriteria = getSession().createCriteria(TextResource.class, "textResource");
		Example textResourceExample = Example.create(input);
		textResourceExample.excludeProperty("key");
		textResourceExample.excludeProperty("value");
		textResourceCriteria.add(textResourceExample);

		return (List<TextResource>) textResourceCriteria.list();
	}

}
