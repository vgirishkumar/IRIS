package com.temenos.interaction.example.hateoas.banking;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

public class DaoHibernate {
    private final static Logger logger = Logger.getLogger(DaoHibernate.class.getName());

    @PersistenceContext(unitName = "ResponderServiceHibernate", type = PersistenceContextType.EXTENDED)
    @Access(AccessType.FIELD) 
    private EntityManager entityManager;

    public DaoHibernate(EntityManagerFactory entityManagerFactory) {
    	entityManager = entityManagerFactory.createEntityManager();
    }
    
    public void putFundTransfer(FundTransfer ft) {
    	try {
    		entityManager.getTransaction().begin();
    		entityManager.persist(ft); 
    		entityManager.getTransaction().commit();    		
    	} catch(EntityExistsException eee) {
			logger.severe("Failed to commit transaction - entity already exists: " + eee.getMessage());
    	} catch(IllegalArgumentException iae) {
			logger.severe("Failed to commit transaction - object is not an entity: " + iae.getMessage());
    	} catch(TransactionRequiredException tre) {
			logger.severe("Failed to commit transaction - No transaction exists: " + tre.getMessage());
    	} finally {
    		if (entityManager.getTransaction().isActive())
    			entityManager.getTransaction().rollback();
    	}
    	
    }

	@SuppressWarnings("unchecked")
	public List<FundTransfer> getFundTransfers() {
		List<FundTransfer> entities = null;
		try {
			Query jpaQuery = entityManager.createQuery("SELECT ft FROM FundTransfer ft");
			entities = jpaQuery.getResultList();
		}
		catch(Exception e) {
			logger.severe("Error while loading entities: " + e.getMessage());
		}
		return entities;
    }

	public FundTransfer getFundTransfer(Long id) {
		FundTransfer ft = null;
		try {
			ft = entityManager.find(FundTransfer.class, id);
		}
		catch(Exception e) {
			logger.severe("Error while loading entity [" + id + "]: " + e.getMessage());
		}
		return ft;
    }
	
	@SuppressWarnings("unchecked")
	public List<Customer> getCustomers() {
		List<Customer> entities = null;
		try {
			Query jpaQuery = entityManager.createQuery("SELECT customer FROM Customer customer");
			entities = jpaQuery.getResultList();
		}
		catch(Exception e) {
			logger.severe("Error while loading entities: " + e.getMessage());
		}
		return entities;
    }

	public Customer getCustomer(String name) {
		Customer customer = null;
		try {
			customer = entityManager.find(Customer.class, name);
		}
		catch(Exception e) {
			logger.severe("Error while loading entity [" + name + "]: " + e.getMessage());
		}
		return customer;
    }
}
