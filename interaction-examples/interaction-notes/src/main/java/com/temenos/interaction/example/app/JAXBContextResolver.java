package com.temenos.interaction.example.app;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.temenos.interaction.core.resource.EntityResource;
import com.temenos.interaction.example.country.Country;
import com.temenos.interaction.example.note.Note;
import com.temenos.interaction.example.sandbox.Book;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * A JAXB context for the entire interaction example.
 * @author aphethean
 */
@Provider
public final class JAXBContextResolver implements ContextResolver<JAXBContext> {
    
    private final JAXBContext context;
    private final Set<Class<?>> types;
    private final Class<?>[] cTypes = {EntityResource.class};
    
    public JAXBContextResolver() throws Exception {
        this.types = new HashSet<Class<?>>(Arrays.asList(cTypes));
        try {
            this.context = JAXBContext.newInstance(EntityResource.class, Note.class, Book.class, Country.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    public JAXBContext getContext(Class<?> objectType) {
        return (types.contains(objectType)) ? context : null;
    }

}