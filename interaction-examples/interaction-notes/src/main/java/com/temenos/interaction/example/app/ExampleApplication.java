package com.temenos.interaction.example.app;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import com.temenos.interaction.core.media.hal.HALProvider;
import com.temenos.interaction.example.country.CountryRIM;
import com.temenos.interaction.example.note.JAXBNoteRIM;
import com.temenos.interaction.example.note.NewNoteRIM;
import com.temenos.interaction.example.note.NoteProducerFactory;
import com.temenos.interaction.example.note.OEntityNoteRIM;
import com.temenos.interaction.example.sandbox.SandboxRIM;


public class ExampleApplication extends Application {

    private Set<Class<?>> classes = new HashSet<Class<?>>();
    private Set<Object> singletons = new HashSet<Object>();
        
    public ExampleApplication() {
        classes.add(CountryRIM.class);
        classes.add(OEntityNoteRIM.class);
        classes.add(JAXBNoteRIM.class);
        classes.add(NewNoteRIM.class);
        classes.add(SandboxRIM.class);
        
        try {
            singletons.add(new JAXBContextResolver());
            singletons.add(new HALProvider(new NoteProducerFactory().getJPAProducer().getMetadata()));
        } catch (Exception e) {
        	throw new RuntimeException(e);
        }
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

