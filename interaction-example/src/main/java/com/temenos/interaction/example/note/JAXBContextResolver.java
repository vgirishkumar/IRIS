package com.temenos.interaction.example.note;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.temenos.interaction.core.EntityResource;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

@Provider
public final class JAXBContextResolver implements ContextResolver<JAXBContext> {
    
    private final JAXBContext context;
    private final Set<Class<?>> types;
    private final Class<?>[] cTypes = {EntityResource.class};
    
    public JAXBContextResolver() throws Exception {
        this.types = new HashSet<Class<?>>(Arrays.asList(cTypes));
        try {
            this.context = JAXBContext.newInstance(EntityResource.class, Note.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    public JAXBContext getContext(Class<?> objectType) {
        return (types.contains(objectType)) ? context : null;
    }

}