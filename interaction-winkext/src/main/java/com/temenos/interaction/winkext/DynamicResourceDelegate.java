package com.temenos.interaction.winkext;

import java.util.Collection;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.DynamicResource;

import com.temenos.interaction.core.dynaresource.HTTPDynaRIM;
import com.temenos.interaction.core.link.Link;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.core.resource.RESTResource;
import com.temenos.interaction.core.state.HTTPResourceInteractionModel;
import com.temenos.interaction.core.state.ResourceInteractionModel;

public class DynamicResourceDelegate implements HTTPResourceInteractionModel, DynamicResource {

	private final HTTPResourceInteractionModel parent;
	private final HTTPDynaRIM resource;
	
	public DynamicResourceDelegate(HTTPResourceInteractionModel parent, HTTPDynaRIM resource) {
		this.parent = parent;
		this.resource = resource;
	}

	@Override
    public String getBeanName() {
        return resource.getCurrentState().getId();
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
		return "DefaultWorkspace";
    }

    public void setCollectionTitle(String collectionTitle) {
        throw new AssertionError("Not supported");
    }

	@Override
    public String getCollectionTitle() {
		return resource.getResourcePath();
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
	public Response post(HttpHeaders headers, String id, UriInfo uriInfo, EntityResource<?> eresource) {
		return resource.post(headers, id, uriInfo, eresource);
	}

	@Override
	public Response put(HttpHeaders headers, String id, UriInfo uriInfo, EntityResource<?> eresource) {
		return resource.put(headers, id, uriInfo, eresource);
	}

	@Override
	public Response delete(HttpHeaders headers, String id) {
		return resource.delete(headers, id);
	}

	public Response options(String id) {
		return resource.options(id);
	}

	@Override
	public ResourceState getCurrentState() {
		return resource.getCurrentState();
	}

	@Override
	public Collection<Link> getLinks(MultivaluedMap<String, String> pathParameters, RESTResource entity) {
		return resource.getLinks(pathParameters, entity); 
	}
	
	@Override
	public String getResourcePath() {
		return resource.getResourcePath();
	}

	@Override
	public String getFQResourcePath() {
		return resource.getFQResourcePath();
	}

	@Override
	public Collection<ResourceInteractionModel> getChildren() {
		return resource.getChildren();
	}
    
}
