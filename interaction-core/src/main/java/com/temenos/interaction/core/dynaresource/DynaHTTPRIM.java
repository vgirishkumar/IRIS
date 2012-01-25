package com.temenos.interaction.core.dynaresource;

import org.apache.wink.common.DynamicResource;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.state.HTTPResourceInteractionModel;
import com.temenos.interaction.core.state.TRANSIENTResourceInteractionModel;

/**
 * Define a Dynamic HTTP based Resource Interaction Model for an individual resource.
 * HTTP interactions with resources to change state are simple, just PUT and DELETE.  You might
 * be wondering about a POST to a resource.  We've defined POST as a transient operation, ie.
 * an operation that does not change an individual resources state see {@link TRANSIENTResourceInteractionModel}
 * @author aphethean
 */
public class DynaHTTPRIM extends HTTPResourceInteractionModel implements DynamicResource {

    private String path;
    private Object parent;
    private String workspaceTitle;
    private String collectionTitle;
    private String beanName;
	
	public DynaHTTPRIM(String entityName, String path, CommandController commandController) {
		super(entityName, path, commandController);
		this.path= path;
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

    public void setPath(String path) {
        this.path = path;
    }

	@Override
    public String getPath() {
        return path;
    }

	@Override
    public void setParent(Object parent) {
        this.parent = parent;
    }

	@Override
    public Object getParent() {
        return parent;
    }
    
}
