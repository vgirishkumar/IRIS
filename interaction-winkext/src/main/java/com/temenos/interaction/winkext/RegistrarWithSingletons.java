package com.temenos.interaction.winkext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wink.common.DynamicResource;
import org.apache.wink.spring.Registrar;

import com.jayway.jaxrs.hateoas.HateoasLinkInjector;
import com.jayway.jaxrs.hateoas.core.HateoasResponse.HateoasResponseBuilder;
import com.jayway.jaxrs.hateoas.support.DefaultCollectionWrapperStrategy;
import com.jayway.jaxrs.hateoas.support.DefaultHateoasViewFactory;
import com.jayway.jaxrs.hateoas.support.HateoasLinkBeanLinkInjector;
import com.jayway.jaxrs.hateoas.support.StrategyBasedLinkInjector;
import com.temenos.interaction.core.dynaresource.HTTPDynaRIM;
import com.temenos.interaction.core.link.CollectionResourceLinkInjector;
import com.temenos.interaction.core.link.EntityResourceLinkInjector;
import com.temenos.interaction.core.link.GenericEntityHateoasLinkBeanLinkInjector;
import com.temenos.interaction.core.link.NaiveLinkInjector;
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
        
    public RegistrarWithSingletons() {
    	// TODO, bit dodge we need to configure jax-rs-hateoas a bit better than this
    	List<HateoasLinkInjector<Object>> strategies = new ArrayList<HateoasLinkInjector<Object>>();
    	strategies.add(new HateoasLinkBeanLinkInjector());
    	// add the GenericEntity version of the HateoasLinkBeanLinkInjector
    	strategies.add(new GenericEntityHateoasLinkBeanLinkInjector());
    	// add the link injector for an individual/item resource
    	strategies.add(new EntityResourceLinkInjector());
    	// add the link injector for an collection resource
    	strategies.add(new CollectionResourceLinkInjector());
    	strategies.add(new NaiveLinkInjector());
		HateoasResponseBuilder.configure(new StrategyBasedLinkInjector(strategies), new DefaultCollectionWrapperStrategy(), new DefaultHateoasViewFactory());

    }
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
    
    public void setServiceRoot(HTTPDynaRIM rootRIM) {
    	if (this.getInstances() == null) {
        	this.setInstances(new HashSet<Object>());
    	}
    	addAllDynamicResource(rootRIM);
    }
    
    private void addAllDynamicResource(ResourceInteractionModel rim) {
    	addDynamicResource(rim);
    	Collection<ResourceInteractionModel> children = rim.getChildren();
    	for (ResourceInteractionModel child : children) {
    		addDynamicResource(child);
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