package com.temenos.interaction.example.app;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.temenos.interaction.example.country.CountryRIM;
import com.temenos.interaction.example.note.NewNoteRIM;
import com.temenos.interaction.example.note.NoteRIM;


public class ExampleApplication extends Application {

    private Set<Class<?>> classes = new HashSet<Class<?>>();
    private Set<Object> singletons = new HashSet<Object>();
        
    public ExampleApplication() {
        classes.add(CountryRIM.class);
        classes.add(NoteRIM.class);
        classes.add(NewNoteRIM.class);
    }
    
	@Override
    public Set<Class<?>> getClasses() {
         return classes;
    }

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}

