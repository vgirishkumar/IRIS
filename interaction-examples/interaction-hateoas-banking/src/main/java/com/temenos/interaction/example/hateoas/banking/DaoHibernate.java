package com.temenos.interaction.example.hateoas.banking;

/*
 * #%L
 * interaction-example-hateoas-banking
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


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
    		entityManager.merge(ft); 
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
