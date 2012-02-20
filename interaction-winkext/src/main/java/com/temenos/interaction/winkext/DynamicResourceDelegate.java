package com.temenos.interaction.winkext;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.DynamicResource;

import com.temenos.interaction.core.EntityResource;
import com.temenos.interaction.core.state.AbstractHTTPResourceInteractionModel;
import com.temenos.interaction.core.state.HTTPResourceInteractionModel;

public class DynamicResourceDelegate implements HTTPResourceInteractionModel, DynamicResource {

	private final HTTPResourceInteractionModel parent;
	private final AbstractHTTPResourceInteractionModel resource;
	
	public DynamicResourceDelegate(HTTPResourceInteractionModel parent, AbstractHTTPResourceInteractionModel resource) {
		this.parent = parent;
		this.resource = resource;
	}

	@Override
    public String getBeanName() {
        return resource.getEntityName();
    }

	@Override
    public void setBeanName(String beanName) {
        throw new AssertionError("Not supported");
    }

    public void setWorkspaceTitle(String workspaceTitle) {
        throw new AssertionError("Not supported");
    }

	@Override
    public String getWorkspaceTitle() {
        return null;
    }

    public void setCollectionTitle(String collectionTitle) {
        throw new AssertionError("Not supported");
    }

	@Override
    public String getCollectionTitle() {
        return null;
    }

	@Override
    public String getPath() {
        return resource.getResourcePath();
    }

	@Override
    public void setParent(Object parent) {
        throw new AssertionError("Not supported");
    }

	@Override
    public HTTPResourceInteractionModel getParent() {
        return parent;
    }

	@Override
	public Response get(HttpHeaders headers, String id, UriInfo uriInfo) {
		return resource.get(headers, id, uriInfo);
	}

	@Override
	public Response put(HttpHeaders headers, String id, EntityResource eresource) {
		return resource.put(headers, id, eresource);
	}

	@Override
	public Response delete(HttpHeaders headers, String id) {
		return resource.delete(headers, id);
	}

	public Response options(String id) {
		return resource.options(id);
	}
    
}
