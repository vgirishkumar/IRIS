package com.temenos.ebank.dao.interfaces.resources;

import java.util.List;

import com.temenos.ebank.domain.TextResource;

public interface IResourcesDao {
	public abstract List<TextResource> getResources(TextResource input);
}
