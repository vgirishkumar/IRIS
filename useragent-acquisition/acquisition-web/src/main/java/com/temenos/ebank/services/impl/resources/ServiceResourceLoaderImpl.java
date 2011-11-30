/**
 * 
 */
package com.temenos.ebank.services.impl.resources;

import java.util.List;

import com.temenos.ebank.dao.interfaces.resources.IResourcesDao;
import com.temenos.ebank.domain.TextResource;
import com.temenos.ebank.services.interfaces.resources.IServiceResourceLoader;

public class ServiceResourceLoaderImpl implements IServiceResourceLoader {

	private IResourcesDao resourceDao;

	public List<TextResource> getResources(TextResource input) {
		return resourceDao.getResources(input);
	}

	public void setResourceDao(IResourcesDao resourceDao) {
		this.resourceDao = resourceDao;
	}
}
