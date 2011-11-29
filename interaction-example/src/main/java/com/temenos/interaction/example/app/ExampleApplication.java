package com.temenos.interaction.example.app;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.temenos.interaction.example.country.CountryResource;
import com.temenos.interaction.example.note.NewNoteRIM;
import com.temenos.interaction.example.note.NoteRIM;


public class ExampleApplication extends Application {

    private Set<Class<?>> classes = new HashSet<Class<?>>();

    public ExampleApplication() {
        classes.add(CountryResource.class);
        classes.add(NoteRIM.class);
        classes.add(NewNoteRIM.class);
    }
    
	@Override
    public Set<Class<?>> getClasses() {
         return classes;
    }

}

