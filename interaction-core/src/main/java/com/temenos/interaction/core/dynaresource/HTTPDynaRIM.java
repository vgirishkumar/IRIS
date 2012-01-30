package com.temenos.interaction.core.dynaresource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.HttpMethod;

import org.apache.wink.common.DynamicResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.link.ResourceState;
import com.temenos.interaction.core.state.HTTPResourceInteractionModel;
import com.temenos.interaction.core.state.ResourceInteractionModel;

/**
 * Define a Dynamic HTTP based Resource Interaction Model for an individual resource.
 * HTTP interactions with resources are simple, just GET, PUT, POST and DELETE.
 * @author aphethean
 */
public class HTTPDynaRIM extends HTTPResourceInteractionModel implements DynamicResource {
	private final Logger logger = LoggerFactory.getLogger(HTTPDynaRIM.class);

    private Object parent;
    private String workspaceTitle;
    private String collectionTitle;
    private String beanName;
    
    private final ResourceState state;
    
	public HTTPDynaRIM(HTTPDynaRIM parent, String entityName, String path, ResourceState state, ResourceRegistry rr, CommandController commandController) {
		super(entityName, path, rr, commandController);
		this.parent = parent;
		this.state = state;
		bootstrap();
	}

	/*
	 * Bootstrap the resource by attempting to fetch a command for all the required
	 * interactions with the resource state.
	 */
	private void bootstrap() {
		getCommandController().fetchGetCommand();
		if (state != null) {
			Set<String> httpMethods = state.getInteractions();
			for (String method : httpMethods) {
				logger.debug("Checking configuration for [" + method + "] " + getPath());
				// check valid http method
				if (!(method.equals(HttpMethod.PUT) || method.equals(HttpMethod.DELETE)))
					throw new RuntimeException("Invalid configuration of state [" + state.getName() + "] for entity [" + getEntityName() + "]- invalid http method [" + method + "]");
				// fetch command from command controller for this method
				getCommandController().fetchStateTransitionCommand(method, getPath());
			}
		}
	}
	
	@Override
    public String getBeanName() {
        return beanName;
    }

	@Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public void setWorkspaceTitle(String workspaceTitle) {
        this.workspaceTitle = workspaceTitle;
    }

	@Override
    public String getWorkspaceTitle() {
        return workspaceTitle;
    }

    public void setCollectionTitle(String collectionTitle) {
        this.collectionTitle = collectionTitle;
    }

	@Override
    public String getCollectionTitle() {
        return collectionTitle;
    }

	@Override
    public String getPath() {
        return getResourcePath();
    }

	@Override
    public void setParent(Object parent) {
        this.parent = parent;
    }

	@Override
    public Object getParent() {
        return parent;
    }
    
	@Override
	public Set<String> getInteractions() {
		return state.getInteractions();
		/*
		Set<DynamicResource> interaction = new HashSet<DynamicResource>();
//		interaction.add(new DynaOPTIONS(this, (ResourceGetCommand) commandController.fetchGetCommand(), "{id}"));
		interaction.add(dynaGET);
		interaction.add(new DynaPUT(this, (ResourcePutCommand) commandController.fetchStateTransitionCommand("PUT", getResourcePath() + "/{id}"), "{id}"));
		interaction.add(new DynaDELETE(this, (ResourceDeleteCommand) commandController.fetchStateTransitionCommand("DELETE", getResourcePath() + "/{id}"), "{id}"));
		return interaction;
		*/
	}

	public ResourceState getState() {
		return state;
	}
	
	public Collection<ResourceInteractionModel> createChildResources() {
		Map<String, ResourceInteractionModel> result = new HashMap<String, ResourceInteractionModel>();
		List<ResourceState> states = new ArrayList<ResourceState>();
		collectRIMs(result, states, this.state, this, getCommandController());
		return result.values();
	}
	
	private void collectRIMs(Map<String, ResourceInteractionModel> result, Collection<ResourceState> states, ResourceState s, HTTPDynaRIM resource, CommandController cc) {
		if (states.contains(s)) return;
		states.add(s);
		for (ResourceState next : s.getAllTargets()) {
			if (!next.equals(this.state) && !next.isFinalState()) {
				// use the state name as the path
				String path = next.getName();
				if (!result.keySet().contains(path)) {
					HTTPDynaRIM child = new HTTPDynaRIM(resource, resource.getEntityName(), path, next, null, cc);
					result.put(path, child);
					collectRIMs(result, states, next, child, cc);
				}
			}
		}
		
	}

}
