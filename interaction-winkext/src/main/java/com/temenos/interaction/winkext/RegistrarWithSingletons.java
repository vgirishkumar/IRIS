package com.temenos.interaction.winkext;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.wink.spring.Registrar;

import com.temenos.interaction.core.dynaresource.HTTPDynaRIM;

/**
 * Extend the Wink Spring support to be able to bind Providers such as JAXB / JSON.
 * This class requires the wink-spring-support be a 'compile' time dependency.
 * @author aphethean
 */
public class RegistrarWithSingletons extends Registrar {
    private Set<Object> singletons = Collections.emptySet();
    private Set<HTTPDynaRIM> interactions = Collections.emptySet();

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

    public void setSingletons(final Set<Object> singletons) {
        this.singletons = singletons;
    }
    
    public Set<HTTPDynaRIM> getInteractions() {
        return interactions;
    }

    public void setInteractions(final Set<HTTPDynaRIM> interactions) {
    	this.interactions = interactions;
    	if (this.getInstances() == null) {
        	this.setInstances(new HashSet<Object>());
    	}
        for (HTTPDynaRIM rim : interactions) {
               this.getInstances().addAll(rim.createChildResources());
        }
    }
    
}