package com.temenos.interaction.commands.solr;

/*
 * #%L
 * interaction-commands-solr
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


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.temenos.interaction.core.entity.Entity;
import com.temenos.interaction.core.entity.EntityProperties;
import com.temenos.interaction.core.entity.EntityProperty;
import com.temenos.interaction.core.resource.CollectionResource;
import com.temenos.interaction.core.resource.EntityResource;

public abstract class AbstractSolrCommand {

	public CollectionResource<Entity> buildCollectionResource(String entityName, SolrDocumentList docs) {
		List<EntityResource<Entity>> results = new ArrayList<EntityResource<Entity>>();
		long numFound = docs.getNumFound();
		for (int i = 0; i < 10 && i < numFound; i++) {
			EntityProperties properties = new EntityProperties();
			SolrDocument doc = docs.get(i);
			Collection<String> fields = doc.getFieldNames();
			for (String propName : fields) {
				properties.setProperty(new EntityProperty(propName, doc.getFirstValue(propName)));
			}
			Entity entity = new Entity(entityName, properties);
			results.add(new EntityResource<Entity>(entity));
		}
		return new CollectionResource<Entity>(results) {};
	}
	
	public CollectionResource<Entity> buildCollectionResource(String entityName, String termName, Map<String, List<Term>> termMap) {
		List<EntityResource<Entity>> results = new ArrayList<EntityResource<Entity>>();
		List<Term> terms = termMap.get(termName);
		for (Term t : terms) {
			EntityProperties properties = new EntityProperties();
			properties.setProperty(new EntityProperty(termName, t.getTerm()));
			Entity entity = new Entity(entityName, properties);
			results.add(new EntityResource<Entity>(entity));
		}
		return new CollectionResource<Entity>(results) {};
	}

}
