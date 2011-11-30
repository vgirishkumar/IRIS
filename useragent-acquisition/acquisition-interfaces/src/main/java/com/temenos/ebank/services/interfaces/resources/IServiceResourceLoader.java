package com.temenos.ebank.services.interfaces.resources;

import java.util.List;

import com.temenos.ebank.domain.TextResource;

public interface IServiceResourceLoader {
	public abstract List<TextResource> getResources(TextResource input);
}