package com.temenos.interaction.example.mashup.twitter;

/*
 * #%L
 * interaction-example-mashup-twitter
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
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;

import com.temenos.interaction.example.mashup.twitter.model.User;

public class Persistence {
    private final static Logger logger = Logger.getLogger(Persistence.class.getName());

    @PersistenceContext(unitName = "ResponderServiceHibernate", type = PersistenceContextType.EXTENDED)
    @Access(AccessType.FIELD) 
    private EntityManager entityManager;

    public Persistence(EntityManagerFactory entityManagerFactory) {
    	entityManager = entityManagerFactory.createEntityManager();
    }

	@SuppressWarnings("unchecked")
	public List<User> getUsers() {
		List<User> entities = null;
		try {
			Query jpaQuery = entityManager.createQuery("SELECT n FROM user n");
			entities = jpaQuery.getResultList();
		} catch(Exception e) {
			logger.severe("Error while loading entities: " + e.getMessage());
		}
		return entities;
    }

	public User getUser(Long id) {
		User user = null;
		try {
			user = entityManager.find(User.class, id);
		} catch(Exception e) {
			logger.severe("Error while loading entity [" + id + "]: " + e.getMessage());
		}
		return user;
    }

}
