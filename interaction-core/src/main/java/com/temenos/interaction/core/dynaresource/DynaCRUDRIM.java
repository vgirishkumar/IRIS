package com.temenos.interaction.core.dynaresource;

import org.apache.wink.common.DynamicResource;

import com.temenos.interaction.core.command.CommandController;
import com.temenos.interaction.core.state.CRUDResourceInteractionModel;

/**
 * Define a Dynamic CRUD based Resource Interaction Model
 * CRUD (Create, Read, Update, Delete) interaction are simple, this class
 * can be used to dynamically define resources that are managed with CRUD
 * interactions
 * @author aphethean
 */
public class DynaCRUDRIM extends CRUDResourceInteractionModel implements DynamicResource {

    private String path;
    private Object parent;
    private String workspaceTitle;
    private String collectionTitle;
    private String beanName;
	
	public DynaCRUDRIM(String entityName, String path, CommandController commandController) {
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
