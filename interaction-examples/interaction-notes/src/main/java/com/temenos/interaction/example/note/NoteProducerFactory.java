package com.temenos.interaction.example.note;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.odata4j.producer.jpa.JPAProducer;

import com.temenos.interaction.example.utils.TestDBUtils;

public class NoteProducerFactory {

	private final static int MAX_RESULTS = 100;
	private final static String JPA_NAMESPACE = "InteractionExample";

	// testing, lets just have one instance of the entity manager
	private static EntityManagerFactory emf;
	
	public NoteProducerFactory() {
		String persistenceUnitName = "NoteServiceHibernate";
		if (emf == null) {
			emf = Persistence.createEntityManagerFactory(persistenceUnitName);
	    	TestDBUtils.fillNoteDatabase();
		}
	}
	
	public JPAProducer getJPAProducer() {
		return new JPAProducer(emf, JPA_NAMESPACE, MAX_RESULTS);
	}

	public NewFunctionProducer getFunctionsProducer() {
		return new NewFunctionProducer(emf, JPA_NAMESPACE, new JPAProducer(emf, JPA_NAMESPACE, MAX_RESULTS));
	}

	public void close() {
		if (emf != null) {
			emf.close();
		}
	}
}
