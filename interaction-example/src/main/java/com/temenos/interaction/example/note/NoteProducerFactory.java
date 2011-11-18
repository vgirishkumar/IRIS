package com.temenos.interaction.example.note;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.odata4j.edm.EdmDataServices;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.jpa.JPAEdmGenerator;
import org.odata4j.producer.jpa.JPAProducer;

public class NoteProducerFactory {

	private final static int MAX_RESULTS = 100;
	private final static String JPA_NAMESPACE = "InteractionExample";

	// testing, lets just have one instance of the entity manager
	private static EntityManagerFactory emf;
	
	public NoteProducerFactory() {
		String persistenceUnitName = "NoteServiceHibernate";
		if (emf == null)
			emf = Persistence.createEntityManagerFactory(persistenceUnitName);
	}
	
	public ODataProducer getProducer() {
		ODataProducer producer = new JPAProducer(emf, JPA_NAMESPACE, MAX_RESULTS);
		return producer;
	}
	
	public EdmDataServices getEdmDataServices() {
		return new JPAEdmGenerator().buildEdm(emf, JPA_NAMESPACE);
	}

	public void close() {
		if (emf != null) {
			emf.close();
		}
	}
}
