package com.temenos.interaction.example.hateoas.simple;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import com.temenos.interaction.example.hateoas.simple.model.Note;

public class Persistence {
    private final static Logger logger = Logger.getLogger(Persistence.class.getName());

    @PersistenceContext(unitName = "ResponderServiceHibernate", type = PersistenceContextType.EXTENDED)
    @Access(AccessType.FIELD) 
    private EntityManager entityManager;

    public Persistence(EntityManagerFactory entityManagerFactory) {
    	entityManager = entityManagerFactory.createEntityManager();
    }

	@SuppressWarnings("unchecked")
	public List<Note> getNotes() {
		List<Note> entities = null;
		try {
			Query jpaQuery = entityManager.createQuery("SELECT n FROM note n");
			entities = jpaQuery.getResultList();
		} catch(Exception e) {
			logger.severe("Error while loading entities: " + e.getMessage());
		}
		return entities;
    }

	public Note getNote(Long id) {
		Note note = null;
		try {
			note = entityManager.find(Note.class, id);
		} catch(Exception e) {
			logger.severe("Error while loading entity [" + id + "]: " + e.getMessage());
		}
		return note;
    }

	public Note removeNote(Long id) {
		Note note = null;
		try {
    		entityManager.getTransaction().begin();
			note = entityManager.find(Note.class, id);
			entityManager.remove(note);
    		entityManager.getTransaction().commit();    		
		} catch(Exception e) {
			logger.severe("Error while removing entity [" + id + "]: " + e.getMessage());
		}
		return note;
    }

}
