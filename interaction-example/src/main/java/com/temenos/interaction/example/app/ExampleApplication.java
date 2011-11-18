package com.temenos.interaction.example.app;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.temenos.interaction.example.country.CountryResource;
import com.temenos.interaction.example.note.NoteResource;


public class ExampleApplication extends Application {

    private Set<Class<?>> classes = new HashSet<Class<?>>();

    public ExampleApplication() {
        classes.add(CountryResource.class);
        classes.add(NoteResource.class);
    }
    
	@Override
    public Set<Class<?>> getClasses() {
         return classes;
    }

}

