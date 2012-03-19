package com.temenos.interaction.winkext;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.wink.common.DynamicResource;
import org.apache.wink.spring.Registrar;

import com.temenos.interaction.core.dynaresource.HTTPDynaRIM;
import com.temenos.interaction.core.link.ResourceRegistry;
import com.temenos.interaction.core.state.HTTPResourceInteractionModel;
import com.temenos.interaction.core.state.ResourceInteractionModel;

/**
 * Extend the Wink Spring support to be able to bind Providers such as JAXB / JSON.
 * This class requires the wink-spring-support be a 'compile' time dependency.
 * @author aphethean
 */
public class RegistrarWithSingletons extends Registrar {
    private Set<Object> singletons = Collections.emptySet();
    private ResourceRegistry resourceRegistry = null;

    // key = resourcePath
    private Map<String, DynamicResourceDelegate> resources = new HashMap<String, DynamicResourceDelegate>();
        
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    public void setSingletons(final Set<Object> singletons) {
        this.singletons = singletons;
    }
    
    public ResourceRegistry getResourceRegistry() {
        return resourceRegistry;
    }

    public void setResourceRegistry(final ResourceRegistry rRegistry) {
    	assert(rRegistry != null);
    	resourceRegistry = rRegistry;
    	Set<ResourceInteractionModel> interactions = rRegistry.getResourceInteractionModels();
    	if (this.getInstances() == null) {
        	this.setInstances(new HashSet<Object>());
    	}
        for (ResourceInteractionModel rim : interactions) {
        	addDynamicResource(rim);
        }
    }
    
    private void addDynamicResource(ResourceInteractionModel rim) {
    	assert(this.getInstances() != null);
    	String rimKey = rim.getFQResourcePath();
    	if (resources.get(rimKey) != null)
    		return;
    	DynamicResource parent = null;
    	// is this a root resource
    	if (rim.getParent() != null) {
    		// climb back up the graph adding parent if necessary
        	String parentKey = rim.getParent().getFQResourcePath();
    		if (resources.get(parentKey) == null) {
    			addDynamicResource(rim.getParent());
    		}
    		parent = resources.get(parentKey);
    	}
    	
    	// TODO could do a lot better then this cast
    	DynamicResourceDelegate dr = new DynamicResourceDelegate(parent != null ? (HTTPResourceInteractionModel) parent : null, (HTTPDynaRIM) rim);
    	resources.put(rimKey, dr);
    	this.getInstances().add(dr);
   	
    }
    
}